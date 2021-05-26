package de.helmholtz.marketplace.cerebrum.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Document
public class Person extends AuditMetadata
{
    @Schema(description = "Unique identifier of the marketplace user.",
            example = "prn-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Setter(AccessLevel.NONE)
    @Id
    private String uuid = generate("prn");

    @Schema(description = "first name of the user.", example = "Paul", required = true)
    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Schema(description = "last name or surname of the user.", example = "Miller", required = true)
    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Schema(description = "List of email address of the user.", example = "[paul.miller@hifis.net]", required = true)
    private List<@Email String> emails = new ArrayList<>();

    public void setUuid(String uuid)
    {
        this.uuid = Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid)) ?
                uuid : generate("prn");
    }

    public void addEmail(String email)
    {
        if (!emails.contains(email)) emails.add(email);
    }

    public void removeEmail(String email)
    {
        emails.remove(email);
    }

    public void replacePrimaryEmail(String email)
    {
        if (emails.contains(email) && emails.size() > 1) removeEmail(email);
        emails.add(0, email);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person that = (Person) o;
        return firstName.equals(that.firstName) &&
                lastName.equals(that.lastName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(firstName, lastName);
    }
}
