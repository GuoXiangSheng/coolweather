package com.coolweather.app.util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.AvoidXfermode.Mode;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.model.WeatherInfo;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvinceRespone(
			CoolWeatherDB coolWeatherDB, String respone) {
		if (!TextUtils.isEmpty(respone)) {
			String[] provinceData = respone.split(",");
			if (provinceData.length > 0) {
				for (int i = 0; i < provinceData.length; i++) {
					String[] datas = provinceData[i].split("\\|");
					Province province = new Province();
					province.setProvinceName(datas[1]);
					province.setProvinceCode(datas[0]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCityRespone(
			CoolWeatherDB coolWeatherDB, String respone,int provinceId) {
		if (!TextUtils.isEmpty(respone)) {
			String[] cityData = respone.split(",");
			if (cityData.length > 0) {
				for (int i = 0; i < cityData.length; i++) {
					String[] datas = cityData[i].split("\\|");
					City city = new City();
					city.setCityName(datas[1]);
					city.setCityCode(datas[0]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的区县数据
	 */
	public synchronized static boolean handleCountyRespone(
			CoolWeatherDB coolWeatherDB, String respone,int cityId) {
		if (!TextUtils.isEmpty(respone)) {
			String[] CountyData = respone.split(",");
			if (CountyData.length > 0) {
				for (int i = 0; i < CountyData.length; i++) {
					String[] datas = CountyData[i].split("\\|");
					County county = new County();
					county.setCountyName(datas[1]);
					county.setCountyCode(datas[0]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * 处理返回的天气信息
	 * @param respone
	 * @return
	 */
	public synchronized static void handleWeatherRespone(Context context,String respone){
		if(!TextUtils.isEmpty(respone)){
			try {
				JSONObject jsonObject = new JSONObject(respone);
				JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
				WeatherInfo weather = new WeatherInfo();
				weather.setCity(weatherInfo.getString("city"));
				weather.setCityCode(weatherInfo.getString("cityid"));
				weather.setTime(weatherInfo.getString("ptime"));
				weather.setMinDegree(weatherInfo.getString("temp2"));
				weather.setMaxDegree(weatherInfo.getString("temp1"));
				weather.setWeather(weatherInfo.getString("weather"));
				weather.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()));
				saveWeatherInfo(context,weather);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static void saveWeatherInfo(Context context,WeatherInfo weInfo){
		SharedPreferences shared = context.getSharedPreferences("weather",Context.MODE_PRIVATE);
		Editor editor = shared.edit();  //获取编译器
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", weInfo.getCity());
		editor.putString("weather_code", weInfo.getCityCode());
		editor.putString("temp1", weInfo.getMinDegree());
		editor.putString("temp2", weInfo.getMaxDegree());
		editor.putString("weather_desp", weInfo.getWeather());
		editor.putString("publish_time", weInfo.getTime());
		editor.putString("current_date", weInfo.getDate());
		editor.commit();
	}
}
