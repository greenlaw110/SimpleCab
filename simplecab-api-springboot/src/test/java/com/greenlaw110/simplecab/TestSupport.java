package com.greenlaw110.simplecab;

import com.greenlaw110.simplecab.entities.CabTrip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestSupport {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void addTestTrips(List<CabTrip> trips) {
        List<Object[]> params = trips.stream()
                .filter((trip) -> trip.getMedallion().startsWith("test"))
                .map((CabTrip trip) -> new Object[]{trip.getMedallion(), trip.getPickupDate()}).collect(Collectors.toList());
        jdbcTemplate.batchUpdate("INSERT INTO cab_trip_data (medallion, pickup_datetime) VALUES (?, ?)", params);
    }

    public void removeAllTestTrips() {
        jdbcTemplate.execute("DELETE FROM cab_trip_data WHERE medallion like 'test%'");
    }
}
