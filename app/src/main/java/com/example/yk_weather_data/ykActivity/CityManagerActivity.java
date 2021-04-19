package com.example.yk_weather_data.ykActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.*;
import com.example.yk_weather_data.ykDataBase.DataBaseManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.yk_weather_data.R;
import com.example.yk_weather_data.ykDataBase.DatabaseBean;

import java.util.ArrayList;
import java.util.List;

//城市管理界面

public class CityManagerActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView addImageView, backImageView, deleteImageView;
    ListView mRecyclerView;
    //    显示列表数据源
    List<DatabaseBean> dataList;
    private CityManagerAdapter cityManagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);

        initButtons();

//        设置适配器
        cityManagerAdapter = new CityManagerAdapter(this, dataList);
        mRecyclerView.setAdapter(cityManagerAdapter);
    }

    private void initButtons() {
        addImageView = findViewById(R.id.city_iv_add);
        backImageView = findViewById(R.id.city_iv_back);
        deleteImageView = findViewById(R.id.city_iv_delete);
        mRecyclerView = findViewById(R.id.city_lv);
        dataList = new ArrayList<>();
//        添加点击监听事件
        addImageView.setOnClickListener(this);
        deleteImageView.setOnClickListener(this);
        backImageView.setOnClickListener(this);
    }

    /*  获取数据库当中真实数据源，添加到原有数据源当中，提示适配器更新*/
    @Override
    protected void onResume() {
        super.onResume();

        List<DatabaseBean> dataList2 = DataBaseManager.queryAllInfo();
        dataList.clear();
        dataList.addAll(dataList2);
        cityManagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_iv_add:
                int cityNumber = DataBaseManager.getCityCount();
                if (cityNumber < 5) {
                    Intent intent = new Intent(this, SearchCityActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "最多只能添加5个城市！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.city_iv_back:
                finish();
                break;
            case R.id.city_iv_delete:
                Intent intent1 = new Intent(this, DeleteCityActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
