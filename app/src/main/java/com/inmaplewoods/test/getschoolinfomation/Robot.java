package com.inmaplewoods.test.getschoolinfomation;

import android.content.Context;
import com.google.gson.Gson;

public abstract class Robot implements IRobot {
    final HttpRequest httpRequest = new HttpRequest();
    Context context;
    String account = "";
    String password = "";

    public Robot(Context context) {
        _FakeX509TrustManager.allowAllSSL();
        this.context = context;
        String data = new FileTools(context).readData();
        if (data != null && !data.equals("")) {
            if (data.split("#").length > 1) {
                new FileTools(context).deleteData();
            } else {
                Configuration configuration = GetConfig();
                if (configuration != null) {
                    account = configuration.user.Account;
                    password = configuration.user.Password;
                }
            }
        }
    }

    public Robot(String data) {
        if (data != null && !data.equals("")) {
            data = data.replace("\r", "");
            data = data.replace("\n", "");
            account = data.split("#")[0];
            password = data.split("#")[1];
        }
    }

    public Configuration GetConfig() {
        String data = new FileTools(context).readData();
        if (data != null && !data.equals("")) {
            if (data.split("#").length > 1) {
                new FileTools(context).deleteData();
            } else {
                return new Gson().fromJson(data, Configuration.class);
            }
        }
        return null;
    }

    public abstract boolean Init();

    public class ThreadInfo extends Thread{
        public String info = "";

        public IGetInfo iGetInfo;
        public ThreadInfo(IGetInfo iGetInfo){
            this.iGetInfo=iGetInfo;
        }
        public ThreadInfo(){}

        @Override
        public void run() {
            synchronized (this) {
                info = iGetInfo.GetInfo();
                //(完成计算了)唤醒在此对象监视器上等待的单个线程，在本例中线程ThreadInteractionTest被唤醒
                notify();
            }

        }
    }

    public String GetInfomationThread(IGetInfo iGetInfo) {
        String httpText;
        ThreadInfo text = new ThreadInfo(iGetInfo);
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

    public String GetInfomation() {
        return GetInfomationThread(()-> GetInfo());
    }

    public abstract String GetInfo();
}
