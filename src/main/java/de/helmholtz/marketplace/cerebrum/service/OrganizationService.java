package de.helmholtz.marketplace.cerebrum.service;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import de.helmholtz.marketplace.cerebrum.entity.MarketService;
import de.helmholtz.marketplace.cerebrum.entity.MarketUser;
import de.helmholtz.marketplace.cerebrum.entity.Organization;
import de.helmholtz.marketplace.cerebrum.repository.MarketServiceRepository;
import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

@Service
public class OrganizationService extends CerebrumServiceBase<Organization, OrganizationRepository>
{
    private final OrganizationRepository organizationRepository;
    private final MarketUserRepository marketUserRepository;
    private final MarketServiceRepository marketServiceRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
                               MarketUserRepository marketUserRepository,
                               MarketServiceRepository marketServiceRepository)
    {
        super(Organization.class, OrganizationRepository.class);
        this.organizationRepository = organizationRepository;
        this.marketUserRepository = marketUserRepository;
        this.marketServiceRepository = marketServiceRepository;
    }

    public Page<Organization> getOrganizations(PageRequest page)
    {
        return getAllEntities(page, organizationRepository);
    }

    public Organization getOrganization(String uuid)
    {
        return getEntity(uuid, organizationRepository);
    }

    public Organization getOrganizationByAttributes(String attr, String value)
    {
        return getEntity(attr, value, organizationRepository);
    }

    public ResponseEntity<Organization> createOrganisation(
            Organization entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return createEntity(entity, organizationRepository, uriComponentsBuilder);
    }

    public ResponseEntity<Organization> updateOrganisation(
            String uuid, Organization entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return updateEntity(uuid, entity, organizationRepository, uriComponentsBuilder);
    }

    public ResponseEntity<Organization> partiallyUpdateOrganisation(String uuid, JsonPatch patch)
    {
        return partiallyUpdateEntity(uuid, organizationRepository, patch);
    }

    public ResponseEntity<Organization> deleteOrganisation(String uuid)
    {
        return deleteEntity(uuid, organizationRepository);
    }

    public Page<MarketService> getHostedServices(String uuid, PageRequest page)
    {
        return marketServiceRepository.findByServiceProvidersUsingUuid(uuid,  page);
    }

    public Page<MarketUser> listKnownMembers(String uuid, PageRequest page)
    {
        return marketUserRepository.findAllMembers(uuid,  page);
    }
}
