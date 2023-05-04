package tests.booking;

import io.restassured.http.Method;
import org.testng.annotations.Test;
import tests.BookingTestBase;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUpdateBooking extends BookingTestBase {
    @Test(description = "Update booking")
    public void updateBooking() {
        //given
        final var requestBodyPost = readJSONFromFile("saveBooking.json");
        final var props = loadProperties("booking");
        final var responsePost = invokePost(requestBodyPost, "booking", BOOKING);
        final var bookingId = getFromJSONString(responsePost.asString(), "bookingid");
        final var requestBody = createJson(
                Map.of(
                        "firstname", props.get("firstname"),
                        "lastname", props.get("lastname"),
                        "totalprice", props.get("totalprice"),
                        "depositpaid", props.get("depositpaid"),
                        "additionalneeds", props.get("additionalneeds"))
                , props.getProperty("bookingRequest"));

        //when
        final var response = invoke(requestBody, Method.PUT, "booking", BOOKINGID, bookingId);

        //then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(getFromJSONString(response.asString(),"$..firstname")).contains("Arnold");
        assertThat(getFromJSONString(response.asString(),"$..lastname")).contains("Black");
        assertThat(Integer.parseInt(getFromJSONString(response.asString(),"totalprice"))).isEqualTo(200);
        assertThat(Boolean.parseBoolean(getFromJSONString(response.asString(),"depositpaid"))).isTrue();
        assertThat(getFromJSONString(response.asString(),"$..checkin")).contains("2018-01-01");
        assertThat(getFromJSONString(response.asString(),"$..checkout")).contains("2019-01-01");
        assertThat(getFromJSONString(response.asString(),"$..additionalneeds")).contains("Dinner");
    }
}
