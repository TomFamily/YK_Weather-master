package com.example.yk_weather_data.ykFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.yk_weather_data.ykDataBase.DataBaseManager;
import com.example.yk_weather_data.R;
import com.example.yk_weather_data.ykWangluo.HttpUtils;
import com.example.yk_weather_data.ykWangluo.IndexBean;
import com.example.yk_weather_data.ykWangluo.TempBean;
import com.example.yk_weather_data.ykWangluo.URLUtils;
import com.google.gson.Gson;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CityWeatherFragment extends BaseFragment implements View.OnClickListener{
    private TextView tempTextView,cityTextView,conditionTextView, windTextView, tempRangeTextView;
    private TextView dateTextView, clothIndexTextView, carIndexTextView, coldIndexTextView;
    private TextView sportIndexTextView, raysIndexTextView, airIndexTextView;
    private ImageView dayImageView;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
//    指数信息
    private IndexBean.ResultBean.LifeBean lifeBean;
    private String cityName;
    private SharedPreferences sharedPreferences;
    private int bgNumumber;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        initView(view);
        exchangeBg();
//        可以通过activity传值获取到当前fragment加载的是那个地方的天气情况
        Bundle bundle = getArguments();
        cityName = bundle.getString("city");
        String tempUrl = URLUtils.getTempUrl(cityName);
//      调用父类方法获取数据的方法
        loadData(tempUrl);

        // 获取指数信息的网址
        String indexUrl = URLUtils.getIndexUrl(cityName);
        loadIndexData(indexUrl);


        return view;
    }

    //        换壁纸的函数
    private void exchangeBg(){
        sharedPreferences = getActivity().getSharedPreferences("ykBg", MODE_PRIVATE);
        bgNumumber = sharedPreferences.getInt("bg", 2);
        switch (bgNumumber) {
            case 0:
                scrollView.setBackgroundResource(R.drawable.bg);
                break;
            case 1:
                scrollView.setBackgroundResource(R.drawable.bg2);
                break;
            case 2:
                scrollView.setBackgroundResource(R.drawable.bg3);
                break;
        }
    }

//   获取指数信息
    private void loadIndexData(final String index_url) {
//        开辟一个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = HttpUtils.getJsonData(index_url);
                IndexBean jhIndexBean = new Gson().fromJson(json, IndexBean.class);
                if (jhIndexBean.getResult()!=null) {
                    lifeBean = jhIndexBean.getResult().getLife();
                }
            }
        }).start();
    }

    @Override
    public void onSuccess(String result) {
//        解析并展示数据
        parseShowData(result);
//         更新数据
        int i = DataBaseManager.updateInfoByCity(cityName, result);
        if (i<=0) {
//            更新数据库失败，说明没有这条城市信息，增加这个城市记录
            DataBaseManager.addCityInfo(cityName,result);
        }
    }
    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
//        数据库当中查找上一次信息显示在Fragment当中
        String s = DataBaseManager.queryInfoByCity(cityName);
        if (!TextUtils.isEmpty(s)) {
            parseShowData(s);
        }

    }
    @SuppressLint("SetTextI18n")
    private void parseShowData(String result) {
//        使用gson解析数据
        TempBean tempBean = new Gson().fromJson(result, TempBean.class);
        TempBean.ResultBean result1 = tempBean.getResult();
//        设置TextView
        dateTextView.setText(result1.getFuture().get(0).getDate());
        cityTextView.setText(result1.getCity());
//        获取今日天气情况
        TempBean.ResultBean.FutureBean todayFuture = result1.getFuture().get(0);
        TempBean.ResultBean.RealtimeBean realtime = result1.getRealtime();

        windTextView.setText(realtime.getDirect()+realtime.getPower());
        tempRangeTextView.setText(todayFuture.getTemperature());
        conditionTextView.setText(realtime.getInfo());
//     获取实时天气温度情况
        tempTextView.setText(realtime.getTemperature()+"℃");

//        获取未来三天的天气情况，加载到layout当中
        List<TempBean.ResultBean.FutureBean> futureList = result1.getFuture();
        futureList.remove(0);
        for (int i = 0; i < futureList.size(); i++) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_center, null);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(itemView);
            TextView dataTextView = itemView.findViewById(R.id.item_center_tv_date);
            TextView iconTextView = itemView.findViewById(R.id.item_center_tv_con);
            TextView windTextView = itemView.findViewById(R.id.item_center_tv_wind);
            TextView itemprangeTextView = itemView.findViewById(R.id.item_center_tv_temp);
