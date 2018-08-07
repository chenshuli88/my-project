package base.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import base.memcached.SettingsUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Constant {
    static Properties prop = new Properties();
    protected static Log log = LogFactory.getLog(Constant.class);
    public static String UPLOAD_FILE_ROOT_PATH;
    public static final String FILE_UPLOAD_TYPE = ".png,.jpg,.jpeg,.gif";
    public static String UPLOAD_FILE_PRE_PATH;
    public static String UPLOAD_DIR;
    public static String UPLOAD_FILE_TMP_PATH;
    public static String UPLOAD_FILE_URL_PRE;
    public static final String ENGINEER_SESSION_KEY = "__sessionEngineer__";
    public static final String CHANNEL_SESSION_KEY = "__sessionchannel__";
    public static final String USER_SESSION_KEY = "__sessionUser__";
    public static final String REQUEST_USER_MAP_KEY = "_request_user_map_";
    public static final String USER_APP_LOGIN_KEY = "__appLoginInfo__";
    public static final String APP_USER_LOGIN_TOKEN = "__appLoginToken__";
    public static final String MQ_TOPIC_SURVEY = "TOPIC_COMMON_SURVEY";
    public static String WEIXIN_QCODE_URL;
    public static String WEIXIN_LOGIN_URL;
    public static String APP_INFO_URL = "http://apis.haoservice.com/lifeservice/AppleInfo";
    public static String WAP_ORDER_INFO_URL;
    public static String MY_ORDER;
    public static String ACCESS_TOKEN;
    public static String UPLOAD_FILE_POLICY;
    public static String CFG_PROJECT_APPLY_DEADLINE = "project_apply_deadline";

    public Constant() {
    }

    private static void init(Properties prop) {
        String weixinQcodeUrl = prop.getProperty("weixinQcodeUrl");
        if (weixinQcodeUrl != null) {
            WEIXIN_QCODE_URL = weixinQcodeUrl;
        }

        WEIXIN_LOGIN_URL = prop.getProperty("weixinLoginUrl");
        String uploadFileRootPath = prop.getProperty("uploadFileRootPath");
        if (uploadFileRootPath != null) {
            UPLOAD_FILE_ROOT_PATH = uploadFileRootPath;
        }

        String uploadFilePrePath = prop.getProperty("uploadFilePrePath");
        if (uploadFilePrePath != null) {
            UPLOAD_FILE_PRE_PATH = uploadFilePrePath;
        }

        UPLOAD_DIR = UPLOAD_FILE_ROOT_PATH + UPLOAD_FILE_PRE_PATH;
        String uploadFileTmpPath = prop.getProperty("uploadFileTmpPath");
        if (uploadFileTmpPath != null) {
            UPLOAD_FILE_TMP_PATH = uploadFileTmpPath;
        }

        UPLOAD_FILE_URL_PRE = SettingsUtils.get("RS_HOST");
        String wapOrderInfoUrl = prop.getProperty("wap_order_info_url");
        if (wapOrderInfoUrl != null) {
            WAP_ORDER_INFO_URL = wapOrderInfoUrl;
        }

        String myOrder = prop.getProperty("myOrder");
        if (myOrder != null && myOrder.length() != 0) {
            MY_ORDER = myOrder;
        }

        String accessToken = prop.getProperty("accessToken");
        if (accessToken != null && accessToken.length() != 0) {
            ACCESS_TOKEN = accessToken;
        }

        String uploadFilePolicy = prop.getProperty("uploadFilePolicy");
        if (uploadFilePolicy != null && uploadFilePolicy.length() != 0) {
            UPLOAD_FILE_POLICY = uploadFilePolicy;
        }

    }

    public static String getConfig(String key) {
        return prop.getProperty(key);
    }

    public static String getConfig(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    static {
        InputStream in = null;

        try {
            URL resource = Constant.class.getClassLoader().getResource("config.properties");
            String fullName = resource.getFile();
            File folder = (new File(fullName)).getParentFile();
            File[] var4 = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !"jdbc.properties".equals(name) && name.matches("^([\\w\\-_]+)\\.properties$");
                }
            });
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                File file = var4[var6];
                in = Constant.class.getClassLoader().getResourceAsStream(file.getName());
                prop.load(in);
                in.close();
            }

            init(prop);
        } catch (Exception var16) {
            log.error("初始化数据失败" + var16.getMessage(), var16);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var15) {
                    ;
                }
            }

        }

    }
}
