package de.helmholtz.marketplace.cerebrum.service.common;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumEntityNotFoundException;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumControllerUtilities;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.checkUuidValidity;

public abstract class CerebrumServiceBase<T, R> implements CerebrumService<T, R>
{

    private final Class<R> repositoryClass;
    private final Class<T> entityClass;

    protected CerebrumServiceBase(Class<T> entityClass, Class<R> repository)
    {
        this.repositoryClass = repository;
        this.entityClass = entityClass;
    }

    protected Method repositoryMethod(String methodName, Class<?>... parameterTypes)
    {
        try {
            return repositoryClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "the method, "+ methodName + "is unknown to the repository: " + repositoryClass.getSimpleName(), e);
        }
    }

    protected Method entityMethod(String methodName, Class<?>... parameterTypes)
    {
        try {
            return entityClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "the method, "+ methodName + "is unknown to the entity: " + entityClass.getSimpleName(), e);
        }
    }

    protected Object invoke(Method method, Object obj, Object... args)
    {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Due to lack of accessibility, this method cannot be invoke", e);
        } catch (InvocationTargetException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "The called method throws an exception, check the cause for details", e);
        }
    }

    protected String getPath(T entity)
    {
        String path;
        switch (entity.getClass().getSimpleName().toLowerCase()) {
            case "organization":
                path = "organizations";
                break;
            case "marketservice":
                path = "services";
                break;
            case "marketuser":
                path = "users";
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Unknown entity class");
        }
        return path;
    }

    protected Optional<T> findByUUid(String uuid, R repository)
    {
        checkUuidValidity(uuid);
        //noinspection unchecked
        return (Optional<T>) invoke(repositoryMethod("findByUuid", String.class), repository, uuid);
    }

    @Override
    public Page<T> getAllEntities(PageRequest page, R repository)
    {
        //noinspection unchecked
        return (Page<T>) invoke(repositoryMethod("findAll", Pageable.class), repository, page);
    }

    @Override
    public T getEntity(String uuid, R repository)
    {
        return findByUUid( uuid, repository)
                .orElseThrow(() -> new CerebrumEntityNotFoundException(this.entityClass.getName(), uuid));
    }

    @Override
    public ResponseEntity<T> createEntity(T entity, R repository, UriComponentsBuilder uriComponentsBuilder)
    {
        UriComponents uriComponents = uriComponentsBuilder
                .path("/api/v0/" + getPath(entity) + "/{id}")
                .buildAndExpand(invoke(entityMethod("getUuid"), entity));
        URI location = uriComponents.toUri();

        //noinspection unchecked
        T createdEntity = (T) invoke(repositoryMethod("save", Object.class), repository, entity);

        return ResponseEntity.created(location).body(createdEntity);
    }

    @Override
    public ResponseEntity<T> updateEntity(String uuid, T submittedEntity,
                                          R repository, UriComponentsBuilder uriComponentsBuilder)
    {
        AtomicBoolean isCreated = new AtomicBoolean(false);
        T updatedEntity = findByUUid(uuid, repository).map(retrievedEntity -> {
            Field[] fields = this.entityClass.getDeclaredFields();

            for (Field field : fields) {
                if (field.getDeclaringClass() == this.entityClass
                        && Modifier.toString(field.getModifiers()).contains("public")) {
                    try {
                        field.set(retrievedEntity,
                                this.entityClass.getDeclaredField(field.getName()).get(submittedEntity));
                    } catch (IllegalAccessException e) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Due to lack of accessibility, this method cannot be invoke", e);
                    } catch (NoSuchFieldException e) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "the field, "+ field.getName() + "is unknown to the entity: "
                                        + entityClass.getSimpleName(), e);
                    }
                }
            }

            //noinspection unchecked
            return (T) invoke(repositoryMethod("save", Object.class), repository, retrievedEntity);
        }).orElseGet(() -> {
            isCreated.set(true);
            invoke(entityMethod("setUuid", String.class), submittedEntity, uuid);

            //noinspection unchecked
            return (T) invoke(repositoryMethod("save", Object.class), repository, submittedEntity);
        });

        if (isCreated.get()) {
            UriComponents uriComponents = uriComponentsBuilder
                    .path("/api/v0/" + getPath(submittedEntity) + "/{id}").buildAndExpand(uuid);
            URI location = uriComponents.toUri();

            return ResponseEntity.created(location).body(updatedEntity);
        }
        return ResponseEntity.ok().body(updatedEntity);
    }

    @Override
    public ResponseEntity<T> partiallyUpdateEntity(String uuid, R repository, JsonPatch patch)
    {
        T partiallyUpdatedEntity = findByUUid(uuid, repository)
                .map(retrievedEntity -> {
                    T patchedEntity = CerebrumControllerUtilities
                            .applyPatch(patch, retrievedEntity, this.entityClass);

                    //noinspection unchecked
                    return (T) invoke(repositoryMethod("save", Object.class), repository, patchedEntity);
                })
                .orElseThrow(()-> new CerebrumEntityNotFoundException("organization", uuid));
        return ResponseEntity.ok().body(partiallyUpdatedEntity);
    }

    @Override
    public ResponseEntity<T>  deleteEntity(String uuid, R repository)
    {
        checkUuidValidity(uuid);
        invoke(repositoryMethod("deleteByUuid", String.class), repository, uuid);
        return ResponseEntity.noContent().build();
    }
}
