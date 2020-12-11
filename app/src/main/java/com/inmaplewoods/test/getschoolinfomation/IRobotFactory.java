package com.inmaplewoods.test.getschoolinfomation;

public interface IRobotFactory {
    public enum RobotType{
        Notice,
        Grade,
        Exam,
        Bath,
        Canteen
    }
    public Robot CreateRobot(RobotType type);
}

