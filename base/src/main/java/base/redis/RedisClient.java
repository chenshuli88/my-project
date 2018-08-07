package base.redis;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 15:12
 * @description
 * @since 2.8.1
 */
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {
    private static Log logger = LogFactory.getLog(RedisClient.class);
    private static JedisPool pool;
    private static int timeout = 60000;
    private static String host;
    private static int port;
    private static int maxTotal = 800;
    private static int maxIdle = 400;
    private static long maxWaitMillis = 3000L;
    public static final int EXPIRE_TIME = 86400;

    private RedisClient() {
    }

    public static RedisClient getInstance() {
        return RedisClient.RedisUtilHolder.instance;
    }

    public static void initPool() {
        logger.info("Init Redis Pool [" + host + "]:[" + port + "]");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        pool = new JedisPool(config, host, port, timeout);
    }

    public static Jedis getJedis() {
        Jedis jedis = null;

        do {
            try {
                if (pool == null) {
                    initPool();
                }

                jedis = pool.getResource();
                jedis.select(1);
            } catch (Exception var4) {
                logger.error("get redis master1 failed!", var4);
                if (jedis != null) {
                    jedis.close();
                }

                try {
                    Thread.sleep(500L);
                } catch (Exception var3) {
                    ;
                }
            }
        } while(jedis == null);

        return jedis;
    }

    public static void closeJedis(Jedis jedis) {
        if (pool != null && jedis != null) {
            jedis.close();
        }

    }

    static {
        Properties properties = new Properties();

        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("redis.properties"));
            host = (String)properties.get("redis.serverhost");
            port = Integer.parseInt((String)properties.get("redis.serverport"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    private static class RedisUtilHolder {
        private static RedisClient instance = new RedisClient();

        private RedisUtilHolder() {
        }
    }
}
