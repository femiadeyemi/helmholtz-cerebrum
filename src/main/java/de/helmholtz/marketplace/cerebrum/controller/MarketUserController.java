package de.helmholtz.marketplace.cerebrum.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;
import de.helmholtz.marketplace.cerebrum.errorhandling.CerebrumApiError;
import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumEntityNotFoundException;
import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumInvalidUuidException;
import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumControllerUtilities;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE,
        path = "${spring.data.rest.base-path}/users")
@Tag(name = "users", description = "The User API")
public class MarketUserController {
    private final WebClient authorisationServer;
    private final MarketUserRepository marketUserRepository;

    @Autowired
    public MarketUserController(WebClient authorisationServer, MarketUserRepository marketUserRepository) {
        this.authorisationServer = authorisationServer;
        this.marketUserRepository = marketUserRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/whoami", produces = {"application/json"})
    @Operation(
            summary = "display user information",
            description = "This display an authenticated End-User user details " +
                    "in JSON format. The result shown is by querying the UserInfo " +
                    "endpoint of HDF AAI.",
            security = @SecurityRequirement(name = "hdf-aai"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MarketUser.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorised",
                            content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content()),
                    @ApiResponse(responseCode = "500", description = "Server Error",
                            content = @Content(schema = @Schema(
                                    implementation = CerebrumApiError.class)))
            }
    )
    public JsonNode whoami() {
        return this.authorisationServer
                .get()
                .uri("https://login.helmholtz.de/oauth2/userinfo")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /* get users */
    @Operation(summary = "get array list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MarketUser.class)))),
            @ApiResponse(responseCode = "400", description = "invalid request",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CerebrumApiError.class))))
    })
    @GetMapping(path = "")
    public Iterable<MarketUser> getMarketUsers(
            @Parameter(description = "specify the page number")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @Parameter(description = "limit the number of records returned in one page")
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size,
            @Parameter(description = "sort the fetched data in either ascending (asc) " +
                    "or descending (desc) according to one or more of the user " +
                    "properties. Eg. to sort the list in ascending order base on the " +
                    "firstName property; the value will be set to firstName.asc")
            @RequestParam(value = "sort", defaultValue = "firstName.asc") List<String> sorts)
    {
        return marketUserRepository.findAll(
                PageRequest.of(page, size, Sort.by(CerebrumControllerUtilities.getOrders(sorts))));
    }

    /* get user */
    @Operation(summary = "find user by UUID", description = "Returns a detailed user information corresponding to the UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MarketUser.class))),
            @ApiResponse(responseCode = "400", description = "invalid user UUID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "404", description = "user not found",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class)))
    })
    @GetMapping(path = "/{uuid}")
    public MarketUser getMarketUser(
            @Parameter(description = "UUID of the user that needs to be fetched")
            @PathVariable() String uuid)
    {
        if (Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid))) {
            return marketUserRepository.findByUuid(uuid)
                    .orElseThrow(() -> new CerebrumEntityNotFoundException("user", uuid));
        }
        else throw new CerebrumInvalidUuidException(uuid);
    }

    /* create user */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new user",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "user created",
                    content = @Content(schema = @Schema(implementation = MarketUser.class))),
            @ApiResponse(responseCode = "400", description = "invalid UUID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content())
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarketUser> createMarketUser(
            @Parameter(description = "user object that needs to be added to the marketplace",
                    required = true, schema = @Schema(implementation = MarketUser.class))
            @Valid @RequestBody MarketUser marketUser, UriComponentsBuilder uriComponentsBuilder)
    {
        MarketUser newUser = marketUserRepository.save(marketUser);
        UriComponents uriComponents =
                uriComponentsBuilder.path("/api/v0/users/{id}").buildAndExpand(newUser.getUuid());
        URI location = uriComponents.toUri();

        return ResponseEntity.created(location).body(newUser);
    }

    /* update user */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing user",
            description = "Update part (or all) of a user information",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MarketUser.class))),
            @ApiResponse(responseCode = "201", description = "user created",
                    content = @Content(schema = @Schema(implementation = MarketUser.class))),
            @ApiResponse(responseCode = "400", description = "invalid UUID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content())
    })
    @PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MarketUser> updateMarketUser(
            @Parameter(
                    description = "User to update or replace. This cannot be null or empty.",
                    schema = @Schema(implementation = MarketUser.class),
                    required = true) @Valid @RequestBody MarketUser newMarketUser,
            @Parameter(description = "UUID of the user that needs to be updated")
            @PathVariable() String uuid, UriComponentsBuilder uriComponentsBuilder)
    {
        if (Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid))) {
            AtomicBoolean isCreated = new AtomicBoolean(false);
            MarketUser org = marketUserRepository.findByUuid(uuid)
                    .map(marketUser -> {
                        marketUser.setEmail(newMarketUser.getEmail());
                        marketUser.setFirstName(newMarketUser.getFirstName());
                        marketUser.setLastName(newMarketUser.getLastName());
                        marketUser.setScreenName(newMarketUser.getScreenName());
                        marketUser.setSub(newMarketUser.getSub());
                        return marketUserRepository.save(marketUser);
                    })
                    .orElseGet(() -> {
                        newMarketUser.setUuid(uuid);
                        isCreated.set(true);
                        return marketUserRepository.save(newMarketUser);
                    });

            if (isCreated.get()) {
                UriComponents uriComponents =
                        uriComponentsBuilder.path("/api/v0/users/{id}").buildAndExpand(org.getUuid());
                URI location = uriComponents.toUri();
                return ResponseEntity.created(location).body(org);
            }
            return ResponseEntity.ok().body(org);
        }
        else throw new CerebrumInvalidUuidException(uuid);
    }

    /* JSON PATCH user */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "partially update an existing user",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MarketUser.class))),
            @ApiResponse(responseCode = "400", description = "invalid UUID or json patch body",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "user not found",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class)))
    })
    @PatchMapping(path = "/{uuid}", consumes = "application/json-patch+json")
    public ResponseEntity<MarketUser> partialUpdateMarketUser(
            @Parameter(description = "JSON Patch document structured as a JSON " +
                    "array of objects where each object contains one of the six " +
                    "JSON Patch operations: add, remove, replace, move, copy, and test",
                    schema = @Schema(implementation = JsonPatch.class),
                    required = true) @Valid @RequestBody JsonPatch patch,
            @Parameter(description = "UUID of the user that needs to be partially updated")
            @PathVariable() String uuid)
    {
        if (Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid))) {
            MarketUser partiallyUpdatedUser = marketUserRepository.findByUuid(uuid)
                    .map(user -> {
                        MarketUser marketUserPatched =
                                CerebrumControllerUtilities.applyPatch(patch, user, MarketUser.class);
                        return marketUserRepository.save(marketUserPatched);
                    })
                    .orElseThrow(() -> new CerebrumEntityNotFoundException("user", uuid));
            return ResponseEntity.ok().body(partiallyUpdatedUser);
        }
        else throw new CerebrumInvalidUuidException(uuid);
    }

    /* delete user */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes a user",
            description = "Removes the record of the specified " +
                    "user UUID from the database. The user " +
                    "unique identification number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation", content = @Content()),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "403", description = "forbidden", content = @Content()),
    })
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<MarketUser> deleteMarketUser(
            @Parameter(description = "user UUID to delete", required = true)
            @PathVariable(name = "uuid") String uuid)
    {
        marketUserRepository.deleteByUuid(uuid);
        return ResponseEntity.noContent().build();
    }
}