package com.example.yk_weather_data;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.yk_weather_data.ykActivity.CityManagerActivity;
import com.example.yk_weather_data.ykActivity.MoreActivity;
import com.example.yk_weather_data.ykDataBase.DataBaseManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yk_weather_data.ykFragment.CityFragmentPagerAdapter;
import com.example.yk_weather_data.ykFragment.CityWeatherFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView addCityImageView, moreImageView;
    LinearLayout pointLayout;
    ConstraintLayout constraintLayout;
    ViewPager ykViewPager;
    //    ViewPager的数据源
    List<Fragment> fragmentList;
    //    表示需要显示的城市的集合
    List<String>cityList;
    //    表示ViewPager的页数指数器显示集合
    List<ImageView> ImageList;
    private CityFragmentPagerAdapter cityFragmentPagerAdapter;
    private SharedPreferences sharedPreference;
    private int bgNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
//        获取数据库包含的城市信息列表
        cityList = DataBaseManager.queryAllCityName();
        exchangeBg();
        if (cityList.size()==0) {
            cityList.add("兴义");
        }
//        因为可能搜索界面点击跳转此界面，会传值，所以此处获取一下
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        if (!cityList.contains(city)&&!TextUtils.isEmpty(city)) {
            cityList.add(city);
        }
        initViewPager();
        cityFragmentPagerAdapter = new CityFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        ykViewPager.setAdapter(cityFragmentPagerAdapter);
//        创建小圆点指示器
        initPoint();
//        设置最后一个城市信息
        ykViewPager.setCurrentItem(fragmentList.size()-1);
//        设置ViewPager页面监听
        setPagerListener();
    }

    private void initView() {
        addCityImageView = findViewById(R.id.mainAddImage);
        moreImageView = findViewById(R.id.mainMoreImage);
        pointLayout = findViewById(R.id.mainPointLayout);
        constraintLayout = findViewById(R.id.ykMain);
//        ViewPager
        ykViewPager = findViewById(R.id.ykViewPager);
//        添加点击事件
        addCityImageView.setOnClickListener(this);
        moreImageView.setOnClickListener(this);

        fragmentList = new ArrayList<>();
        ImageList = new ArrayList<>();
    }

    //        换壁纸的函数
    public void exchangeBg(){
        sharedPreference = getSharedPreferences("ykBg", MODE_PRIVATE);
        bgNumber = sharedPreference.getInt("bg", 2);
        switch (bgNumber) {
            case 0:
                constraintLayout.setBackgroundResource(R.drawable.bg);
                break;
            case 1:
                constraintLayout.setBackgroundResource(R.drawable.bg2);
                break;
            case 2:
                constraintLayout.setBackgroundResource(R.drawable.bg3);
                break;
        }

    }

    //    当viewPager滑动时，相应的改变底部指示圆点的位置
    private void setPagerListener() {
        /* 设置监听事件*/
        ykViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < ImageList.size(); i++) {
                    ImageList.get(i).setImageResource(R.drawable.a1);
                }
                ImageList.get(position).setImageResource(R.drawable.a2);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initPoint() {
//        创建小圆点 ViewPager页面指示器的函数
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView pImageView = new ImageView(this);
            pImageView.setImageResource(R.drawable.a1);
            pImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pImageView.getLayoutParams();
            lp.setMargins(0,0,20,0);
            ImageList.add(pImageView);
            pointLayout.addView(pImageView);
        }
        ImageList.get(ImageList.size()-1).setImageResource(R.drawable.a2);
    }

    private void initViewPager() {
        /* 创建Fragment对象，添加到ViewPager数据源当中*/
        for (int i = 0; i < cityList.size(); i++) {
            CityWeatherFragment cwFrag = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city",cityList.get(i));
            cwFrag.setArguments(bundle);
            fragmentList.add(cwFrag);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.mainAddImage:
                intent.setClass(this, CityManagerActivity.class);
                break;
            case R.id.mainMoreImage:
                intent.setClass(this, MoreActivity.class);
                break;
        }
        startActivity(intent);
    }

    //    当在删除页面进行了操作时，回到此界面需要重写到数据库获取数据（改进：可以设置一个标签，当删除页面真的进行了
//    操作时，就才调用一下的相应的函数
    @Override
    protected void onRestart() {
        super.onRestart();
//        获取数据库当中还剩下的城市集合
        List<String> list = DataBaseManager.queryAllCityName();
        if (list.size()==0) {
            list.add("重庆");
        }
        cityList.clear();    //重写加载之前，清空原本数据源
        cityList.addAll(list);
//        剩余城市也要创建对应的fragment页面
        fragmentList.clear();
//        创建fragment
        initViewPager();
        cityFragmentPagerAdapter.notifyDataSetChanged();
//        页面数量发生改变，指示器的数量也会发生变化，重写设置添加指示器
        ImageList.clear();
        pointLayout.removeAllViews();   //将布局当中所有元素全部移除
        initPoint();
        ykViewPager.setCurrentItem(fragmentList.size()-1);
    }
}
