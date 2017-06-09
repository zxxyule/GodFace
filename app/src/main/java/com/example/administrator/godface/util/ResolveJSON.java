package com.example.administrator.godface.util;

import android.text.TextUtils;

import com.example.administrator.godface.db.City;
import com.example.administrator.godface.db.County;
import com.example.administrator.godface.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class ResolveJSON {
    //解析处理服务器返回的省份信息
    public static boolean handleprovinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray array=new JSONArray(response);
              for(int i=0;i<array.length();i++){
                  JSONObject object=array.getJSONObject(i);
                  Province province=new Province();
                  province.setProvinceCode(object.getInt("id"));
                  province.setProvinceName(object.getString("name"));
                  province.save();
              }
              return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析处理服务器返回的城市信息
    public static boolean handlecityResponse(String response,Province province)  {
        if(!TextUtils.isEmpty(response)){
            JSONArray array= null;
            try {
                array = new JSONArray(response);
                for(int i=0;i<array.length();i++){
                    JSONObject object=array.getJSONObject(i);
                    City city=new City();
                    city.setCityCode(object.getInt("id"));
                    city.setCityName(object.getString("name"));
                    city.setProvince(province);
                    city.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
    //解析处理服务器返回的县信息
    public static boolean handlecountyResponse(String response,City city) {
        if(!TextUtils.isEmpty(response)){
            JSONArray array= null;
            try {
                array = new JSONArray(response);
                for(int i=0;i<array.length();i++){
                    JSONObject object=array.getJSONObject(i);
                    County county=new County();
                    county.setCity(city);
                    county.setCountyName(object.getString("name"));
                    county.setWeatherNumber(object.getString("weather_id"));
                    county.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
