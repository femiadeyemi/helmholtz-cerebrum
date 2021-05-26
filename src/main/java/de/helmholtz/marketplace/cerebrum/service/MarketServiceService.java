package de.helmholtz.marketplace.cerebrum.service;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import de.helmholtz.marketplace.cerebrum.entity.MarketService;
import de.helmholtz.marketplace.cerebrum.entity.Organization;
import de.helmholtz.marketplace.cerebrum.entity.Person;
import de.helmholtz.marketplace.cerebrum.repository.MarketServiceRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

@Service
public class MarketServiceService extends CerebrumServiceBase<MarketService, MarketServiceRepository>
{
    private final MarketServiceRepository marketServiceRepository;
    private final OrganizationService organizationService;
    private final PersonService personService;

    public MarketServiceService(MarketServiceRepository marketServiceRepository,
                                OrganizationService organizationService,
                                PersonService personService)
    {
        super(MarketService.class, MarketServiceRepository.class);
        this.marketServiceRepository = marketServiceRepository;
        this.organizationService = organizationService;
        this.personService = personService;
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
    public ResponseEntity<MarketService> addProvider(String serviceUuid, String providerUuid)
    {
        return provider(serviceUuid, providerUuid, true);
    }

    public ResponseEntity<MarketService> deleteProvider(String serviceUuid, String providerUuid)
    {
        return provider(serviceUuid, providerUuid, false);
    }

    private ResponseEntity<MarketService> provider(String serviceUuid, String providerUuid, boolean toAdd)
    {
        MarketService service = getService(serviceUuid);
        Organization provider = organizationService.getOrganization(providerUuid);

        if (service.getServiceProviders() != null) {
            for (Organization sp: service.getServiceProviders()) {
                if (sp.getUuid().equals(providerUuid)) {
                    return ResponseEntity.noContent().build();
                }
            }
        }

        if (toAdd) {
            service.addProvider(provider);
        } else {
            service.removeProvider(provider);
        }
        MarketService updatedService = marketServiceRepository.save(service);
        return ResponseEntity.ok().body(updatedService);
    }

    //Service Management Team
    public ResponseEntity<MarketService> addTeamMember(String serviceUuid, String personUuid)
    {
        return teamMember(serviceUuid, personUuid, true);
    }

    public ResponseEntity<MarketService> deleteTeamMember(String serviceUuid, String personUuid)
    {
        return teamMember(serviceUuid, personUuid, false);
    }

    private ResponseEntity<MarketService> teamMember(String serviceUuid, String personUuid, boolean toAdd)
    {
        MarketService service = getService(serviceUuid);
        Person teamMember = personService.getPerson(personUuid);

        if (service.getManagementTeam() != null) {
            for (Person tm: service.getManagementTeam()) {
                if (tm.getUuid().equals(personUuid)) {
                    return ResponseEntity.noContent().build();
                }
            }
        }

        if (toAdd) {
            service.addTeamMember(teamMember);
        } else {
            service.removeTeamMember(teamMember);
        }
        MarketService updatedService = marketServiceRepository.save(service);
        return ResponseEntity.ok().body(updatedService);
    }
}