//          获取对应的位置的天气情况
            TempBean.ResultBean.FutureBean dataBean = futureList.get(i);
            dataTextView.setText(dataBean.getDate());
            iconTextView.setText(dataBean.getWeather());
            itemprangeTextView.setText(dataBean.getTemperature());
            windTextView.setText(dataBean.getDirect());
        }
    }


    //        用于初始化控件操作
    private void initView(View view) {
        tempTextView = view.findViewById(R.id.frag_tv_currenttemp);
        cityTextView = view.findViewById(R.id.frag_tv_city);
        conditionTextView = view.findViewById(R.id.frag_tv_condition);
        windTextView = view.findViewById(R.id.frag_tv_wind);
        tempRangeTextView = view.findViewById(R.id.frag_tv_temprange);
        dateTextView = view.findViewById(R.id.frag_tv_date);
        clothIndexTextView = view.findViewById(R.id.frag_index_tv_dress);
        carIndexTextView = view.findViewById(R.id.frag_index_tv_washcar);
        coldIndexTextView = view.findViewById(R.id.frag_index_tv_cold);
        sportIndexTextView = view.findViewById(R.id.frag_index_tv_sport);
        raysIndexTextView = view.findViewById(R.id.frag_index_tv_rays);
        airIndexTextView = view.findViewById(R.id.frag_index_tv_air);
        dayImageView = view.findViewById(R.id.frag_iv_today);
        linearLayout = view.findViewById(R.id.frag_center_layout);
        scrollView = view.findViewById(R.id.out_layout);
//        设置点击事件的监听
        clothIndexTextView.setOnClickListener(this);
        carIndexTextView.setOnClickListener(this);
        coldIndexTextView.setOnClickListener(this);
        sportIndexTextView.setOnClickListener(this);
        raysIndexTextView.setOnClickListener(this);
        airIndexTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (v.getId()) {
            case R.id.frag_index_tv_dress:
                builder.setTitle("穿衣指数");
                String msg = "没有数据";
                if (lifeBean!=null){
                    msg = lifeBean.getChuanyi().getV()+"\n"+lifeBean.getChuanyi().getDes();
                }
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                break;
            case R.id.frag_index_tv_washcar:
                builder.setTitle("洗车指数");
                msg = "没有数据";
                if (lifeBean!=null){
                    msg = lifeBean.getXiche().getV()+"\n"+lifeBean.getXiche().getDes();
                }
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                break;
            case R.id.frag_index_tv_cold:
                builder.setTitle("感冒指数");
                msg = "没有数据";
                if (lifeBean!=null){
                    msg = lifeBean.getGanmao().getV()+"\n"+lifeBean.getGanmao().getDes();
                }
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                break;
            case R.id.frag_index_tv_sport:
                builder.setTitle("运动指数");
                msg = "没有数据";
                if (lifeBean!=null){
                    msg = lifeBean.getYundong().getV()+"\n"+lifeBean.getYundong().getDes();
                }
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                break;
            case R.id.frag_index_tv_rays:
                builder.setTitle("紫外线指数");
                msg = "没有数据";
                if (lifeBean!=null){
                    msg = lifeBean.getZiwaixian().getV()+"\n"+lifeBean.getZiwaixian().getDes();
                }
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                break;
            case R.id.frag_index_tv_air:
                builder.setTitle("空调指数");
                msg = "没有数据";
                if (lifeBean!=null){
                    msg = lifeBean.getKongtiao().getV()+"\n"+lifeBean.getKongtiao().getDes();
                }
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                break;
        }
        builder.create().show();
    }
}
