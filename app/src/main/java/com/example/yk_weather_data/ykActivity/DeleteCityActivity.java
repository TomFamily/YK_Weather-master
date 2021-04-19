package com.example.yk_weather_data.ykActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.yk_weather_data.ykDataBase.DataBaseManager;
import com.example.yk_weather_data.R;

import java.util.ArrayList;
import java.util.List;

public class DeleteCityActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView errorImageView;
    ImageView rightImageView;
    ListView listView;
//    listview的数据源
    List<String> dataList;
//    表示存储了删除的城市信息
    List<String> deleteList;
    private DeleteCityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_city);

        initButtons();
//        适配器的设置
        adapter = new DeleteCityAdapter(this, dataList, deleteList);
        listView.setAdapter(adapter);
    }
    private void initButtons(){
        errorImageView = findViewById(R.id.delete_iv_error);
        rightImageView = findViewById(R.id.delete_iv_right);
        listView = findViewById(R.id.delete_lv);
        dataList = DataBaseManager.queryAllCityName();
        deleteList = new ArrayList<>();
//        设置点击监听事件
        errorImageView.setOnClickListener(this);
        rightImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_iv_error:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示信息").setMessage("确定放弃更改？")
                        .setPositiveButton("舍弃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();   //关闭当前的activity
                            }
                        });
                builder.setNegativeButton("取消",null);
                builder.create().show();
                break;
            case R.id.delete_iv_right:
                for (int i = 0; i < deleteList.size(); i++) {
                    String city = deleteList.get(i);
//                    调用删除城市的函数
                    int i1 = DataBaseManager.deleteInfoByCity(city);
                }
//                删除成功返回上一级页面
                finish();
                break;
        }
    }
}