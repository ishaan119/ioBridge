package com.example.iobridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;





import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration.Status;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CollectSensordata extends Activity {

	Button start,stop,graph,bmiIndex,bpm;
	int check = 0;
	Boolean mKeepRunning = true;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_sensordata);
       // sendEmail();
       // sendSms();
        
        start = (Button)findViewById(R.id.button1);
        start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 
				  check = 0;
				  mKeepRunning = true;
				  new MyAsyncTask().execute();
				  
				// new StreamTask().execute();
				
			}
		});
        
        stop = (Button)findViewById(R.id.button2);
        stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				check = 1;
				mKeepRunning = false;
				  new MyAsyncTask().execute();
				
			}
		});
        
        
        
        bmiIndex = (Button)findViewById(R.id.button5);
        bmiIndex.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CollectSensordata.this, BmiIndex.class);
				startActivity(intent);
				
			}
		});
        
        bpm = (Button)findViewById(R.id.button4);
        bpm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CollectSensordata.this, Graph.class);
				startActivity(intent);
				
			}
		});
        
        
    }
        
    

   
    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
    	 
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			
				try {
					postData();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			return null;
		}
 
		protected void onPostExecute(Double result){
			
			Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
		}
		protected void onProgressUpdate(Integer... progress){
			
		}
 
		public void postData() throws JSONException {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://api.realtime.io/v1/gateway/request/register/write");
			httppost.addHeader("X-APIKEY","H4RK8AGYHR04K0LR");
			httppost.addHeader("Content-Type","application/json");
			
			
 
			try {
				
				
				StringEntity se = null;
				
				
				
					System.out.println("Start Executes");
					if(check == 0){
						 
						se = new StringEntity("{\"serial\":\"000277E665B0B90D\",\"channel\":\"2\",\"register\":\"trig.interval\",\"content\": [.3]}");
					}else if(check == 1){
						 se = new StringEntity("{\"serial\":\"000277E665B0B90D\",\"channel\":\"2\",\"register\":\"trig.interval\",\"content\": [0]}");
					}
					
				
				
				
			    
				httppost.setEntity(se);
				
 
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
			   
			  
 
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
 
	}

    private class StreamTask extends AsyncTask<Integer, Integer, Integer> {
    	
    	

    	@Override
    	protected Integer doInBackground(Integer... params) {
    		try {
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpGet request = new HttpGet();
		request.setURI(new URI("http://api.realtime.io/v1/stream?apikey=H4RK8AGYHR04K0LR"));
		HttpResponse response = client.execute(request);
		
		InputStream in = response.getEntity().getContent();
		final byte[] buffer = new byte[250];
		while(true){
			int i = in.read(buffer);
			System.out.println(i);
			String s = new String(buffer);
			System.out.println(s);
		}
		
		//parseTweets(reader);
  
		
		

		} 
	catch (Exception e) {
		Log.e("Twitter", "doInBackground_" + e.toString());
		}
	return new Integer(1);
	}


 



		protected void onProgressUpdate(Integer... progress) {
			
			}

		@Override
		protected void onPostExecute(Integer i) {

			}
    	
    	}
    
    
    public void sendEmail(){
    	Intent i = new Intent(Intent.ACTION_SEND);
    	i.setType("message/rfc822");
    	i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"ishaansutaria@gmail.com"});
    	i.putExtra(Intent.EXTRA_SUBJECT, "Test");
    	i.putExtra(Intent.EXTRA_TEXT   , "Test");
    	try {
    	    startActivity(Intent.createChooser(i, "Send mail..."));
    	} catch (android.content.ActivityNotFoundException ex) {
    	    Toast.makeText(CollectSensordata.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void sendSms(){
    	Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
    			Uri.parse("sms:4088399161")); 
    			smsIntent.putExtra("sms_body", "Hello");
    			startActivity(smsIntent);
    }
    
   
}