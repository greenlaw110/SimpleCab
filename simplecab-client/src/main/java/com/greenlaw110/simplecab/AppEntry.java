package com.greenlaw110.simplecab;

import okhttp3.OkHttpClient;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * SimpleCab API client
 */
public class AppEntry {

    @Option(names = {"-o", "--host"}, defaultValue = "localhost", description = "specify service host")
    private String host;

    @Option(names = {"-p", "--port"}, defaultValue = "8080", description = "specify service port")
    private int port;

    @Option(names = {"-s", "--secure"}, defaultValue = "false", description = "specify http secure")
    private boolean secure = false;

    @Option(names = {"-c", "--context"}, defaultValue = "/api/v1/cab_trips", description = "specify service URL path context")
    private String apiContext;

    @Option(names = "-m", description = "specify cab medallion(s). In case multiple cabs, use comma to separate")
    private String medallion;

    @Option(names = {"-u", "--pickup-date"}, defaultValue = "", description = "specify pickup date with format yyyy-MM-dd, e.g. 2013-12-02")
    private String pickupDate;

    @Option(names = "--no-cache", defaultValue = "false", description = "when specified then service shall ignore cache and get fresh data")
    private boolean noCache;

    private void run() throws SimpleCabServiceException {
        OkHttpClient http = new OkHttpClient();
        SimpleCabClient client = new SimpleCabClient(buildServiceEndpointUrl(), http);
        String[] medallions = this.medallion.split(",");
        Map<String, Integer> result;
        if (!pickupDate.trim().isEmpty()) {
            Date thePickupDate = validDate(pickupDate);
            String firstCab = medallions[0];
            result = client.getTripsCountByCabAndPickupDate(firstCab, thePickupDate, this.noCache);
        } else {
            result = client.getTripsCountByCabs(this.noCache, medallions);
        }
        System.out.println(String.format("%-32s  trips", "Cab(Medallion)"));
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            String line = String.format("%-32s: %-3d", entry.getKey(), entry.getValue());
            System.out.println(line);
        }
    }

    private String buildServiceEndpointUrl() {
        if (null == medallion) {
            throw new IllegalArgumentException("At least one medallion required");
        }
        String[] medallions = medallion.split(",");
        if (medallions.length == 0) {
            throw new IllegalArgumentException("At least one medallion required");
        }
        StringBuilder sb = new StringBuilder("http");
        if (secure) sb.append('s');
        sb.append(":");
        while (!apiContext.isEmpty() && apiContext.endsWith("/")) {
            apiContext = apiContext.substring(0, apiContext.length() - 1);
        }
        sb.append("//").append(host).append(":").append(port).append(apiContext);
        return sb.toString();
    }

    public static void main(String[] args) throws SimpleCabServiceException {
        AppEntry client = new AppEntry();
        new CommandLine(client).parseArgs(args);
        client.run();
    }

    private static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    private static Date validDate(String dateString) {
        try {
            return fmt.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date string: " + dateString);
        }
    }
}
