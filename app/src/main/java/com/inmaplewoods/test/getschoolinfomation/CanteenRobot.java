package com.inmaplewoods.test.getschoolinfomation;

import android.content.Context;
import com.google.gson.Gson;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.List;

class CanteenDatas {
    private List<CanteenData> data;
    private int max;
    private String time;

    public List<CanteenData> getData() {
        return data;
    }

    public void setData(List<CanteenData> data) {
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

class CanteenData {
    private int over;
    private int red;
    private int cur;
    private int yellow;
    private String id;
    private int state;
    private String title;
    public void setOver(int over) {
        this.over = over;
    }
    public int getOver() {
        return over;
    }

    public void setRed(int red) {
        this.red = red;
    }
    public int getRed() {
        return red;
    }

    public void setCur(int cur) {
        this.cur = cur;
    }
    public int getCur() {
        return cur;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }
    public int getYellow() {
        return yellow;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setState(int state) {
        this.state = state;
    }
    public int getState() {
        return state;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
public class CanteenRobot extends Robot {

    public CanteenRobot(Context context) {
        super(context);
    }

    public CanteenRobot(String data) {
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
            String data = httpRequest.GetHttpText("http://bdhq.ncepu.edu.cn/ncepucenter/house/wx/threaded_groupData.json?type=2");
            CanteenDatas canteenDatas = new Gson().fromJson(data, CanteenDatas.class);
            StringBuilder result = new StringBuilder();
            for (CanteenData d : canteenDatas.getData()) {
                result.append(d.getTitle()).append("  ").append(d.getCur()).append("/").append(d.getOver()).append("\r\n");
            }
            return result.toString();
        } else {
            return null;
        }
    }
}
