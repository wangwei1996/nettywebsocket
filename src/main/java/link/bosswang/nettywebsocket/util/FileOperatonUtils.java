package link.bosswang.nettywebsocket.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 保存文件工具类
 */
public class FileOperatonUtils {

    private static final Logger log = LoggerFactory.getLogger(FileOperatonUtils.class);

    /**
     * 将文件保存到本地磁盘(现在是保存到webapp/files下)
     *
     * @param name 文件名
     * @param path 文件保存路径
     * @param in   文件输入流
     * @return 文件名
     */
    public static String saveFile(String name, String path, InputStream in) {
        FileOperatonUtils.mkdirPath(path);

        File file = new File(path + name);

        OutputStream out = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buff = new byte[4096];
            int count = 0;
            while ((count = in.read(buff)) > 0) {
                out.write(buff, 0, count);
            }
        } catch (FileNotFoundException e) {
            FileOperatonUtils.log.error("文件保存异常: " + e.getMessage());
        } catch (IOException e) {
            FileOperatonUtils.log.error("文件保存异常: " + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    FileOperatonUtils.log.error("文件关闭异常: " + e.getMessage());
                }
            }
        }

        return name;
    }

    /**
     * 递归产生保存文件的文件夹
     *
     * @param path
     */
    private static void mkdirPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}

