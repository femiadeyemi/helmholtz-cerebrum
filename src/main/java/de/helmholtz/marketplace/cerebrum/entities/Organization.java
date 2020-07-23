package de.helmholtz.marketplace.cerebrum.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.URL;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

@Schema(name = "Organization", description = "POJO that represents a single organization entry.")
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
    @Schema(description = "A list of Services which are provided by the organization")
    private Iterable<MarketService> serviceList;
    @Schema(description = "A list with users, to have a contact in case of trouble")
    private Iterable<MarketUser> contactPersons;

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

    public Iterable<MarketService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(Iterable<MarketService> serviceList) {
        this.serviceList = serviceList;
    }

    public Iterable<MarketUser> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(Iterable<MarketUser> contactPersons) {
        this.contactPersons = contactPersons;
    }
}
