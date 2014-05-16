package com.example.sinusnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import android.view.View;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.net.ConnectivityManager; 
import android.net.NetworkInfo; 
import android.content.Context; 
import android.content.Intent;

public class MainActivity extends Activity {

	final String LOG_TAG = "sinusLogs";
	final String ATTR_NAME_ID = "post_id";
	final String ATTR_NAME_TITLE = "post_title";
	final String ATTR_NAME_DESC = "post_content";
	final String ATTR_NAME_DATE = "post_date";
	
	TextView tvInfo;
	TextView tvResult;
	ListView lvResult;
	TextView tvId;
	TextView tvTitle;
	TextView tvDesc;
	TextView tvDate;
	AsyncNewsLoader loader;
	//ArrayList<String> massive;
	//ArrayList<Map<String, String>> result;
	//Map<String, String> hashmap;
	String json_txt = "";
	//ArrayAdapter<String> adapter;
	SimpleAdapter sAdapter;
	ArrayList<Map<String, Object>> massive_map;
	Map<String, Object> m;
	View footer;
	Integer lastNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//screen elements
		 tvInfo = (TextView) findViewById(R.id.tvInfo);
		 tvResult = (TextView) findViewById(R.id.tvResult);
		 lvResult = (ListView) findViewById(R.id.newsListView);
		 tvId = (TextView) findViewById(R.id.tvId);
		 tvTitle = (TextView) findViewById(R.id.tvTitle);
		 tvDesc = (TextView) findViewById(R.id.tvDesc);
		 tvDate = (TextView) findViewById(R.id.tvDate);
		 
		 //massive = new ArrayList<String>();
		 
		 Log.d(LOG_TAG, "onCreate");
		 
		 if( isOnline(this) ){
			 Log.d(LOG_TAG, "isOnline onCreate");
			 Toast toast = Toast.makeText(getApplicationContext(), 
						   "Интернет есть!", Toast.LENGTH_SHORT); 
			 toast.show(); 
			 lastNew = 5;
			 loader = new AsyncNewsLoader();
			 loader.execute(0);
			 
		 } else {
			 Log.d(LOG_TAG, "isOffline");
				Toast toast = Toast.makeText(getApplicationContext(), 
						   "Интернета нет!", Toast.LENGTH_SHORT); 
				toast.show();
		 }
		 
		 //make map structure
		 massive_map = new ArrayList<Map<String, Object>>();
		  m = new HashMap<String, Object>();
		  /* m.put(ATTR_NAME_TITLE, massive);
		 m.put(ATTR_NAME_DESC, massive);
		 massive_map.add(m);*/
		 
		 String[] from = {ATTR_NAME_ID, ATTR_NAME_TITLE, ATTR_NAME_DESC, ATTR_NAME_DATE};
		 int[] to = {R.id.tvId, R.id.tvTitle, R.id.tvDesc, R.id.tvDate};
		 
		 sAdapter = new SimpleAdapter(this, massive_map, R.layout.list_item_map, from, to){
			 //long newsId;
			 /*public long getItemId(int position){
				 
				 return position;
			 }*/
		 };
		 
		 
		 //Footer
		 //footer = getLayoutInflater().inflate(R.layout.footer, null);
		 //lvResult.addFooterView(footer);
		 
		 //create adapter and set it
		// adapter = new ArrayAdapter<String>(this, R.layout.list_item, massive);
		// lvResult.setAdapter(adapter); 
		 lvResult.setAdapter(sAdapter);
		 
		 lvResult.setOnScrollListener(new OnScrollListener(){
			 public void onScrollStateChanged(AbsListView view, int scrollState){}
			 
			 public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
				 Log.d(LOG_TAG, "isScrolling");
				 Log.d(LOG_TAG, "visibleItemCount " + visibleItemCount + " firstVisibleItem " + firstVisibleItem + 
						 " totalItemCount " + totalItemCount + " lastNew " + lastNew);
				 if (view.getAdapter() != null && visibleItemCount > 0 && ( firstVisibleItem + visibleItemCount >= totalItemCount )/* && hasNextPage()*/) {
					 
					 if( isOnline(MainActivity.this) ){
						 Log.d(LOG_TAG, "isOnline onScroll");
						 if( totalItemCount == lastNew ) {
							 Log.d(LOG_TAG, "loadSecondNews");
							  
							 loader = new AsyncNewsLoader();
							 loader.execute(totalItemCount);
							 lastNew = lastNew + 5;
						 }
					 } else {
						 Log.d(LOG_TAG, "isOffline");
							Toast toast = Toast.makeText(getApplicationContext(), 
									   "Интернета нет!", Toast.LENGTH_SHORT); 
							toast.show();
					 }
	             }
			 }
		 });
		 
		 lvResult.setOnItemClickListener( new OnItemClickListener(){
			 public void onItemClick(AdapterView<?> parent, View view,
			          int position, long id)
			 {
				 TextView tvId = (TextView) view.findViewById(R.id.tvId);
				 String newsId = tvId.getText().toString();
				 TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
				 String newsTitle = tvTitle.getText().toString();
				 TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
				 String newsDate = tvDate.getText().toString();
				 
				 Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + newsId);
				 
				 Intent intent = new Intent(MainActivity.this, OneNews.class);
				 intent.putExtra("newsId", newsId);
				 intent.putExtra("newsTitle", newsTitle);
				 intent.putExtra("newsDate", newsDate);
				 startActivity(intent);
			 }
		 });
		
	}
	
	protected void onStart(){
		super.onStart();
		Log.d(LOG_TAG, "onStart");
		
		if( isOnline(this) ){
			Log.d(LOG_TAG, "isOnline onStart");
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
	
	//AsyncTask
	private class AsyncNewsLoader extends AsyncTask<Integer, Void, String> {
		protected void onPreExecute() {
			super.onPreExecute();
		      	tvInfo.setText("Новости: загружаются...");
		      	Log.d(LOG_TAG, "startLoading");
		}
		
		protected String doInBackground(Integer... params){
			Integer param = 0;
			if( params.length > 0 ){
            	param = params[0];		    	
            }
			
			try{
				//TimeUnit.SECONDS.sleep(2);
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://sinustech.net/android_news.php?n=" + param);
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
		      	tvInfo.setText("Новости:");
		      	//tvResult.setText(json_txt);
		      	
		      	Log.d(LOG_TAG, "endLoading");
		      	
		      //JSON parsing
				JSONArray ja = null;
				try {
					ja = new JSONArray(json_txt);
					for(int i=0; i<ja.length(); i++){
						JSONObject jo = ja.getJSONObject(i);
						m = new HashMap<String, Object>();
						m.put(ATTR_NAME_ID, jo.getString("ID"));
						m.put(ATTR_NAME_TITLE, jo.getString("post_title"));
						m.put(ATTR_NAME_DESC, jo.getString("post_content").replaceAll("\\<[^>]*>", ""));
						m.put(ATTR_NAME_DATE, jo.getString("post_date"));
						massive_map.add(m);
						//massive.add(jo.getString("post_title"));
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				//say adapter - smth changed
				//adapter.notifyDataSetChanged();
				sAdapter.notifyDataSetChanged();
				
				
		}
		      	
	}
	
	

}
