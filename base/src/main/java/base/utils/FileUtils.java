package base.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class FileUtils extends org.apache.commons.io.FileUtils {
    public FileUtils() {
    }

    public static void fileWrite(String fileFullName, String fileContent, boolean append) throws IOException {
        File f = new File(fileFullName);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        fileWrite(f, fileContent, append);
    }

    public static void fileWrite(File fileFullName, String fileContent, boolean append) throws IOException {
        FileWriter writer = null;

        try {
            writer = new FileWriter(fileFullName, append);
            writer.write(fileContent);
            writer.flush();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    writer = null;
                } catch (IOException var10) {
                    ;
                }
            }

        }

    }

    public static void fileWrite(String fileFullName, byte[] fileContent, boolean append) throws IOException {
        fileWrite(new File(fileFullName), fileContent, append);
    }

    public static void fileWrite(File fileFullName, byte[] fileContent, boolean append) throws IOException {
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(fileFullName, append);
            outputStream.write(fileContent);
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (Exception var10) {
                    ;
                }
            }

        }

    }

    public static void fileWrite(File path, String fileName, String content, boolean append) throws IOException {
        if (!path.exists() || !path.isDirectory()) {
            path.mkdirs();
        }

        File file = new File(path, fileName);
        fileWrite(file.getPath(), content, append);
    }

    public static void fileWrite(File path, String filename, byte[] data, boolean append) throws IOException {
        FileOutputStream fos = null;

        try {
            if (!path.exists()) {
                path.mkdirs();
            }

            fos = new FileOutputStream(new File(path, filename));
            fos.write(data);
            fos.close();
        } finally {
            if (fos != null) {
                fos.close();
            }

        }

    }

    public static String getUrlContent(String path) {
        String rtn = "";

        try {
            if (path.indexOf("http://") == -1) {
                path = "http://" + path;
            }

            URL l_url = new URL(path);
            HttpURLConnection l_connection = (HttpURLConnection)l_url.openConnection();
            l_connection.setRequestProperty("User-agent", "Mozilla/4.0");
            l_connection.connect();

            InputStream l_urlStream;
            byte[] b;
            for(l_urlStream = l_connection.getInputStream(); l_urlStream.read() != -1; rtn = rtn + new String(b, "UTF-8")) {
                int all = l_urlStream.available();
                b = new byte[all];
                l_urlStream.read(b);
            }

            l_urlStream.close();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return rtn;
    }

    public static String[] getImgStr(String htmlStr) {
        String img = "";
        String tmp = "";
        String regEx_img = "<img\\s+[^>]*src=['\"]?([^'\"\\s>]*)['\"]?\\s*[^>]*>";
        Pattern p_image = Pattern.compile(regEx_img, 2);

        for(Matcher m_image = p_image.matcher(htmlStr); m_image.find(); img = img + "," + m_image.group(1)) {
            ;
        }

        if (img.indexOf(",") >= 0) {
            String images = img.substring(1);
            return images.split(",");
        } else {
            return null;
        }
    }

    public static String byteCountToDisplaySize(long size, int precision) {
        String displaySize;
        if (size / 1073741824L > 0L) {
            displaySize = size / 1073741824L + " GB";
        } else if (size / 1048576L > 0L) {
            BigDecimal bd = new BigDecimal((double)size / 1048576.0D);
            double filesize = bd.setScale(precision, 4).doubleValue();
            String size2 = String.valueOf(filesize);
            if (size2.indexOf(".") == -1) {
                size2 = size2 + ".00";
            } else {
                for(int i = 0; i < precision - size2.substring(size2.indexOf(".") + 1).trim().length(); ++i) {
                    size2 = size2 + "0";
                }
            }

            displaySize = size2 + " MB";
        } else if (size / 1024L > 0L) {
            displaySize = size / 1024L + " KB";
        } else {
            displaySize = size + " bytes";
        }

        return displaySize;
    }

    public static String detectFileEncoding(String file) throws IOException {
        FileInputStream in = openInputStream(new File(file));

        String encode;
        try {
            int head = (in.read() << 8) + in.read();
            encode = null;
            switch(head) {
                case 0:
                    throw new IOException("empty file : " + file);
                case 61371:
                    encode = "UTF-8";
                    break;
                case 65279:
                    encode = "Unicode";
                    break;
                case 65534:
                    encode = "Unicode";
                    break;
                default:
                    encode = "GBK";
            }
        } finally {
            IOUtils.closeQuietly(in);
        }

        return encode;
    }

    public static String getExt(String fileName) {
        return fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }

}
