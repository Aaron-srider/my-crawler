package fit.wenchao.mycrawler.utils.http.httpSender;

import fit.wenchao.mycrawler.utils.http.HttpClientResponseWrapper;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fit.wenchao.mycrawler.utils.http.HttpUtils.getHttpClient;


public abstract class HttpSender {

    protected Map<String, String> paramMap;
    protected String url;
    protected Map<String, String> headerMap;
    protected String urlWithParam;

    public HttpSender(Map<String, String> paramMap, Map<String, String> headerMap, String url) {
        this.paramMap = paramMap;
        this.url = url;
        this.headerMap = headerMap;
    }

    /**
     * 从map中拼出get请求的字符串参数
     *
     * @param paramMap 封装了请求参数的map
     * @return 正常返回拼接好的参数串；否则返回null
     */
    protected static String getParamStr(Map<String, String> paramMap) {
        String paramStr = null;
        try {
            paramStr = EntityUtils.toString(getParamEntity(paramMap));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return paramStr;
    }

    protected static UrlEncodedFormEntity getParamEntity(Map<String, String> paramMap) {
        List<NameValuePair> params = new ArrayList<>();
        UrlEncodedFormEntity entity = null;
        try {
            for (String k : paramMap.keySet()) {
                String v = paramMap.get(k);
                params.add(new BasicNameValuePair(k, v));
            }
            entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 向httpReq中添加headers，如果headerMap中没有键值对，则方法do nothing
     *
     * @param httpReq 要添加headers的HttpRequest
     */
    protected void addHeaders(HttpRequestBase httpReq) {
        try {
            for (String k : headerMap.keySet()) {
                String v = headerMap.get(k);
                httpReq.addHeader(k, v);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpClientResponseWrapper send() {
        HttpRequestBase httpReq = getHttpReq();

        populateReq(httpReq);

        return doSend(httpReq);
    }

    /**
     * 将request发送，并返回响应
     *
     * @param httpReq 请求
     * @return 正常返回响应，否则返回null
     */
    protected HttpClientResponseWrapper doSend(HttpRequestBase httpReq) {
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        HttpClient httpClient = getHttpClient(basicCookieStore);
        //send
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpReq);
            return new HttpClientResponseWrapper(response, basicCookieStore);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 装配httpRequest，不同方法的Http请求有不同的装配方式，由相应的子类实现
     *
     * @param httpReq 待装配的请求
     */
    protected abstract void populateReq(HttpRequestBase httpReq);

    /**
     * 创建HttpRequest，不同方法的Http请求有不同的创建方式，由相应的子类实现
     *
     * @return 返回创建的HttpRequest对象
     */
    protected abstract HttpRequestBase getHttpReq();
}




