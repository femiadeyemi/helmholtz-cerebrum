package de.helmholtz.marketplace.cerebrum.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.URL;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

@NodeEntity
public class Organization {

    @Schema(description = "Unique identifier of the organisation",
            example = "0", required = true)
    @Id
    @GeneratedValue
    private Long id;

    @Schema(description = "Name of the organisation in full",
            example = "Deutsches Elektronen-Synchrotron", required = true)
    @NotNull
    private String name;

    @Schema(description = "The shortened form of an organisation's name - this " +
            "can be an acronym or initial",
            example = "DESY", required = false)
    private String abbreviation;

    @Schema(description = "Valid web address link to the organisation logo " +
            "or base64 encoded string of the organisation logo", required = false,
            example = "https://www.desy.de/++resource++desy/images/desy_logo_3c_web.svg")
    private String img;

    @Schema(description = "The organisation web address",
            example = "https://www.desy.de/", required = true)
    @URL(message = "Web address")
    @NotNull
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
