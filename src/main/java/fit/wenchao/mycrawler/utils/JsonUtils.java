package fit.wenchao.mycrawler.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

import java.util.List;

public class JsonUtils {

    public static <T> List<T> getList(String json, String path, Class<T> clazz) {
        JSONArray read = ((JSONArray) JSONPath.read(json, path));
        return JSONArray.parseArray(read.toJSONString(), clazz);
    }

    public static <T> T getObject(String json, String path, Class<T> clazz) {
        JSONObject read = ((JSONObject) JSONPath.read(json, path));
        return JSONObject.parseObject(read.toJSONString(), clazz);
    }

    public static JSONObject getObject(String json, String path) {
        JSONObject read = ((JSONObject) JSONPath.read(json, path));
        return read;
    }
}
