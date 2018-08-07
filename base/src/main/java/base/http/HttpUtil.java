package base.http;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 14:53
 * @description
 * @since 2.8.1
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import base.utils.ServiceExpection;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    protected static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    protected static final CloseableHttpClient httpClient;

    public HttpUtil() {
    }

    public static String post(String url, Map<String, String> params) throws Exception {
        return post(url, params, "UTF-8");
    }

    public static String post(String url, Map<String, String> params, String charset) throws Exception {
        return post(url, params, (Map)null, charset);
    }

    public static String post(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        return post(url, params, header, "UTF-8");
    }

    public static String post(String url, Map<String, String> params, Map<String, String> header, String charset) throws Exception {
        if (url != null && url.trim().length() != 0) {
            logger.debug("HTTP POST请求 URL:" + url + ", 参数:" + params + " ,编码:" + charset);

            try {
                List<NameValuePair> pairs = null;
                if (params != null && !params.isEmpty()) {
                    pairs = new ArrayList(params.size());
                    Iterator var5 = params.entrySet().iterator();

                    while(var5.hasNext()) {
                        Entry<String, String> entry = (Entry)var5.next();
                        String value = (String)entry.getValue();
                        if (value != null) {
                            pairs.add(new BasicNameValuePair((String)entry.getKey(), value));
                        }
                    }
                }

                HttpPost httpPost = new HttpPost(url);
                if (pairs != null && pairs.size() > 0) {
                    httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
                }

                setHeader(httpPost, header);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    httpPost.abort();
                    throw new ServiceExpection(statusCode, response.getStatusLine().getReasonPhrase());
                } else {
                    HttpEntity entity = response.getEntity();
                    String result = "";
                    if (entity != null) {
                        result = EntityUtils.toString(entity);
                    }

                    EntityUtils.consume(entity);

                    try {
                        response.close();
                    } catch (IOException var12) {
                        logger.error("关闭response失败:" + var12.getMessage(), var12);
                    }

                    return result;
                }
            } catch (Exception var13) {
                Exception e = var13;

                try {
                    throw e;
                } catch (IOException var11) {
                    logger.error(var11.toString(), var11);
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static String get(String url) {
        return get(url, (Map)null);
    }

    public static String get(String url, Map<String, String> header) {
        HttpGet httpGet = new HttpGet(url);
        setHeader(httpGet, header);

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            } else {
                HttpEntity entity = response.getEntity();
                String result = "";
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }

                EntityUtils.consume(entity);

                try {
                    response.close();
                } catch (IOException var8) {
                    logger.error("关闭response失败:" + var8.getMessage(), var8);
                }

                return result;
            }
        } catch (Exception var9) {
            throw new RuntimeException("GET请求到" + url + "失败:" + var9.getMessage(), var9);
        }
    }

    private static void setHeader(HttpRequestBase request, Map<String, String> headerMap) {
        if (headerMap != null && !headerMap.isEmpty()) {
            Iterator var2 = headerMap.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                request.setHeader(key, (String)headerMap.get(key));
            }
        }

    }

    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
        HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }
}
