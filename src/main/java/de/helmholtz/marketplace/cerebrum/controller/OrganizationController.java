package de.helmholtz.marketplace.cerebrum.controller;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE,
        path = "${spring.data.rest.base-path}/organizations")
@Tag(name = "organizations", description = "The Organization API")
public class OrganizationController {
    private final OrganizationRepository organizationRepository;

    public OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Operation(summary = "get array list of all organisations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = Organization.class)))
            ),
            @ApiResponse(responseCode = "400", description = "invalid request"),
            @ApiResponse(responseCode = "500", description = "server side error")
    })
    @GetMapping(path = "")
    public Iterable<Organization> getOrganizations(
            @Parameter(description = "specify the page number")
            @RequestParam(value = "page", defaultValue = "0") @Nullable Integer page,
            @Parameter(description = "limit the number of records returned in one page")
            @RequestParam(value = "size", defaultValue = "20") @Nullable Integer size) {
        if (page != null && size != null) {
            return organizationRepository.findAll(PageRequest.of(page, size));
        } else {
            return organizationRepository.findAll();
        }
    }

    @Operation(summary = "find organisation by id",
            description = "Returns a detailed organization information corresponding to the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Organization.class))
            ),
            @ApiResponse(responseCode = "400", description = "invalid request"),
            @ApiResponse(responseCode = "500", description = "server side error")
    })
    @GetMapping(path = "/{id}")
    public Optional<Organization> getOrganization(
            @Parameter(description = "organisation unique identification number")
            @PathVariable(required = true) Long id) {
        return organizationRepository.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new organization",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "organization created",
                    content = @Content(schema = @Schema(implementation = Organization.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Organisation already exists")
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Organization insertOrganization(
            @Parameter(description = "Organization to add. This cannot be null or empty.",
                    required=true, schema=@Schema(implementation = Organization.class))
            @Valid @RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing organization",
            description = "Update part (or all) of an organisation information",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Organization not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception") })
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Organization updateOrganization(
            @Parameter(description="Organization to update. This cannot be null or empty.",
            required=true, schema=@Schema(implementation = Organization.class))
                                               @Valid @RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes an organisation",
            description = "Removes the record of the specified " +
                    "organisation id from the database. The organisation " +
                    "unique identification number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404",
                    description = "Organisation with the specified ID not found"),
            @ApiResponse(responseCode = "500", description = "server side error")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteOrganization(
            @Parameter(description="organisation unique identification number", required=true)
            @PathVariable(name = "id", required = true) Long id) {
        organizationRepository.deleteById(id);
    }
}
