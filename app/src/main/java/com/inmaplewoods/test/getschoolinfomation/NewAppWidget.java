package com.inmaplewoods.test.getschoolinfomation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    static int countClick = 0;
    static final String tag_action = "Widget.Click";

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        String s;
        RobotFactory robot = new RobotFactory(context);
        s = robot.GetData();
        CharSequence widgetText = s;
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        if (robot.GetColors() != null) {
            views.setInt(R.id.appwidget_text, "setBackgroundColor", robot.GetColors()[0]);
            views.setTextColor(R.id.appwidget_text, robot.GetColors()[1]);
        }
        views.setOnClickPendingIntent(R.id.appwidget_text, getPendingIntent(context, R.id.appwidget_text));
        //更新widget

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    public PendingIntent getPendingIntent(Context context, int resID) {
        Intent intent = new Intent();
        intent.setClass(context, NewAppWidget.class);//如果没有这一句，表示匿名的。加上表示是显式的。在单个按钮的时候是没啥区别的，但是多个的时候就有问题了
        intent.setAction(tag_action);
        //设置data域的时候，把控件id一起设置进去，
        // 因为在绑定的时候，是将同一个id绑定在一起的，所以哪个控件点击，发送的intent中data中的id就是哪个控件的id
        intent.setData(Uri.parse("id:" + resID));

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Objects.requireNonNull(intent.getAction()).equals(tag_action)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            Uri data = intent.getData();
            int resId = -1;
            if (data != null) {
                resId = Integer.parseInt(data.getSchemeSpecificPart());
            }
            int[] colors = new RobotFactory(context).GetColors();
            if (colors != null) {
                remoteViews.setInt(R.id.appwidget_text, "setBackgroundColor", colors[0]);
                remoteViews.setTextColor(R.id.appwidget_text, colors[1]);
            }
            CharSequence widgetText = "";
            switch (resId) {
                case R.id.appwidget_text: {

                    if (countClick >= 20) {
                        countClick = 0;
                    } else {
                        countClick++;
                    }
                    Robot notice=new RobotFactory(context).CreateRobot(IRobotFactory.RobotType.Notice);
                    Robot grade=new RobotFactory(context).CreateRobot(IRobotFactory.RobotType.Grade);
                    Robot exam=new RobotFactory(context).CreateRobot(IRobotFactory.RobotType.Exam);
                    Robot bath=new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Bath);
                    Robot canteen=new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Canteen);
                    switch (countClick % 5) {
                        case 0: {
                            widgetText = ((GradeRobot)grade).GetInfomation(false);
                            break;
                        }
                        case 1: {
                            widgetText = exam.GetInfomation();
                            break;
                        }
                        case 2: {
                            widgetText = notice.GetInfomation();
                            break;
                        }
                        case 3: {
                            widgetText = bath.GetInfomation();
                            break;
                        }
                        case 4: {
                            widgetText = canteen.GetInfomation();
                            break;
                        }
                        default:
                            break;
                    }
                    remoteViews.setTextViewText(R.id.appwidget_text, widgetText);
                    break;

                }
            }
            //获得appwidget管理实例，用于管理appwidget以便进行更新操作
            AppWidgetManager manger = AppWidgetManager.getInstance(context);
            // 相当于获得所有本程序创建的appwidget
            ComponentName thisName = new ComponentName(context, NewAppWidget.class);
            //更新widget
            manger.updateAppWidget(thisName, remoteViews);
        }
    }
}