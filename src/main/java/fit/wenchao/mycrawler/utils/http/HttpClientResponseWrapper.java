package fit.wenchao.mycrawler.utils.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;


public class HttpClientResponseWrapper implements Closeable {

    private CookieStore cookieStore;

    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

    private CloseableHttpResponse response;

    public HttpClientResponseWrapper(CloseableHttpResponse response) {
        this.response = response;
    }

    public HttpClientResponseWrapper(CloseableHttpResponse response, CookieStore cookieStore) {
        this.response = response;
        this.cookieStore = cookieStore;
    }

    public boolean isOk() {
        return this.response.getStatusLine()
                            .getStatusCode() == 200;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public String getEntityAsString() throws IOException {
        if (response.getEntity() != null) {
            String content = EntityUtils.toString(response.getEntity(), "utf8");
            return content;
        }
        return null;
    }

    public JSONObject getJsonEntity() throws IOException {
        Header contentType = response.getEntity()
                                     .getContentType();
        String value = contentType.getValue();
        if (value.contains("application/json")) {
            String entityAsString = this.getEntityAsString();
            JSONObject jsonObject = JSONObject.parseObject(entityAsString);
            return jsonObject;
        }
        else {
            throw new IllegalStateException("can not get content as application/json, " + "the actual contentType is: " + value);
        }
    }


    @Override
    public void close() throws IOException {
        response.close();
    }

}