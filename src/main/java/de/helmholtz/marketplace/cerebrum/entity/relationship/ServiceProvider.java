package de.helmholtz.marketplace.cerebrum.entity.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Objects;

import de.helmholtz.marketplace.cerebrum.entity.MarketService;
import de.helmholtz.marketplace.cerebrum.entity.Organization;

@RelationshipEntity(type = "HOSTED_BY")
public class ServiceProvider
{
    @Id @GeneratedValue Long id;

    private String serviceTechnicalName;

    @StartNode
    @JsonIgnoreProperties("serviceProviders")
    private MarketService marketService;

    @EndNode
    @JsonIgnoreProperties({"hostedServices", "members"})
    private Organization organization;

    public String getServiceTechnicalName()
    {
        return serviceTechnicalName;
    }

    public void setServiceTechnicalName(String serviceTechnicalName)
    {
        this.serviceTechnicalName = serviceTechnicalName;
    }

    public MarketService getMarketService()
    {
        return marketService;
    }

    public void setMarketService(MarketService marketService)
    {
        this.marketService = marketService;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(Organization organization)
    {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceProvider serviceProvider = (ServiceProvider) o;
        return Objects.equals(serviceTechnicalName, serviceProvider.serviceTechnicalName) &&
                marketService.equals(serviceProvider.marketService) &&
                organization.equals(serviceProvider.organization);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(serviceTechnicalName, marketService, organization);
    }
}
