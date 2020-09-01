package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.Organization;

public interface OrganizationRepository extends Neo4jRepository<Organization, Long>
{
    Optional<Organization> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
