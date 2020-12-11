package com.inmaplewoods.test.getschoolinfomation;

public interface IRobot {
    public interface IGetInfo{
        String GetInfo();
    }
    public boolean Init();
    public String GetInfomationThread(IGetInfo iGetInfo);
    public String GetInfomation();
}
