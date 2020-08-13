package com.greenlaw110.simplecab.endpoints;

import act.controller.annotation.UrlContext;
import act.data.annotation.DateFormatPattern;
import act.util.CacheFor;
import com.greenlaw110.simplecab.models.CabTrip;
import org.osgl.mvc.annotation.GetAction;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;

@UrlContext("/api/v1/cab_trips")
public class CabTripEndpoint {

    @Inject
    private CabTrip.Dao dao;

    /**
     * Get NY Cab trips count by Cab ID (Medallion) and pickup date.
     *
     * Note pickup date must be specified with format `yyyy-MM-dd`, e.g. `2013-12-31`
     * @param medallion the medallion specifies the cab
     * @param pickupDate the pickup date in format `yyyy-MM-dd`
     * @return the trips count associated with the cab medallion.
     */
    @CacheFor
    @GetAction("{medallion}/{pickupDate}/count")
    public Map<String, Long> getTripsCountByCabAndPickupDate(
            String medallion,
            @DateFormatPattern("yyyy-MM-dd") Date pickupDate
    ) {
        return dao.countByCabAndPickupDate(medallion, pickupDate);
    }

    /**
     * Get trips count for all cabs specified.
     * @param medallions one or more cab medallion.
     * @return the trips count for all cabs specified by `medallions`
     */
    @CacheFor
    @GetAction("count_all")
    public Map<String, Long> getTripsCountByCabs(
            List<String> medallions
    ) {
        return dao.countByCabs(medallions);
    }

}
