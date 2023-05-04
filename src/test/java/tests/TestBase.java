package tests;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;

public class TestBase {

    private static final Configuration CONFIGURATION = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime now = LocalDateTime.now();
    protected static String env;
    private final Properties props = new Properties();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public void setGlobalProp() throws IOException {
        props.load(getClass().getResourceAsStream("/common_resource.properties"));
    }

    @SneakyThrows
    protected Properties loadProperties(final String path) {
        System.setProperty("file.encoding", "UTF-8");
        Charset.forName(System.getProperty("file.encoding"));
        final var testProps = new Properties();
        testProps.load(getClass().getResourceAsStream(
                String.format("/%s/test.properties".replace("test", path), getClass().getPackage().getName().replaceAll("\\.", "/"))));

        return testProps;
    }

    @BeforeTest(alwaysRun = true)
    public void setUp() throws IOException {
        setGlobalProp();
        env = props.getProperty("env");
        RestAssured.useRelaxedHTTPSValidation();
    }

    protected String getUrl(final String ms) {
        return props.getProperty(ms);
    }

    protected Map<String, String> getHeaders(final String header, final Map<String, String> params) {
        final var headerMap = getHeaders(header);
        headerMap.putAll(params);
        return headerMap;
    }

    protected Map<String, String> getHeaders(final String header) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("accept", "*/*");
        return headerMap;
    }

    @SneakyThrows
    protected String readJSONFromFile(final String fileName) {
        return Files.readString(Paths.get(getClass().getResource(fileName).toURI()), StandardCharsets.UTF_8);
    }


    protected String getFromJSONString(final String json, final String path) {
        final var value = JsonPath.read(json, path);
        if (value == null)
            return null;
        return value.toString();
    }


    protected boolean isValidDate(String input) {
        try {
            format.parse(input.replace("[\"", ""));
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    protected String generateRandomChars(final int length, final boolean useLetters, final boolean useNumbers) {
        return RandomStringUtils.random(length, useLetters, useNumbers).replace("0", "9");
    }

    @SneakyThrows
    protected void sleep() {
        Thread.sleep(Integer.parseInt(props.getProperty("sleepToCheckOrder")));
    }

    protected String randomValue(final String[] list) {
        final var r = new Random();
        return list[r.nextInt(list.length)];
    }

    protected int randomValue(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    protected int countMatches(final String input, final String text) {
        return StringUtils.countMatches(input, text);
    }

    protected String getChild(final Response response, final String key, final String value) {
        final var j = new io.restassured.path.json.JsonPath(response.asString());
        for (var i = 0; i < countMatches(response.body().asString(), key); i++) {
            if (j.getString("[" + i + "]." + key).equals(value)) {
                return j.getString("[" + i + "]");
            }
        }
        return null;
    }

    protected Response invokeGet(final String service, final String enpoint,
                                 final Object... pathParams) {
        return invoke(null, Method.GET, service, enpoint, pathParams);
    }

    protected Response invokeDelete(final String service, final String enpoint,
                                    final Object... pathParams) {
        return invoke(null, Method.DELETE, service, enpoint, pathParams);
    }

    protected Response invokePost(final String requestBody, final String service, final String endpoint,
                                  final Object... pathParams) {
        return invoke(requestBody, Method.POST, service, endpoint, pathParams);
    }

    protected Response invoke(final String requestBody, final Method method, final String service, final String endpoint,
                              final Object... pathParams) {
        final var requestSpecification = given()
                .auth()
                .preemptive()
                .basic(props.getProperty("userName"), props.getProperty("userPassword"))
                .log().all()
                .filter(new AllureRestAssured())
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
        if (requestBody != null) {
            requestSpecification.contentType(ContentType.JSON).body(requestBody);
        }
        return requestSpecification.when().request(method, getUrl(service) + endpoint, pathParams).then().extract().response();
    }

    protected String createJson(final String fileName, final Map<String, Object> params) {
        final var requestBody = readJSONFromFile(fileName);
        final var jsonBody = JsonPath.using(CONFIGURATION).parse(requestBody);
        params.forEach(jsonBody::set);

        return jsonBody.jsonString();
    }

    protected String createJson(final Map<String, Object> params, final String requestBody) {
        final var jsonBody = JsonPath.using(CONFIGURATION).parse(requestBody);
        params.forEach(jsonBody::set);

        return jsonBody.jsonString();
    }

    protected String getEmail() {
        return props.getProperty("email");
    }

    protected String getCurrentDateAndTime() {
        return dtf.format(now);
    }
}
