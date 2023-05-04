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
        assertThat(getFromJSONString(response.asString(),"bookingid")).isNotEmpty();
        assertThat(getFromJSONString(response.asString(),"$..firstname")).contains("Jim");
        assertThat(getFromJSONString(response.asString(),"$..lastname")).contains("Brown");
        assertThat(Integer.parseInt(getFromJSONString(response.asString(),"$.booking.totalprice"))).isEqualTo(111);
        assertThat(Boolean.parseBoolean(getFromJSONString(response.asString(),"$.booking.depositpaid"))).isTrue();
        assertThat(getFromJSONString(response.asString(),"$..checkin")).contains("2018-01-01");
        assertThat(getFromJSONString(response.asString(),"$..checkout")).contains("2019-01-01");
        assertThat(getFromJSONString(response.asString(),"$..additionalneeds")).contains("Breakfast");
    }
}
