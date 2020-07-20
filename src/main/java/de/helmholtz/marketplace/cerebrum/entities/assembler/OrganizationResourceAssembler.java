package de.helmholtz.marketplace.cerebrum.entities.assembler;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;

import de.helmholtz.marketplace.cerebrum.controller.OrganizationController;
import de.helmholtz.marketplace.cerebrum.entities.Organization;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrganizationResourceAssembler
        implements RepresentationModelAssembler<Organization, EntityModel<Organization>>
{

    @Override
    public EntityModel<Organization> toModel(Organization organization)
    {
        return EntityModel.of(organization,
                linkTo(methodOn(OrganizationController.class)
                        .getOrganization(organization.getUuid()))
                        .withSelfRel()
                        .andAffordance(afford(methodOn(OrganizationController.class).updateOrganization(null, organization.getUuid())))
                        .andAffordance(afford(methodOn(OrganizationController.class).partialUpdateOrganisation(null, organization.getUuid())))
                        .andAffordance(afford(methodOn(OrganizationController.class).deleteOrganization(organization.getUuid()))),
                linkTo(methodOn(OrganizationController.class)
                        .getOrganizations(0, 20, new ArrayList<>(Collections.singleton("name.desc"))))
                        .withRel("organizations")
        );
        //.getOrganizations(PageRequest.of(0, 20, Sort.by("name"))))
    }
}
