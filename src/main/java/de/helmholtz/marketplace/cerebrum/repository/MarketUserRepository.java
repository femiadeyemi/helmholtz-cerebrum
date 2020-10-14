package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;

public interface MarketUserRepository extends Neo4jRepository<MarketUser, Long>
{
    Optional<MarketUser> findBySub(@Param("sub") String sub);

    Optional<MarketUser> findByFirstName(@Param("firstName") String firstName);

    Optional<MarketUser> findByLastName(@Param("lastName") String lastName);

    Optional<MarketUser> findByEmail(@Param("email") String email);

    Optional<MarketUser> findByScreenName(@Param("screenName") String screenName);

    MarketUser findByEmailAndSub(@Param("email") String email, @Param("sub") String sub);

    @Query("MATCH (user:MarketUser),(org:Organization) " +
            "WHERE user.uuid = userUuid AND org.uuid = $orgUuid " +
            "CREATE (user)-[r:BELONGS_TO { status : $status, isAContactPerson : $isAContactPerson }]->(org) " +
            "RETURN user, r")
    MarketUser createBelongsToRelationship(@Param("userUuid") String userUuid,
                                           @Param("orgUuid") String orgUuid,
                                           @Param("status") String status,
                                           @Param("isAContactPerson") boolean isAContactPerson);

    @SuppressWarnings("UnusedReturnValue")
    @Query("MATCH (user:MarketUser)-[r:BELONGS_TO]->(org:Organization) " +
            "WHERE user.uuid = userUuid AND org.uuid = $orgUuid " +
            "DELETE r")
    Long deleteAffiliations(@Param("userUuid") String userUuid, @Param("orgUuid") String orgUuid);

    Optional<MarketUser> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String uuid);
}