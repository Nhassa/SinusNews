package com.example.sinusnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OneNews extends Activity {
	
	final String LOG_TAG = "sinusLogs";
	
	TextView tvTitle;
	TextView tvDesc;
	TextView tvDate;
	ProgressBar pbLoading;
	String json_txt = "";
	AsyncOneNewsLoader loader;
	String newsTitle = "";
	String newsDate = "";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_news);
		
		Log.d(LOG_TAG, "OneNews onCreate");
		
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
		tvTitle = (TextView) findViewById(R.id.tvOneTitle);
		tvDesc = (TextView) findViewById(R.id.tvOneDesc);
		tvDate = (TextView) findViewById(R.id.tvOneDate);
		
		//Get news id
		Intent intent = getIntent();
		String newsId = intent.getStringExtra("newsId");
		newsTitle = intent.getStringExtra("newsTitle");
		newsDate = intent.getStringExtra("newsDate");
		
		Log.d(LOG_TAG, "OneNews beforeLoading");
		//tvTitle.setText(newsId);
		loader = new AsyncOneNewsLoader();
		loader.execute(newsId);
	}
	
	private class AsyncOneNewsLoader extends AsyncTask<String, Void, String>{
		protected void onPreExecute() {
			super.onPreExecute();
			
		    Log.d(LOG_TAG, "OneNews startLoading");
		}
		
		protected String doInBackground(String... ids){
			String id = "";
			if( ids.length > 0 ){
            	id = ids[0];		    	
            }
			
			try{
				
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://sinustech.net/android_news.php?news_id=" + id);
				HttpResponse response = client.execute(request);
				
				//Get response
				InputStreamReader is = new InputStreamReader(response.getEntity().getContent());
				BufferedReader reader = new BufferedReader(is);
				String line = "";
				
				while((line = reader.readLine()) != null){
					json_txt = line;
				}
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json_txt;
		}
		
		protected void onPostExecute(String json_txt){
			super.onPostExecute(json_txt);
			
			Log.d(LOG_TAG, "OneNews endLoading");
			pbLoading.setVisibility(View.GONE);
			
			//JSON parsing
			JSONArray ja = null;
			try {
				ja = new JSONArray(json_txt);
				for(int i=0; i<ja.length(); i++){
					JSONObject jo = ja.getJSONObject(i);
					
					tvTitle.setText(newsTitle);
					tvDesc.setText(jo.getString("post_content").replaceAll("\\<[^>]*>", ""));
					tvDate.setText(newsDate);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}

