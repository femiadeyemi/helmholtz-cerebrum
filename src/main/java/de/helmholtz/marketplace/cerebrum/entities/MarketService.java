package de.helmholtz.marketplace.cerebrum.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import de.helmholtz.marketplace.cerebrum.entities.relationship.ServiceProvider;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;

public class MarketService
{
    @Schema(description = "Unique identifier of the market service.",
            example = "svc-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Id @GeneratedValue(strategy = CerebrumEntityUuidGenerator.class)
    private String uuid;

    @NotNull
    @Schema(description = "Name of a Service", example = "Sync+Share", required = true)
    private String name;

    @Schema(description = "Description of a Service",
            example = "A awesome Sync+Share Service provides by Helmholtz Zentrum xy")
    private String description;

    @Schema(description = "Url to a Service", example = "serviceXy.helmholtz.de")
    private String url;

    @Schema(description = "Creation date of a Service", example = "2020-02-19")
    private Date created;

    @Schema(description = "Date of last modification", example = "2020-03-24")
    private Date lastModified;

    @Schema(description = "Specifies the current lifecycle")
    private LifecycleStatus lifecycleStatus;

    @Schema(description = "Specifies the authentication which a user can use to log in to a service")
    private Authentication authentication;

    @Schema(description = "List of services provided by this organisation")
    @JsonIgnoreProperties({"marketService"})
    @Relationship(type = "HOSTED_BY")
    private List<ServiceProvider> serviceProviders;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid =  Boolean.TRUE.equals(
                CerebrumEntityUuidGenerator.isValid(uuid))
                ? uuid : generate("org");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public LifecycleStatus getLifecycleStatus()
    {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus)
    {
        this.lifecycleStatus = lifecycleStatus;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public List<ServiceProvider> getServiceProviders()
    {
        return serviceProviders;
    }

    public void setOrganizations(List<ServiceProvider> serviceProviders)
    {
        this.serviceProviders = serviceProviders;
    }
}
