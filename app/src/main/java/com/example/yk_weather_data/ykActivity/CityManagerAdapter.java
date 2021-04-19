package com.example.yk_weather_data.ykActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yk_weather_data.R;
import com.example.yk_weather_data.ykDataBase.DatabaseBean;
import com.example.yk_weather_data.ykWangluo.TempBean;
import com.google.gson.Gson;

import java.util.List;

// 城市管理界面的listView的适配器

public class CityManagerAdapter extends BaseAdapter {
    Context context;
    List<DatabaseBean> dataList;

    public CityManagerAdapter(Context context, List<DatabaseBean> dataList) {
        this.context = context;
        this.dataList = dataList;
    }



    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
//    对每个item的内容进行初始化
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city_manager, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DatabaseBean bean = dataList.get(position);
        holder.cityTextView.setText(bean.getCity());

//        对数据库中的json数据进行解析
        TempBean tempBean = new Gson().fromJson(bean.getContent(), TempBean.class);
        TempBean.ResultBean result = tempBean.getResult();
        TempBean.ResultBean.RealtimeBean realtime = result.getRealtime();
        TempBean.ResultBean.FutureBean todayBean = result.getFuture().get(0);
//        获取今日天气情况
        holder.conditionTextView.setText("天气:" + realtime.getInfo());
        holder.currentTempTextView.setText(realtime.getTemperature() + "℃");
        holder.windTextView.setText(realtime.getDirect() + realtime.getPower());
        holder.tempRangeTextView.setText(todayBean.getTemperature());
        return convertView;
    }


    static class ViewHolder {
        TextView cityTextView, conditionTextView, currentTempTextView, windTextView, tempRangeTextView;

        ViewHolder(View itemView) {
            cityTextView = itemView.findViewById(R.id.item_city_tv_city);
            conditionTextView = itemView.findViewById(R.id.item_city_tv_condition);
            currentTempTextView = itemView.findViewById(R.id.item_city_tv_temp);
            windTextView = itemView.findViewById(R.id.item_city_wind);
            tempRangeTextView = itemView.findViewById(R.id.item_city_temprange);

        }
    }
}
