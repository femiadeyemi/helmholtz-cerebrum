package de.helmholtz.marketplace.cerebrum.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;

@Schema(name = "Organization", description = "POJO that represents a single organization entry.")
@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Document
public class Organization
{
    @Schema(description = "Unique identifier of the organisation",
            example = "org-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Setter(AccessLevel.NONE)
    @Id
    private String uuid = generate("org");

    @Schema(description = "Name of the organisation in full",
            example = "Deutsches Elektronen-Synchrotron", required = true)
    @NotNull
    private String name;

    @Schema(description = "Name of the organisation in German")
    private String nameDE;

    @Schema(description = "The shortened form of an organisation's name - this " +
            "can be an acronym or initial",
            example = "DESY")
    @Setter(AccessLevel.NONE)
    private String abbreviation;

    @Schema(description = "Valid web address link to the organisation logo " +
            "or base64 encoded string of the organisation logo",
            example = "https://www.desy.de/++resource++desy/images/desy_logo_3c_web.svg")
    private String img;

    @Schema(description = "The organisation web address",
            example = "https://www.desy.de/", required = true)
    @URL(message = "Web address")
    @NotNull
    private String url;

    @Schema(description = "", example = "HELMHOLTZ_CENTRE")
    private Type type;

    public void setUuid(String uuid)
    {
        this.uuid =  Boolean.TRUE.equals(
                CerebrumEntityUuidGenerator.isValid(uuid))
                ? uuid : generate("org");
    }

    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation.toUpperCase();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return name.equals(that.name) &&
                Objects.equals(abbreviation, that.abbreviation) &&
                Objects.equals(img, that.img) &&
                url.equals(that.url);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, abbreviation, img, url);
    }
}

enum Type
{
    HELMHOLTZ_CENTRE,
    DEPARTMENT,
    GROUP,
    UNIT,
    OTHERS
}
