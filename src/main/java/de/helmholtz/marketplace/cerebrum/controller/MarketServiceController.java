package de.helmholtz.marketplace.cerebrum.controller;

import com.github.fge.jsonpatch.JsonPatch;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;
import de.helmholtz.marketplace.cerebrum.errorhandling.CerebrumApiError;
import de.helmholtz.marketplace.cerebrum.service.MarketServiceService;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumControllerUtilities;

@RestController
@Validated
@RequestMapping(path = "${spring.data.rest.base-path}/services", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "services", description = "The API of the Service")
public class MarketServiceController
{
    private final MarketServiceService marketServiceService;

    public MarketServiceController(MarketServiceService marketServiceService) {
        this.marketServiceService = marketServiceService;
    }

    /* get Services */
    @Operation(summary = "get array list of all services")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MarketService.class)))),
            @ApiResponse(responseCode = "400", description = "invalid request")
    })
    @GetMapping(path = "")
    public Iterable<MarketService> getMarketServices(
            @Parameter(description = "specify the page number")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @Parameter(description = "limit the number of records returned in one page")
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size,
            @Parameter(description = "sort the fetched data in either ascending (asc) " +
                    "or descending (desc) according to one or more of the service " +
                    "properties. Eg. to sort the list in ascending order base on the " +
                    "name property; the value will be set to name.asc")
            @RequestParam(value = "sort", defaultValue = "name.asc") List<String> sorts)
    {
        return marketServiceService.getServices(
                PageRequest.of(page, size, Sort.by(CerebrumControllerUtilities.getOrders(sorts))));
    }

    /* get single Service */
    @Operation(summary = "find a service by UUID",
            description = "Returns detailed service information corresponding to the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid service UUID supplied"),
            @ApiResponse(responseCode = "404", description = "service not found",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class)))
    })
    @GetMapping(path = "/{uuid}")
    public MarketService getMarketService(
            @Parameter(description = "UUID of the service that needs to be fetched")
            @PathVariable() String uuid)
    {
        return marketServiceService.getUser(uuid);
    }

    /* create Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new service", security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create operation was successful",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid UUID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content())
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarketService> createMarketService(
            @Parameter(description = "Service object that needs to be added to the marketplace",
                    required = true, schema = @Schema(implementation = MarketService.class))
            @Valid @RequestBody MarketService marketService, UriComponentsBuilder uriComponentsBuilder)
    {
        return marketServiceService.createService(marketService, uriComponentsBuilder);
    }

    /* update Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing service",
            description = "Update all attributes and relations of a service",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update operation was successful",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "201", description = "user created",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid UUID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content())
    })
    @PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarketService> updateMarketService(
            @Parameter(description = "Service to update or replace. This cannot be null or empty.",
                    required = true, schema = @Schema(implementation = MarketService.class))
            @Valid @RequestBody MarketService marketService,
            @Parameter(description = "UUID of the service that needs to be updated")
            @PathVariable() String uuid, UriComponentsBuilder uriComponentsBuilder)
    {
        return marketServiceService.updateService(uuid, marketService, uriComponentsBuilder);
    }

    /* JSON PATCH Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "partially update an existing service",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid id or json patch body"),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "service not found",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class)))
    })
    @PatchMapping(path = "/{uuid}", consumes = "application/json-patch+json")
    public ResponseEntity<MarketService> partialUpdateMarketService(
            @Parameter(description = "JSON Patch document structured as a JSON " +
                    "array of objects where each object contains one of the six " +
                    "JSON Patch operations: add, remove, replace, move, copy, and test",
                    schema = @Schema(implementation = JsonPatch.class),
                    required = true) @Valid @RequestBody JsonPatch patch,
            @Parameter(description = "ID of the service that needs to be partially updated")
            @PathVariable() String uuid)
    {
        return marketServiceService.partiallyUpdateService(uuid, patch);
    }

    /* delete Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes a service",
            description = "Removes the record of the specified service id " +
                    "from the database. The service unique identification " +
                    "number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation"),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content()),
    })
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<MarketService> deleteMarketService(@PathVariable("uuid") String uuid)
    {
        return marketServiceService.deleteService(uuid);
    }
}
