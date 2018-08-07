package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 14:49
 * @description
 * @since 2.8.1
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.Executors.*;

public class VideoUtils {
    private static Logger logger = LoggerFactory.getLogger(VideoUtils.class);
    public static String ffmpegPath = "ffmpeg";

    public VideoUtils() {
    }

    public void setFfmpegPath(String ffmpegPath) {
        ffmpegPath = ffmpegPath;
    }

    public static void toFlv(File source, File flvFile) throws IOException {
        String common = String.format(ffmpegPath + " -i \"%s\" -y -ab 32 -ar 22050 -b 800000  %s", source.toString(), flvFile.toString());
        logger.debug(common);

        try {
            Runtime.getRuntime().exec(common);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void toMp4(final File source, final File targetFile) throws IOException {
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                VideoUtils.logger.info("开始转换视频");
                String common = String.format(VideoUtils.ffmpegPath + " -i %s -y  -vcodec libx264 -ab 32 -ar 22050 -b 800000 -vpre libx264-default  %s", source.toString(), targetFile.toString());
                VideoUtils.logger.debug(common);
                Process process = null;

                try {
                    process = Runtime.getRuntime().exec(common);
                } catch (IOException var5) {
                    var5.printStackTrace();
                }

                VideoUtils.doWaitFor(process);

                try {
                    process.waitFor();
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }

                process.destroy();
            }
        });
    }

    public static int doWaitFor(Process p) {
        InputStream in = null;
        InputStream err = null;
        int exitValue = -1;

        try {
            logger.debug("开始等待执行处理");
            in = p.getInputStream();
            err = p.getErrorStream();
            boolean finished = false;

            while(!finished) {
                try {
                    Character c;
                    while(in.available() > 0) {
                        c = new Character((char)in.read());
                        logger.info(c.toString());
                    }

                    while(err.available() > 0) {
                        c = new Character((char)err.read());
                        logger.info(c.toString());
                    }

                    exitValue = p.exitValue();
                    p.waitFor();
                    finished = true;
                } catch (IllegalThreadStateException var19) {
                    Thread.currentThread();
                    Thread.sleep(500L);
                }
            }
        } catch (Exception var20) {
            logger.debug("执行doWaitFor()失败;" + var20.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException var18) {
                logger.debug(var18.getMessage());
            }

            if (err != null) {
                try {
                    err.close();
                } catch (IOException var17) {
                    logger.debug(var17.getMessage());
                }
            }

        }

        return exitValue;
    }

    public static void toThumbnail(File source, File thumbnailFile) throws IOException {
        logger.info("视频缩略图开始处理");
        String command = String.format(ffmpegPath + " -i %s -y -f image2 -t 0.001 %s", source.toString(), thumbnailFile.toString());
        logger.debug("运行命令:" + command);
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
            doWaitFor(process);
            process.waitFor();
            process.destroy();
        } catch (IOException var5) {
            var5.printStackTrace();
        } catch (InterruptedException var6) {
            var6.printStackTrace();
        }

    }

    public List<File> getThumbnails(File flvFile) {
        return null;
    }

    public static String[] getVideoInfo(File videoFile) throws IOException, InterruptedException {
        boolean isWindow = System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > 0;
        String common;
        if (isWindow) {
            common = String.format(ffmpegPath + " -i \"" + videoFile.getPath() + "\"");
        } else {
            common = String.format(ffmpegPath + " -i " + videoFile.getPath() + "");
        }

        Process process = Runtime.getRuntime().exec(common);
        BufferedReader bufferedReader = null;

        String[] videoInfo;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            videoInfo = new String[2];

            String line;
            while((line = bufferedReader.readLine()) != null) {
                logger.debug(line);
                String resolution;
                if (line.contains("Duration:")) {
                    resolution = line.replaceFirst(".*(\\d{2}:\\d{2}:\\d{2}).\\d{2,3}.*", "$1");
                    videoInfo[0] = resolution;
                } else if (line.contains("Video:")) {
                    resolution = line.replaceAll(".*[^\\d](\\d+x\\d+).*", "$1");
                    videoInfo[1] = resolution;
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

        }

        return videoInfo;
    }
}
