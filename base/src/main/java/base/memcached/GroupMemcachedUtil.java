package base.memcached;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 15:09
 * @description
 * @since 2.8.1
 */
import java.io.Serializable;

public class GroupMemcachedUtil {
    public static final String GROUP_PREFIX = "@group_";
    public static final String SPLIT_CHAR = "_";

    public GroupMemcachedUtil() {
    }

    public static Serializable get(String groupName, String key) {
        return (Serializable)MemcachedUtil.get(groupName + "_" + key);
    }

    public static void remove(String groupName, String key) {
        MemcachedUtil.delete(groupName + "_" + key);
    }

    public static void store(String groupName, String key, Serializable value, long time) {
        MemcachedUtil.set(groupName + "_" + key, value, time);
    }

    public static void store(String groupName, String key, Serializable value) {
        store(groupName, key, value, -1L);
    }

    public static boolean contains(String groupName, String key) {
        return MemcachedUtil.contains(groupName + "_" + key);
    }

    public static void main(String[] args) {
        String name = (String)get("groupName", "name");
        System.out.println("name = " + name);
    }
}
