package tests.booking;

import org.testng.annotations.Test;
import tests.BookingTestBase;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSaveBooking extends BookingTestBase {
    @Test(description = "Save booking")
    public void saveBooking() {
        //given
        final var requestBody = readJSONFromFile("saveBooking.json");

        //when
        final var response = invokePost(requestBody, "booking", BOOKING);

        //then
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
