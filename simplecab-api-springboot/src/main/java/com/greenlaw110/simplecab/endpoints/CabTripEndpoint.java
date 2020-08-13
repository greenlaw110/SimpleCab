package com.greenlaw110.simplecab.endpoints;

import com.greenlaw110.simplecab.services.CabTripService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cab_trips")
public class CabTripEndpoint {

    @Autowired
    private CabTripService service;

    @ApiOperation("Get trips count of one or more cab(s)")
    @GetMapping(value = "count_all", produces = "application/json")
    public Map<String, Integer> getTripsCountByCabs(
            @RequestParam List<String> medallions,
            boolean noCache
    ) {
        return service.getTripsCountByCabs(medallions, noCache);
    }

    @ApiOperation(value = "Get trips count of a particular cab and pick up date"
            , notes = "`pickupDate` must be specified with format `yyyy-MM-dd`, e.g. `2013-12-03`")
    @GetMapping(value = "{medallion}/{pickupDate}/count")
    public Map<String, Integer> getTripsCountByCabAndPickupDate(
            @PathVariable String medallion,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date pickupDate,
            boolean noCache
    ) {
        return service.getTripsCountByCabAndPickupDate(medallion, pickupDate, noCache);
    }

}
