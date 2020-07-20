package de.helmholtz.marketplace.cerebrum.controller;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.entities.assembler.OrganizationResourceAssembler;
import de.helmholtz.marketplace.cerebrum.exception.OrganizationNotFoundException;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = "application/prs.hal-forms+json",
        path = "${spring.data.rest.base-path}/organizations")
@Tag(name = "organizations", description = "The Organization API")
public class OrganizationController {
    private final OrganizationRepository organizationRepository;
    private final OrganizationResourceAssembler assembler;

    public OrganizationController(
            OrganizationRepository organizationRepository,
            OrganizationResourceAssembler assembler)
    {
        this.organizationRepository = organizationRepository;
        this.assembler = assembler;
    }

    @Operation(summary = "get array list of all organisations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = Organization.class)))),
            @ApiResponse(responseCode = "400", description = "invalid request")
    })
    @GetMapping(path = "")
    public CollectionModel<EntityModel<Organization>> getOrganizations(
            @Parameter(description = "specify the page number")
                @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "limit the number of records returned in one page")
                @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "")
                @RequestParam(value = "sort", defaultValue = "name.desc") List<String> sorts)
    {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sort: sorts) {
            if (sort.contains(".")) {
                String[] order = sort.split("\\.");
                orders.add(new Sort.Order(getSortDirection(order[1]), order[0]));
            } else {
                orders.add(new Sort.Order(getSortDirection("desc"), sort));
            }
        }

        List<EntityModel<Organization>> organizations =
                organizationRepository.findAll(PageRequest.of(page, size, Sort.by(orders)))
                        .stream()
                        .map(assembler::toModel)
                        .collect(Collectors.toList());

        return CollectionModel.of(
                organizations,
                linkTo(methodOn(OrganizationController.class).getOrganizations(page, size, sorts))
                        .withSelfRel());
    }

    @Operation(summary = "find organisation by ID",
            description = "Returns a detailed organization information " +
                    "corresponding to the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid organization ID supplied")
    })
    @GetMapping(path = "/{uuid}")
    public EntityModel<Organization> getOrganization(
            @Parameter(description = "ID of the organisation that needs to be fetched")
            @PathVariable(name = "uuid") String uuid)
    {
        Organization organization =
                organizationRepository.findByUuid(uuid)
                        .orElseThrow(OrganizationNotFoundException::new);

        return assembler.toModel(organization);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new organization",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "organization created",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertOrganization(
            @Parameter(description = "Organisation object that needs to be added to the marketplace",
                    required=true, schema=@Schema(implementation = Organization.class))
            @Valid @RequestBody Organization organization)
    {
        EntityModel<Organization> organizationModel =
                assembler.toModel(organizationRepository.save(organization));
        return ResponseEntity
                .created(organizationModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(organizationModel);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing organization",
            description = "Update part (or all) of an organisation information",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOrganization(
            @Parameter(
                    description="Organization to update or replace. This cannot be null or empty.",
                    schema=@Schema(implementation = Organization.class),
                    required=true) @Valid @RequestBody Organization newOrganization,
            @Parameter(description = "ID of the organisation that needs to be updated")
            @PathVariable(name = "uuid") String id)
    {
        Organization updatedOrganization = organizationRepository.findByUuid(id)
                .map(organisation -> {
                    organisation.setAbbreviation(newOrganization.getAbbreviation());
                    organisation.setName(newOrganization.getName());
                    organisation.setImg(newOrganization.getImg());
                    organisation.setUrl(newOrganization.getUrl());
                    return organizationRepository.save(organisation);
                })
                .orElseGet(() -> {
                    newOrganization.setUuid(id);
                    return organizationRepository.save(newOrganization);
                });

        EntityModel<Organization> entityModel = assembler.toModel(updatedOrganization);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "partially update an existing organisation",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "invalid id or json patch body"),
            @ApiResponse(responseCode = "404", description = "organisation not found")
    })
    @PatchMapping(path = "/{uuid}", consumes = "application/json-patch+json")
    public ResponseEntity<?> partialUpdateOrganisation(
            @Parameter(description="JSON Patch document structured as a JSON " +
                    "array of objects where each object contains one of the six " +
                    "JSON Patch operations: add, remove, replace, move, copy, and test",
                    schema=@Schema(implementation = JsonPatch.class),
                    required=true) @Valid @RequestBody JsonPatch patch,
            @Parameter(description = "ID of the organisation that needs to be partially updated")
            @PathVariable(name = "uuid") String id)
    {
        Organization partialUpdateOrganisation = organizationRepository.findByUuid(id)
                .map(organisation -> {
                    try {
                        Organization organizationPatched =
                                applyPatchToOrganization(patch, organisation);
                        return organizationRepository.save(organizationPatched);
                    } catch (JsonPatchException e) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "invalid id or json patch body", e);
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
                    }
                })
                .orElseThrow(OrganizationNotFoundException::new);

        EntityModel<Organization> entityModel = assembler.toModel(partialUpdateOrganisation);
        return ResponseEntity.ok().body(entityModel);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes an organisation",
            description = "Removes the record of the specified " +
                    "organisation id from the database. The organisation " +
                    "unique identification number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "invalid organisation id supplied")
    })
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<?> deleteOrganization(
            @Parameter(description="organisation id to delete", required=true)
            @PathVariable(name = "uuid") String id)
    {
        organizationRepository.deleteByUuid(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * util methods for this controller
     */

    private Organization applyPatchToOrganization(
            JsonPatch patch,
            Organization targetOrganization) throws JsonPatchException, JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetOrganization, JsonNode.class));
        return objectMapper.treeToValue(patched, Organization.class);
    }

    private Sort.Direction getSortDirection(String direction)
    {
        return direction.equals("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
    }
}
