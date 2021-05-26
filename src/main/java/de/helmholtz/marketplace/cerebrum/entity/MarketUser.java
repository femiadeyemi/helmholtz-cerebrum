package de.helmholtz.marketplace.cerebrum.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Document
public class MarketUser extends AuditMetadata
{
    @Schema(description = "Unique identifier of the market service.",
            example = "svc-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Setter(AccessLevel.NONE)
    @Id
    private String uuid = generate("usr");

    @Schema(description = "User chosen name to represent him or herself", example = "pm")
    @Size(max = 20)
    private String screenName;

    @Schema(description = "Helmholtz AAI generated unique user identifier",
            example = "110248495921238986420", required = true)
    private String sub;

    @Schema(description = "Helmholtz AAI generated unique user identifier")
    @DBRef
    private Person profile;

    @Schema(description = "")
    @DBRef
    private List<Organization> affiliations = new ArrayList<>();

    @Schema(description = "")
    @DBRef
    private List<MarketService> managedServices = new ArrayList<>();

    public void addAffiliation(Organization org)
    {
        affiliations.add(org);
    }

    public void removeAffiliation(Organization org)
    {
        affiliations.remove(org);
    }

    public void setUuid(@Nullable String uuid)
    {
        this.uuid =  Boolean.TRUE.equals(
                CerebrumEntityUuidGenerator.isValid(uuid))
                ? uuid : generate("usr");
    }
}
