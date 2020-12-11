package com.inmaplewoods.test.getschoolinfomation;

import android.content.Context;
import android.os.Build;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExamRobot extends Robot {

    public ExamRobot(Context context) {
        super(context);
    }

    public ExamRobot(String data) {
        super(data);
    }

    /**
     * 获取当前学期时间
     *
     * @return 学期时间字符串
     */
    private String GetTime() {
//        return "2019-2020-1";
        Configuration configuration = GetConfig();
        if (configuration != null) {
            return configuration.time;
        }
        Calendar dateTime = Calendar.getInstance();
        int month = dateTime.get(Calendar.MONTH) + 1;
        int year = dateTime.get(Calendar.YEAR);
        String result;
        if (month <= 8) {
            result = (year - 1) + "-" + (year) + "-2";
        } else {
            result = (year) + "-" + (year + 1) + "-1";
        }
        return result;
    }

    /**
     * 处理考试信息
     *
     * @param examList 考试信息
     * @return 考试结果
     */
    private String handleExamInfo(String examList) {
        String examPattern = "座位号</th></tr>(.*)</table>";
        Pattern r = Pattern.compile(examPattern);
        Matcher m = r.matcher(examList);
        String examData = "";
        if (m.find()) {
            examData = m.group(1);
        } else {
            System.out.println("NO MATCH");
        }
        if (examData != null && !examData.equals("")) {
            examData = examData.replace("style=\"background-color:#8b8b8b69;\"", "");
            String classExamPattern = "<tr><td>(.{0,4})</td><tdalign=\"left\"style=\"max-width:100px;word-wrap:break-word;word-break:break-all;white-space:normal;\">(.{0,10})</td><tdalign=\"left\">(.{7,9})</td><tdalign=\"left\">(.{0,20})</td><td>(.{0,30})</td><td>(.{0,20})</td><td>(.{0,8})</td><td>(.{0,8})</td><tdstyle=\"max-width:100px;word-wrap:break-word;word-break:break-all;white-space:normal;\">(.{0,200})</td><td>(.{0,5})</td></tr>";
            r = Pattern.compile(classExamPattern);
            m = r.matcher(examData);
            StringBuilder tempExam = new StringBuilder();
            while (m.find()) {
                tempExam.append(m.group(4)).append("\r\n");
                tempExam.append(m.group(5)).append("\r\n");
                tempExam.append(m.group(6)).append("\r\n");
                if (!Objects.requireNonNull(m.group(9)).isEmpty()) {
                    tempExam.append("说明：").append(m.group(9)).append("\r\n");
                }
                tempExam.append("\r\n");
            }
            if (tempExam.length() == 0) {
                return "暂无考试信息";
            }
            return tempExam.toString();
        }
        return null;
    }

    @Override
    public boolean Init() {
        _FakeX509TrustManager.allowAllSSL();
        CookieManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            manager = new CookieManager();
            // 保存这个定制的CookieManager
            CookieHandler.setDefault(manager);
        }
        httpRequest.GetHttpText("https://jwxt.ncepu.edu.cn/");
        String data = httpRequest.PostHttpText("https://jwxt.ncepu.edu.cn/Logon.do?method=logon&flag=sess", "");
        String scode = data.split("#")[0];
        String sxh = data.split("#")[1];
        String code = account + "%%%" + password;
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            if (i < 20) {
                encoded.append(code.charAt(i)).append(scode.substring(0, Integer.parseInt(sxh.substring(i, i + 1))));
                scode = scode.substring(Integer.parseInt(sxh.substring(i, i + 1)));
            } else {
                encoded.append(code.substring(i));
                i = code.length();
            }
        }
        try {
            encoded = new StringBuilder(URLEncoder.encode(encoded.toString(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert manager != null;
        CookieStore cookieJar = manager.getCookieStore();
        String postData = "userAccount=" + account + "&userPassword=" + password + "&encoded=" + encoded;
        URL url;
        try {
            url = new URL("https://jwxt.ncepu.edu.cn/Logon.do?method=logon");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // 设置Content-Type
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3941.4 Safari/537.36");
            connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            // 设置是否向httpUrlConnection输出，post请求设置为true，默认是false
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            //重定向
            HttpURLConnection.setFollowRedirects(true);
            connection.setRequestProperty("Charset", "UTF-8");
            byte[] postdatabyte = postData.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(postdatabyte.length));
            // 设置RequestBody
            if (!postData.equals("")) {
                DataOutputStream stream = new DataOutputStream(connection.getOutputStream());
                stream.writeBytes(postData);
                stream.flush();
            } else {
                PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
                printWriter.write(postData);
                printWriter.flush();
            }
            int statusCode = connection.getResponseCode();
            if (statusCode == 302) {
                httpRequest.GetHttpText(connection.getHeaderField("Location"));
            } else {
                return false;
            }
            InputStream inputStream;
            statusCode = connection.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            // 用List获取cookie，因为cookie中可能包含多个信息
            // 这里我们就获取到了cookie，将其返回。
            cookieJar.getCookies();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String GetInfo() {
        if (Init()) {
            String url = "https://jwxt.ncepu.edu.cn/jsxsd/xsks/xsksap_list";
            String postData = "xqlbmc=&xnxqid=" + GetTime() + "&kc=&ksjs=&jkls=";
            String ResponseGet = httpRequest.PostHttpText(url, postData);
            String examList = ResponseGet.replace("\r\n", "").replace(" ", "").replace("\t", "").replace("&nbsp;", "");
            return handleExamInfo(examList);
        } else {
            return "登录失败";
        }
    }
}
