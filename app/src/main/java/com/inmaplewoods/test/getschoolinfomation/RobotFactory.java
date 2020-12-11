package com.inmaplewoods.test.getschoolinfomation;

import android.content.Context;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

public class RobotFactory implements IRobotFactory {

    private Context context;

    private String data;

    public RobotFactory(Context context) {
        this.context = context;
    }

    public RobotFactory(String data) {
        this.data = data;
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

    /**
     * 获取当前学期时间
     *
     * @return 学期时间字符串
     */
    public String[] GetTimes() {
        ArrayList<String> arrayList = new ArrayList<>();
        Calendar dateTime = Calendar.getInstance();
        dateTime.add(Calendar.MONTH, 24);
        for (int i = 0; i < 8; i++) {
            dateTime.add(Calendar.MONTH, dateTime.get(Calendar.MONTH) > 7 ? -7 : -5);
            int month = dateTime.get(Calendar.MONTH) + 1;
            int year = dateTime.get(Calendar.YEAR);
            String result;
            if (month <= 8) {
                result = (year - 1) + "-" + (year) + "-2";
            } else {
                result = (year) + "-" + (year + 1) + "-1";
            }
            arrayList.add(result);
            //System.out.println(result);
        }
        return arrayList.toArray(new String[0]);
    }

    @Override
    public Robot CreateRobot(RobotType type) {
        switch (type) {
            case Notice:
                return new NoticeRobot(context);
            case Grade:
                return new GradeRobot(context);
            case Exam:
                return new ExamRobot(context);
            case Bath:
                return new BathRobot(data);
            case Canteen:
                return new CanteenRobot(data);
            default:
                return null;
        }
    }

    public String GetData() {
        String data = new FileTools(context).readData();
        Robot notice=new RobotFactory(context).CreateRobot(IRobotFactory.RobotType.Notice);
        Robot grade=new RobotFactory(context).CreateRobot(IRobotFactory.RobotType.Grade);
        Robot exam=new RobotFactory(context).CreateRobot(IRobotFactory.RobotType.Exam);
        Robot bath=new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Bath);
        Robot canteen=new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Canteen);
        if (data != null && !data.equals("")) {
            Configuration configuration = new Gson().fromJson(data, Configuration.class);
            if (context.getClass().equals(MainActivity.class)) {
                switch (configuration.appConfig.choose) {
                    case ExamPage:
                        return exam.GetInfomation();
                    case NewsPage:
                        return notice.GetInfomation();
                    case GradePage:
                        return ((GradeRobot)grade).GetInfomation(true);
                    case BathPage:
                        return bath.GetInfomation();
                    case CanteenPage:
                        return canteen.GetInfomation();
                    default:
                        break;
                }
            } else {
                switch (configuration.widgetConfig.choose) {
                    case ExamPage:
                        return exam.GetInfomation();
                    case NewsPage:
                        return notice.GetInfomation();
                    case GradePage:
                        return ((GradeRobot)grade).GetInfomation(false);
                    case BathPage:
                        return bath.GetInfomation();
                    case CanteenPage:
                        return canteen.GetInfomation();
                    default:
                        break;
                }
            }
        }
        return "error";
    }

    public int[] GetColors() {
        String data = new FileTools(context).readData();
        if (data != null && !data.equals("")) {
            Configuration configuration = new Gson().fromJson(data, Configuration.class);
            return new int[]{configuration.widgetConfig.backgroundColor, configuration.widgetConfig.textColor};
        }
        return null;
    }
}
