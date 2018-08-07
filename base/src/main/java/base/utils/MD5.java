package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 14:47
 * @description
 * @since 2.8.1
 */
import java.security.MessageDigest;

public class MD5 {
    public MD5() {
    }

    public static String md5(String str) {
        if (str == null) {
            return "";
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes());
                byte[] b = md.digest();
                StringBuffer buf = new StringBuffer("");

                for(int offset = 0; offset < b.length; ++offset) {
                    int i = b[offset];
                    if (i < 0) {
                        i += 256;
                    }

                    if (i < 16) {
                        buf.append("0");
                    }

                    buf.append(Integer.toHexString(i));
                }

                return buf.toString();
            } catch (Exception var6) {
                var6.printStackTrace();
                throw new RuntimeException("MD5加密失败:" + var6.getMessage(), var6);
            }
        }
    }
}
