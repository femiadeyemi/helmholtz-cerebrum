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

import de.helmholtz.marketplace.cerebrum.entities.MarketService;
import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.entities.relationship.ServiceProvider;
import de.helmholtz.marketplace.cerebrum.repository.MarketServiceRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumControllerUtilities.checkField;

@Service
public class MarketServiceService extends CerebrumServiceBase<MarketService, MarketServiceRepository>
{
    private final MarketServiceRepository marketServiceRepository;
    private final OrganizationService organizationService;

    public MarketServiceService(MarketServiceRepository marketServiceRepository,
                                OrganizationService organizationService)
    {
        super(MarketService.class, MarketServiceRepository.class);
        this.marketServiceRepository = marketServiceRepository;
        this.organizationService = organizationService;
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
            MarketService updatedService = marketServiceRepository.createHostedInRelationship(
                    service.getUuid(), organization.getUuid(), serviceProvider.getServiceTechnicalName());
            return ResponseEntity.ok().body(updatedService);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The uuid of either or both service and organisation entity is not supplied. " +
                            "Please check that the request body conform with the definition of " +
                            "host class.");
        }
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
}
