package com.example.sinusnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
//import android.util.JsonReader;
import android.view.Menu;
import android.widget.*;
import android.net.ConnectivityManager; 
import android.net.NetworkInfo; 
import android.content.Context; 

public class MainActivity extends Activity {

	final String LOG_TAG = "sinusLogs";
	
	TextView tvInfo;
	TextView tvResult;
	ListView lvResult;
	AsyncNewsLoader loader;
	ArrayList<String> massive;
	//ArrayList<Map<String, String>> result;
	//Map<String, String> hashmap;
	String json_txt = "";
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//screen elements
		 tvInfo = (TextView) findViewById(R.id.tvInfo);
		 tvResult = (TextView) findViewById(R.id.tvResult);
		 lvResult = (ListView) findViewById(R.id.newsListView);
		 
		 massive = new ArrayList<String>();
		 
		 Log.d(LOG_TAG, "onCreate");
		 
		 if( isOnline(this) ){
			 Log.d(LOG_TAG, "isOnline");
			 Toast toast = Toast.makeText(getApplicationContext(), 
						   "Интернет есть!", Toast.LENGTH_SHORT); 
			 toast.show(); 
			 loader = new AsyncNewsLoader();
			 loader.execute();
		 } else {
			 Log.d(LOG_TAG, "isOffline");
				Toast toast = Toast.makeText(getApplicationContext(), 
						   "Интернета нет!", Toast.LENGTH_SHORT); 
				toast.show();
		 }
		 
		
		 //JSON parsing
		/*	JSONArray ja = null;
			try {
				ja = new JSONArray(json_txt);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String title = "";
			 
			 result = new ArrayList<Map<String, String>>();
			 for(int i=0; i < ja.length(); i++){
				 try {
					 JSONObject post_obj = ja.getJSONObject(i);
					 title = post_obj.getString("post_title");
				 } catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				 }
				 hashmap = new HashMap<String, String>();
				 hashmap.put("title", title);
				 hashmap.put("desc", "desc"+i);
				 result.add(hashmap);
			 }
			 
			// массив имен атрибутов, из которых будут читаться данные
			String[] from = { "title" };
			// массив ID View-компонентов, в которые будут вставлять данные
			int[] to = { R.id.tvTitle };*/
		 
		 //create adapter and set it
		 adapter = new ArrayAdapter<String>(this, R.layout.list_item, massive);
		 lvResult.setAdapter(adapter);
		 
		 
		 
		
	}
	
	protected void onStart(){
		super.onStart();
		if( isOnline(this) ){
			Toast toast = Toast.makeText(getApplicationContext(), 
					   "Интернет есть!", Toast.LENGTH_SHORT); 
			toast.show(); 
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), 
					   "Интернета нет!", Toast.LENGTH_SHORT); 
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
	
	private class AsyncNewsLoader extends AsyncTask<Void, Void, String> {
		protected void onPreExecute() {
			super.onPreExecute();
		      	tvInfo.setText("Latest news: загружаются...");
		      	Log.d(LOG_TAG, "startLoading");
		}
		
		protected String doInBackground(Void... params){
			try{
				//TimeUnit.SECONDS.sleep(2);
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://sinustech.net/android_news.php");
				HttpResponse response = client.execute(request);
				
				//Get response
				InputStreamReader is = new InputStreamReader(response.getEntity().getContent());
				//JsonReader jr = new JsonReader(is);
				//jr.beginArray();
				BufferedReader reader = new BufferedReader(is);
				
				//StringBuilder sb = new StringBuilder();
				String line = "";
				
				while((line = reader.readLine()) != null){
					json_txt = line;
				}
				

				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			return json_txt;
		}
		
		protected void onPostExecute(String json_txt) {
			super.onPostExecute(json_txt);
		      	tvInfo.setText("Latest news:");
		      	//tvResult.setText(json_txt);
		      	
		      	Log.d(LOG_TAG, "endLoading");
		      	
		      //JSON parsing
				JSONArray ja = null;
				try {
					ja = new JSONArray(json_txt);
					for(int i=0; i<ja.length(); i++){
						JSONObject jo = ja.getJSONObject(i);
						massive.add(jo.getString("post_title"));
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				adapter.notifyDataSetChanged();
				
				/*String title = "";
				 
				 result = new ArrayList<Map<String, String>>();
				 for(int i=0; i < ja.length(); i++){
					 try {
						 JSONObject post_obj = ja.getJSONObject(i);
						 title = post_obj.getString("post_title");
					 } catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					 }
					 hashmap = new HashMap<String, String>();
					 hashmap.put("title", title);
					 hashmap.put("desc", "desc"+i);
					 result.add(hashmap);
					 adapter.notifyDataSetChanged();
				 }*/
		}
		      	
	}

}
