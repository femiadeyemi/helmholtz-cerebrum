package de.helmholtz.marketplace.cerebrum.entity.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Arrays;
import java.util.Objects;

import de.helmholtz.marketplace.cerebrum.entity.MarketService;
import de.helmholtz.marketplace.cerebrum.entity.MarketUser;

@RelationshipEntity(type = "MANAGES")
public class Management
{
    @Id @GeneratedValue Long id;

    private String[] roles = {"OTHERS"};

    @StartNode
    @JsonIgnoreProperties({"managedServices"})
    private MarketUser marketUser;

    @EndNode
    @JsonIgnoreProperties({"managementTeam"})
    private MarketService marketService;

    public String[] getRoles()
    {
        return roles;
    }

    public void setRoles(String[] roles)
    {
        this.roles = roles;
    }

    public MarketUser getMarketUser()
    {
        return marketUser;
    }

    public void setMarketUser(MarketUser marketUser)
    {
        this.marketUser = marketUser;
    }

    public MarketService getMarketService() {
        return marketService;
    }

    public void setMarketService(MarketService marketService)
    {
        this.marketService = marketService;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Management that = (Management) o;
        return Arrays.equals(roles, that.roles) &&
                marketUser.equals(that.marketUser) &&
                marketService.equals(that.marketService);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(roles, marketUser, marketService);
    }
}

