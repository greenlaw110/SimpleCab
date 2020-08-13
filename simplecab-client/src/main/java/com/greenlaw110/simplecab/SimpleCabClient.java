package com.greenlaw110.simplecab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class SimpleCabClient {

    private static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    private String serviceContext;
    private OkHttpClient http;
    private ObjectMapper mapper;

    public SimpleCabClient(String serviceContext, OkHttpClient http) {
        if (!serviceContext.endsWith("/")) serviceContext = serviceContext + "/";
        this.serviceContext = serviceContext;
        this.http = Objects.requireNonNull(http);
        this.mapper = new ObjectMapper();
    }

    public Map<String, Integer> getTripsCountByCabAndPickupDate(String medallion, Date pickupDate, boolean noCache) throws SimpleCabServiceException {
        String url = urlForTripsCountByCabAndPickupDate(medallion, pickupDate, noCache);
        return callTripsCountService(url);
    }

    public Map<String, Integer> getTripsCountByCabs(boolean noCache, String ... medallions) throws SimpleCabServiceException {
        if (medallions.length == 0) throw new IllegalArgumentException("At least one medallions required");
        String url = urlForTripsCountByCabs(noCache, medallions);
        return callTripsCountService(url);
    }

    private Map<String, Integer> callTripsCountService(String url) throws SimpleCabServiceException {
        try {
            Request.Builder reqBuilder = new Request.Builder().url(url).get();
            Request req = reqBuilder.build();
            Response resp = http.newCall(req).execute();
            if (resp.isSuccessful()) {
                return mapper.readValue(resp.body().string(), new TypeReference<Map<String, Integer>>() {
                });
            } else {
                throw new SimpleCabServiceException(String.format("Error requesting SimpleCab service: %d %s", resp.code(), resp.body().string()));
            }
        } catch (IOException e) {
            throw new SimpleCabServiceException("Error request SimpleCab service", e);
        }
    }

    private String urlForTripsCountByCabAndPickupDate(String medallion, Date pickupDate, boolean noCache) {
        StringBuilder sb = new StringBuilder(serviceContext);
        sb.append(medallion).append("/").append(formatDate(pickupDate)).append("/count");
        if (noCache) {
            sb.append("?noCache=true");
        }
        return sb.toString();
    }

    private String urlForTripsCountByCabs(boolean noCache, String ... medallions) {
        StringBuilder sb = new StringBuilder(serviceContext).append("count_all?");
        for (String medallion: medallions) {
            sb.append("medallions=").append(medallion).append("&");
        }
        if (noCache) {
            sb.append("noCache=true");
        } else {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String formatDate(Date date) {
        return fmt.format(date);
    }


}
