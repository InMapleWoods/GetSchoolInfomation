package com.inmaplewoods.test.getschoolinfomation;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testHttp() {
//        Robot robot = new Robot("201709001013#llf99723");
//        List<String> strings = robot.GetInfo();
//        if (strings != null) {
//            for (String s : strings) {
//                System.out.println(s);
//            }
//        }
    }

    @Test
    public void test2() {
       // Robot robot = new Robot("201709001015#Lqy2105777524");
       // GradeRobot robot = new GradeRobot("201709001013#llf99723");
//        System.out.println(robot.GetInfo(false));
//        System.out.println(Arrays.toString(robot.GetTimes()));
        //    String a=robot.PostHttpText("https://jwxt.ncepu.edu.cn/Logon.do?method=logon&flag=sess",false,"");
    }

    @Test
    public void testBath(){
//        BathSome bathSome=new BathSome("201709001013#llfllf");
//        System.out.println(bathSome.GetBathTime());
    }
    @Test
    public void testCanteen(){
//        CanteenSome canteenSome=new CanteenSome("201709001013#llfllf");
//        System.out.println(canteenSome.GetCanteenTime());
    }
}