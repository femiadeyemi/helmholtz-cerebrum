package de.helmholtz.marketplace.cerebrum.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Document
public class MarketService extends AuditMetadata
{
    @Schema(description = "Unique identifier of the market service.",
            example = "svc-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Setter(AccessLevel.NONE)
    @Id
    private String uuid = generate("svc");

    @NotNull
    @Schema(description = "Name of a Service", example = "Sync+Share", required = true)
    private String name;

    @Schema(description = "Description of a Service",
            example = "A awesome Sync+Share Service provides by Helmholtz Zentrum xy")
    private String description;

    @Schema(description = "Summary of the service's description", example = "Sync+Share Service")
    private String summary;

    @Schema(description = "Url to a Service", example = "serviceXy.helmholtz.de")
    private String entryPoint;

    @Schema(description = "The service version number", example = "1.0.1")
    private String version;

    @Schema(description = "Service's email address", example = "fake-email@example.org")
    @Email
    private String email;

    @Schema(description = "", example = "True")
    private boolean multiTenancy;

    @Schema(description = "")
    private String enrolmentPolicy;

    @Schema(description = "")
    private String policy;

    @Schema(description = "")
    private String documentation;

    @Schema(description = "", example = "PRODUCTION")
    private Phase phase;

    @Schema(description = "")
    private Set<String> targetGroup = new TreeSet<>();

    @Schema(description = "")
    private Set<String> tags = new TreeSet<>();

    @Schema(description = "List of services provided by this organisation")
    @DBRef
    private List<Organization> serviceProviders = new ArrayList<>();

    @Schema(description = "")
    @DBRef
    private List<Person> managementTeam = new ArrayList<>();

    public void setUuid(@Nullable String uuid)
    {
        this.uuid =  Boolean.TRUE.equals(
                CerebrumEntityUuidGenerator.isValid(uuid))
                ? uuid : generate("svc");
    }

    public void addTarget(String target)
    {
        targetGroup.add(target);
    }

    public void removeTarget(String target)
    {
        targetGroup.remove(target);
    }

    public void addTag(String tag)
    {
        tags.add(tag);
    }

    public void removeTag(String tag)
    {
        tags.remove(tag);
    }

    public void addProvider(Organization org)
    {
        serviceProviders.add(org);
    }

    public void removeProvider(Organization org)
    {
        serviceProviders.remove(org);
    }

    public void addTeamMember(Person member)
    {
        managementTeam.add(member);
    }

    public void removeTeamMember(Person member)
    {
        managementTeam.remove(member);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketService service = (MarketService) o;
        return name.equals(service.name) &&
                entryPoint.equals(service.entryPoint);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, entryPoint);
    }
}

enum Phase
{
    TEST,
    PILOT,
    PRODUCTION
}
