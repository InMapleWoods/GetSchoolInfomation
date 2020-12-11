package com.inmaplewoods.test.getschoolinfomation;

/**
 * 配置类
 */
public class Configuration {
    /**
     * 用户
     */
    public final User user = new User();

    /**
     * 应用配置
     */
    public final AppConfig appConfig = new AppConfig();

    /**
     * 小程序配置
     */
    public final WidgetConfig widgetConfig = new WidgetConfig();

    /**
     * 学期
     */
    public String time = "";

    /**
     * 是否开启通知栏常驻
     */
    public boolean isOnGoing = false;
}

/**
 * 用户类
 */
class User {
    public String Account;
    public String Password;
}

/**
 * 配置类
 */
class Config {
    public enum PageChoose {
        GradePage,
        ExamPage,
        NewsPage,
        BathPage,
        CanteenPage
    }
}

/**
 * 应用配置类
 */
class AppConfig extends Config {
    public PageChoose choose;
}

/**
 * 小部件配置类
 */
class WidgetConfig extends Config {

    public PageChoose choose;

    public int backgroundColor;

    public int textColor;
}