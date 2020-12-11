package com.inmaplewoods.test.getschoolinfomation;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoticeRobot extends Robot {

    public NoticeRobot(Context context) {
        super(context);
    }

    public NoticeRobot(String data) {
        super(data);
    }

    @Override
    public boolean Init() {
        return false;
    }

    @Override
    public String GetInfo() {
        String result = httpRequest.GetHttpText("https://jw.ncepu.edu.cn/jiaowuchu");
        if (result == null || result.length() <= 0)
            return null;
        result = result.replaceAll("\\r\\n", "");
        result = result.replaceAll("list-group-item highlighted", "list-group-item ");
        String ResponseGetPattern = "<div class=\"panel-body infopanel\">        <ul class=\"list-group\">                (.*)        </ul>    </div></div><div class=\"panel panel-default panel-infolist\">    <div class=\"panel-heading\">        <span class=\"glyphicon glyphicon-th-list\"></span>        <span class=\"panel-heading-title\">教务新闻</span>";
        Pattern r = Pattern.compile(ResponseGetPattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(result);
        String Data = "";
        if (m.find()) {
            Data = m.group(1);
        } else {
            System.out.println("NO MATCH");
        }
        assert Data != null;
        if (!Data.equals("")) {
            ResponseGetPattern = "<span>(.{0,70})</span>";
            r = Pattern.compile(ResponseGetPattern);
            m = r.matcher(Data);
            StringBuilder tempNotice = new StringBuilder();
            while (m.find()) {
                tempNotice.append(m.group(1)).append("\r\n");
            }
            return tempNotice.toString();
        }
        return null;
    }
}
