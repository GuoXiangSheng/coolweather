package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection  = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream is = connection.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String line="";
					StringBuilder sb = new StringBuilder();
					while((line = br.readLine())!=null){
						sb.append(line);
					}
					if(listener != null){
						listener.onFinish(sb.toString());
					}
				} catch (Exception e) {
					if(listener != null){
						listener.onError(e);
					}
				}
				
			}
		}).start();
	}
}
