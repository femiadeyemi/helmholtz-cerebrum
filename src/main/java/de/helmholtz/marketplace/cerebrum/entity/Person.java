package de.helmholtz.marketplace.cerebrum.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
public class Person extends AuditMetadata
{
    @Schema(description = "Unique identifier of the marketplace user.",
            example = "usr-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue(strategy = CerebrumEntityUuidGenerator.class)
    private String uuid;

    @Schema(description = "first name of the user.",
            example = "Paul", required = true)
    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Schema(description = "last name or surname of the user.",
            example = "Miller", required = true)
    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Schema(description = "Email address of the user.",
            example = "paul.miller@hifis.net", required = true)
    @Email(message = "Email Address")
    @NotBlank
    @Size(max = 100)
    private String email;

    public void setUuid(String uuid)
    {
        this.uuid = Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid)) ?
                uuid : generate("usr");
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person that = (Person) o;
        return firstName.equals(that.firstName) &&
                lastName.equals(that.lastName) &&
                email.equals(that.email);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(firstName, lastName, email);
    }
}
