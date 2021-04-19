package com.example.yk_weather_data.ykWangluo;

public class URLUtils {

    public static final String KEY = "864bdd5bfbaa5474078aa98ec94ed947";
//    温度接口
    public static String tempUrl = "http://apis.juhe.cn/simpleWeather/query";
//    生活指数接口
    public static String indexUrl = "http://apis.juhe.cn/simpleWeather/life";

    public static String getTempUrl(String city){
        String url = tempUrl +"?city="+city+"&key="+KEY;
        return url;
    }
    public static String getIndexUrl(String city){
        String url = indexUrl +"?city="+city+"&key="+KEY;
        return url;
    }
}
