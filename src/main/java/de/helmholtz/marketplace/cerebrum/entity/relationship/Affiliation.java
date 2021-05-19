package de.helmholtz.marketplace.cerebrum.entity.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Objects;

import de.helmholtz.marketplace.cerebrum.entity.MarketUser;
import de.helmholtz.marketplace.cerebrum.entity.Organization;

@RelationshipEntity(type = "BELONGS_TO")
public class Affiliation
{
    @Id @GeneratedValue Long id;
    private String status;
    private boolean isAContactPerson;

    @StartNode
    @JsonIgnoreProperties({"affiliations"})
    private MarketUser user;

    @EndNode
    @JsonIgnoreProperties({"members"})
    private Organization organization;

    public MarketUser getUser()
    {
        return user;
    }

    public void setUser(MarketUser user)
    {
        this.user = user;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(Organization organization)
    {
        this.organization = organization;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Boolean getIsAContactPerson()
    {
        return isAContactPerson;
    }

    public void setIsAContactPerson(Boolean liaison)
    {
        isAContactPerson = liaison != null && liaison;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Affiliation that = (Affiliation) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(isAContactPerson, that.isAContactPerson) &&
                user.equals(that.user) &&
                organization.equals(that.organization);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(status, isAContactPerson, user, organization);
    }
}