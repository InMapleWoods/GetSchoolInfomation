package com.inmaplewoods.test.getschoolinfomation;

import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpRequest {
    /**
     * 获取http报文
     *
     * @return http报文
     */
    public String GetHttpText(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            InputStream inputStream;
            int statusCode = connection.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }
            // 返回结果-字节输入流转换成字符输入流，控制台输出字符
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "error";
    }

    /**
     * 获取http报文
     *
     * @param urlStr  URL
     * @param cookies Cookies
     * @return http报文
     */
    public String GetHttpText(String urlStr, List<HttpCookie> cookies) {
        URL url;
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            InputStream inputStream;
            int statusCode = connection.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }
            // 返回结果-字节输入流转换成字符输入流，控制台输出字符
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "error";
    }

    /**
     * 发送Post请求
     *
     * @param urlStr   请求URL
     * @param postData 请求携带的数据
     * @return 返回报文
     */
    public String PostHttpText(String urlStr, String postData) {
        URL url;
        try {
            url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // 设置Content-Type
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置是否向httpUrlConnection输出，post请求设置为true，默认是false
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //重定向
            HttpURLConnection.setFollowRedirects(false);
            connection.setRequestProperty("Charset", "UTF-8");
            byte[] postdatabyte = postData.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(postdatabyte.length));
            // 设置RequestBody
            if (!postData.equals("")) {
                OutputStream stream = connection.getOutputStream();
                stream.write(postdatabyte, 0, postdatabyte.length);
                stream.flush();
            } else {
                PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
                printWriter.write(postData);
                printWriter.flush();
            }
            InputStream inputStream;
            int statusCode = connection.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }
            // 返回结果-字节输入流转换成字符输入流，控制台输出字符
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "error";
    }
}
