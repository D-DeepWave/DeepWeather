package xyz.deepwave.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xyz.deepwave.db.City;
import xyz.deepwave.db.County;
import xyz.deepwave.db.Province;
import xyz.deepwave.db.DbManager;
import xyz.deepwave.gen.*;



public class Utility {

    public  static  boolean handleProvinceResponse(String response, Context context)
    {
        if(!TextUtils.isEmpty(response))
        {
            try{
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0;i<allProvinces.length();i++)
                {
                    Province province = new Province();
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName((provinceObject.getString("name")));
                    ProvinceDao mProvinceDao = DbManager.getInstance(context).getDaoSession(context).getProvinceDao();
                   mProvinceDao.insert(province);
                }
                return  true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId,Context context) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    CityDao mCityDao = DbManager.getInstance(context).getDaoSession(context).getCityDao();
                    mCityDao.insert(city);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handleCountyResponse(String response, int cityId,Context context) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    CountyDao mCountyDao = DbManager.getInstance(context).getDaoSession(context).getCountyDao();
                    mCountyDao.insert(county);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



}
