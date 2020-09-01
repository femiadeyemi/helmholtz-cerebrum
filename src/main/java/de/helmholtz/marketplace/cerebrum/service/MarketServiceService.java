package de.helmholtz.marketplace.cerebrum.service;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;
import de.helmholtz.marketplace.cerebrum.repository.MarketServiceRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

@Service
public class MarketServiceService extends CerebrumServiceBase<MarketService, MarketServiceRepository>
{
    private final MarketServiceRepository marketServiceRepository;

    public MarketServiceService(MarketServiceRepository marketServiceRepository)
    {
        super(MarketService.class, MarketServiceRepository.class);
        this.marketServiceRepository = marketServiceRepository;
    }

    public Page<MarketService> getServices(PageRequest page)
    {
        return getAllEntities(page, marketServiceRepository);
    }

    public MarketService getUser(String uuid)
    {
        return getEntity(uuid, marketServiceRepository);
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
}
