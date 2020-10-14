package de.helmholtz.marketplace.cerebrum.service.common;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public interface CerebrumService<T, R>
{
    Page<T> getAllEntities(PageRequest request, R repository);

    T getEntity(String uuid, R repository);

    T getEntity(String key, String value, R repository);

    T createEntity(T entity, R repository);

    ResponseEntity<T> createEntity(T entity, R repository, UriComponentsBuilder uriComponentsBuilder);

    ResponseEntity<T> updateEntity(String uuid, T entity, R repository, UriComponentsBuilder uriComponentsBuilder);

    ResponseEntity<T> partiallyUpdateEntity(String uuid, R repository, JsonPatch patch);

    ResponseEntity<T> deleteEntity(String uuid, R repository);
}
