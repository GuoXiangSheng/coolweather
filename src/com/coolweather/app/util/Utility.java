package com.coolweather.app.util;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

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
}
