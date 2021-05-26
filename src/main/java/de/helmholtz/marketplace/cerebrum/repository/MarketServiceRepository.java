package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entity.MarketService;

public interface MarketServiceRepository extends MongoRepository<MarketService, String>
{
    Optional<MarketService> findByUuid(String uuid);

    Optional<MarketService> findByName(@Param("name") String name);

    Optional<MarketService> findByEntryPoint(@Param("entryPoint") String url);

    @Query(value = "{'serviceProviders.$id' : ?0 }", fields = "{'serviceProviders' : 0}")
    Page<MarketService> findByServiceProvidersUsingUuid(String uuid, PageRequest pageRequest);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
