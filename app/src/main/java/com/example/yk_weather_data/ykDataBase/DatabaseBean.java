package com.example.yk_weather_data.ykDataBase;

public class DatabaseBean {
    private int _id;
    private String city;
    private String content;

//    数据类：对城市中保存的数据进行封装
    public DatabaseBean(int _id, String city, String content) {
        this._id = _id;
        this.city = city;
        this.content = content;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
