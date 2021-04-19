package com.example.yk_weather_data.ykActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yk_weather_data.R;

import java.util.List;

public class DeleteCityAdapter extends BaseAdapter {
    Context context;
    List<String> dataList;
    List<String> deleteList;

    public DeleteCityAdapter(Context context, List<String> mDatas, List<String> deleteCitys) {
        this.context = context;
        this.dataList = mDatas;
        this.deleteList = deleteCitys;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_deletecity, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String city = dataList.get(position);
        holder.textView.setText(city);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(city);
                deleteList.add(city);
//               提示适配器更新
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            textView = itemView.findViewById(R.id.item_delete_tv);
            imageView = itemView.findViewById(R.id.item_delete_iv);
        }
    }
}
