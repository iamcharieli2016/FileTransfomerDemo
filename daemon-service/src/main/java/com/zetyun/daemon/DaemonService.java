package com.zetyun.daemon;

import com.zetyun.daemon.config.ConfigLoader;
import com.zetyun.daemon.db.DatabaseManager;
import com.zetyun.daemon.http.HttpClient;
import com.zetyun.daemon.model.Tenant;
import com.zetyun.daemon.util.ModuleExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class DaemonService {
    private static final Logger logger = LoggerFactory.getLogger(DaemonService.class);
    private static ConfigLoader config;
    private static DatabaseManager dbManager;
    private static HttpClient httpClient;

    private static List<String> fieldList = new ArrayList<>();

    public static void main(String[] args) {
        String configPath = "config.properties";
        if (args.length > 0) {
            configPath = args[0];
        }

        try {
            logger.info("Start to init.");
            init(configPath);
            logger.info("Finished init.");
            scheduleTask();
        } catch (Exception e) {
            logger.error("Error in main", e);
        }
    }

    private static void init(String configPath) throws Exception {
        config = new ConfigLoader(configPath);
        dbManager = new DatabaseManager(config);
        httpClient = new HttpClient(config);

        dbManager.initializeTables();

        fieldList = Arrays.asList(config.getProperty("wide.table.fields").split(","));
    }

    private static void scheduleTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        String scheduledTimeStr = config.getProperty("scheduled.time");
        LocalTime scheduledTime = LocalTime.parse(scheduledTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

        long initialDelay = getInitialDelay(scheduledTime);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runTask();
            }
        }, initialDelay, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
    }

    private static long getInitialDelay(LocalTime scheduledTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(scheduledTime);
        if (now.toLocalTime().isAfter(scheduledTime)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).toMinutes();
    }

    private static void runTask() {
        try {
            //1. 获取租户列表
            logger.info("Start to get tenant list.");
            List<Tenant> tenants = dbManager.getTenants();
            logger.info("Finished to get tenant list.");
            //2. 通过登录获取UUMS和PLATFORM_UUMS
            String UUMS = getUums(config.getProperty("login.url"), config.getProperty("login.params"));
            String PLATFORM_UUMS = getPlatformUums(config.getProperty("login.oc.url"), config.getProperty("login.oc.params"));
            logger.info("UUMS: {}, PLATFORM_UUMS: {}", UUMS, PLATFORM_UUMS);
            Map<String, Object> resultMap = new HashMap<>();
            //3. 遍历租户列表，获取统计信息，更新宽表内容
            for (Tenant tenant : tenants) {
                String stats = httpClient.getStatistics(tenant.getId(), UUMS, config.getProperty("stats.url"));
                logger.info("stats: {}", stats);
                Map<String, Integer> statsMap = ModuleExtractor.extractModules(fieldList, stats);
                resultMap.putAll(statsMap);
                String cpuStr = httpClient.getStatisticsWithParam(tenant.getId(), UUMS, PLATFORM_UUMS, config.getProperty("stats.cpu"));
                logger.info("cpuStr: {}", cpuStr);
                if (!cpuStr.isEmpty()) {
                    double cpu = ModuleExtractor.extractValue(cpuStr);
                    resultMap.put("CPU", cpu);
                }
                String memStr = httpClient.getStatisticsWithParam(tenant.getId(), UUMS, PLATFORM_UUMS, config.getProperty("stats.mem"));
                logger.info("memStr: {}", memStr);
                if (!memStr.isEmpty()) {
                    double mem = ModuleExtractor.extractValue(memStr);
                    resultMap.put("MEM", mem);
                }
                dbManager.insertOrupdateStats(tenant.getId(), resultMap);
            }
        } catch (Exception e) {
            logger.error("Error in runTask", e);
        }
    }

    private static String getUums(String url, String params) {

        String cookies = null;
        try {
            cookies = httpClient.login(url, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return extractUUMSWithIndexOf(cookies);
    }

    private static String getPlatformUums(String url, String params) {
        String cookies = null;
        try {
            cookies = httpClient.login(url, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return extractUUMSWithIndexOf(cookies);
    }

    private static String extractUUMSWithIndexOf(String cookies) {
        int start = 0;
        if (cookies.contains("PLATFORM_UUMS")) {
            start = cookies.indexOf("PLATFORM_UUMS=");
        } else {
            start = cookies.indexOf("UUMS=");
        }
        if (start != -1) {
            if (cookies.contains("PLATFORM_UUMS")) {
                start += 14;
            } else {
                start += 5; // length of "UUMS="
            }
            int end = cookies.indexOf(';', start);
            if (end != -1) {
                return cookies.substring(start, end);
            } else {
                return cookies.substring(start);
            }
        }
        return null;
    }

}
