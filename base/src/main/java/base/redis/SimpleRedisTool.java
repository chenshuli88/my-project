package base.redis;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 15:14
 * @description
 * @since 2.8.1
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import redis.clients.jedis.Jedis;

public class SimpleRedisTool {
    public SimpleRedisTool() {
    }

    public void set(String key, String value) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            jedis.set(key, value);
            RedisClient.closeJedis(jedis);
        }

    }

    public void set(String key, String value, long maxAgeSeconds) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            jedis.set(key, value, "NX", "EX", maxAgeSeconds);
            RedisClient.closeJedis(jedis);
        }

    }

    public String get(String key) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            String s = jedis.get(key);
            RedisClient.closeJedis(jedis);
            return s;
        } else {
            return null;
        }
    }

    public void del(String key) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            jedis.del(key);
            RedisClient.closeJedis(jedis);
        }

    }

    public void rpush(String key, String str) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            jedis.rpush(key, new String[]{str});
            RedisClient.closeJedis(jedis);
        }

    }

    public void rpush(String key, List<String> list) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
                String str = (String)var4.next();
                jedis.rpush(key, new String[]{str});
            }

            RedisClient.closeJedis(jedis);
        }

    }

    public String rpop(String key) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            String rpop = jedis.rpop(key);
            RedisClient.closeJedis(jedis);
            return rpop;
        } else {
            return null;
        }
    }

    public boolean exists(String key) {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            Boolean exists = jedis.exists(key);
            RedisClient.closeJedis(jedis);
            return exists.booleanValue();
        } else {
            return false;
        }
    }

    public int count(String key) {
        Jedis jedis = RedisClient.getJedis();
        int count = 0;
        if (jedis != null) {
            count = jedis.zcard(key).intValue();
            RedisClient.closeJedis(jedis);
        }

        return count;
    }

    public void setObject(String key, Object value) throws UnsupportedEncodingException {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            jedis.set(key.getBytes("UTF-8"), JSONObject.toJSONString(value).getBytes("UTF-8"));
            RedisClient.closeJedis(jedis);
        }

    }

    public Object getObject(String key) throws UnsupportedEncodingException {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            Object parse = JSONObject.parse(new String(jedis.get(key.getBytes("UTF-8")), "UTF-8"));
            RedisClient.closeJedis(jedis);
            return parse;
        } else {
            return null;
        }
    }

    public <T> T getObject(String key, Class<T> clazz) throws UnsupportedEncodingException {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            byte[] results = jedis.get(key.getBytes());
            if (null != results && results.length != 0) {
                T t = JSONObject.parseObject(new String(jedis.get(key.getBytes("UTF-8")), "UTF-8"), clazz);
                RedisClient.closeJedis(jedis);
                return t;
            }

            RedisClient.closeJedis(jedis);
        }

        return null;
    }

    public <T> T getObjectOfSort(String key, Class<T> clazz) throws UnsupportedEncodingException {
        Jedis jedis = RedisClient.getJedis();
        if (jedis != null) {
            byte[] results = jedis.get(key.getBytes());
            if (null != results && results.length != 0) {
                T t = JSON.parseObject(new String(jedis.get(key.getBytes("UTF-8")), "UTF-8"), new TypeReference<T>() {
                }, new Feature[]{Feature.OrderedField});
                RedisClient.closeJedis(jedis);
                return t;
            }

            RedisClient.closeJedis(jedis);
        }

        return null;
    }
}
