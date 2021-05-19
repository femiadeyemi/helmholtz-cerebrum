package de.helmholtz.marketplace.cerebrum.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.helmholtz.marketplace.cerebrum.entity.relationship.Management;
import de.helmholtz.marketplace.cerebrum.entity.relationship.ServiceProvider;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@NodeEntity
public class MarketService extends AuditMetadata
{
    @Schema(description = "Unique identifier of the market service.",
            example = "svc-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue(strategy = CerebrumEntityUuidGenerator.class)
    private String uuid;

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
    private List<String> targetGroup = new ArrayList<>();

    @Schema(description = "")
    private List<String> tags = new ArrayList<>();

    @Schema(description = "List of services provided by this organisation")
    @JsonIgnoreProperties({"marketService"})
    @Relationship(type = "HOSTED_BY")
    private List<ServiceProvider> serviceProviders;

    @JsonIgnoreProperties({"marketService"})
    @Relationship(type = "MANAGES", direction = INCOMING)
    private List<Management> managementTeam;

    public void setUuid(String uuid)
    {
        this.uuid =  Boolean.TRUE.equals(
                CerebrumEntityUuidGenerator.isValid(uuid))
                ? uuid : generate("org");
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
