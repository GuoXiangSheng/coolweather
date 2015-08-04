package com.coolweather.app.model;

public class WeatherInfo {

	private String cityCode;
	private String city;
	private String date;
	private String time;
	private String minDegree;
	private String maxDegree;
	private String weather; //天气情况
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMinDegree() {
		return minDegree;
	}
	public void setMinDegree(String minDegree) {
		this.minDegree = minDegree;
	}
	public String getMaxDegree() {
		return maxDegree;
	}
	public void setMaxDegree(String maxDegree) {
		this.maxDegree = maxDegree;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	
}
