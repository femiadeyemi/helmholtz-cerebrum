package de.helmholtz.marketplace.cerebrum.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.helmholtz.marketplace.cerebrum.entity.Organization;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(value = Lifecycle.PER_CLASS)
class OrganizationControllerTest
{
    private static final String API_URI_PREFIX = "/api/v0";
    private static final String ORG_API_URI = API_URI_PREFIX + "/organizations";
    @Value("${cerebrum.test.oauth2-token}") private String TOKEN;
    @Autowired private MockMvc mvc;
    @MockBean private OrganizationRepository mockRepository;
    @Autowired private ObjectMapper objectMapper;
    private final List<Organization> listOrganisation = new ArrayList<>();

    @BeforeAll
    public void setUp()
    {
        Organization awi = createNewOrganisation(
                "Alfred-Wegener-Institut, Helmholtz-Zentrum fuer Polar- und Meeresforschung",
                "AWI",
                "http://www.awi.de/",
                "https://www.awi.de/typo3conf/ext/sms_boilerplate/Resources/Public/Images/AWI/awi_logo.svg"
        );
        Organization desy = createNewOrganisation(
                "Deutsches Elektronen-Synchrotron",
                "DESY",
                "https://www.desy.de/",
                "https://www.desy.de/++resource++desy/images/desy_logo_3c_web.svg"
        );
        Organization dlr = createNewOrganisation(
                "Deutsches Zentrum fuer Luft- und Raumfahrt",
                "DLR",
                "https://www.dlr.de/",
                "https://www.dlr.de/at/en/Portaldata/20/Resources/images/artem/logos/Logo-DLR-500.png"
        );
        Organization dkfz = createNewOrganisation(
                "Deutsches Krebsforschungszentrum",
                "DKFZ",
                "https://www.dkfz.de/",
                "https://www.dkfz.de/global2/img/dkfz-logo.png"
        );
        Organization dzne = createNewOrganisation(
                "Deutsches Zentrum fuer Neurodegenerative Erkrankungen",
                "DZNE",
                "http://www.dzne.de/",
                "https://www.dzne.de/typo3temp/assets/_processed_/c/f/csm_logo_de_61754fc04c.png"
        );
        Organization fzj = createNewOrganisation(
                "Forschungszentrum Juelich",
                "FZJ",
                "https://www.fz-juelich.de/",
                "https://www.fz-juelich.de/SiteGlobals/StyleBundles/Bilder/NeuesLayout/logo.jpg;jsessionid=C00058A7F07D6BF4E7116B306214EE8E?__blob=normal"
        );
        Organization geomar = createNewOrganisation(
                "Helmholtz-Zentrum fuer Ozeanforschung",
                "GEOMAR",
                "https://www.geomar.de/",
                "https://www.geomar.de/typo3conf/ext/geomar_provider/Resources/Public/Images/Geomar-Logo.svg"
        );
        Organization gsi = createNewOrganisation(
                "Helmholtzzentrum fuer Schwerionenforschung",
                "GSI",
                "http://www.gsi.de/",
                "https://www.gsi.de/fileadmin/oeffentlichkeitsarbeit/logos/GSI_Logo.svg"
        );
        Organization hzb = createNewOrganisation(
                "Helmholtz-Zentrum Berlin fuer Materialien und Energie",
                "HZB",
                "http://www.helmholtz-berlin.de/",
                "https://www.helmholtz-berlin.de/media/design/logo/hzb-logo.svg"
        );
        Organization hzdr = createNewOrganisation(
                "Helmholtz-Zentrum Dresden-Rossendorf",
                "HZDR",
                "http://www.hzdr.de/",
                "https://www.hzdr.de/db/Pic?pOid=57529"
        );
        Organization hzi = createNewOrganisation(
                "Helmholtz-Zentrum fuer Infektionsforschung",
                "HZI",
                "http://www.helmholtz-hzi.de/",
                "https://www.helmholtz-hzi.de/typo3conf/ext/hzi_site/Resources/Public/Images/logo-hzi2-de.svg");
        Organization cispa = createNewOrganisation(
                "Helmholtz-Zentrum fuer Informationssicherheit",
                "CISPA",
                "https://cispa.saarland/de/",
                "https://cispa.saarland/img/CISPA_Logo_EN_RZ_RGB.svg");
        Organization ufz = createNewOrganisation(
                "Helmholtz-Zentrum fuer Umweltforschung",
                "UFZ",
                "http://www.ufz.de/",
                "https://www.ufz.de/static/custom/weblayout/DefaultInternetLayout/img/logos/ufz_transparent_de_blue.png");
        Organization hzg = createNewOrganisation(
                "Helmholtz-Zentrum Geesthacht Zentrum fuer Material- und Kuestenforschung",
                "HZG",
                "http://www.hzg.de/",
                "https://www.hzg.de/_common/img/logos/logo_hzg_cms10.gif");
        Organization hmgu = createNewOrganisation(
                "Helmholtz Zentrum Muenchen - Deutsches Forschungszentrum fuer Gesundheit und Umwelt",
                "HMGU",
                "http://www.helmholtz-muenchen.de/",
                "https://www.helmholtz-muenchen.de/fileadmin/0-templates/relaunch.helmholtz-muenchen.de/layout/head/logo.svg");
        Organization gfz = createNewOrganisation(
                "Helmholtz-Zentrum Potsdam - Deutsches GeoForschungsZentrum",
                "GFZ",
                "http://www.gfz-potsdam.de/",
                "https://www.gfz-potsdam.de/fileadmin/gfz/GFZ.svg");
        Organization kit = createNewOrganisation(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg");
        Organization mdc = createNewOrganisation(
                "Max-Delbrueck-Centrum fuer Molekulare Medizin in der Helmholtz-Gemeinschaft",
                "MDC",
                "http://www.mdc-berlin.de/",
                "https://www.mdc-berlin.de/system/files/styles/mdc_original_size_mdc_desktop_1x/private/2018-02/MDClogo-CMYK-blau-weiss-EN.jpg?itok=03Ri4a2n");
        Organization ipp = createNewOrganisation(
                "Max-Planck-Institut fuer Plasmaphysik",
                "IPP",
                "http://www.ipp.mpg.de/",
                "https://www.ipp.mpg.de/assets/institutes/headers/ipp-mobile-de-9dfcf4cec936dca0cbe073d6e628d12c900b7979d962928008a739ee9c68995c.svg");

        listOrganisation.add(awi);
        listOrganisation.add(desy);
        listOrganisation.add(dlr);
        listOrganisation.add(dkfz);
        listOrganisation.add(dzne);
        listOrganisation.add(fzj);
        listOrganisation.add(geomar);
        listOrganisation.add(gsi);
        listOrganisation.add(hzb);
        listOrganisation.add(hzdr);
        listOrganisation.add(hzi);
        listOrganisation.add(cispa);
        listOrganisation.add(ufz);
        listOrganisation.add(hzg);
        listOrganisation.add(hmgu);
        listOrganisation.add(gfz);
        listOrganisation.add(kit);
        listOrganisation.add(mdc);
        listOrganisation.add(ipp);
    }

    private Organization createNewOrganisation(String name, String abbreviation, String url, String img)
    {
        return createNewOrganisationWithUuiD(name, abbreviation, url, img, null);
    }

    private Organization createNewOrganisationWithUuiD(
            String name, String abbreviation, String url, String img, String uuid)
    {
        Organization org = new Organization();
        org.setName(name);
        org.setAbbreviation(abbreviation);
        org.setUrl(url);
        org.setImg(img);
        org.setUuid(uuid);
        return org;
    }

    // GETs
    @Test void
    whenGetRequestToOrganisations_thenOK() throws Exception
    {
        mvc.perform(get(ORG_API_URI))
                .andExpect(status().isOk());
    }

    @Test void
    givenValidAcceptHeader_whenGetRequestToOrganisations_verify_output_and_businessLogicCalls_thenOK()
            throws Exception
    {
        Page<Organization> page = new PageImpl<>(listOrganisation);
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));

