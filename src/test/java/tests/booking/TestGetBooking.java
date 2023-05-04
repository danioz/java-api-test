package tests.booking;

import org.testng.annotations.Test;
import tests.BookingTestBase;

import static org.assertj.core.api.Assertions.assertThat;

public class TestGetBooking extends BookingTestBase {
    @Test(description = "Get booking")
    public void getBooking() {
        //given
        final var requestBodyPost = readJSONFromFile("saveBooking.json");
        final var props = loadProperties("booking");
        final var responsePost = invokePost(requestBodyPost, "booking", BOOKING);
        final var bookingId = getFromJSONString(responsePost.asString(), "bookingid");

        //when
        final var response = invokeGet("booking", BOOKINGID, bookingId);

        //then
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
