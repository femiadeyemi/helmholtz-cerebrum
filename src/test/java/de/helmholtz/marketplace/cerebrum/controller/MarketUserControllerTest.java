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

import de.helmholtz.marketplace.cerebrum.entity.MarketUser;
import de.helmholtz.marketplace.cerebrum.entity.Person;
import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;

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
class MarketUserControllerTest
{
    private static final String API_URI_PREFIX = "/api/v0";
    private static final String USR_API_URI = API_URI_PREFIX + "/users";
    @Value("${cerebrum.test.oauth2-token}") private String TOKEN;
    @Autowired private MockMvc mvc;
    @MockBean private MarketUserRepository mockRepository;
    @Autowired private ObjectMapper objectMapper;
    private MarketUser singleUser;
    private String fakeToken;
    private final List<MarketUser> listUsers = new ArrayList<>();

    @BeforeAll
    public void setUp()
    {
        fakeToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.ey" +
                "JzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiY" +
                "WRtaW4iOnRydWUsImp0aSI6IjQ0MzlkZWE4LWJkYWYtNDY0ZC1hY" +
                "mQ2LWY0Njk5NzRkNmQ5MCIsImlhdCI6MTU5Nzc0OTkwNCwiZXhwI" +
                "joxNTk3NzUzNTA0fQ.WML8ACxrPD3bVUTfMCw9V9GhzE03MG_Mv4h" +
                "GIU9QhkY";

        singleUser = createNewUserWithUuiD(
                "glages0@tmall.com",
                "Garreth",
                "Lages",
                "glages0",
                "QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN",
                "usr-5189a7bc-d630-11ea-87d0-0242ac130003"
        );
        String users = "[{ \"email\": \"glages0@tmall.com\", \"firstName\": \"Garreth\", \"lastName\": \"Lages\", " +
                "\"screenName\": \"glages0\", \"sub\": \"QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN\" }, { \"email\": " +
                "\"acossor1@oakley.com\", \"firstName\": \"Andriana\", \"lastName\": \"Cossor\", \"screenName\": " +
                "\"acossor1\", \"sub\": \"aaxHMjrb-oBfu-R0Kc-fNw3-MslR4cPq8be7\" }, { \"email\": " +
                "\"rleslie2@parallels.com\", \"firstName\": \"Roth\", \"lastName\": \"Leslie\", \"screenName\": " +
                "\"rleslie2\", \"sub\": \"qxr3APR8-JW3B-s0T1-dGEa-41QEF4r6G9Ze\" }, { \"email\": " +
                "\"nhultberg3@shutterfly.com\", \"firstName\": \"Noel\", \"lastName\": \"Hultberg\", \"screenName\": " +
                "\"nhultberg3\", \"sub\": \"9h894qHR-OO7i-CI0K-qP0e-4vwx9zpPoSKg\" }, { \"email\": " +
                "\"hbrunone4@opera.com\", \"firstName\": \"Harmonia\", \"lastName\": \"Brunone\", \"screenName\": " +
                "\"hbrunone4\", \"sub\": \"s5Vs23Br-eoaK-CzJC-WWKv-X4jJN6SMyAZ9\" }, { \"email\": " +
                "\"rgaize5@amazon.com\", \"firstName\": \"Rickert\", \"lastName\": \"Gaize\", \"screenName\": " +
                "\"rgaize5\", \"sub\": \"hJ1Z8sx5-yG1O-xQSi-qzMP-TPfgF5MIOmM8\" }, { \"email\": \"jbirts6@baidu.com\", " +
                "\"firstName\": \"Jermaine\", \"lastName\": \"Birts\", \"screenName\": \"jbirts6\", \"sub\": " +
                "\"V7IVPtBb-oLgq-CXE0-cuit-dJDFGw6xb4Kf\" }, { \"email\": \"leagle7@economist.com\", \"firstName\": " +
                "\"Lucienne\", \"lastName\": \"Eagle\", \"screenName\": \"leagle7\", \"sub\": " +
                "\"ZbPF199v-rsMT-TFFp-IGwg-s3XOl7YVTBOC\" }, { \"email\": \"qvoase8@reference.com\", \"firstName\": " +
                "\"Quentin\", \"lastName\": \"Voase\", \"screenName\": \"qvoase8\", \"sub\": " +
                "\"lHkvqFJW-QnLH-eeee-0CQQ-C5vl6CZfpdnI\" }, { \"email\": \"lmashal9@dion.ne.jp\", \"firstName\": " +
                "\"Leland\", \"lastName\": \"Mashal\", \"screenName\": \"lmashal9\", \"sub\": " +
                "\"XBwll0Xq-JhmZ-tX3E-YMRU-tNwM2cdDUTzV\" }, { \"email\": \"kraubenheima@t-online.de\", \"firstName\": " +
                "\"Kamilah\", \"lastName\": \"Raubenheim\", \"screenName\": \"kraubenheima\", \"sub\": " +
                "\"kFqNDCdK-dcSR-N2GS-sRtb-0FTj7J0yVOSu\" }, { \"email\": \"vfloyedb@mozilla.org\", \"firstName\": " +
                "\"Veronica\", \"lastName\": \"Floyed\", \"screenName\": \"vfloyedb\", \"sub\": " +
                "\"7MrCbg1N-LZZE-kGCO-TH08-8kE1zulzAIrv\" }, { \"email\": \"zesselinc@list-manage.com\", \"firstName\": " +
                "\"Zelma\", \"lastName\": \"Esselin\", \"screenName\": \"zesselinc\", \"sub\": " +
                "\"qAT5TqDR-I9ed-FzRr-AeIt-6YrLFRdpdt0p\" }, { \"email\": \"vhartnupd@theguardian.com\", \"firstName\": " +
                "\"Valencia\", \"lastName\": \"Hartnup\", \"screenName\": \"vhartnupd\", \"sub\": " +
                "\"fihwM6hU-jCwZ-8Mcg-Rhru-smzkm4Cw8uLK\" }, { \"email\": \"clemarchande@amazon.com\", \"firstName\": " +
                "\"Carleen\", \"lastName\": \"Le Marchand\", \"screenName\": \"clemarchande\", \"sub\": " +
                "\"zz1S5ugQ-7LYn-GXys-S9q1-Z0hJxuiRW7EG\" }, { \"email\": \"mlotheanf@hp.com\", \"firstName\": " +
                "\"Marten\", \"lastName\": \"Lothean\", \"screenName\": \"mlotheanf\", \"sub\": " +
                "\"HnelzYIw-drhs-hpA4-uESa-McZzjL0eVT0f\" }, { \"email\": \"rsimicg@weather.com\", \"firstName\": " +
                "\"Ring\", \"lastName\": \"Simic\", \"screenName\": \"rsimicg\", \"sub\": \"ToEIKvgm-jndW-MAXa-yyFl-XydBLdb0HJ74\" }, { \"email\": \"jcampsallh@sfgate.com\", \"firstName\": \"Joellyn\", \"lastName\": \"Campsall\", \"screenName\": \"jcampsallh\", \"sub\": \"3OJmHRCI-fJNV-qhn3-Tucg-dPoAqtZz35SJ\" }, { \"email\": \"lkunnekei@mediafire.com\", \"firstName\": \"Leodora\", \"lastName\": \"Kunneke\", \"screenName\": \"lkunnekei\", \"sub\": \"Ks4CkQSo-hHwE-ECiw-CxCO-L0nkdgGvT0DK\" }, { \"email\": \"estranierij@myspace.com\", \"firstName\": \"Eziechiele\", \"lastName\": \"Stranieri\", \"screenName\": \"estranierij\", \"sub\": \"mi9w7yKl-Jm9Q-ZARl-DNg8-rtNdyyug6Mz2\" }, { \"email\": \"bmasedonk@tinyurl.com\", \"firstName\": \"Bryanty\", \"lastName\": \"Masedon\", \"screenName\": \"bmasedonk\", \"sub\": \"KHwkqqrO-eURQ-M1hc-4I6H-ZRHshiM3gQYI\" }, { \"email\": \"bhalstonl@seesaa.net\", \"firstName\": \"Bert\", \"lastName\": \"Halston\", \"screenName\": \"bhalstonl\", \"sub\": \"vPWS142l-yti5-IpXo-rVOR-haeg8XsJoZ1Z\" }, { \"email\": \"ecolletm@pcworld.com\", \"firstName\": \"Ebeneser\", \"lastName\": \"Collet\", \"screenName\": \"ecolletm\", \"sub\": \"Rjax7gp4-EgZi-HROx-scDh-fDfA6M3HSSG3\" }, { \"email\": \"mshaefern@4shared.com\", \"firstName\": \"Max\", \"lastName\": \"Shaefer\", \"screenName\": \"mshaefern\", \"sub\": \"r9bbqUjf-Uwdo-AKWX-hCok-J1zsdmTNODcq\" }, { \"email\": \"asegeswoetho@upenn.edu\", \"firstName\": \"Annecorinne\", \"lastName\": \"Segeswoeth\", \"screenName\": \"asegeswoetho\", \"sub\": \"v4C9XkoS-Z2cN-Ob9v-o0p7-todJ495caNdL\" }, { \"email\": \"jfissendenp@techcrunch.com\", \"firstName\": \"Jo\", \"lastName\": \"Fissenden\", \"screenName\": \"jfissendenp\", \"sub\": \"3xm2dkAD-Cup9-j5Uk-R2kp-tYEbph41WDwE\" }, { \"email\": \"eglynq@cmu.edu\", \"firstName\": \"Ethan\", \"lastName\": \"Glyn\", \"screenName\": \"eglynq\", \"sub\": \"XugA4670-3BTM-2HwB-TIob-rKb5549SyoNS\" }, { \"email\": \"fpoletr@goo.ne.jp\", \"firstName\": \"Felicity\", \"lastName\": \"Polet\", \"screenName\": \"fpoletr\", \"sub\": \"K20j0UAm-ESE2-PkpX-JFI6-SZMWL1l1UHvs\" }, { \"email\": \"sgirardets@nature.com\", \"firstName\": \"Sherrie\", \"lastName\": \"Girardet\", \"screenName\": \"sgirardets\", \"sub\": \"9UAdNPfJ-vAZw-1SkE-b0Ec-dpkatck9ppx0\" }, { \"email\": \"zlordent@purevolume.com\", \"firstName\": \"Zachary\", \"lastName\": \"Lorden\", \"screenName\": \"zlordent\", \"sub\": \"a0FmNRoH-Lzr7-pzXh-FuHk-oojqYcrntLWU\" }, { \"email\": \"glacasau@over-blog.com\", \"firstName\": \"Gwyneth\", \"lastName\": \"Lacasa\", \"screenName\": \"glacasau\", \"sub\": \"DZSlaGNK-Rxf5-5VHA-G0wD-7dM85Gks1JRe\" }, { \"email\": \"mconrathv@youtube.com\", \"firstName\": \"Mariam\", \"lastName\": \"Conrath\", \"screenName\": \"mconrathv\", \"sub\": \"0BIr3u0g-NWQI-hGMF-CgT0-9XkdyXKghBh8\" }, { \"email\": \"mnorresw@storify.com\", \"firstName\": \"Magnum\", \"lastName\": \"Norres\", \"screenName\": \"mnorresw\", \"sub\": \"F1I9GiKK-FB6S-dp2Q-E0Sx-CbrkRphRQKS9\" }, { \"email\": \"khartnellx@51.la\", \"firstName\": \"Karyl\", \"lastName\": \"Hartnell\", \"screenName\": \"khartnellx\", \"sub\": \"2seA6Zmj-io1n-pqdy-Qpg5-sSuZvuy88sTi\" }, { \"email\": \"dlabbey@multiply.com\", \"firstName\": \"Dorothee\", \"lastName\": \"Labbe\", \"screenName\": \"dlabbey\", \"sub\": \"3NthJ788-ywJh-dmoG-EI5W-SZgrEDsDU37K\" }, { \"email\": \"rpescottz@artisteer.com\", \"firstName\": \"Rahal\", \"lastName\": \"Pescott\", \"screenName\": \"rpescottz\", \"sub\": \"25h9Hdb6-lA4k-ZmQV-ePbM-kWGJe7JMwPSL\" }, { \"email\": \"mcolter10@slate.com\", \"firstName\": \"Marius\", \"lastName\": \"Colter\", \"screenName\": \"mcolter10\", \"sub\": \"1cl6LIGv-2nYC-91tg-rm33-QfR2B9w6xXMc\" }, { \"email\": \"wshaw11@who.int\", \"firstName\": \"Wright\", \"lastName\": \"Shaw\", \"screenName\": \"wshaw11\", \"sub\": \"pNmA02dR-pIgE-6U1U-z9iM-jWEz3dijzfOi\" }, { \"email\": \"jdoyley12@moonfruit.com\", \"firstName\": \"Janenna\", \"lastName\": \"Doyley\", \"screenName\": \"jdoyley12\", \"sub\": \"xTRpwe7n-18aa-UgYU-QXaf-lyYqVkmXIPCj\" }, { \"email\": \"rhuby13@hostgator.com\", \"firstName\": \"Riannon\", \"lastName\": \"Huby\", \"screenName\": \"rhuby13\", \"sub\": \"nHNb0WPS-13EK-reAF-xWx5-M4k16XN6o5TJ\" }]";
        try {
            JSONArray jsonArr = new JSONArray(users);
            for (int i=0; i < jsonArr.length(); i++) {
                JSONObject usrJson = jsonArr.getJSONObject(i);

                listUsers.add(createNewUser(usrJson.getString("email"),
                        usrJson.getString("firstName"),
                        usrJson.getString("lastName"),
                        usrJson.getString("screenName"),
                        usrJson.getString("sub")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listUsers.sort(Comparator.comparing(MarketUser::getScreenName));
    }

    private MarketUser createNewUser(
            String email, String first, String last, String screenName, String sub)
    {
        return createNewUserWithUuiD(email, first, last, screenName, sub, null);
    }

    private MarketUser createNewUserWithUuiD(
            String email, String first, String last, String screenName, String sub, String uuid)
    {
        Person p = new Person();
        p.addEmail(email);
        p.setFirstName(first);
        p.setLastName(last);

        MarketUser usr = new MarketUser();
        usr.setUuid(uuid);
        usr.setScreenName(screenName);
        usr.setSub(sub);
        usr.setProfile(p);

        return usr;
    }

    // GETs
    @Test void
    givenValidAuthToken_whenGetRequestToUsers_whoami_thenOK() throws Exception
    {
        mvc.perform(get(USR_API_URI + "/whoami")
                .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk());
    }

    @Test void
    givenInvalidAuthToken_whenGetRequestToUsers_whoami_thenUnauthorized() throws Exception
    {
        mvc.perform(get(USR_API_URI + "/whoami")
                .header("Authorization", "Bearer " + fakeToken))
                .andExpect(status().isUnauthorized());
    }

    @Test void
    givenNoAuthToken_whenGetRequestToUsers_whoami_thenForbidden() throws Exception
    {
        mvc.perform(get(USR_API_URI + "/whoami"))
                .andExpect(status().isForbidden());
    }

    @Test void
    whenGetRequestToUsers_thenOK() throws Exception
    {
        mvc.perform(get(USR_API_URI))
                .andExpect(status().isOk());
    }

    @Test void
    givenValidAcceptHeader_whenGetRequestToUsers_verify_output_and_businessLogicCalls_thenOK() throws Exception
    {
        Page<MarketUser> page = new PageImpl<>(listUsers);
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")));

        given(mockRepository.findAll(pageable)).willReturn(page);

        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI).accept("application/json"))
                .andReturn().getResponse();

        //then
        JsonNode actualResponseBody = objectMapper.readTree(response.getContentAsString());
        JsonNode actualUserList = actualResponseBody.get("content");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("content-type")).isEqualTo("application/json");
        assertThat(actualUserList.toString()).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(listUsers));
        verify(mockRepository, times(1)).findAll(pageable);
    }

