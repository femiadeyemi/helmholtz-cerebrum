package de.helmholtz.marketplace.cerebrum.service;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

@Service
public class OrganizationService extends CerebrumServiceBase<Organization, OrganizationRepository>
{
    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository)
    {
        super(Organization.class, OrganizationRepository.class);
        this.organizationRepository = organizationRepository;
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
}
