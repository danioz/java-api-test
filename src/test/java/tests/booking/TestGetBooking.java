package tests.booking;

import org.testng.annotations.Test;
import tests.BookingTestBase;

import static org.assertj.core.api.Assertions.assertThat;

public class TestGetBooking extends BookingTestBase {
    @Test(description = "Get booking")
    public void getBooking() {
        //given
        final var requestBodyPost = readJSONFromFile("saveBooking.json");
        final var responsePost = invokePost(requestBodyPost, "booking", BOOKING);
        final var bookingId = getFromJSONString(responsePost.asString(), "bookingid");

        //when
        final var response = invokeGet("booking", BOOKINGID, bookingId);

        //then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(getFromJSONString(response.asString(),"firstname")).isEqualTo("Jim");
        assertThat(getFromJSONString(response.asString(),"lastname")).isEqualTo("Brown");
        assertThat(Integer.parseInt(getFromJSONString(response.asString(),"totalprice"))).isEqualTo(111);
        assertThat(Boolean.parseBoolean(getFromJSONString(response.asString(),"depositpaid"))).isTrue();
        assertThat(getFromJSONString(response.asString(),"$..checkin")).contains("2018-01-01");
        assertThat(getFromJSONString(response.asString(),"$..checkout")).contains("2019-01-01");
        assertThat(getFromJSONString(response.asString(),"additionalneeds")).isEqualTo("Breakfast");
    }
}