    @Test void
    givenInvalidAcceptHeader_whenGetRequestToUsers_thenNotAcceptable() throws Exception
    {
        Page<MarketUser> page = new PageImpl<>(listUsers);
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")));

        given(mockRepository.findAll(pageable)).willReturn(page);

        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI).accept("application/xml"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getHeader("content-type")).isNotEqualTo("application/json");
    }

    @Test void
    givenValidAcceptHeader_and_validUuid_whenGetRequestToUser_verify_output_and_businessLogicCalls_thenOK()
            throws Exception
    {
        given(mockRepository.findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleUser));

        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("Content-Type")).isEqualTo("application/json");
        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(singleUser));
        verify(mockRepository, times(1)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidAcceptHeader_and_invalidUuid_whenGetRequestToUser_thenNotFound() throws Exception
    {
        given(mockRepository.findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleUser));

        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130004")
                        .accept("application/json"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(mockRepository, times(1)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130004");
    }

    @Test void
    givenValidAcceptHeader_and_invalidUuid_whenGetRequestToUser_thenBadRequest() throws Exception
    {
        given(mockRepository.findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleUser));

        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI + "/2")
                        .accept("application/json"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(mockRepository, times(0)).findByUuid("2");
    }

    @Test void
    givenInvalidAcceptHeader_and_validUuid_whenGetRequestToUser_thenNotAcceptable() throws Exception
    {
        given(mockRepository.findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleUser));

        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .accept("application/xml"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test void
    givenValidAcceptHeader_and_validPageValue_whenGetRequestToUsers_thenOK() throws Exception
    {
        //given
        Pageable pageable = PageRequest.of(
                1,20, Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")));
        Page<MarketUser> page = new PageImpl<>(listUsers, pageable, 200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(get(USR_API_URI +"?page=1")
                .accept("application/json"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(jsonPath("$['pageable']['pageNumber']").value(1));
    }

    @Test void
    givenInValidPageValue_whenGetRequestToUsers_thenBadRequest() throws Exception
    {
        given(mockRepository
                .findAll())
                .willReturn(listUsers);

        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI +"?page=-1"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test void
    givenValidSizeValue_whenGetRequestToUsers_thenOK() throws Exception
    {
        //given
        Pageable pageable = PageRequest.of(
                0,2, Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")));
        Page<MarketUser> page = new PageImpl<>(listUsers.subList(0, 2), pageable, 20L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(USR_API_URI +"?size=2"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['pageSize']").value(2));
    }

    @Test void
    givenInValidSizeValue_whenGetRequestToUsers_thenBadRequest() throws Exception
    {
        MockHttpServletResponse response = mvc.perform(
                get(USR_API_URI +"?size=0"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test void
    givenValidPageValueAndValidLimitValue_whenGetRequestToUsers_thenOK() throws Exception
    {
        //given
        Pageable pageable = PageRequest.of(
                2,4, Sort.by(Sort.Order.asc("firstName")));
        Page<MarketUser> page = new PageImpl<>(listUsers.subList(0, 4), pageable,200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(USR_API_URI +"?page=2&size=4"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(jsonPath("$['pageable']['pageNumber']").value(2))
                .andExpect(jsonPath("$['pageable']['pageSize']").value(4));
    }

    @Test void
    givenValidSortValue_whenGetRequestToUsers_thenOK() throws Exception
    {
        //given
        listUsers.sort(Comparator.comparing(
                MarketUser::getScreenName, Comparator.reverseOrder()));
        Pageable pageable = PageRequest.of(
                0,20, Sort.by(Sort.Order.desc("lastName")));
        Page<MarketUser> page = new PageImpl<>(listUsers, pageable,200L);
        given(mockRepository.findAll(pageable)).willReturn(page);

        //when
        mvc.perform(
                get(USR_API_URI +"?sort=screenName.desc"))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['sort']['sorted']").value("true"));
    }

    //POST
    @Test void
    givenValidMarketUserWithoutUuid_whenPostRequestToUsers_verifyOutput_and_businessLogicCall_thenCreated()
            throws Exception
    {
        //given
        MarketUser usr = createNewUser(
                "glages0@tmall.com",
                "Garreth",
                "Lages",
                "glages0",
                "QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN");

        given(mockRepository.save(any(MarketUser.class))).willReturn(usr);

        //when
        mvc.perform(post(USR_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(usr)))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(USR_API_URI + "/" + usr.getUuid())))
                .andExpect(jsonPath("$['sub']").value(usr.getSub()))
                .andExpect(jsonPath("$['uuid']").value(usr.getUuid()));

        verify(mockRepository, times(1)).save(any(MarketUser.class));
    }

    @Test void
    givenValidMarketUserWithUuid_whenPostRequestToUsers_verifyOutput_and_businessLogicCall_thenCreated() throws Exception
    {
        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        mvc.perform(post(USR_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(singleUser)))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")))
                .andExpect(jsonPath("$['screenName']").value(singleUser.getScreenName()))
                .andExpect(jsonPath("$['uuid']")
                        .value("usr-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketUser.class));
    }

    @Test void
    givenInvalidAuthToken_whenPostRequestToUsers_thenUnauthorised() throws Exception
    {
        //given
        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        mvc.perform(post(USR_API_URI)
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(singleUser)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(MarketUser.class));
    }

    @Test void
    givenNoAuthToken_whenPostRequestToUsers_thenForbidden() throws Exception
    {
        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        mvc.perform(post(USR_API_URI)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(singleUser)))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(MarketUser.class));
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validMarketUser_whenPostRequestToUsers_thenNotAcceptable() throws Exception
    {
        //given
        MarketUser usr = createNewUser(
                "glages0@tmall.com",
                "Garreth",
                "Lages",
                "glages0",
                "QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN");

        given(mockRepository.save(any(MarketUser.class))).willReturn(usr);

        //when
        mvc.perform(post(USR_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(usr)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validMarketUser_whenPostRequestToUsers_thenUnsupportedMediaType()
            throws Exception
    {
        //given
        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        //when
        mvc.perform(post(USR_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(singleUser)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
    }

    @Test void
    givenInvalidMarketUser_whenPostRequestToUsers_thenBadRequest() throws Exception
    {
        String invalidSingleUser = "{\"email\": \"dgrabeham0@baidu.com\", \"firstName\": \"Doti\", " +
                "\"screenName\": \"dmagnus0\", \"sub\": \"6sidSTqA-Vc96-jwvB-MnhL-zI3fnhltIvMp\", " +
                "\"uuid\": \"usr-5189a7bc-d630-11ea-87d0-0242ac130003\"}";

        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        mvc.perform(post(USR_API_URI)
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(invalidSingleUser))

                //then
                .andExpect(status().isBadRequest());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
    }

    //PUT
    @Test void
    givenValidUuid_and_validMarketUser_whenPutRequestToUsers_verifyOutput_and_businessLogicCall_thenCreated()
            throws Exception
    {
        //given
        String usr = "{\"firstName\":\"Garreth\"," +
                "\"lastName\":\"Lages\"," +
                "\"email\":\"glages0@tmall.com\"," +
                "\"sub\":\"QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN\"}";

        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        //when
        mvc.perform(put(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(usr))

                //then
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("location"))
                .andExpect(header().string("location",
                        containsString(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")))
                .andExpect(jsonPath("$['sub']").value(singleUser.getSub()))
                .andExpect(jsonPath("$['uuid']").value("usr-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketUser.class));
        verify(mockRepository, times(1)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidUuid_and_validMarketUser_whenPutRequestToUsers_verifyOutput_and_businessLogicCall_thenUpdated()
            throws Exception
    {
        //given
        String usr = "{\"firstName\":\"Garreth\"," +
                "\"lastName\":\"Lages\"," +
                "\"email\":\"glages0@tmall.com\"," +
                "\"sub\":\"QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN\"}";

        given(mockRepository.findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleUser));
        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        //when
        mvc.perform(put(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(usr))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['sub']").value(singleUser.getSub()))
                .andExpect(jsonPath("$['uuid']").value("usr-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketUser.class));
        verify(mockRepository, times(1)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidUuid_and_validMarketUser_whenPutRequestToUsers_thenBadRequest() throws Exception
    {
        //given
        String validUser = "{\"firstName\":\"Garreth\"," +
                "\"lastName\":\"Lages\"," +
                "\"email\":\"glages0@tmall.com\"," +
                "\"sub\":\"QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN\"}";

        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        //when
        mvc.perform(put(USR_API_URI + "/abc")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json").content(validUser))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("abc");
    }

    @Test void
    givenInvalidAuthToken_whenPutRequestToUsers_thenUnauthorised() throws Exception
    {
        String validUser = "{\"firstName\":\"Garreth\"," +
                "\"lastName\":\"Lages\"," +
                "\"email\":\"glages0@tmall.com\"," +
                "\"sub\":\"QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN\"}";

        mvc.perform(put(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json").content(objectMapper.writeValueAsString(validUser)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenNoAuthToken_whenPutRequestToUsers_thenForbidden() throws Exception
    {
        String validUser = "{\"firstName\":\"Garreth\"," +
                "\"lastName\":\"Lages\"," +
                "\"email\":\"glages0@tmall.com\"," +
                "\"sub\":\"QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN\"}";

        mvc.perform(put(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .accept("application/json")
                .contentType("application/json").content(validUser))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validMarketUser_whenPutRequestToUsers_thenNotAcceptable() throws Exception
    {
        //when
        mvc.perform(put(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json").content(objectMapper.writeValueAsString(singleUser)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validMarketUser_whenPutRequestToUsers_thenUnsupportedMediaType()
            throws Exception
    {
        //when
        mvc.perform(put(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(singleUser)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidMarketUser_whenPutRequestToUsers_thenBadRequest() throws Exception
    {
        String invalidUser = "{\"firstName\":\"Garreth\"," +
                "\"email\":\"glages0@tmall.com\"," +
                "\"sub\":\"QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN\"}";

        given(mockRepository.save(any(MarketUser.class))).willReturn(singleUser);

        mvc.perform(put(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .content(invalidUser))

                //then
                .andExpect(status().isBadRequest());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    //PATCH
    @Test void
    givenValidUuid_and_validJsonPatch_whenPatchRequestToUsers_verifyOutput_and_businessLogicCall_thenCreated()
            throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/email");
        patch.put("value", "fm@yahoo.com");
        Object[] validJsonPatch = {patch};

        MarketUser patchedUser = createNewUserWithUuiD(
                "fm@yahoo.com",
                "Garreth",
                "Lages",
                "glages0",
                "QJQwuNkp-GElX-aSqc-ddNx-HsvatNkUUSVN",
                "usr-5189a7bc-d630-11ea-87d0-0242ac130003");

        given(mockRepository.findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(singleUser));
        given(mockRepository.save(any(MarketUser.class))).willReturn(patchedUser);

        //when
        mvc.perform(patch(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$['email']").value("fm@yahoo.com"))
                .andExpect(jsonPath("$['uuid']").value("usr-5189a7bc-d630-11ea-87d0-0242ac130003"));

        verify(mockRepository, times(1)).save(any(MarketUser.class));
        verify(mockRepository, times(1)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenValidUuid_and_invalidJsonPatch_whenPatchRequestToUsers_thenBadRequest() throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("path", "/firstName");
        patch.put("value", "Phillip");

        //when
        mvc.perform(patch(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(patch)))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidUuid_and_validJsonPatch_whenPatchRequestToUsers_thenBadRequest() throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/email");
        patch.put("value", "fm@yahoo.com");
        Object[] validJsonPatch = {patch};

        //when
        mvc.perform(patch(USR_API_URI + "/abc")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidAuthToken_whenPatchRequestToUsers_thenUnauthorised() throws Exception
    {
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/email");
        patch.put("value", "fm@yahoo.com");
        Object[] validJsonPatch = {patch};

        mvc.perform(patch(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + fakeToken)
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isUnauthorized());
        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenNoAuthToken_whenPatchRequestToUsers_thenForbidden() throws Exception
    {
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/email");
        patch.put("value", "fm@yahoo.com");
        Object[] validJsonPatch = {patch};

        mvc.perform(patch(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .accept("application/json")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isForbidden());
        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedAcceptHeader_and_validJsonPatch_whenPatchRequestToUsers_thenNotAcceptable() throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/email");
        patch.put("value", "fm@yahoo.com");
        Object[] validJsonPatch = {patch};

        //when
        mvc.perform(patch(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/xml")
                .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isNotAcceptable());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenUnsupportedContentTypeHeader_and_validJsonPatch_whenPatchRequestToUsers_thenUnsupportedMediaType()
            throws Exception
    {
        //given
        Map<String, String> patch = new HashMap<>();
        patch.put("op", "replace");
        patch.put("path", "/email");
        patch.put("value", "fm@yahoo.com");
        Object[] validJsonPatch = {patch};

        //when
        mvc.perform(patch(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                .header("Authorization", "Bearer " + TOKEN)
                .accept("application/json")
                .contentType("application/text")
                .content(objectMapper.writeValueAsString(validJsonPatch)))

                //then
                .andExpect(status().isUnsupportedMediaType());

        verify(mockRepository, times(0)).save(any(MarketUser.class));
        verify(mockRepository, times(0)).findByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    // DELETE
    @Test void
    givenValidAuthToken_whenDeleteRequestToUsers_verify_output_and_businessLogicCalls_thenOK() throws Exception
    {
        MockHttpServletResponse response = mvc.perform(
                delete(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .header("Authorization", "Bearer " + TOKEN))

                //then
                .andExpect(status().isNoContent())
                .andReturn().getResponse();


        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("");
        verify(mockRepository, times(1)).deleteByUuid("usr-5189a7bc-d630-11ea-87d0-0242ac130003");
    }

    @Test void
    givenInvalidAuthToken_whenDeleteRequestToUsers_thenUnauthorised() throws Exception
    {
        mvc.perform(
                delete(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .header("Authorization", "Bearer " + fakeToken))

                //then
                .andExpect(status().isUnauthorized());
    }

    @Test void
    givenNoAuthToken_whenDeleteRequestToUsers_thenForbidden() throws Exception
    {
        mvc.perform(
                delete(USR_API_URI + "/usr-5189a7bc-d630-11ea-87d0-0242ac130003"))

                //then
                .andExpect(status().isForbidden());
    }
}
