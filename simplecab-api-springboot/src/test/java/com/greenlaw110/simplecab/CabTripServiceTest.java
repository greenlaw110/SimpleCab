package com.greenlaw110.simplecab;

import com.greenlaw110.simplecab.entities.CabTrip;
import com.greenlaw110.simplecab.entities.CabTripCount;
import com.greenlaw110.simplecab.repositories.CabTripRepository;
import com.greenlaw110.simplecab.services.CabTripService;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.greenlaw110.simplecab.CabTripEndpointTest.fmtDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest(classes = SimpleCabApiApplication.class)
public class CabTripServiceTest {

    private static class CabTripCountMock implements CabTripCount {
        @Override
        public String getMedallion() {
            return "abc";
        }

        @Override
        public int getCount() {
            return 47;
        }
    }

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CabTripService service;

    @Autowired
    private TestSupport testSupport;

    @BeforeEach
    private void setUp() {
        service.clearCache();
        List<CabTrip> testTrips = Arrays.asList(
                new CabTrip("testA", fmtDate("2020-08-13")),
                new CabTrip("testA", fmtDate("2020-08-13")),
                new CabTrip("testA", fmtDate("2020-08-12")),
                new CabTrip("testB", fmtDate("2020-08-13"))
        );
        testSupport.addTestTrips(testTrips);
    }

    @Test
    public void testCache() {
        Date date = fmtDate("2020-08-13");
        Object result = service.getTripsCountByCabAndPickupDate("testA", date, false);
        MatcherAssert.assertThat("", Objects.equals(result, getFromCache("testA", date)));
    }

    @Test
    public void verifyCacheCleared() {
        Date date = fmtDate("2020-08-13");
        Object result = service.getTripsCountByCabAndPickupDate("testA", date, false);
        service.clearCache();
        MatcherAssert.assertThat("should not exist any more", null == getFromCache("testA", date));
    }

    private Object getFromCache(String medallion, Date date) {
        Cache.ValueWrapper wrapper = cacheManager.getCache(CacheConfig.CACHE_TRIP_COUNT).get(new SimpleKey(medallion, date, false));
        return null == wrapper ? null : wrapper.get();
    }

    private Object getFromCache(List<String> medallions) {
        Cache.ValueWrapper wrapper = cacheManager.getCache(CacheConfig.CACHE_TRIP_COUNT).get(new SimpleKey(medallions, false));
        return null == wrapper ? null : wrapper.get();
    }
}
