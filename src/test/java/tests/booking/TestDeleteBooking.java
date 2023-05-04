package tests.booking;

import org.testng.annotations.Test;
import tests.BookingTestBase;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDeleteBooking extends BookingTestBase {
    @Test(description = "Delete booking")
    public void deleteBooking() {
        //given
        final var requestBodyPost = readJSONFromFile("saveBooking.json");
        final var props = loadProperties("booking");
        final var responsePost = invokePost(requestBodyPost, "booking", BOOKING);
        final var bookingId = getFromJSONString(responsePost.asString(), "bookingid");

        //when
        final var response = invokeDelete("booking", BOOKINGID, bookingId);

        //then
        assertThat(response.statusCode()).isEqualTo(201);
    }
}
