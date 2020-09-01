package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;

public interface MarketServiceRepository extends Neo4jRepository<MarketService, Long>
{
    Optional<MarketService> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
