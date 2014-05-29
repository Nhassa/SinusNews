package com.example.sinusnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.text.Html;
//import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class OneNews extends Activity {
	
	final String LOG_TAG = "sinusLogs";
	
	TextView tvTitle;
	//TextView tvDesc;
	WebView wvDesc;
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
		//tvDesc = (TextView) findViewById(R.id.tvOneDesc);
		wvDesc = (WebView) findViewById(R.id.tvOneDesc);
		tvDate = (TextView) findViewById(R.id.tvOneDate);
		
		//Get news id
		Intent intent = getIntent();
		String newsId = intent.getStringExtra("newsId");
		newsTitle = intent.getStringExtra("newsTitle");
		newsDate = intent.getStringExtra("newsDate");
		
		Log.d(LOG_TAG, "OneNews beforeLoading");
		//tvTitle.setText(newsId);
		
		if( isOnline(this) ){
			Log.d(LOG_TAG, "OneNews isOnline");
			loader = new AsyncOneNewsLoader();
			loader.execute(newsId);
		} else {
			Log.d(LOG_TAG, "OneNews isOffline");
			Toast toast = Toast.makeText(getApplicationContext(), 
					   "Интернета нет!", Toast.LENGTH_SHORT); 
			toast.show();
		}
	}
	
	public static boolean isOnline(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
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
					
					/*Spanned s = Html.fromHtml("<style>p{margin: 5px 0;}</style>" + jo.getString("post_content"));
					tvDesc.setText(s);*/
					
					String html = "<html><head>" +
							"<style>body{color:#777777;font-size:14px;} p{margin: 5px 0;} img{width: 100%;margin-bottom: 5px;} a{color:#52b7fd;}</style>" +
							"</head><body>" + jo.getString("post_content") + 
							"</body></html>";
					String str = "<?xml version='1.0' encoding='UTF-8' ?>" + URLEncoder.encode(html, "UTF-8").replaceAll("\\+"," ");
					wvDesc.loadData(str, "text/html; charset=UTF-8", null);
					tvTitle.setText(newsTitle);
					tvDate.setText(newsDate);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

