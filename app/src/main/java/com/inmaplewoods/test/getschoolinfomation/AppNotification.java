package com.inmaplewoods.test.getschoolinfomation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AppNotification extends BroadcastReceiver {
    protected static NotificationManager manager;

    protected static void createCustomNotification(Context context, String packageName) {
        RemoteViews remoteViews = getRemoteViews(context, packageName);
        sendNotification(context, remoteViews);
    }

    private static void sendNotification(Context context, RemoteViews remoteViews) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "to-do")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setContentText("text")
                .setCustomContentView(remoteViews);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("to-do"
                    , "待办消息",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{500});
            manager.createNotificationChannel(channel);
            builder.setChannelId("to-do");
            notify = builder.build();
        } else {
            notify = builder.build();
        }

        notify.flags |= Notification.FLAG_ONGOING_EVENT;

        manager.notify(0, notify);
    }

    protected static void cancelCustomNotification() {
        manager.cancel(0);
    }

    protected static void setNotificationState(Context context) {
        String data = new FileTools(context).readData();
        if (data != null && !data.equals("")) {
            Configuration configuration = new Gson().fromJson(data, Configuration.class);
            boolean isOnGoing = configuration.isOnGoing;
            if (isOnGoing) {
                createCustomNotification(context, context.getPackageName());
            } else {
                cancelCustomNotification();
            }
        }
    }

    protected static String[] getNotificationText(Context context) {
        Robot notice = new RobotFactory(context).CreateRobot(IRobotFactory.RobotType.Notice);
        Robot bath = new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Bath);
        Robot canteen = new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Canteen);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        double time = hour + (minute / 60.0);
        boolean isEating = (time >= 6.5 && time <= 8.5) || (time >= 11.5 && time <= 13.5) || (time >= 17.5 && time <= 19.5);
        boolean isBath = (time >= 14.0 && time <= 22.0) && (!isEating);
        if (isEating) {
            return new String[]{"食堂", getNoticeSplit(canteen.GetInfomation())};
        } else if (isBath) {
            return new String[]{"浴室", getNoticeSplit(bath.GetInfomation())};
        } else {
            return new String[]{"通知", getNoticeSplit(notice.GetInfomation())};
        }
    }

    protected static String getNoticeSplit(String str) {
        String[] tem = str.split("\r\n");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            s.append(tem[i]).append("\r\n");
            if (tem[i].length() >= 20) {
                break;
            }
        }
        return s.toString();
    }

    public static PendingIntent getPendingIntent(Context context, int resID) {
        Intent intent = new Intent();
        intent.setClass(context, AppNotification.class);//如果没有这一句，表示匿名的。加上表示是显式的。在单个按钮的时候是没啥区别的，但是多个的时候就有问题了
        intent.setAction("Notice_Click");
        //设置data域的时候，把控件id一起设置进去，
        // 因为在绑定的时候，是将同一个id绑定在一起的，所以哪个控件点击，发送的intent中data中的id就是哪个控件的id
        intent.setData(Uri.parse("id:" + resID));

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private static RemoteViews getRemoteViews(Context context, String packageName) {
        PendingIntent contentIntent = getPendingIntent(context, R.id.noticeBtn);
        RemoteViews views = new RemoteViews(packageName, R.layout.new_notice_layout);
        String[] result = getNotificationText(context);
        views.setTextViewText(R.id.tv_title, result[0]); //设置TextView组件显示文本
        views.setTextViewText(R.id.tv_content, result[1]);
        views.setOnClickPendingIntent(R.id.noticeBtn, contentIntent);
        return views;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getAction()).equals("Notice_Click")) {
            RemoteViews views = getRemoteViews(context, context.getPackageName());
            views.setViewVisibility(R.id.noticeProgressBar, View.VISIBLE);
            views.setViewVisibility(R.id.noticeBtn, View.INVISIBLE);
            sendNotification(context, views);
            views.setViewVisibility(R.id.noticeProgressBar, View.INVISIBLE);
            views.setViewVisibility(R.id.noticeBtn, View.VISIBLE);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    sendNotification(context, views);
                }
            }, 800);
        }
    }
}
