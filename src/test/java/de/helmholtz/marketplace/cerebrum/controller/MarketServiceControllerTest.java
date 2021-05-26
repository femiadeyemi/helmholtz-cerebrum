package de.helmholtz.marketplace.cerebrum.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import de.helmholtz.marketplace.cerebrum.entity.MarketService;
import de.helmholtz.marketplace.cerebrum.repository.MarketServiceRepository;

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
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class MarketServiceControllerTest
{
    private static final String SVC_API_URI = "/api/v0/services";
    @Value("${cerebrum.test.oauth2-token}") private String TOKEN;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private MarketServiceRepository mockRepository;
    @Autowired private ObjectMapper objectMapper;
    private final List<MarketService> services = new ArrayList<>();
    private final MarketService singleService = createNewServiceWithUuiD(
            "svc-5189a7bc-d630-11ea-87d0-0242ac130003",
            "Treeflex",
            "Nullam varius. Nulla facilisi.",
            "https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx");
    private final static String fakeToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.ey" +
            "JzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiY" +
            "WRtaW4iOnRydWUsImp0aSI6IjQ0MzlkZWE4LWJkYWYtNDY0ZC1hY" +
            "mQ2LWY0Njk5NzRkNmQ5MCIsImlhdCI6MTU5Nzc0OTkwNCwiZXhwI" +
            "joxNTk3NzUzNTA0fQ.WML8ACxrPD3bVUTfMCw9V9GhzE03MG_Mv4h" +
            "GIU9QhkY";
    private Object[] validJsonPatch;

    @BeforeAll
    public void setUp()
    {
        String servicesString = "[{ \"name\": \"Treeflex\", \"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\" }, " +
                "{ \"name\": \"Trippledex\", \"description\": \"Vestibulum ante ipsum primis in " +
                "faucibus orci luctus et ultrices posuere cubilia Curae; Mauris viverra diam vitae quam.\", " +
                "\"url\": \"https://issuu.com/libero/rutrum/ac/lobortis/vel/dapibus.json\" }, { \"name\": \"Span\", " +
                "\"description\": \"Suspendisse accumsan tortor quis turpis. Sed ante.\", \"url\": " +
                "\"https://ucoz.ru/nisl/aenean.html\" }, { \"name\": \"Cardify\", \"description\": " +
                "\"Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\", \"url\": " +
                "\"http://mapy.cz/luctus/rutrum/nulla/tellus.xml\" }, { \"name\": \"Stringtough\", \"description\": " +
                "\"Proin eu mi. Nulla ac enim.\", \"url\": \"http://mediafire.com/accumsan/tellus/nisi/eu.js\" }, " +
                "{ \"name\": \"Job\", \"description\": \"Fusce consequat.\", \"url\": " +
                "\"http://amazon.de/aliquam/quis/turpis/eget/elit/sodales.js\" }, { \"name\": \"Keylex\", " +
                "\"description\": \"Quisque porta volutpat erat.\", \"url\": " +
                "\"https://sohu.com/fusce/consequat/nulla/nisl/nunc/nisl/duis.json\" }, { \"name\": \"Biodex\", " +
                "\"description\": \"Vivamus tortor.\", \"url\": " +
                "\"http://prnewswire.com/at/ipsum/ac/tellus/semper/interdum.png\" }, { \"name\": \"Aerified\", " +
                "\"description\": \"Donec ut mauris eget massa tempor convallis. Nulla neque libero, convallis eget, " +
                "eleifend luctus, ultricies eu, nibh.\", \"url\": \"https://discovery.com/volutpat.aspx\" }, " +
                "{ \"name\": \"Daltfresh\", \"description\": \"Proin leo odio, porttitor id, consequat in, consequat " +
                "ut, nulla.\", \"url\": \"http://utexas.edu/eu.aspx\" }, { \"name\": \"Alphazap\", " +
                "\"description\": \"Nulla nisl.\", \"url\": " +
                "\"http://flavors.me/vivamus/vestibulum/sagittis/sapien/cum/sociis.js\" }, { \"name\": \"Tresom\", " +
                "\"description\": \"Etiam justo.\", \"url\": \"https://so-net.ne.jp/blandit/non/interdum.jsp\" }, " +
                "{ \"name\": \"Voyatouch\", \"description\": \"Duis bibendum.\", \"url\": " +
                "\"https://list-manage.com/suspendisse/ornare.aspx\" }, { \"name\": \"Quo Lux\", \"description\": " +
                "\"Nulla ac enim. In tempor, turpis nec euismod scelerisque, quam turpis adipiscing lorem, vitae " +
                "mattis nibh ligula nec sem.\", \"url\": \"http://simplemachines.org/donec/dapibus/duis.html\" }, " +
                "{ \"name\": \"Andalax\", \"description\": \"Vestibulum sed magna at nunc commodo placerat. " +
                "Praesent blandit.\", \"url\": \"http://angelfire.com/donec.js\" }, { \"name\": \"Quo Lux\", " +
                "\"description\": \"Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia " +
                "Curae; Mauris viverra diam vitae quam.\", \"url\": " +
                "\"http://amazon.de/cubilia/curae/donec/pharetra/magna/vestibulum/aliquet.jpg\" }, { \"name\": " +
                "\"Sonsing\", \"description\": \"Etiam pretium iaculis justo.\", \"url\": " +
                "\"http://hp.com/vestibulum/proin/eu/mi/nulla/ac.json\" }, { \"name\": \"Toughjoyfax\", " +
                "\"description\": \"Donec vitae nisi. Nam ultrices, libero non mattis pulvinar, nulla pede " +
                "ullamcorper augue, a suscipit nulla elit ac nulla.\", \"url\": " +
                "\"http://chicagotribune.com/orci/pede/venenatis/non/sodales/sed.js\" }, { \"name\": \"Gembucket\", " +
                "\"description\": \"Suspendisse potenti. Nullam porttitor lacus at turpis.\", \"url\": " +
                "\"http://usda.gov/potenti/in/eleifend/quam/a/odio.jpg\" }, { \"name\": \"Daltfresh\", " +
                "\"description\": \"Ut at dolor quis odio consequat varius.\", \"url\": " +
                "\"http://alibaba.com/sapien/placerat/ante/nulla/justo/aliquam/quis.png\" }, { \"name\": \"Zaam-Dox\", " +
                "\"description\": \"Fusce consequat.\", \"url\": " +
                "\"http://slate.com/nisi/volutpat/eleifend/donec/ut/dolor/morbi.xml\" }, { \"name\": \"Y-Solowarm\", " +
                "\"description\": \"Duis mattis egestas metus.\", \"url\": \"http://dedecms.com/quisque.xml\" }, " +
                "{ \"name\": \"Tempsoft\", \"description\": \"Aenean lectus.\", \"url\": " +
                "\"http://com.com/ligula/sit/amet.aspx\" }, { \"name\": \"Flexidy\", \"description\": \"Morbi " +
                "vestibulum, velit id pretium iaculis, diam erat fermentum justo, nec condimentum neque sapien place" +
                "rat ante. Nulla justo.\", \"url\": \"http://msu.edu/hendrerit/at/vulputate/vitae/nisl/aenean.html\" }," +
                " { \"name\": \"Bitchip\", \"description\": \"In quis justo. Maecenas rhoncus aliquam lacus.\", " +
                "\"url\": \"https://shop-pro.jp/at/diam/nam.html\" }, { \"name\": \"Andalax\", \"description\": " +
                "\"Aenean auctor gravida sem. Praesent id massa id nisl venenatis lacinia.\", \"url\": " +
                "\"https://sourceforge.net/libero.xml\" }, { \"name\": \"Sonsing\", \"description\": \"Sed ante.\", " +
                "\"url\": \"https://digg.com/elit/proin/interdum/mauris/non.xml\" }, { \"name\": \"Konklux\", " +
                "\"description\": \"Sed sagittis. Nam congue, risus semper porta volutpat, quam pede lobortis ligula," +
                " sit amet eleifend pede libero quis orci.\", " +
                "\"url\": \"http://addthis.com/justo/sollicitudin/ut.png\" }, { \"name\": \"Ronstring\", " +
                "\"description\": \"Praesent blandit lacinia erat. Vestibulum sed magna at nunc commodo placerat.\", " +
                "\"url\": \"http://twitter.com/in/hac/habitasse/platea/dictumst/etiam.jsp\" }, { \"name\": \"Zathin\"," +
                " \"description\": \"Maecenas pulvinar lobortis est.\", \"url\": " +
                "\"http://canalblog.com/felis/ut/at/dolor/quis/odio.jsp\" }]";
        try {
            JSONArray jsonArr = new JSONArray(servicesString);
            for (int i=0; i < jsonArr.length(); i++) {
                JSONObject serviceJson = jsonArr.getJSONObject(i);

                services.add(createNewService(serviceJson.getString("name"),
                        serviceJson.getString("description"),
                        serviceJson.getString("url")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        services.sort(Comparator.comparing(MarketService::getName));

        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/name");
        patch.put("value", "Treeflex 2.0");
        validJsonPatch = new Object[]{patch};
    }

    private MarketService createNewService(String name, String description, String url)
    {
        return createNewServiceWithUuiD(null, name, description, url);
    }

    private MarketService createNewServiceWithUuiD(String uuid, String name, String description, String url)
    {
        MarketService src = new MarketService();
        src.setUuid(uuid);
        src.setName(name);
        src.setDescription(description);
        src.setEntryPoint(url);
        return src;
    }

    // GETs
    @Test void
    whenGetRequestToServices_thenOK() throws Exception
    {
        mvc.perform(get(SVC_API_URI))
                .andExpect(status().isOk());
    }

    @Test void
    givenValidAcceptHeader_whenGetRequestToServices_verify_output_and_businessLogicCalls_thenOK() throws Exception
    {
        Page<MarketService> page = new PageImpl<>(services);
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));

        given(mockRepository.findAll(pageable)).willReturn(page);

        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI).accept("application/json"))
                .andReturn().getResponse();

        //then
        JsonNode actualResponseBody = objectMapper.readTree(response.getContentAsString());
        JsonNode actualServiceList = actualResponseBody.get("content");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("content-type")).isEqualTo("application/json");
        assertThat(actualServiceList.toString()).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(services));
        verify(mockRepository, times(1)).findAll(pageable);
    }

    @Test void
    givenInvalidAcceptHeader_whenGetRequestToServices_thenNotAcceptable() throws Exception
    {
        Page<MarketService> page = new PageImpl<>(services);
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));

        given(mockRepository.findAll(pageable)).willReturn(page);

        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI).accept("application/xml"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getHeader("content-type")).isNotEqualTo("application/json");
    }

    @Test void
    givenValidAcceptHeader_and_validUuid_whenGetRequestToService_verify_output_and_businessLogicCalls_thenOK()
            throws Exception
    {
        given(mockRepository.findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleService));

        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(response.getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(singleService));
        verify(mockRepository, times(1)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidAcceptHeader_and_invalidUuid_whenGetRequestToService_thenNotFound() throws Exception
    {
        given(mockRepository.findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleService));

        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130004")
                        .accept("application/json"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(mockRepository, times(1)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130004");
    }

    @Test void
    givenValidAcceptHeader_and_invalidUuid_whenGetRequestToService_thenBadRequest() throws Exception
    {
        given(mockRepository.findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleService));

        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI + "/2")
                        .accept("application/json"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(mockRepository, times(0)).findByUuid("2");
    }

    @Test void
    givenInvalidAcceptHeader_and_validUuid_whenGetRequestToService_thenNotAcceptable() throws Exception
    {
        given(mockRepository.findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleService));

        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .accept("application/xml"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test void
    givenValidAcceptHeader_and_validPageValue_whenGetRequestToServices_thenOK() throws Exception
    {
        //given
        Pageable pageable = PageRequest.of(
                1,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));
        Page<MarketService> page = new PageImpl<>(services, pageable, 200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(get(SVC_API_URI +"?page=1")
                .accept("application/json"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(jsonPath("$['pageable']['pageNumber']").value(1));
    }

    @Test void
    givenInValidPageValue_whenGetRequestToServices_thenBadRequest() throws Exception
    {
        given(mockRepository
                .findAll())
                .willReturn(services);

        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI +"?page=-1"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test void
    givenValidSizeValue_whenGetRequestToServices_thenOK() throws Exception
    {
        //given
        Pageable pageable = PageRequest.of(
                0,2, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));
        Page<MarketService> page = new PageImpl<>(services.subList(0, 2), pageable, 20L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(SVC_API_URI +"?size=2"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['pageSize']").value(2));
    }

    @Test void
    givenInValidSizeValue_whenGetRequestToServices_thenBadRequest() throws Exception
    {
        MockHttpServletResponse response = mvc.perform(
                get(SVC_API_URI +"?size=0"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test void
    givenValidPageValueAndValidLimitValue_whenGetRequestToServices_thenOK() throws Exception
    {
        //given
        Pageable pageable = PageRequest.of(
                2,4, Sort.by(Sort.Order.asc("name")));
        Page<MarketService> page = new PageImpl<>(services.subList(0, 4), pageable,200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(SVC_API_URI +"?page=2&size=4"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(jsonPath("$['pageable']['pageNumber']").value(2))
                .andExpect(jsonPath("$['pageable']['pageSize']").value(4));
    }

    @Test void
    givenValidSortValue_whenGetRequestToServices_thenOK() throws Exception
    {
        //given
        services.sort(Comparator.comparing(
                MarketService::getEntryPoint, Comparator.reverseOrder()));
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(Sort.Order.desc("url")));
        Page<MarketService> page = new PageImpl<>(services, pageable,200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(SVC_API_URI +"?sort=url.desc"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['sort']['sorted']").value("true"));
    }

    //POST
    @Test void
    givenValidMarketServiceWithoutUuid_whenPostRequestToServices_verifyOutput_and_businessLogicCall_thenCreated()
            throws Exception
    {
        //given
        MarketService svc = createNewService(
                "Treeflex",
                "Nullam varius. Nulla facilisi.",
                "https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx");

        given(mockRepository.save(any(MarketService.class))).willReturn(svc);

        //when
        mvc.perform(post(SVC_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(svc)))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(SVC_API_URI + "/" + svc.getUuid())))
                .andExpect(jsonPath("$['name']").value(svc.getName()))
                .andExpect(jsonPath("$['uuid']").value(svc.getUuid()));

        verify(mockRepository, times(1)).save(any(MarketService.class));
    }

    @Test void
    givenValidMarketServiceWithUuid_whenPostRequestToServices_verifyOutput_and_businessLogicCall_thenCreated()
            throws Exception
    {
        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        mvc.perform(post(SVC_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(singleService)))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")))
                .andExpect(jsonPath("$['name']").value(singleService.getName()))
                .andExpect(jsonPath("$['uuid']")
                        .value("svc-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketService.class));
    }

    @Test void
    givenInvalidAuthToken_whenPostRequestToServices_thenUnauthorised() throws Exception
    {
        //given
        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        mvc.perform(post(SVC_API_URI)
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(singleService)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(MarketService.class));
    }

    @Test void
    givenNoAuthToken_whenPostRequestToServices_thenForbidden() throws Exception
    {
        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        mvc.perform(post(SVC_API_URI)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(singleService)))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(MarketService.class));
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validMarketService_whenPostRequestToServices_thenNotAcceptable() throws Exception
    {
        //given
        MarketService svc = createNewService(
                "Treeflex",
                "Nullam varius. Nulla facilisi.",
                "https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx");

        given(mockRepository.save(any(MarketService.class))).willReturn(svc);

        //when
        mvc.perform(post(SVC_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(svc)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(MarketService.class));
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validMarketService_whenPostRequestToServices_thenUnsupportedMediaType()
            throws Exception
    {
        //given
        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        //when
        mvc.perform(post(SVC_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(singleService)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(MarketService.class));
    }

    @Test void
    givenInvalidMarketService_whenPostRequestToServices_thenBadRequest() throws Exception
    {
        String invalidService = "{\"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\"}";

        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        mvc.perform(post(SVC_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(invalidService))

                //then
                .andExpect(status().isBadRequest());

        verify(mockRepository, times(0)).save(any(MarketService.class));
    }

    //PUT
    @Test void
    givenValidUuid_and_validMarketService_whenPutRequestToServices_verifyOutput_and_businessLogicCall_thenCreated()
            throws Exception
    {
        //given
        String validService = "{\"name\": \"Treeflex\", \"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\"}";

        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        //when
        mvc.perform(put(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(validService))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")))
                .andExpect(jsonPath("$['name']").value(singleService.getName()))
                .andExpect(jsonPath("$['uuid']").value("svc-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketService.class));
        verify(mockRepository, times(1)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidUuid_and_validMarketService_whenPutRequestToServices_verifyOutput_and_businessLogicCall_thenUpdated()
            throws Exception
    {
        //given
        String validService = "{\"name\": \"Treeflex\", \"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\"}";

        given(mockRepository.findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleService));
        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        //when
        mvc.perform(put(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(validService))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['name']").value(singleService.getName()))
                .andExpect(jsonPath("$['uuid']").value("svc-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketService.class));
        verify(mockRepository, times(1)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidUuid_and_validMMarketService_whenPutRequestToServices_thenBadRequest() throws Exception
    {
        //given
        String validService = "{\"name\": \"Treeflex\", \"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\"}";

        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        //when
        mvc.perform(put(SVC_API_URI + "/abc")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(validService))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("abc");
    }

    @Test void
    givenInvalidAuthToken_whenPutRequestToServices_thenUnauthorised() throws Exception
    {
        String validService = "{\"name\": \"Treeflex\", \"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\"}";

        mvc.perform(put(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(validService)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenNoAuthToken_whenPutRequestToServices_thenForbidden() throws Exception
    {
        String validService = "{\"name\": \"Treeflex\", \"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\"}";

        mvc.perform(put(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .accept("application/json")
                .contentType("application/json").content(validService))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validMarketService_whenPutRequestToServices_thenNotAcceptable() throws Exception
    {
        //when
        mvc.perform(put(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json").content(objectMapper.writeValueAsString(singleService)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validMarketService_whenPutRequestToServices_thenUnsupportedMediaType()
            throws Exception
    {
        //when
        mvc.perform(put(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(singleService)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidMarketService_whenPutRequestToServices_thenBadRequest() throws Exception
    {
        String invalidService = "{\"description\": \"Nullam varius. Nulla facilisi.\", " +
                "\"url\": \"https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx\"}";

        given(mockRepository.save(any(MarketService.class))).willReturn(singleService);

        mvc.perform(put(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(invalidService))

                //then
                .andExpect(status().isBadRequest());

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    //PATCH
    @Test void
    givenValidUuid_and_validJsonPatch_whenPatchRequestToServices_verifyOutput_and_businessLogicCall_thenCreated()
            throws Exception
    {
        //given
        MarketService patchedService = createNewServiceWithUuiD(
                "svc-5189a7bc-d630-11ea-87d0-0242ac130003",
                "Treeflex 2.0",
                "Nullam varius. Nulla facilisi.",
                "https://independent.co.uk/ipsum/dolor/sit/amet/consectetuer.aspx");

        given(mockRepository.findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleService));
        given(mockRepository.save(any(MarketService.class))).willReturn(patchedService);

        //when
        mvc.perform(patch(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['name']").value("Treeflex 2.0"))
                .andExpect(jsonPath("$['uuid']").value("svc-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketService.class));
        verify(mockRepository, times(1)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidUuid_and_invalidJsonPatch_whenPatchRequestToServices_thenBadRequest() throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("path", "/name");
        patch.put("value", "Phillip");

        //when
        mvc.perform(patch(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(patch)))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidUuid_and_validJsonPatch_whenPatchRequestToServices_thenBadRequest() throws Exception
    {
        //when
        mvc.perform(patch(SVC_API_URI + "/abc")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("abc");
    }

    @Test void
    givenInvalidAuthToken_whenPatchRequestToServices_thenUnauthorised() throws Exception
    {
        mvc.perform(patch(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenNoAuthToken_whenPatchRequestToServices_thenForbidden() throws Exception
    {
        mvc.perform(patch(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validJsonPatch_whenPatchRequestToServices_thenNotAcceptable() throws Exception
    {
        //when
        mvc.perform(patch(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validJsonPatch_whenPatchRequestToServices_thenUnsupportedMediaType()
            throws Exception
    {
        //when
        mvc.perform(patch(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(MarketService.class));
        verify(mockRepository, times(0)).findByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    // DELETE
    @Test void
    givenValidAuthToken_whenDeleteRequestToServices_verify_output_and_businessLogicCalls_thenOK() throws Exception
    {
        MockHttpServletResponse response = mvc.perform(
                delete(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .header("Authorization", "Bearer " + TOKEN))

                //then
                .andExpect(status().isNoContent())
                .andReturn().getResponse();


        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("");
        verify(mockRepository, times(1)).deleteByUuid("svc-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidAuthToken_whenDeleteRequestToServices_thenUnauthorised() throws Exception
    {
        mvc.perform(
                delete(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .header("Authorization", "Bearer " + fakeToken))

                //then
                .andExpect(status().isUnauthorized());
    }

    @Test void
    givenNoAuthToken_whenDeleteRequestToServices_thenForbidden() throws Exception
    {
        mvc.perform(
                delete(SVC_API_URI + "/svc-5189a7bc-d630-11ea-87d0-0242ac130003"))

                //then
                .andExpect(status().isForbidden());
    }
}
