package base.memcached;

import java.util.HashMap;

public class SettingsUtils {
    private static final String SETTINGS_GROUP = "settings";
    private static final String SETTINGS_GROUP_ALLKEY = "settings_all";

    public SettingsUtils() {
    }

    public static String get(String key) {
        String ret = "";

        try {
            Object storeVali = GroupMemcachedUtil.get("settings", "settings_all");
            if (storeVali != null) {
                HashMap<String, String> map = (HashMap)storeVali;
                if (map.containsKey(key)) {
                    ret = (String)map.get(key);
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return ret;
    }
}
