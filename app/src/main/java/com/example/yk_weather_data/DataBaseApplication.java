package com.example.yk_weather_data;

import android.app.Application;
import com.example.yk_weather_data.ykDataBase.DataBaseManager;
import org.xutils.x;

//    在清单文件的application的name属性中设置这个activity，在APP启动时会默认调用这个activity
//    程序启动只会调用这个函数一次，所以只这里适合初始化数据库
public class DataBaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        DataBaseManager.initDB(this);
    }
}
