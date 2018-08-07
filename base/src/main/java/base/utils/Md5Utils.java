package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 14:47
 * @description
 * @since 2.8.1
 */
import java.security.MessageDigest;

public class Md5Utils {
    private static final String ALGORITHM = "MD5";
    private static MessageDigest md;

    private Md5Utils() {
    }

    private static char[] hexDump(byte[] src) {
        char[] buf = new char[src.length * 2];

        for(int b = 0; b < src.length; ++b) {
            String byt = Integer.toHexString(src[b] & 255);
            if (byt.length() < 2) {
                buf[b * 2 + 0] = '0';
                buf[b * 2 + 1] = byt.charAt(0);
            } else {
                buf[b * 2 + 0] = byt.charAt(0);
                buf[b * 2 + 1] = byt.charAt(1);
            }
        }

        return buf;
    }

    public static void smudge(char[] pwd) {
        if (pwd != null) {
            for(int b = 0; b < pwd.length; ++b) {
                pwd[b] = 0;
            }
        }

    }

    public static void smudge(byte[] pwd) {
        if (pwd != null) {
            for(int b = 0; b < pwd.length; ++b) {
                pwd[b] = 0;
            }
        }

    }

    public static char[] cryptPassword(char[] pwd) throws Exception {
        if (md == null) {
            md = MessageDigest.getInstance("MD5");
        }

        md.reset();
        byte[] pwdb = new byte[pwd.length];

        for(int b = 0; b < pwd.length; ++b) {
            pwdb[b] = (byte)pwd[b];
        }

        char[] crypt = hexDump(md.digest(pwdb));
        smudge(pwdb);
        return crypt;
    }
}