        given(mockRepository.findAll(pageable)).willReturn(page);

        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI).accept("application/json"))
                .andReturn().getResponse();

        //then
        JsonNode actualResponseBody = objectMapper.readTree(response.getContentAsString());
        JsonNode actualListOrganisation = actualResponseBody.get("content");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("content-type")).isEqualTo("application/json");
        assertThat(actualListOrganisation.toString()).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(listOrganisation));
        verify(mockRepository, times(1)).findAll(pageable);
    }

    @Test void
    givenInvalidAcceptHeader_whenGetRequestToOrganisations_thenNotAcceptable() throws Exception
    {
        Page<Organization> page = new PageImpl<>(listOrganisation);
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));

        given(mockRepository.findAll(pageable)).willReturn(page);

        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI).accept("application/xml"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getHeader("content-type")).isNotEqualTo("application/json");
    }

    @Test void
    givenValidAcceptHeader_and_validUuid_whenGetRequestToOrganisation_verify_output_and_businessLogicCalls_thenOK()
            throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");
        given(mockRepository.findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(kit));

        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(kit));
        verify(mockRepository, times(1)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidAcceptHeader_and_invalidUuid_whenGetRequestToOrganisation_thenNotFound() throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");
        given(mockRepository.findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(kit));

        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130004")
                        .accept("application/json"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(mockRepository, times(1)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130004");
    }

    @Test void
    givenValidAcceptHeader_and_invalidUuid_whenGetRequestToOrganisation_thenBadRequest() throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");
        given(mockRepository.findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(kit));

        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI + "/2")
                        .accept("application/json"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(mockRepository, times(0)).findByUuid("2");
    }

    @Test void
    givenInvalidAcceptHeader_and_validUuid_whenGetRequestToOrganisation_thenNotAcceptable() throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");
        given(mockRepository.findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(kit));

        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .accept("application/xml"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test void
    givenValidAcceptHeader_and_validPageValue_whenGetRequestToOrganisations_thenOK() throws Exception
    {
        //given
        listOrganisation.sort(Comparator.comparing(Organization::getName));
        Pageable pageable = PageRequest.of(
                1,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));
        Page<Organization> page = new PageImpl<>(listOrganisation, pageable, 200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(get(ORG_API_URI +"?page=1")
                .accept("application/json"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(jsonPath("$['pageable']['pageNumber']").value(1));
    }

    @Test void
    givenInValidPageValue_whenGetRequestToOrganisations_thenBadRequest() throws Exception
    {
        Page<Organization> page = new PageImpl<>(listOrganisation);

        given(mockRepository
                .findAll())
                .willReturn(page);

        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI +"?page=-1"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test void
    givenValidSizeValue_whenGetRequestToOrganisations_thenOK() throws Exception
    {
        //given
        listOrganisation.sort(Comparator.comparing(Organization::getName));
        Pageable pageable = PageRequest.of(
                0,2, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));
        Page<Organization> page = new PageImpl<>(listOrganisation.subList(0, 2), pageable, 20L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(ORG_API_URI +"?size=2"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['pageSize']").value(2));
    }

    @Test void
    givenInValidSizeValue_whenGetRequestToOrganisations_thenBadRequest() throws Exception
    {
        MockHttpServletResponse response = mvc.perform(
                get(ORG_API_URI +"?size=0"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test void
    givenValidSortValue_whenGetRequestToOrganisations_thenOK() throws Exception
    {
        //given
        listOrganisation.sort(Comparator.comparing(
                Organization::getAbbreviation, Comparator.reverseOrder()));
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(Sort.Order.desc("abbreviation")));
        Page<Organization> page = new PageImpl<>(listOrganisation, pageable,200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(ORG_API_URI +"?sort=abbreviation.desc"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['sort']['sorted']").value("true"));
    }

    @Test void
    givenValidPageValueAndValidLimitValue_whenGetRequestToOrganisations_thenOK() throws Exception
    {
        //given
        Pageable pageable = PageRequest.of(
                2,4, Sort.by(Sort.Order.asc("name")));
        Page<Organization> page = new PageImpl<>(listOrganisation.subList(0, 4), pageable,200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(ORG_API_URI +"?page=2&size=4"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(jsonPath("$['pageable']['pageNumber']").value(2))
                .andExpect(jsonPath("$['pageable']['pageSize']").value(4));
    }

    // POST
    @Test void
    givenValidOrganisationWithoutUuid_whenPostRequestToOrganisations_verifyOutput_and_BusinessLogicCall_thenCreated()
            throws Exception
    {
        //given
        Organization kit = createNewOrganisation(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg");

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        //when
        mvc.perform(post(ORG_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(ORG_API_URI + "/" + kit.getUuid())))
                .andExpect(jsonPath("$['name']").value(kit.getName()))
                .andExpect(jsonPath("$['uuid']").value(kit.getUuid()));

        verify(mockRepository, times(1)).save(any(Organization.class));
    }

    @Test void
    givenValidOrganisationWithUuid_whenPostRequestToOrganisations_verifyOutput_and_BusinessLogicCall_thenCreated()
            throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-01eae16b-030a-1a14-883d-81d852138375");

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        mvc.perform(post(ORG_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(ORG_API_URI + "/org-01eae16b-030a-1a14-883d-81d852138375")))
                .andExpect(jsonPath("$['name']").value(kit.getName()))
                .andExpect(jsonPath("$['uuid']")
                        .value("org-01eae16b-030a-1a14-883d-81d852138375"));

        verify(mockRepository, times(1)).save(any(Organization.class));
    }

    @Test void
    givenInvalidAuthToken_whenPostRequestToOrganisations_thenUnauthorised() throws Exception
    {
        String fakeToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.ey" +
                "JzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiY" +
                "WRtaW4iOnRydWUsImp0aSI6IjQ0MzlkZWE4LWJkYWYtNDY0ZC1hY" +
                "mQ2LWY0Njk5NzRkNmQ5MCIsImlhdCI6MTU5Nzc0OTkwNCwiZXhwI" +
                "joxNTk3NzUzNTA0fQ.WML8ACxrPD3bVUTfMCw9V9GhzE03MG_Mv4h" +
                "GIU9QhkY";
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        mvc.perform(post(ORG_API_URI)
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(Organization.class));
    }

    @Test void
    givenNoAuthToken_whenPostRequestToOrganisations_thenForbidden() throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        mvc.perform(post(ORG_API_URI)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(Organization.class));
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validOrganisation_whenPostRequestToOrganisations_thenNotAcceptable() throws Exception
    {
        //given
        Organization kit = createNewOrganisation(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg");

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        //when
        mvc.perform(post(ORG_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(Organization.class));
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validOrganisation_whenPostRequestToOrganisations_thenUnsupportedMediaType()
            throws Exception
    {
        //given
        Organization kit = createNewOrganisation(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg");

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        //when
        mvc.perform(post(ORG_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(Organization.class));
    }

    @Test void
    givenInvalidOrganisation_whenPostRequestToOrganisations_thenBadRequest() throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        String kit2 = "{\"uuid\":\"org-5189a7bc-d630-11ea-87d0-0242ac130003\"," +
                "\"name\":\"Karlsruher Institut fuer Technologie\"," +
                "\"abbreviation\":\"KIT\"," +
                "\"img\":\"http://www.kit.edu/img/intern/kit_logo_V2_de.svg\"}";

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        mvc.perform(post(ORG_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(kit2))

                //then
                .andExpect(status().isBadRequest());

        verify(mockRepository, times(0)).save(any(Organization.class));
    }

    //PUT
    @Test void
    givenValidUuid_and_validOrganisation_whenPutRequestToOrganisations_verifyOutput_and_BusinessLogicCall_thenCreated()
            throws Exception
    {
        //given
        String kit= "{\"name\":\"Karlsruher Institut three fuer Technologie\"," +
                "\"img\":\"http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg\"," +
                "\"url\":\"http://www.kiiiit.edu/\"}";

        Organization newKit = createNewOrganisationWithUuiD(
                "Karlsruher Institut three fuer Technologie",
                "KIT",
                "http://www.kiiiit.edu/",
                "http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        given(mockRepository.save(any(Organization.class))).willReturn(newKit);

        //when
        mvc.perform(put(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(kit))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")))
                .andExpect(jsonPath("$['name']").value(newKit.getName()))
                .andExpect(jsonPath("$['uuid']").value("org-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(Organization.class));
        verify(mockRepository, times(1)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidUuid_and_validOrganisation_whenPutRequestToOrganisations_verifyOutput_and_BusinessLogicCall_thenUpdated()
            throws Exception
    {
        //given
        String kit= "{\"name\":\"Karlsruher Institut three fuer Technologie\"," +
                "\"img\":\"http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg\"," +
                "\"url\":\"http://www.kiiiit.edu/\"}";

        Organization newKit = createNewOrganisationWithUuiD(
                "Karlsruher Institut three fuer Technologie",
                "KIT",
                "http://www.kiiiit.edu/",
                "http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        given(mockRepository.findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(newKit));
        given(mockRepository.save(any(Organization.class))).willReturn(newKit);

        //when
        mvc.perform(put(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(kit))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['name']").value(newKit.getName()))
                .andExpect(jsonPath("$['uuid']").value("org-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(Organization.class));
        verify(mockRepository, times(1)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidUuid_and_validOrganisation_whenPutRequestToOrganisations_thenBadRequest() throws Exception
    {
        //given
        String kit= "{\"name\":\"Karlsruher Institut three fuer Technologie\"," +
                "\"img\":\"http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg\"," +
                "\"url\":\"http://www.kiiiit.edu/\"}";

        Organization newKit = createNewOrganisationWithUuiD(
                "Karlsruher Institut three fuer Technologie",
                "KIT",
                "http://www.kiiiit.edu/",
                "http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        given(mockRepository.save(any(Organization.class))).willReturn(newKit);

        //when
        mvc.perform(put(ORG_API_URI + "/abc")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(kit))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("abc");
    }

    @Test void
    givenInvalidAuthToken_whenPutRequestToOrganisations_thenUnauthorised() throws Exception
    {
        String fakeToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.ey" +
                "JzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiY" +
                "WRtaW4iOnRydWUsImp0aSI6IjQ0MzlkZWE4LWJkYWYtNDY0ZC1hY" +
                "mQ2LWY0Njk5NzRkNmQ5MCIsImlhdCI6MTU5Nzc0OTkwNCwiZXhwI" +
                "joxNTk3NzUzNTA0fQ.WML8ACxrPD3bVUTfMCw9V9GhzE03MG_Mv4h" +
                "GIU9QhkY";
        String kit= "{\"name\":\"Karlsruher Institut three fuer Technologie\"," +
                "\"img\":\"http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg\"," +
                "\"url\":\"http://www.kiiiit.edu/\"}";

        mvc.perform(put(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenNoAuthToken_whenPutRequestToOrganisations_thenForbidden() throws Exception
    {
        String kit= "{\"name\":\"Karlsruher Institut three fuer Technologie\"," +
                "\"img\":\"http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg\"," +
                "\"url\":\"http://www.kiiiit.edu/\"}";

        mvc.perform(put(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .accept("application/json")
                .contentType("application/json").content(kit))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validOrganisation_whenPutRequestToOrganisations_thenNotAcceptable() throws Exception
    {
        //given
        Organization kit = createNewOrganisation(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg");

        //when
        mvc.perform(put(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json").content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validOrganisation_whenPutRequestToOrganisations_thenUnsupportedMediaType()
            throws Exception
    {
        //given
        Organization kit = createNewOrganisation(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg");

        //when
        mvc.perform(put(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(kit)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidOrganisation_whenPutRequestToOrganisations_thenBadRequest() throws Exception
    {
        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        String kit2 = "{\"uuid\":\"org-5189a7bc-d630-11ea-87d0-0242ac130003\"," +
                "\"name\":\"Karlsruher Institut fuer Technologie\"," +
                "\"abbreviation\":\"KIT\"," +
                "\"img\":\"http://www.kit.edu/img/intern/kit_logo_V2_de.svg\"}";

        given(mockRepository.save(any(Organization.class))).willReturn(kit);

        mvc.perform(put(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(kit2))

                //then
                .andExpect(status().isBadRequest());

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    //PATCH
    @Test void
    givenValidUuid_and_validJsonPatch_whenPatchRequestToOrganisations_verifyOutput_and_BusinessLogicCall_thenCreated()
            throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");
        Object[] validJsonPatch = {patch};

        Organization kit = createNewOrganisationWithUuiD(
                "Karlsruher Institut fuer Technologie",
                "KIT",
                "http://www.kit.edu/",
                "http://www.kit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");

        Organization newKit = createNewOrganisationWithUuiD(
                "Karlsruher Institut three fuer Technologie",
                "KI3T",
                "http://www.kiiiit.edu/",
                "http://www.kiiiit.edu/img/intern/kit_logo_V2_de.svg",
                "org-5189a7bc-d630-11ea-87d0-0242ac130003");
        given(mockRepository.findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(kit));
        given(mockRepository.save(any(Organization.class))).willReturn(newKit);

        //when
        mvc.perform(patch(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['abbreviation']").value("KI3T"))
                .andExpect(jsonPath("$['uuid']").value("org-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(Organization.class));
        verify(mockRepository, times(1)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidUuid_and_invalidJsonPatch_whenPatchRequestToOrganisations_thenBadRequest() throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");

        //when
        mvc.perform(patch(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(patch)))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidUuid_and_validJsonPatch_whenPatchRequestToOrganisations_thenBadRequest() throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");
        Object[] validJsonPatch = {patch};

        //when
        mvc.perform(patch(ORG_API_URI + "/abc")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidAuthToken_whenPatchRequestToOrganisations_thenUnauthorised() throws Exception
    {
        String fakeToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.ey" +
                "JzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiY" +
                "WRtaW4iOnRydWUsImp0aSI6IjQ0MzlkZWE4LWJkYWYtNDY0ZC1hY" +
                "mQ2LWY0Njk5NzRkNmQ5MCIsImlhdCI6MTU5Nzc0OTkwNCwiZXhwI" +
                "joxNTk3NzUzNTA0fQ.WML8ACxrPD3bVUTfMCw9V9GhzE03MG_Mv4h" +
                "GIU9QhkY";
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");
        Object[] validJsonPatch = {patch};

        mvc.perform(patch(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenNoAuthToken_whenPatchRequestToOrganisations_thenForbidden() throws Exception
    {
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");
        Object[] validJsonPatch = {patch};

        mvc.perform(patch(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validJsonPatch_whenPatchRequestToOrganisations_thenNotAcceptable() throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");
        Object[] validJsonPatch = {patch};

        //when
        mvc.perform(patch(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validJsonPatch_whenPatchRequestToOrganisations_thenUnsupportedMediaType()
            throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");
        Object[] validJsonPatch = {patch};

        //when
        mvc.perform(patch(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(Organization.class));
        verify(mockRepository, times(0)).findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }
    
    // DELETE
    @Test void
    givenValidAuthToken_whenDeleteRequestToOrganisations_verify_output_and_businessLogicCalls_thenOK() throws Exception
    {
        MockHttpServletResponse response = mvc.perform(
                delete(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .header("Authorization", "Bearer " + TOKEN))

                //then
                .andExpect(status().isNoContent())
                .andReturn().getResponse();


        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("");
        verify(mockRepository, times(1)).deleteByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidAuthToken_whenDeleteRequestToOrganisations_thenUnauthorised() throws Exception
    {
        mvc.perform(
                delete(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .header("Authorization", "Bearer " + "Some-invalid-TOKEN"))

                //then
                .andExpect(status().isUnauthorized());
    }

    @Test void
    givenNoAuthToken_whenDeleteRequestToOrganisations_thenForbidden() throws Exception
    {
        mvc.perform(
                delete(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003"))

                //then
                .andExpect(status().isForbidden());
    }
}
