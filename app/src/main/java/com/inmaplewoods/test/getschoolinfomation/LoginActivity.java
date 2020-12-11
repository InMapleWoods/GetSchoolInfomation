package com.inmaplewoods.test.getschoolinfomation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.gson.Gson;
import top.defaults.colorpicker.ColorPickerPopup;
import top.defaults.colorpicker.ColorPickerView;

public class LoginActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
    };
    //请求状态码
    private static final int REQUEST_PERMISSION_CODE = 1;
    private final Configuration configuration = new Configuration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        FileTools fileTools = new FileTools(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        Button loginBtn = findViewById(R.id.loginBtn);//获取事件源
        loginBtn.setOnClickListener(new loginButton());//将事件源与相应的相应绑定
        Switch switchBtn = findViewById(R.id.switchButton);
        switchBtn.setOnCheckedChangeListener(new switchButton());
        View viewBackground = findViewById(R.id.WidgetBackgroundColorView);//获取事件源
        viewBackground.setOnClickListener(v -> showDialog("Background"));
        View viewText = findViewById(R.id.WidgetTextColorView);//获取事件源
        viewText.setOnClickListener(v -> showDialog("Text"));
        Spinner spinner = findViewById(R.id.SemesterChooseList);
        final String[] arr = new RobotFactory(this).GetTimes();
        //创建ArrayAdapter对象
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, arr);
        spinner.setAdapter(adapter);
        Intent intent1 = this.getIntent();
        Bundle bundle = intent1.getExtras();
        if (bundle != null) {
            boolean isEdit = bundle.getBoolean("isEdit");
            if (isEdit) {
                setConfiguration();
                return;
            }
        }

        if (fileTools.isExist()) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);  //从LoginActivity跳转到MainActivity
            startActivity(intent);  //开始跳转
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setConfiguration() {
        Configuration configuration = new RobotFactory(this).GetConfig();
        if (configuration != null) {
            ((EditText) findViewById(R.id.accountEditText)).setText(configuration.user.Account);
            ((EditText) findViewById(R.id.passwordEditText)).setText((configuration.user.Password));
            ((Spinner) findViewById(R.id.spinner_App)).setSelection(configuration.appConfig.choose.ordinal());
            ((Spinner) findViewById(R.id.spinner_Widget)).setSelection(configuration.widgetConfig.choose.ordinal());
            findViewById(R.id.WidgetBackgroundColorView).setBackgroundColor(configuration.widgetConfig.backgroundColor);
            findViewById(R.id.WidgetTextColorView).setBackgroundColor(configuration.widgetConfig.textColor);
            String[] s = new RobotFactory(this).GetTimes();
            for (int i = 0; i < s.length; i++) {
                if (s[i].equals(configuration.time)) {
                    ((Spinner) findViewById(R.id.SemesterChooseList)).setSelection(i);
                    break;
                }
                ((Spinner) findViewById(R.id.SemesterChooseList)).setSelection(0);
            }
            //设置是否常驻通知栏
            ((Switch) findViewById(R.id.switchButton)).setChecked(configuration.isOnGoing);
        }
    }

    private void showDialog(String source) {
        final String okTitle = (String) LoginActivity.this.getResources().getText(R.string.chooseColor);
        final String cancelTitle = (String) LoginActivity.this.getResources().getText(R.string.cancelChooseColor);
        final ColorPickerView v = new ColorPickerView(this);
        new ColorPickerPopup.Builder(this)
                .initialColor(Color.RED) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle(okTitle)
                .cancelTitle(cancelTitle)
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(v, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        if (source.trim().equals("Background")) {
                            configuration.widgetConfig.backgroundColor = color;
                            View view = findViewById(R.id.WidgetBackgroundColorView);//获取事件源
                            view.setBackgroundColor(color);
                        } else if (source.trim().equals("Text")) {
                            configuration.widgetConfig.textColor = color;
                            View view = findViewById(R.id.WidgetTextColorView);//获取事件源
                            view.setBackgroundColor(color);
                        }
                    }
                });
    }

    class loginButton implements View.OnClickListener {
        public void onClick(View view) {
            FileTools fileTools = new FileTools(LoginActivity.this);
            String account = ((EditText) findViewById(R.id.accountEditText)).getText().toString();
            String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
            if (account.isEmpty() || password.isEmpty())
                return;
            configuration.user.Account = account;
            configuration.user.Password = password;
            Spinner spinnerApp = findViewById(R.id.spinner_App);
            int idApp = spinnerApp.getSelectedItemPosition();
            configuration.appConfig.choose = Config.PageChoose.values()[idApp];
            Spinner spinnerWidget = findViewById(R.id.spinner_Widget);
            int idWidget = spinnerWidget.getSelectedItemPosition();
            configuration.widgetConfig.choose = Config.PageChoose.values()[idWidget];
            configuration.widgetConfig.backgroundColor = ((ColorDrawable) (findViewById(R.id.WidgetBackgroundColorView).getBackground())).getColor();
            configuration.widgetConfig.textColor = ((ColorDrawable) (findViewById(R.id.WidgetTextColorView).getBackground())).getColor();
            configuration.time = ((Spinner) findViewById(R.id.SemesterChooseList)).getSelectedItem().toString();
            configuration.isOnGoing = ((Switch) findViewById(R.id.switchButton)).isChecked();
            Gson gson = new Gson();
            String data = gson.toJson(configuration);
            fileTools.writeData(data);
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);  //从LoginActivity跳转到MainActivity
            startActivity(intent);  //开始跳转
        }
    }

    class switchButton implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.switchButton) {
                if (buttonView.isChecked()) {
                    AppNotification.createCustomNotification(LoginActivity.this, getPackageName());
                } else {
                    AppNotification.cancelCustomNotification();
                }
            }
        }


    }

}
