package com.example.yk_weather_data.ykActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yk_weather_data.MainActivity;
import com.example.yk_weather_data.R;
import com.example.yk_weather_data.ykWangluo.TempBean;
import com.example.yk_weather_data.ykWangluo.URLUtils;
import com.google.gson.Gson;

public class SearchCityActivity extends BaseActivity implements View.OnClickListener{
    EditText searchEditText;
    ImageView submitImageView;
    GridView gridView;
    String[] cityArray = {"北京","上海","广州","深圳","珠海","佛山","南京","苏州","厦门","长沙","成都","福州",
            "杭州","武汉","青岛","西安","太原","沈阳","重庆","天津","南宁"};
    private ArrayAdapter<String> adapter;

    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        initButtons();

//        设置适配器
        adapter = new ArrayAdapter<>(this, R.layout.item_hotcity, cityArray);
        gridView.setAdapter(adapter);
    }

    private void initButtons() {
        searchEditText = findViewById(R.id.search_et);
        submitImageView = findViewById(R.id.search_iv_submit);
        gridView = findViewById(R.id.search_gv);
        submitImageView.setOnClickListener(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = cityArray[position];
                String url = URLUtils.getTempUrl(city);
                loadData(url);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv_submit:
                city = searchEditText.getText().toString();
                if (!TextUtils.isEmpty(city)) {
//                      判断是否能够找到这个城市
                    String url = URLUtils.getTempUrl(city);
                    loadData(url);
                }else {
                    Toast.makeText(this,"请输入城市名！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
        public void onSuccess(String result) {
        TempBean weatherBean = new Gson().fromJson(result, TempBean.class);
        if (weatherBean.getError_code()==0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city",city);
            startActivity(intent);
        }else{
            Toast.makeText(this,"没有该城市信息！",Toast.LENGTH_SHORT).show();
        }
    }
}

