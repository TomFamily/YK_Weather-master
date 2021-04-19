package com.example.yk_weather_data.ykWangluo;

//  获取生活指数信息

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static String getJsonData(String path) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            int read = 0;
            byte[] arr = new byte[1024];
            while ((read = inputStream.read(arr)) != -1) {
                byteArrayOutputStream.write(arr, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
