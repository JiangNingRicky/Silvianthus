package com.expert.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("all")
public class HttpClientUtil implements WebAccessible {
    private static CloseableHttpClient httpClient = null;


    static {

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(150);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(600000)
                .setConnectionRequestTimeout(600000)
                .setSocketTimeout(600000)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        // 重试处理器，StandardHttpRequestRetryHandler
        HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler();

        httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler).build();
    }


    public static CloseableHttpResponse getResponse(String uri, Map<String, String> getParams, Map<String, String> headers) {
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                uriBuilder.setParameters(list);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            if (headers != null) {
                headers.forEach((k, v) -> httpGet.addHeader(k, v));
            }
            response = httpClient.execute(httpGet);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String doHttpGet(String uri, Map<String, String> getParams, Map<String, String> headers) throws IOException {
        CloseableHttpResponse response = getResponse(uri, getParams, headers);
        String responseString = getStringFromResponse(response);
        if (null != response) {
            //关闭输出流
            response.close();
        }
        return responseString;
    }

    public static String getStringFromResponse(CloseableHttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK == statusCode) {
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                return EntityUtils.toString(entity, "utf-8");
            }
        }

        return null;
    }

    public static String doHttpPost(String uri, Map<String, String> getParams) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(uri);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                HttpEntity httpEntity = new UrlEncodedFormEntity(list, "utf-8");
                httpPost.setEntity(httpEntity);
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    return EntityUtils.toString(entity, "utf-8");
                }
            }
        } catch (Exception e) {
            //log.error("CloseableHttpClient-post-请求异常", e);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public void downloadFile(String url, File nativeFile) throws IOException {
        CloseableHttpResponse closeableHttpResponse = getResponse(url, null, null);
        try (InputStream is = closeableHttpResponse.getEntity().getContent();
             OutputStream os = new FileOutputStream(nativeFile)) {
            StreamUtils.copy(is, os);
        }
    }

    @Override
    public String requestPage(String url) throws IOException {
        return doHttpGet(url, new HashMap<String, String>(), null);
    }

    @Override
    public String requestPage(String url, Map<String, String> header) throws URISyntaxException, IOException {
        return doHttpGet(url, new HashMap<String, String>(), header);
    }
}