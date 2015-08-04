package com.coolweather.app.activity;


import com.coolweather.app.R;
import com.coolweather.app.model.WeatherInfo;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WeatherActivity extends Activity implements android.view.View.OnClickListener{
	
	private TextView cityNameText,publishTimeText,currentDateText,descText,degreeText;
	private Button selectCityBtn,refreshBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishTimeText = (TextView) findViewById(R.id.publish_text);
		currentDateText = (TextView) findViewById(R.id.current_date);
		descText = (TextView) findViewById(R.id.weather_desc);
		degreeText = (TextView) findViewById(R.id.degree);
		selectCityBtn = (Button) findViewById(R.id.select_city);
		refreshBtn = (Button) findViewById(R.id.refresh_btn);
		String cityCode = getIntent().getStringExtra("cityCode");
		if(!TextUtils.isEmpty(cityCode)){
			publishTimeText.setText("更新中...");
			findViewById(R.id.weather_info_layout).setVisibility(View.INVISIBLE); //不可见
			cityNameText.setText("");
			getWeather(cityCode);
		}else{
			showWeather();
		}
		
		selectCityBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
	}
	
	private void getWeather(String cityCode){
		String address = "http://www.weather.com.cn/adat/cityinfo/101"+cityCode+".html";
		System.out.println(address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String respone) {
				Utility.handleWeatherRespone(WeatherActivity.this,respone);
					//回到主线程更新UI
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishTimeText.setText("同步失败");
					}
				});
			}
		});
	}
	
	private void showWeather(){
		SharedPreferences share = getSharedPreferences("weather", MODE_PRIVATE);
		cityNameText.setText(share.getString("city_name",""));
		currentDateText.setText(share.getString("current_date",""));
		publishTimeText.setText("今日 "+share.getString("publish_time","")+" 发布");
		descText.setText(share.getString("weather_desp",""));
		degreeText.setText(share.getString("temp1","")+"  ~ "+share.getString("temp2","")+" ");
		findViewById(R.id.weather_info_layout).setVisibility(View.VISIBLE);
		Intent i = new Intent(this,AutoUpdateService.class);
		startService(i);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_city:
			Intent intent  = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_btn:
			publishTimeText.setText("更新中...");
			SharedPreferences share = getSharedPreferences("weather", MODE_PRIVATE);
			String cityCode = share.getString("weather_code", "");
			if(!TextUtils.isEmpty(cityCode)){
				getWeather(cityCode);
			}
			break;
		default:
			break;
		}
		
	}


	
}
