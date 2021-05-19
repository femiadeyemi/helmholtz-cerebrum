package de.helmholtz.marketplace.cerebrum.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;

import de.helmholtz.marketplace.cerebrum.entity.MarketUser;
import de.helmholtz.marketplace.cerebrum.entity.Organization;
import de.helmholtz.marketplace.cerebrum.entity.relationship.Affiliation;
import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumControllerUtilities.checkField;

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
        //email and sub
        return marketUserRepository.findByEmailAndSub(user.get("email").asText(), user.get("sub").asText());
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
    public ResponseEntity<MarketUser> addAffiliations(Affiliation affiliation)
    {
        MarketUser inputUser = affiliation.getUser();
        Organization inputOrganization = affiliation.getOrganization();

        if (inputUser.getUuid() != null && inputOrganization.getUuid() != null) {
            MarketUser user = getUserByAttributes("uuid", inputUser.getUuid());

            if (user.getAffiliations() != null) {
                for (Affiliation a : user.getAffiliations()) {
                    if (a.equals(affiliation)) {
                        return ResponseEntity.noContent().build();
                    }
                }
            }
            Organization organization = organizationService.getOrganization(inputOrganization.getUuid());
            MarketUser updatedUser = marketUserRepository.createBelongsToRelationship(user.getUuid(),
                    organization.getUuid(), affiliation.getStatus(), affiliation.getIsAContactPerson());
            return ResponseEntity.ok().body(updatedUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The uuid of either or both user and organisation entity is not supplied. " +
                            "Please check that the request body conform with the definition of " +
                            "affiliation class.");
        }
    }

    public ResponseEntity<MarketUser> deleteAffiliations(
            String userKey, String userValue, String organizationKey, String organizationValue)
    {
        Boolean userFieldExist = checkField(userKey, MarketUser.class);
        Boolean organizationFieldExist = checkField(organizationKey, Organization.class);
        if (userFieldExist && organizationFieldExist) {
            MarketUser user = getUserByAttributes(userKey, userValue);
            if (user.getAffiliations() != null) {
                for (Affiliation a : user.getAffiliations()) {
                    try {
                        Field field = Organization.class.getDeclaredField(organizationKey);
                        field.setAccessible(true);
                        if (field.get(a.getOrganization()).equals(organizationValue)) {
                            marketUserRepository.deleteAffiliations(user.getUuid(), a.getOrganization().getUuid());
                            break;
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                    }
                }
            }
        }
        return ResponseEntity.noContent().build();
    }
}
