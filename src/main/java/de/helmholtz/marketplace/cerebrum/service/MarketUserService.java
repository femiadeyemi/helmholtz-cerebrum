package de.helmholtz.marketplace.cerebrum.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import de.helmholtz.marketplace.cerebrum.entity.MarketUser;
import de.helmholtz.marketplace.cerebrum.entity.Organization;
import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

@Service
public class MarketUserService extends CerebrumServiceBase<MarketUser, MarketUserRepository>
{
    private final MarketUserRepository marketUserRepository;
    private final OrganizationService organizationService;

    public MarketUserService(MarketUserRepository marketUserRepository,
                             OrganizationService organizationService)
    {
        super(MarketUser.class, MarketUserRepository.class);
        this.marketUserRepository = marketUserRepository;
        this.organizationService = organizationService;
    }

    public Page<MarketUser> getUsers(PageRequest page)
    {
        return getAllEntities(page, marketUserRepository);
    }

    public MarketUser getUser(String uuid)
    {
        return getEntity(uuid, marketUserRepository);
    }

    public MarketUser getUser(JsonNode user)
    {
        //sub
        return marketUserRepository.findBySub(user.get("sub").asText());
    }

    public MarketUser getUserByAttributes(String attr, String value)
    {
        return getEntity(attr, value, marketUserRepository);
    }

    public MarketUser createUser(MarketUser entity)
    {
        return createEntity(entity, marketUserRepository);
    }

    public ResponseEntity<MarketUser> createUser(
            MarketUser entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return createEntity(entity, marketUserRepository, uriComponentsBuilder);
    }

    public ResponseEntity<MarketUser> updateUser(
            String uuid, MarketUser entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return updateEntity(uuid, entity, marketUserRepository, uriComponentsBuilder);
    }

    public ResponseEntity<MarketUser> partiallyUpdateUser(String uuid, JsonPatch patch)
    {
        return partiallyUpdateEntity(uuid, marketUserRepository, patch);
    }

    public ResponseEntity<MarketUser> deleteUser(String uuid)
    {
        return deleteEntity(uuid, marketUserRepository);
    }

    //affiliation
    public ResponseEntity<MarketUser> addAffiliation(String userUuid, String affiliationUuid)
    {
        return affiliation(userUuid, affiliationUuid, true);
    }

    public ResponseEntity<MarketUser> deleteAffiliation(String userUuid, String affiliationUuid)
    {
        return affiliation(userUuid, affiliationUuid, false);
    }

    private ResponseEntity<MarketUser> affiliation(String userUuid, String affiliationUuid, boolean toAdd)
    {
        MarketUser user = getUser(userUuid);
        Organization affiliate = organizationService.getOrganization(affiliationUuid);
        if (user.getAffiliations() != null) {
            for (Organization a : user.getAffiliations()) {
                if (a.getUuid().equals(affiliationUuid)) {
                    return ResponseEntity.noContent().build();
                }
            }
        }
        if (toAdd) {
            user.addAffiliation(affiliate);
        } else {
            user.removeAffiliation(affiliate);
        }
        MarketUser updatedUser = marketUserRepository.save(user);
        return ResponseEntity.ok().body(updatedUser);
    }
}
