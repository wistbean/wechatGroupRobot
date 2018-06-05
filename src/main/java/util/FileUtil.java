package util;

import java.io.*;

/**
 * Created by wistbean on 2018/6/2.
 * 文件工具类
 */
public class FileUtil {

    public static File saveFile(InputStream inputStream, String fileName) {

        OutputStream outputStream = null;
        File output = new File(fileName);

        try {
            outputStream = new FileOutputStream(output);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output;
    }
}
