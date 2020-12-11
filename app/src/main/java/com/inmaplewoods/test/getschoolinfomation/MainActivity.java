package com.inmaplewoods.test.getschoolinfomation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY};
    //请求状态码
    private static final int REQUEST_PERMISSION_CODE = 1;

    protected int countClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RobotFactory robot = new RobotFactory(this);
        GestureDetectorCompat detector = new GestureDetectorCompat(this, new viewMotion());
        String data = robot.GetData();
        TextView textView = findViewById(R.id.text_view);
        textView.setText(data);
        textView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());//实现滑动
        AppNotification.setNotificationState(MainActivity.this);//设置通知栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        Toast toast=Toast.makeText(MainActivity.this, Html.fromHtml("<font color='#FFFFFF'>左右滑动切换内容</font>"),2500);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = toast.getView();
        view.setBackgroundColor(Color.BLACK);
        toast.setView(view);
        toast.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEdit", true);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, LoginActivity.class);  //从MainActivity跳转到LoginActivity
                startActivity(intent);  //开始跳转
                return true;
            case R.id.menu_deletes:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.deletes)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                        .setTitle("确认对话框")
                        .setMessage("删除已有配置吗？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确认", (dialog, which) -> {
                            deleteSettings();
                            finish();
                        }).show();
                return true;
            case R.id.menu_helps:
                Toast.makeText(this, "左右滑动切换显示内容", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteSettings() {
        FileTools fileTools = new FileTools(MainActivity.this);
        if (fileTools.deleteData()) {
            String deleteSuccess = (String) MainActivity.this.getResources().getText(R.string.deleteSuccess);
            showDialog(deleteSuccess);
        } else {
            String deleteFailed = (String) MainActivity.this.getResources().getText(R.string.deleteFailed);
            showDialog(deleteFailed);
        }
    }

    private void showDialog(final String str) {
        final String deleteSuccess = (String) MainActivity.this.getResources().getText(R.string.deleteSuccess);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(str);

        builder.setPositiveButton("我知道了", (dialogInterface, i) -> {
            if (str.equals(deleteSuccess)) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);  //从MainActivity跳转到LoginActivity
                startActivity(intent);  //开始跳转
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class viewMotion implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float minMove = 120;         //最小滑动距离
            float minVelocity = 0;      //最小滑动速度
            float beginX = e1.getX();
            float endX = e2.getX();
            if (countClick >= 20) {
                countClick = 0;
            }
            if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) {   //左滑
                countClick++;
            } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) {   //右滑
                if(countClick>=1)
                {
                    countClick-=1;
                }
                else{
                    countClick+=4;
                }
            }
            Robot notice = new RobotFactory(MainActivity.this).CreateRobot(IRobotFactory.RobotType.Notice);
            Robot grade = new RobotFactory(MainActivity.this).CreateRobot(IRobotFactory.RobotType.Grade);
            Robot exam = new RobotFactory(MainActivity.this).CreateRobot(IRobotFactory.RobotType.Exam);
            Robot bath = new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Bath);
            Robot canteen = new RobotFactory("201709001013#llfllf").CreateRobot(IRobotFactory.RobotType.Canteen);
            TextView textView = findViewById(R.id.text_view);
            switch (countClick % 5) {
                case 0: {
                    textView.setText(notice.GetInfomation());
                    break;
                }
                case 1: {
                    textView.setText(exam.GetInfomation());
                    break;
                }
                case 2: {
                    textView.setText(((GradeRobot) grade).GetInfomation(true));
                    break;
                }
                case 3: {
                    textView.setText(bath.GetInfomation());
                    break;
                }
                case 4: {
                    textView.setText(canteen.GetInfomation());
                    break;
                }
                default:
                    break;
            }
            return false;

        }
    }

}
