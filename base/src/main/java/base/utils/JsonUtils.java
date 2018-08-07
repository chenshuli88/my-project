package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 14:31
 * @description
 * @since 2.8.1
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

public final class JsonUtils {
    public JsonUtils() {
    }

    public static String toJSONString(Object bean) {
        return JSON.toJSONString(bean);
    }

    public static String toJSONString(List beanlist) {
        return JSONArray.toJSONString(beanlist);
    }

    public static String toJSONString(Map map) {
        return JSON.toJSONString(map);
    }

    public static <T> T toBean(String jsonstr, Class<T> clazz) {
        return JSON.parseObject(jsonstr, clazz);
    }

    public static <T> List<T> toList(String jsonstr, Class<T> clszz) {
        return JSONArray.parseArray(jsonstr, clszz);
    }

    public static Map toMap(String jsonstr) {
        return (Map)JSON.parseObject(jsonstr, Map.class);
    }
}
