package com.greenlaw110.simplecab.services;

import com.greenlaw110.simplecab.CacheConfig;
import com.greenlaw110.simplecab.entities.CabTripCount;
import com.greenlaw110.simplecab.repositories.CabTripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CabTripService {

    public static final String CACHE_NAME = CacheConfig.CACHE_TRIP_COUNT;

    private CabTripRepository repository;

    @Autowired
    private CacheManager cacheManager;

    public CabTripService(@Autowired CabTripRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = CACHE_NAME, condition = "!#noCache")
    public Map<String, Integer> getTripsCountByCabAndPickupDate(
            String medallion,
            Date pickupDate,
            boolean noCache
    ){
        Integer count = repository.countTripsByMedallionAndPickupDate(medallion, pickupDate);
        return Collections.singletonMap(medallion, count);
    }

    @Cacheable(value = CACHE_NAME, condition = "!#noCache")
    public Map<String, Integer> getTripsCountByCabs(
            List<String> medallions,
            boolean noCache
    ) {
        List<CabTripCount> counts = repository.countTripsByMultipleMedallions(medallions);
        return counts.stream().collect(Collectors.toMap(CabTripCount::getMedallion, CabTripCount::getCount));
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearCache() {
    }

}
