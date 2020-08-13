package com.greenlaw110.simplecab.repositories;

import com.greenlaw110.simplecab.entities.CabTrip;
import com.greenlaw110.simplecab.entities.CabTripCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CabTripRepository extends JpaRepository<CabTrip, Integer> {

    @Query("SELECT count(c) FROM CabTrip c WHERE " +
            "c.medallion = :medallion and " +
            "date(c.pickupDate) = date(:pickupDate)")
    Integer countTripsByMedallionAndPickupDate(
            @Param("medallion") String medallion,
            @Param("pickupDate") Date pickupDate);

    @Query("SELECT c.medallion as medallion, count(c) as count FROM CabTrip c \n" +
            "WHERE c.medallion IN :medallions " +
            "GROUP BY c.medallion \n")
    List<CabTripCount> countTripsByMultipleMedallions(@Param("medallions") List<String> medallions);

}
