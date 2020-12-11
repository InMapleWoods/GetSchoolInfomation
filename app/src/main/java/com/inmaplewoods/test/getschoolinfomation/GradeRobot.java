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

public class GradeRobot extends Robot {
    public GradeRobot(Context context) {
        super(context);
    }

    public GradeRobot(String data) {
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
     * 处理成绩信息
     *
     * @param gradeList 成绩信息
     * @return 成绩结果
     */
    private String handleGradeInfo(String gradeList, boolean isRank) {
//        String gradePattern = ">操作</th></tr>(.*)</table><divid=\"PagingControl1";

        String gradePattern = ">课程性质</th></tr>(.*)</table></div><br/>";
        Pattern r = Pattern.compile(gradePattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(gradeList);
        String gradeData = "";
        if (m.find()) {
            gradeData = m.group(1);
        } else {
            System.out.println("NO MATCH");
        }
        if (gradeData != null && !gradeData.equals("")) {
            gradeData = gradeData.replaceAll("<ahref=\"javascript:openWindow\\((.{0,55})jx0404id=(\\d+)&(.{0,40})\\)\">", "jx0404id=$2;");
            gradeData = gradeData.replaceAll("<ahref=\"javascript:openWindow(.{0,200})\">", "");
            gradeData = gradeData.replaceAll("color:red;", "");
            gradeData = gradeData.replaceAll("<tdstyle=\"color:#8B8B8B\">", "<tdstyle=\"\">");
            String classGradePattern = "<tr><td>(.{1,2})</td><td>(.{10,20})</td><tdalign=\"left\">(.{5,15})</td>" +
                    "<tdalign=\"left\">(.{0,30})</td><!--控制成绩显示--><tdstyle=\"\">((jx0404id=(\\d+);(.{0,8}))|(.{0,4}))</a>(.{50,200})</tr>";
            r = Pattern.compile(classGradePattern);
            m = r.matcher(gradeData);
            StringBuilder tempGrade = new StringBuilder();
            while (m.find()) {
                tempGrade.append(padLeft(ToSBC(Objects.requireNonNull(m.group(4))), 13)).append(" ");
                String tempM = m.group(5);
                String temp = "jx0404id=(\\d+);(.{0,8})";
                Pattern rt = Pattern.compile(temp);
                // 现在创建 matcher 对象
                Matcher mt = rt.matcher(tempM);
                if (mt.find()) {
                    tempGrade.append(mt.group(2));
                    if (isRank) {
                         String rank = GetGradeRank(mt.group(1), mt.group(2));
                        if (!rank.isEmpty()) {
                            tempGrade.append(" ").append(rank);
                        }
                    }
                    tempGrade.append("\r\n");
                } else {
                    tempGrade.append(tempM);
                    tempGrade.append("\r\n");
                }
            }
            return tempGrade.toString().length() > 0 ? tempGrade.toString() : "暂无成绩";
        }
        return null;
    }

    /**
     * 半角符号转全角
     *
     * @param input 待转字符串
     * @return 目的字符串
     */
    private String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 文本左对齐
     *
     * @param src 源文本
     * @param len 目的文本长度
     * @return 目的文本
     */
    private String padLeft(String src, int len) {
        if (src != null && src.length() > 0) {
            int diff = len - src.length();
            if (diff <= 0) {
                return src;
            }

            char[] charr = new char[len];
            System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
            for (int i = src.length(); i < len; i++) {
                charr[i] = '\u3000';
            }
            return new String(charr);
        }
        return "";
    }

    private String GetGradeRank(String id, String grade) {

        //获取成绩排名
        String url = "https://jwxt.ncepu.edu.cn/jsxsd/xspj/xspj_ckpm.do?jx0404id=" + id
                + "&tktime=" + (Calendar.getInstance().getTimeInMillis());
        String ResponseGet = httpRequest.GetHttpText(url);
        String rankList = ResponseGet.replace("\r\n", "").replace(" ", "").replace("\t", "").replace("&nbsp;", "");
        String rankPattern = ">排名</th></tr>(.*)</table><divid";
        Pattern r = Pattern.compile(rankPattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(rankList);
        String rankData = "";
        if (m.find()) {
            rankData = m.group(1);
        } else {
            System.out.println("NO MATCH");
        }
        if (rankData != null && !rankData.equals("")) {
            int count = rankData.split("<tr>").length - 1;
            String classRankPattern = "<tr><td>((\\d{1,3})|(\\d{1,3}\\.\\d{1,2})|((优)|(良)|(中)|(及格)|(不及格)))</td><td>(\\d{1,3})</td></tr>";
            r = Pattern.compile(classRankPattern);
            m = r.matcher(rankData);
            while (m.find()) {
                String gradeStr = m.group(1);
                assert gradeStr != null;
                if (Pattern.matches("^(优)|(良)|(中)|(及格)|(不及格)$", gradeStr)) {
                    if (gradeStr.equals(grade)) {
                        int result;
                        try {
                            return Integer.parseInt(m.group(10)) + "/" + count;
                        } catch (Exception e) {
                            return "";
                        }
                    }
                } else {
                    try {
                        double rankGrade = Double.parseDouble(gradeStr);
                        if (rankGrade - Double.parseDouble(grade) <= 1e-6) {
                            int result;
                            try {
                                return Integer.parseInt(m.group(10)) + "/" + count;
                            } catch (Exception e) {
                                return "";
                            }
                        }
                    } catch (Exception e) {
                        return "";
                    }
                }
            }
        }
        return "";
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
        return null;
    }

    public String GetInfo(boolean isRank) {
        if (Init()) {
            //获取成绩
            String url = "https://jwxt.ncepu.edu.cn/jsxsd/kscj/cjcx_list";
            String postData = "kksj=" + GetTime() + "&kcxz=&kcmc=&xsfs=all";
            String ResponseGet = httpRequest.PostHttpText(url, postData);
            String gradeList = ResponseGet.replace("\r\n", "").replace(" ", "").replace("\t", "").replace("&nbsp;", "");
            return handleGradeInfo(gradeList, isRank);
        } else {
            return "登录失败";
        }
    }

    public String GetInfomationThread(boolean isRank) {
        String httpText;
        ThreadInfo text = new ThreadInfo() {
            @Override
            public void run() {
                synchronized (this) {
                    info = GetInfo(isRank);
                    //(完成计算了)唤醒在此对象监视器上等待的单个线程，在本例中线程ThreadInteractionTest被唤醒
                    notify();
                }

            }
        };
        // 启动计算线程
        text.start();
        // 线程ThreadInteractionTest拥有sum对象上的锁。
        // 线程为了调用wait()或notify()方法，该线程ThreadInteractionTest必须是那个对象锁的拥有者
        synchronized (text) {
            try {
                // 当前线程ThreadInteractionTest等待
                text.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            httpText = text.info;
        }
        return httpText;
    }

    public String GetInfomation(boolean isRank) {
        return GetInfomationThread(() -> GetInfo(isRank));
    }
}
