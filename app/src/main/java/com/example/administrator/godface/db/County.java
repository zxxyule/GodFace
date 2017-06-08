package com.example.administrator.godface.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherNumber;
    private City city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherNumber() {
        return weatherNumber;
    }

    public void setWeatherNumber(String weatherNumber) {
        this.weatherNumber = weatherNumber;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
