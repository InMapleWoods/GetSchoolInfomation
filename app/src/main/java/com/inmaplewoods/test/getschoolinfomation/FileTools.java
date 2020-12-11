package com.inmaplewoods.test.getschoolinfomation;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 文件操作工具类
 */
public class FileTools {

    /**
     * 上下文
     */
    private final Context _context;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public FileTools(Context context) {
        _context = context;
    }

    /**
     * 判断是否存在配置文件
     *
     * @return 存在与否
     */
    boolean isExist() {
        String sdCardDir = GetPath();
        String filePath = sdCardDir + "/GetSchoolInfomation/";
        String fileName = "data.txt";
        File file = new File(filePath + fileName);
        return file.exists();
    }

    /**
     * 将文本写入文件
     *
     * @param text 待写入文本
     */
    void writeData(String text) {
        deleteData();
        String sdCardDir = GetPath();
        String filePath = sdCardDir + "/GetSchoolInfomation/";
        String fileName = "data.txt";
        writeTxtToFile(text, filePath, fileName);
    }

    /**
     * 从文件读取文本
     *
     * @return 文本
     */
    String readData() {
        String sdCardDir = GetPath();
        String filePath = sdCardDir + "/GetSchoolInfomation/data.txt";
        File file = new File(filePath);
        return getFileContent(file);
    }

    /**
     * 删除文本文件
     *
     * @return 删除成功与否
     */
    boolean deleteData() {
        String sdCardDir = GetPath();
        String filePath = sdCardDir + "/GetSchoolInfomation/data.txt";
        File file = new File(filePath);
        return deleteSingleFile(file);
    }

    /**
     * 将字符串写入到文本文件中
     *
     * @param strcontent 要储存的文本
     * @param filePath   文件位置
     * @param fileName   文件名
     */
    private void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                Objects.requireNonNull(file.getParentFile()).mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    /**
     * 生成文件
     *
     * @param filePath 文件位置
     * @param fileName 文件名
     * @return 文件
     */
    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 生成文件夹
     *
     * @param filePath 文件地址
     */
    private static void makeRootDirectory(String filePath) {
        File file;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    /**
     * 读取指定目录下的所有TXT文件的文件内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private String getFileContent(File file) {
        String content = "";
        if (file.exists()) {
            if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
                if (file.getName().endsWith("txt")) {//文件格式为""文件
                    try {
                        InputStream instream = new FileInputStream(file);
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, StandardCharsets.UTF_8);
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();//关闭输入流
                    } catch (java.io.FileNotFoundException e) {
                        Log.d("TestFile", "The File doesn't not exist.");
                    } catch (IOException e) {
                        Log.d("TestFile", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }
        }
        return content;
    }

    /**
     * 删除单个文件
     *
     * @param file 要删除的文件
     * @return 单个文件删除成功返回true，否则返回false
     */
    private boolean deleteSingleFile(File file) {
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 获取环境存储根地址
     *
     * @return 根地址
     */
    private String GetPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Objects.requireNonNull(_context.getExternalFilesDir(null)).getAbsolutePath();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }
}
