package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.Organization;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface OrganizationRepository
        extends PagingAndSortingRepository<Organization, Long>
{
    Optional<Organization> findByUuid(String uuid);

    void deleteByUuid(String id);
}
