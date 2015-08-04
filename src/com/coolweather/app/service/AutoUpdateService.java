package com.coolweather.app.service;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
				
			}
		}).start();
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		long hour = 8*60*60*1000; //8小时
		long triggerTime = SystemClock.elapsedRealtime()+hour; //开机完成后的时间
		Intent i = new Intent(this,AutoUpdateService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private void updateWeather(){
		SharedPreferences share = getSharedPreferences("weather", MODE_PRIVATE);
		String cityCode = share.getString("weather_code", "");
		String address = "http://www.weather.com.cn/adat/cityinfo/101" + 
				cityCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String respone) {
				Utility.handleWeatherRespone(AutoUpdateService.this, respone);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
