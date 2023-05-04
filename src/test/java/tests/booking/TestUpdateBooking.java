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
        final var responseUpdate = invoke(requestBody, Method.PUT, "booking", BOOKINGID, bookingId);

        //then
        assertThat(responseUpdate.statusCode()).isEqualTo(200);
    }
}
