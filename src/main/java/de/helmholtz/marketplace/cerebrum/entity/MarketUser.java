package de.helmholtz.marketplace.cerebrum.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.Size;
import java.util.List;

import de.helmholtz.marketplace.cerebrum.entity.relationship.Affiliation;
import de.helmholtz.marketplace.cerebrum.entity.relationship.Management;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@NodeEntity
public class MarketUser extends Person
{
    @Schema(description = "User chosen name to represent him or herself", example = "pm")
    @Size(max = 20)
    private String screenName;

    @Schema(description = "Helmholtz AAI generated unique user identifier",
            example = "110248495921238986420", required = true)
    private String sub;

    @JsonIgnoreProperties("user")
    @Relationship(type = "BELONGS_TO")
    private List<Affiliation> affiliations;

    @JsonIgnoreProperties("marketUser")
    @Relationship(type = "MANAGES")
    private List<Management> managedServices;
}
