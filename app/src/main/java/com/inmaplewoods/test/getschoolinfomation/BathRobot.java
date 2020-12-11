package com.inmaplewoods.test.getschoolinfomation;

import android.content.Context;
import com.google.gson.Gson;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.List;

class BathDatas {

    private List<BathData> data;
    private int max;
    private String time;

    public List<BathData> getData() {
        return data;
    }

    public void setData(List<BathData> data) {
        this.data = data;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

class BathData {

    private int over;
    private int red;
    private int cur;
    private int yellow;
    private int state;
    private String title;

    public int getOver() {
        return over;
    }

    public void setOver(int over) {
        this.over = over;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getCur() {
        return cur;
    }

    public void setCur(int cur) {
        this.cur = cur;
    }

    public int getYellow() {
        return yellow;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

public class BathRobot extends Robot {

    public BathRobot(Context context) {
        super(context);
    }

    public BathRobot(String data) {
        super(data);
    }

    @Override
    public boolean Init() {
        _FakeX509TrustManager.allowAllSSL();
        CookieManager manager = new CookieManager();
        // 保存这个定制的CookieManager
        CookieHandler.setDefault(manager);
        String login = httpRequest.PostHttpText("http://bdhq.ncepu.edu.cn/ncepucenter/weixinlogin.json", "reg_name=" + account + "&password=" + password);
        return login != null && login.length() > 0;
    }

    @Override
    public String GetInfo() {
        if (Init()) {
            String data = httpRequest.GetHttpText("http://bdhq.ncepu.edu.cn/ncepucenter/house/wx/threaded_groupData.json?type=3");
            BathDatas bathDatas = new Gson().fromJson(data, BathDatas.class);
            StringBuilder result = new StringBuilder();
            for (BathData d : bathDatas.getData()) {
                result.append(d.getTitle()).append("  ").append(d.getCur()).append("/").append(d.getOver()).append("\r\n");
            }
            return result.toString();
        } else {
            return null;
        }
    }
}
