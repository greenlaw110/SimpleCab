package com.greenlaw110.simplecab;

import com.greenlaw110.simplecab.entities.CabTrip;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CabTripEndpointTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestSupport testSupport;

    private static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    public void prepareTestData() {
        List<CabTrip> testTrips = Arrays.asList(
                new CabTrip("testA", fmtDate("2020-08-13")),
                new CabTrip("testA", fmtDate("2020-08-13")),
                new CabTrip("testA", fmtDate("2020-08-12")),
                new CabTrip("testB", fmtDate("2020-08-13"))
        );
        testSupport.addTestTrips(testTrips);
    }

    @AfterEach
    public void clearTestData() {
        testSupport.removeAllTestTrips();
    }

    @Test
    public void theApiMustAllowQueryHowManyTripsAParticularCabHasMadeGivenAParticularPickupDate() {
        String url = urlForCabAndPickupDate("testA", "2020-08-13");
        Map<String, Integer> result = restTemplate.getForObject(url, Map.class);
        Integer count = result.get("testA");
        MatcherAssert.assertThat("testA count should be 2 on 2020-08-13", count == 2);
    }

    @Test
    public void theApiMustReturnOneOrMoreMedallionsAndReturnHowManyTripsEachMedallionHasMade() {
        String url = urlForCabs("testA", "testB");
        Map<String, Integer> result = restTemplate.getForObject(url, Map.class);
        MatcherAssert.assertThat("testA count should be 3", result.get("testA") == 3);
        MatcherAssert.assertThat("testB count should be 1", result.get("testB") == 1);
    }

    private String urlForCabAndPickupDate(String medallion, String date) {
        return "http://localhost:" + port + "/api/v1/cab_trips/" + medallion + "/" + date + "/count";
    }

    private String urlForCabs(String... medallions) {
        StringBuilder sb = new StringBuilder("http://localhost:");
        sb.append(port).append("/api/v1/cab_trips/count_all?");
        for (String medallion : medallions) {
            sb.append("medallions=").append(medallion).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static Date fmtDate(String s) {
        try {
            return fmt.parse(s);
        } catch (ParseException e) {
            throw new AssertionError("Unknown date pattern: " + s);
        }
    }
}
