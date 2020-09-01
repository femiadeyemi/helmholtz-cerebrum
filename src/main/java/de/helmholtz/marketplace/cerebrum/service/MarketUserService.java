package de.helmholtz.marketplace.cerebrum.service;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;
import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

@Service
public class MarketUserService extends CerebrumServiceBase<MarketUser, MarketUserRepository>
{
    private final MarketUserRepository marketUserRepository;

    public MarketUserService(MarketUserRepository marketUserRepository)
    {
        super(MarketUser.class, MarketUserRepository.class);
        this.marketUserRepository = marketUserRepository;
    }

    public Page<MarketUser> getUsers(PageRequest page)
    {
        return getAllEntities(page, marketUserRepository);
    }

    public MarketUser getUser(String uuid)
    {
        return getEntity(uuid, marketUserRepository);
    }

    public ResponseEntity<MarketUser> createUser(
            MarketUser entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return createEntity(entity, marketUserRepository, uriComponentsBuilder);
    }

    public ResponseEntity<MarketUser> updateUser(
            String uuid, MarketUser entity, UriComponentsBuilder uriComponentsBuilder)
    {
        return updateEntity(uuid, entity, marketUserRepository, uriComponentsBuilder);
    }

    public ResponseEntity<MarketUser> partiallyUpdateUser(String uuid, JsonPatch patch)
    {
        return partiallyUpdateEntity(uuid, marketUserRepository, patch);
    }

    public ResponseEntity<MarketUser> deleteUser(String uuid)
    {
        return deleteEntity(uuid, marketUserRepository);
    }
}
