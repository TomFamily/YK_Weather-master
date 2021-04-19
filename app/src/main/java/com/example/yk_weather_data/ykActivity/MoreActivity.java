package com.example.yk_weather_data.ykActivity;

import androidx.appcompat.app.AppCompatActivity;
import com.example.yk_weather_data.ykDataBase.DataBaseManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yk_weather_data.MainActivity;
import com.example.yk_weather_data.R;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener{
    TextView bgTextView, cacheTextView, versionTextView, shareTextView;
    RadioGroup radioGroup;
    ImageView backImageView;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        initButtons();

        sharedPreferences = getSharedPreferences("ykBg", MODE_PRIVATE);
        String versionName = getVersion();
        versionTextView.setText("APP版本:    v"+versionName);
        setRGListener();
    }

    private void initButtons() {
        bgTextView = findViewById(R.id.more_tv_exchangebg);
        cacheTextView = findViewById(R.id.more_tv_cache);
        versionTextView = findViewById(R.id.more_tv_version);
        shareTextView = findViewById(R.id.more_tv_share);
        backImageView = findViewById(R.id.more_iv_back);
//        设置背景
        radioGroup = findViewById(R.id.more_rg);
        bgTextView.setOnClickListener(this);
        cacheTextView.setOnClickListener(this);
        shareTextView.setOnClickListener(this);
        backImageView.setOnClickListener(this);
    }

    private void setRGListener() {
//        设置改变背景图片的单选按钮的监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                switch (checkedId) {
                    case R.id.more_rb_green:
                        editor.putInt("bg",0);
                        editor.apply();
                        break;
                    case R.id.more_rb_pink:
                        editor.putInt("bg",1);
                        editor.apply();
                        break;
                    case R.id.more_rb_blue:
                        editor.putInt("bg",2);
                        editor.apply();
                        break;
                }
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_iv_back:
                finish();
                break;
            case R.id.more_tv_cache:
                clearCache();
                break;
            case R.id.more_tv_share:
                shareAPP("天气APP的分享!");
                break;
            case R.id.more_tv_exchangebg:
                if (radioGroup.getVisibility() == View.VISIBLE) {
                    radioGroup.setVisibility(View.GONE);
                }else{
                    radioGroup.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void shareAPP(String s) {
        /* 分享软件的函数*/
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,s);
        startActivity(Intent.createChooser(intent,"天气"));
    }

    private void clearCache() {
        /* 清除缓存的函数*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("删除所有缓存？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataBaseManager.deleteAllInfo();
                Toast.makeText(MoreActivity.this,"已清除全部缓存！",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }

    public String getVersion() {
        /* 获取应用的版本名称*/
        PackageManager manager = getPackageManager();
        String version = null;
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}