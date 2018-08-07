package base.memcached;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 15:06
 * @description
 * @since 2.8.1
 */
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import net.rubyeye.xmemcached.Counter;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.WhalinTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemcachedUtil {
    private static Logger logger = LoggerFactory.getLogger(MemcachedUtil.class);
    private static MemcachedClient mcc = null;

    public MemcachedUtil() {
    }

    public static void init() {
        Properties properties = new Properties();

        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("memcache.properties"));
            String servers = properties.getProperty("memcache.serverlist");
            String[] weights = properties.getProperty("memcache.weights", "1").split(",");
            int[] ws = new int[weights.length];

            for(int i = 0; i < weights.length; ++i) {
                ws[i] = Integer.parseInt(weights[i]);
            }

            XMemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(servers), ws);
            builder.setSessionLocator(new KetamaMemcachedSessionLocator());
            builder.setTranscoder(new WhalinTranscoder());
            builder.setConnectionPoolSize(Integer.parseInt(properties.getProperty("memcache.initCon", "5")));
            builder.setConnectTimeout(3000L);
            mcc = builder.build();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public static void closePool() throws IOException {
        mcc.shutdown();
        logger.info("Memcached pool closed");
    }

    public static boolean set(String key, Serializable obj) {
        try {
            return mcc.set(key, 0, obj);
        } catch (Exception var3) {
            logger.error("Pool set error!");
            var3.printStackTrace();
            return false;
        }
    }

    public static boolean set(String key, Serializable obj, long time) {
        try {
            return mcc.set(key, (int)(time / 1000L), obj);
        } catch (Exception var5) {
            logger.error("Pool set error!");
            var5.printStackTrace();
            return false;
        }
    }

    public static void replace(String key, Serializable value, long cachelTime) {
        try {
            mcc.replace(key, (int)(cachelTime / 1000L), value);
        } catch (Exception var5) {
            logger.error(" pool set error!");
        }

    }

    public static Object get(String key) {
        Object result = null;

        try {
            result = mcc.get(key);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return result;
    }

    public static void setCounter(String key, long count) {
        try {
            mcc.set(key, 0, count);
        } catch (Exception var4) {
            logger.error("Pool setCounter error!");
        }

    }

    public static void addCounter(String key) {
        try {
            mcc.incr(key, 1L);
        } catch (Exception var2) {
            logger.error("Pool setCounter error!");
        }

    }

    public static void addCounter(String key, long addValue) {
        try {
            mcc.incr(key, addValue);
        } catch (Exception var4) {
            logger.error(" pool setCounter error!");
        }

    }

    public static long getCounter(String key) {
        try {
            Counter counter = mcc.getCounter(key);
            return counter.get();
        } catch (Exception var2) {
            logger.error(var2.getMessage());
            return -1L;
        }
    }

    public static boolean delete(String key) {
        try {
            return mcc.delete(key);
        } catch (Exception var2) {
            logger.error(var2.getMessage());
            return false;
        }
    }

    public static long deleteCounter(String key) {
        try {
            return mcc.decr(key, 1L);
        } catch (Exception var2) {
            logger.error(" pool setCounter error!");
            return 0L;
        }
    }

    public static boolean contains(String key) {
        try {
            return mcc.get(key) != null;
        } catch (Exception var2) {
            logger.error(" pool setCounter error: {}", var2.getMessage());
            return true;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        closePool();
    }

    static {
        init();
    }
}
