package io.kischang.readme.app.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * http 工具类
 *
 * @author KisChang
 * @version 1.0
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final Charset DEF_CHARSET = StandardCharsets.UTF_8;
    private static HttpClient HTTP_CLIENT = null;

    static {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        HTTP_CLIENT = clientBuilder.build();
    }

    public static String getData(String url) {
        return getData(url, DEF_CHARSET);
    }

    public static String getData(String url, Charset charset) {
        return getData(HTTP_CLIENT, url, charset);
    }

    public static String getData(HttpClient httpclient, String url) {
        return getData(httpclient, url, DEF_CHARSET);
    }

    public static String getData(HttpClient httpclient, String url, Charset charset) {
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpclientExecute(httpclient, httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, charset);
            }
            httpGet.abort();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    public static String getImgBase64(String url) {
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpclient = HTTP_CLIENT;
        try {
            HttpResponse response = httpclientExecute(httpclient, httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                String type = entity.getContentType().getValue();
                String base = Base64.getEncoder().encodeToString(EntityUtils.toByteArray(entity));
                return String.format("data:%s;base64,%s", type, base);
            }
            httpGet.abort();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String postData(String url, Map<String, Object> data) {
        return postData(url, data, DEF_CHARSET);
    }

    public static String postData(String url, Map<String, Object> data, Charset charset) {
        return postData(HTTP_CLIENT, url, data, charset);
    }


    public static String postData(HttpClient httpclient, String url, Map<String, Object> data) {
        return postData(httpclient, url, data, DEF_CHARSET);
    }

    public static String postData(HttpClient httpclient, String url, Map<String, Object> data, Charset charset) {
        HttpPost post = new HttpPost(url);

        try {
            if (data != null && !data.isEmpty()) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
                }
                post.setEntity(new UrlEncodedFormEntity(nvps, charset));
            }

            HttpResponse response = httpclientExecute(httpclient, post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, charset);
            }
            post.abort();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }


    public static String postData(String url, AbstractHttpEntity data) {
        return postData(HTTP_CLIENT, url, data, DEF_CHARSET);
    }

    public static String postData(String url, AbstractHttpEntity data, Charset charset) {
        return postData(HTTP_CLIENT, url, data, charset);
    }

    public static String postData(HttpClient httpclient, String url, AbstractHttpEntity data, Charset charset) {
        HttpPost post = new HttpPost(url);

        try {
            if (data != null) {
                post.setEntity(data);
            }

            HttpResponse response = httpclientExecute(httpclient, post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, charset);
            }
            post.abort();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    private static HttpResponse httpclientExecute(HttpClient httpclient, HttpRequestBase post) throws IOException {
        if (_proxy != null) {
            return httpclient.execute(_proxy, post);
        } else {
            return httpclient.execute(post);
        }
    }

    private HttpUtils() {
    }

    private static HttpHost _proxy = null;

    public static void setProxy(HttpHost proxy) {
        _proxy = proxy;
    }
}
