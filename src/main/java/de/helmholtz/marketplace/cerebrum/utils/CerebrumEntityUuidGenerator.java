package de.helmholtz.marketplace.cerebrum.utils;

import org.neo4j.ogm.id.IdStrategy;
import org.springframework.web.server.ServerErrorException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumInvalidUuidException;

public class CerebrumEntityUuidGenerator implements IdStrategy
{
    public enum PrefixEnum
    {
        ORGANIZATION("org", "organization"),
        HELMHOLTZMARKETUSER ("usr", "marketuser"),
        MARKETSERVICE ("svc", "marketservice");

        private final String prefix;
        private final String className;

        PrefixEnum(String prefix, String className)
        {
            this.prefix = prefix;
            this.className = className;
        }

        public static String getEntityPrefix(String className)
        {
            for (PrefixEnum p : PrefixEnum.values()) {
                if (Objects.equals(p.className.toLowerCase(),
                        className.toLowerCase())) {
                    return p.prefix;
                }
            }
            throw new IllegalArgumentException(
                    "Unknown entity with class name " + className);
        }

        public static void checkPrefixValidity(String prefix)
        {
            boolean valid = Arrays.stream(
                    PrefixEnum.values()).anyMatch(p -> p.prefix.equals(prefix));
            if (!valid) throw new IllegalArgumentException(
                    "Prefix: '" + prefix +"' is unknown to cerebrum." );
        }
    }

    @Override
    public Object generateId(Object entity)
    {
        String[] packages = entity.getClass().getName().split("\\.");
        int length = packages.length;
        return generate(PrefixEnum.getEntityPrefix(packages[length-1]));
    }

    public static synchronized String generate(String prefix)
    {
        PrefixEnum.checkPrefixValidity(prefix);
        return prefix + "-" + generateType1UUID().toString();
    }

    public static Boolean isValid(String id)
    {
        try {
            PrefixEnum.checkPrefixValidity(id.split("-")[0]);
            UUID uuid = UUID.fromString(id.substring(id.indexOf('-') + 1));
            return uuid.version() > 0;
        } catch (IllegalArgumentException | NullPointerException ex) {
            return false;
        }
    }

    public static void checkUuidValidity(String uuid)
    {
        if (Boolean.FALSE.equals(isValid(uuid)))
            throw new CerebrumInvalidUuidException(uuid);
    }

    /**
     * Type 1 UUID Generation
     * source: https://github.com/eugenp/tutorials/blob/master/core-java-modules/core-java/src/main/java/com/baeldung/uuid/UUIDGenerator.java
     */
    public static UUID generateType1UUID()
    {

        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();

        return new UUID(most64SigBits, least64SigBits);
    }

    private static long get64LeastSignificantBitsForVersion1()
    {
        try {
            Random random = SecureRandom.getInstanceStrong();
            long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
            long variant3BitFlag = 0x8000000000000000L;
            return random63BitLong + variant3BitFlag;
        } catch (NoSuchAlgorithmException e) {
            throw new ServerErrorException("Error in generating secure random number for uuid", e);
        }
    }

    private static long get64MostSignificantBitsForVersion1()
    {
        LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
        Duration duration = Duration.between(start, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long nanos = duration.getNano();
        long timeForUuidIn100Nanos = seconds * 10000000 + nanos * 100;
        long least12SignificatBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
        long version = 1 << 12;
        return (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificatBitOfTime;
    }
}
