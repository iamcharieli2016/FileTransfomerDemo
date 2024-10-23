package com.zetyun.statics.http;

import com.zetyun.statics.config.ConfigLoader;
import com.zetyun.statics.model.Tenant;
import com.zetyun.statics.util.ModuleExtractor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.sql.SQLOutput;
import java.util.List;

public class HttpClient {
    private ConfigLoader config;
    private CloseableHttpClient httpClient;

    public HttpClient(ConfigLoader config) {
        this.config = config;
        this.httpClient = HttpClients.createDefault();
    }

    public String login(String url, String params) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");

        // 设置JSON参数
        StringEntity entity = new StringEntity(params);
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // Extract UUMS from response headers
            return response.getFirstHeader("Set-Cookie").getValue();
        }
    }

    public String getStatistics(String tenantId, String UUMS, String url) throws Exception {
        HttpGet request = new HttpGet(url);
        request.setHeader("Cookie", "UUMS="+UUMS);
        request.setHeader("Tenantid", tenantId);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    public String getStatistics(String tenantId, String UUMS, String PLATFORM_UUMS, String url) throws Exception {
        HttpGet request = new HttpGet(url);
        request.setHeader("Cookie", "UUMS="+UUMS+";PLATFORM_UUMS="+PLATFORM_UUMS);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    public String getStatisticsWithParam(String tenantId, String UUMS, String PLATFORM_UUMS, String url) throws Exception {
        URIBuilder builder = new URIBuilder(url);
        builder.setParameter("tenantId", tenantId);
        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);
        request.setHeader("Cookie", "UUMS="+UUMS+";PLATFORM_UUMS="+PLATFORM_UUMS);
        request.setHeader("Tenantid", "00000000-0000-0000-0000-000000000000");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    public List<Tenant> getTenantList(String pageNum, String UUMS, String PLATFORM_UUMS, String url) throws Exception {
        URIBuilder builder = new URIBuilder(url);
        builder.setParameter("pageNum", pageNum);
        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);
        request.setHeader("Cookie", "UUMS="+UUMS+";PLATFORM_UUMS="+PLATFORM_UUMS);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String entityStr = EntityUtils.toString(response.getEntity());
            System.out.println("Entity String: " + entityStr);
            return ModuleExtractor.extractTenantListValue(entityStr);
        }
    }
}
