package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.model.WeatherInfo;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.coolweather.app.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	private ListView itemLv;
	private TextView titleTv;
	private ProgressDialog progressDialog;
	private ArrayAdapter<String> adapter; 
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private List<String> dataList = new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	
	public static final int LEVEL_PROVINCE = 0; 
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private Province selectedProvince; //选中的省份
	private City selectedCity;  //选中的城市
	

	/**
	* 当前选中的级别
	*/
	private int currentLevel;
	
	private boolean isFromWetherActivity; //是否是从天气窗体跳过来
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isFromWetherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences share = getSharedPreferences("weather", MODE_PRIVATE);
		//如果已选择过城市和不是从天气窗体跳过来的。则直接进入到天气窗体
		if(share.getBoolean("city_selected", false) && !isFromWetherActivity){
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		titleTv = (TextView) findViewById(R.id.title);
		itemLv = (ListView) findViewById(R.id.list_view);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		itemLv.setAdapter(adapter);
		itemLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					queryCity();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					queryCounty();
				}else{
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("cityCode", countyList.get(position).getCountyCode());
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvince();
	}
	

	
	/**
	 * 查询全国的省份，优先去数据库查，查不到则从服务器获取
	 */
	private void queryProvince(){
		provinceList = coolWeatherDB.loadProvinces();
		dataList.clear();
		if(provinceList.size()>0){
			for (Province p : provinceList) {
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			itemLv.setSelection(0);
			titleTv.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * 查询省内所有市，优先去数据库查，查不到则从服务器获取
	 */
	private void queryCity(){
		cityList = coolWeatherDB.loadCitys(selectedProvince.getId());
		dataList.clear();
		if(cityList.size()>0){
			for (City c : cityList) {
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			itemLv.setSelection(0);
			titleTv.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 查询省内所有市，优先去数据库查，查不到则从服务器获取
	 */
	private void queryCounty(){
		dataList.clear();
		countyList = coolWeatherDB.loadCountys(selectedCity.getId());
		if(countyList.size()>0){
			for (County c : countyList) {
				dataList.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			itemLv.setSelection(0);
			titleTv.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	private void queryFromServer(final String code,final String type){
		String address="";
		if(TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			boolean result = false;
			@Override
			public void onFinish(String respone) {
				if(type.equals("province")){
					result = Utility.handleProvinceRespone(coolWeatherDB, respone);
				}else if(type.equals("city")){
					result = Utility.handleCityRespone(coolWeatherDB, respone, selectedProvince.getId());
				}else if(type.equals("county")){
					result = Utility.handleCountyRespone(coolWeatherDB, respone, selectedCity.getId());
				}
				if(result){
					closeProgressDialog();
					//回到主线程处理逻辑,因为queryProvinces()等方法牵扯到了 UI 操作
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(type.equals("province")){
								queryProvince();
							}else if(type.equals("city")){
								queryCity();
							}else if(type.equals("county")){
								queryCounty();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载数据 ...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	
	/**
	 * 捕获back键，根据当前级别来判断是该返回省级列表项还是市级
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_CITY){
			queryProvince();
		}else if(currentLevel == LEVEL_COUNTY){
			queryCity();
		}else {
			if(isFromWetherActivity){
				Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
