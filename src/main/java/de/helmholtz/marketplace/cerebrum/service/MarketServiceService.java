package de.helmholtz.marketplace.cerebrum.service;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;

import de.helmholtz.marketplace.cerebrum.entity.MarketService;
import de.helmholtz.marketplace.cerebrum.entity.MarketUser;
import de.helmholtz.marketplace.cerebrum.entity.Organization;
import de.helmholtz.marketplace.cerebrum.entity.relationship.Management;
import de.helmholtz.marketplace.cerebrum.entity.relationship.ServiceProvider;
import de.helmholtz.marketplace.cerebrum.repository.MarketServiceRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumControllerUtilities.checkField;

@Service
public class MarketServiceService extends CerebrumServiceBase<MarketService, MarketServiceRepository>
{
    private final MarketServiceRepository marketServiceRepository;
    private final OrganizationService organizationService;
    private final MarketUserService marketUserService;

    public MarketServiceService(MarketServiceRepository marketServiceRepository,
                                OrganizationService organizationService,
                                MarketUserService marketUserService)
    {
        super(MarketService.class, MarketServiceRepository.class);
        this.marketServiceRepository = marketServiceRepository;
        this.organizationService = organizationService;
        this.marketUserService = marketUserService;
    }

    public Page<MarketService> getServices(PageRequest page)
    {
        return getAllEntities(page, marketServiceRepository);
    }

    public MarketService getService(String uuid)
    {
        return getEntity(uuid, marketServiceRepository);
    }

    public MarketService getServiceByAttributes(String attr, String value)
    {
        return getEntity(attr, value, marketServiceRepository);
    }

    public ResponseEntity<MarketService> createService(
            MarketService entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return createEntity(entity, marketServiceRepository, uriComponentsBuilder);
    }

    public ResponseEntity<MarketService> updateService(
            String uuid, MarketService entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return updateEntity(uuid, entity, marketServiceRepository, uriComponentsBuilder);
    }

    public ResponseEntity<MarketService> partiallyUpdateService(String uuid, JsonPatch patch)
    {
        return partiallyUpdateEntity(uuid, marketServiceRepository, patch);
    }

    public ResponseEntity<MarketService> deleteService(String uuid)
    {
        return deleteEntity(uuid, marketServiceRepository);
    }

    //service-provider
    public ResponseEntity<MarketService> addProvider(ServiceProvider serviceProvider)
    {
        MarketService inputService = serviceProvider.getMarketService();
        Organization inputOrganization = serviceProvider.getOrganization();

        if (inputService.getUuid() != null && inputOrganization.getUuid() != null) {
            MarketService service = getServiceByAttributes("uuid", inputService.getUuid());

            if (service.getServiceProviders() != null) {
                for (ServiceProvider provider : service.getServiceProviders()) {
                    if (provider.equals(serviceProvider)) {
                        return ResponseEntity.noContent().build();
                    }
                }
            }
            Organization organization = organizationService.getOrganization(inputOrganization.getUuid());
            MarketService updatedService = marketServiceRepository.createHostedByRelationship(
                    service.getUuid(), organization.getUuid(), serviceProvider.getServiceTechnicalName());
            return ResponseEntity.ok().body(updatedService);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The uuid of either or both service and organisation entity is not supplied. " +
                            "Please check that the request body conform with the definition of " +
                            "host class.");
        }
    }

    public ResponseEntity<MarketService> updateProvider(ServiceProvider serviceProvider)
    {
        MarketService serviceNode = serviceProvider.getMarketService();
        Organization organizationNode = serviceProvider.getOrganization();

        if (serviceNode.getUuid() != null && organizationNode.getUuid() != null) {
            MarketService service = getServiceByAttributes("uuid", serviceNode.getUuid());

            if (service.getServiceProviders() == null) {
                return addProvider(serviceProvider);
            }
            for (ServiceProvider provider : service.getServiceProviders()) {
                if (provider.getOrganization().getAbbreviation()
                        .equals(organizationNode.getAbbreviation())) {
                    MarketService updatedRelationship = marketServiceRepository.updateServiceProviderRelationship(
                            service.getUuid(),
                            provider.getOrganization().getUuid(),
                            serviceProvider.getServiceTechnicalName());
                    return ResponseEntity.ok().body(updatedRelationship);
                }
            }
        }
        return addProvider(serviceProvider);
    }

    public ResponseEntity<MarketService> deleteProviders(
            String serviceKey, String serviceValue, String organizationKey, String organizationValue)
    {
        Boolean serviceFieldExist = checkField(serviceKey, MarketService.class);
        Boolean organizationFieldExist = checkField(organizationKey, Organization.class);
        if (serviceFieldExist && organizationFieldExist) {
            MarketService service = getServiceByAttributes(serviceKey, serviceValue);
            if (service.getServiceProviders() != null) {
                for (ServiceProvider provider: service.getServiceProviders()) {
                    try {
                        Field field = Organization.class.getDeclaredField(organizationKey);
                        field.setAccessible(true);
                        if (field.get(provider.getOrganization()).equals(organizationValue)) {
                            marketServiceRepository.deleteServiceProviders(
                                    service.getUuid(), provider.getOrganization().getUuid());
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

    //Service Management Team
    public ResponseEntity<MarketService> addTeamMember(Management management)
    {
        MarketService inputService = management.getMarketService();
        MarketUser inputUser = management.getMarketUser();

        if (inputService.getUuid() != null && inputUser.getUuid() != null) {
            MarketService service = getServiceByAttributes("uuid", inputService.getUuid());
            if (service.getManagementTeam() != null) {
                for (Management member : service.getManagementTeam()) {
                    if (member.equals(management)) {
                        return ResponseEntity.noContent().build();
                    }
                }
            }
            MarketUser user = marketUserService.getUser(inputUser.getUuid());
            MarketService updatedService = marketServiceRepository.createManagesRelationship(
                    service.getUuid(), user.getUuid(), management.getRoles());
            return ResponseEntity.ok().body(updatedService);
        }
        return null;
    }

    public ResponseEntity<MarketService> deleteTeamMember(
            String serviceKey, String serviceValue, String userKey, String userValue)
    {
        Boolean serviceFieldExist = checkField(serviceKey, MarketService.class);
        Boolean userFieldExist = checkField(userKey, MarketUser.class);
        if (serviceFieldExist && userFieldExist) {
            MarketService service = getServiceByAttributes(serviceKey, serviceValue);
            if (service.getManagementTeam() != null) {
                for (Management member : service.getManagementTeam()) {
                    try {
                        Field field = MarketUser.class.getDeclaredField(userKey);
                        field.setAccessible(true);
                        if (field.get(member.getMarketUser()).equals(userValue)) {
                            marketServiceRepository.deleteManagementMember(
                                    service.getUuid(), member.getMarketUser().getUuid());
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
