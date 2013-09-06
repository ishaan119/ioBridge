package com.example.iobridge;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Graph extends Activity {
	
	volatile long rate[];                    // used to hold last ten IBI values
	volatile long sampleCounter = 0;          // used to determine pulse timing
	volatile long lastBeatTime = 0;           // used to find the inter beat interval
	volatile int P =512;                      // used to find peak in pulse wave
	volatile int T = 512;                     // used to find trough in pulse wave
	volatile int thresh = 512;                // used to find instant moment of heart beat
	volatile int amp = 100;                   // used to hold amplitude of pulse waveform
	volatile boolean firstBeat = true;        // used to seed rate array so we startup with reasonable BPM
	volatile boolean secondBeat = true;       // used to seed rate array so we startup with reasonable BPM
	volatile boolean Pulse = false;
	static long BPM;

	
	boolean runhttp = true;
	private SimpleXYSeries aprLevelsSeries = null;
	 private XYPlot mySimpleXYPlot = null;
	 static ArrayList<Integer> series1Numbers1 = new ArrayList<Integer>(); 
	
	 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rate = new long[10];
		setContentView(R.layout.activity_graph);
		
		final ImageView i1 = (ImageView)findViewById(R.id.imageView1);
		final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
	    animation.setDuration(500); // duration - half a second
	    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
	    animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
	    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
	  
	    i1.startAnimation(animation);
	    i1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  v.clearAnimation();
				
			}
		});
	   
		
		
        // add a new series' to the xyplot:
		
		
		
		
		 new StreamTask().execute();
	}
	
	public void set_lcd(String message)
	{
		 try {
	        	StringEntity se = null;
	        	HttpClient httpclient = new DefaultHttpClient();
	        	HttpPost httppost = new HttpPost("http://api.realtime.io/v1/gateway/send");
	        	httppost.addHeader("X-APIKEY","H4RK8AGYHR04K0LR");
	        	httppost.addHeader("Content-Type","application/json");
	        	HttpResponse response;
	        	
	        	
	        		se = new StringEntity("{\"serial\":\"000277E665B0B90D\",\"channel\":\"0000\",\"encoding\":\"base64\",\"payload\":\"" + message + "\"}");
	        		httppost.setEntity(se);         		
	        		// Execute HTTP Post Request
	        		response = httpclient.execute(httppost);
	        		response.getEntity();          		
	        	
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}
	
	public void set_led(String message)
	{
		 try {
	        	StringEntity se = null;
	        	HttpClient httpclient = new DefaultHttpClient();
	        	HttpPost httppost = new HttpPost("http://api.realtime.io/v1/gateway/request/register/write");
	        	httppost.addHeader("X-APIKEY","H4RK8AGYHR04K0LR");
	        	httppost.addHeader("Content-Type","application/json");
	        	HttpResponse response;
	        	
	        	
	        		se = new StringEntity("{\"serial\":\"000277E665B0B90D\",\"channel\":\"2\",\"register\":\"gpio.value\",\"content\": " + message + "}");
	        		
	        		httppost.setEntity(se);         		
	        		// Execute HTTP Post Request
	        		response = httpclient.execute(httppost);
	        		response.getEntity();          		
	        	
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}
	
private class StreamTask extends AsyncTask<Integer, Integer, Integer> {
    	
    
    	
    	
    	@Override
    	protected Integer doInBackground(Integer... params) {
    		 BPM = 0;
    		String match;
    		
    		try {
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpGet request = new HttpGet();
		request.setURI(new URI("http://api.realtime.io/v1/stream?apikey=H4RK8AGYHR04K0LR"));
		HttpResponse response = client.execute(request);
		
		InputStream in = response.getEntity().getContent();
		final byte[] buffer = new byte[250];
		new ThreadTest("Google").start();
		new ThreadTest1("LED_Toggle").start();
		while(runhttp){
			in.read(buffer);
			String s = new String(buffer);
			Pattern pattern = Pattern.compile("gpio \\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+");
			Matcher matcher = pattern.matcher(s);
			
			while (matcher.find()) {
				match = matcher.group();
				int value = Integer.parseInt(match.substring(match.lastIndexOf(",") + 1));
				series1Numbers1.add(value);
				System.out.println(series1Numbers1.size());
				
				if(value != 0){
					long tmpBPM = calculateBPM(value);
					if (tmpBPM != 0) {
						BPM = tmpBPM;
					}	
				}
				
            }
		}
		
		
		System.out.println("Connection closed");
		client.getConnectionManager().shutdown();
		System.out.println("Connection closed");
		if (BPM > 100) {
			set_lcd("/kNNZWFzdXJpbmcuLi4=");
			set_led("[\"0\", \"32\", \"0\", \"1000\", \"0\", \"0\", \"1023\", \"957\", \"95\", \"0\", \"0\", \"0\"]");
		} else if (BPM < 60) {
			set_lcd("/kNZb3UgTWlnaHQgQmUgRHlpbmch");
			set_led("[\"0\", \"32\", \"0\", \"1000\", \"0\", \"0\", \"1023\", \"957\", \"95\", \"0\", \"0\", \"0\"]");	
		} else {
			set_lcd("/kNZb3UncmUgSGVhbHRoeSE=");
			set_led("[\"0\", \"23\", \"0\", \"1000\", \"0\", \"1000\", \"1023\", \"964\", \"89\", \"0\", \"0\", \"0\"]}");
		}
		
		
		
	
		
		} 
    		
	catch (Exception e) {
		Log.e("Twitter", "doInBackground_" + e.toString());
		e.printStackTrace();
		}
    		
    	
	return new Integer(1);
	}

    	public long calculateBPM(int Signal)
    	{
    	    long IBI = 0;
    	    long runningTotal;
    	    long N = 0;
    	    long BPM = 0;
    	    
    	    sampleCounter += 200;					// keep track of the time in mS with this variable
    	    N = sampleCounter - lastBeatTime;		// monitor the time since the last beat to avoid noise	    	//  find the peak and trough of the pulse wave
    	    if(Signal < thresh && N > (IBI/5)*3) {       // avoid dichrotic noise by waiting 3/5 of last IBI
    	    	if (Signal < T){                        // T is the trough
    	    		T = Signal;                         // keep track of lowest point in pulse wave 
    	    	}
    	   	}
    	      
    	   	if(Signal > thresh && Signal > P){          // thresh condition helps avoid noise
    	   		P = Signal;                             // P is the peak
    	   	}                                        // keep track of highest point in pulse wave
    	    
    	    	//  NOW IT'S TIME TO LOOK FOR THE HEART BEAT
    	    	// signal surges up in value every time there is a pulse
    	    	if (N > 250){                                   // avoid high frequency noise

    /*	    		if ( (Signal > thresh) && (Pulse == false) && (N > (IBI/5)*3) ) { */
    	    		if ( (Signal > thresh) && (Pulse == false)) {
    	    			Pulse = true;                               // set the Pulse flag when we think there is a pulse
    	    			IBI = sampleCounter - lastBeatTime;         // measure time between beats in mS
    	    			lastBeatTime = sampleCounter;               // keep track of time for next pulse
    	         
    	    			if(firstBeat){                         // if it's the first time we found a beat, if firstBeat == TRUE
    	    				firstBeat = false;                 // clear firstBeat flag
    	    				return 0;                            // IBI value is unreliable so discard it
    	    			}   
    	    			if(secondBeat){                        // if this is the second beat, if secondBeat == TRUE
    	    				secondBeat = false;                 // clear secondBeat flag
    	    				for(int j=0; j<=9; j++){         // seed the running total to get a realisitic BPM at startup
    	    					rate[j] = IBI;                      
    	                    }
    	    			}
    	          
    	    			// keep a running total of the last 10 IBI values
    	    			runningTotal = 0;

    	    			for(int j=0; j<=8; j++){                // shift data in the rate array
    	    				rate[j] = rate[j+1];        	    // and drop the oldest IBI value 
    	    				runningTotal += rate[j];            // add up the 9 oldest IBI values
    	    			}
    	    			rate[9] = IBI;                          // add the latest IBI to the rate array
    	    			runningTotal += rate[9];                // add the latest IBI to runningTotal
    	    			runningTotal /= 10;                     // average the last 10 IBI values 
    	    			System.out.println("Pulse Up");
    	    			BPM = 60000/runningTotal;               // how many beats can fit into a minute? that's BPM!
    	    			System.out.println("BPM = " + BPM);
    	    		}                       
    	    	}

    	    	if (Signal < thresh && Pulse == true){     // when the values are going down, the beat is over
    	    		Pulse = false;                         // reset the Pulse flag so we can do it again
    	    		amp = P - T;                           // get amplitude of the pulse wave
    	    		thresh = amp/2 + T;                    // set thresh at 50% of the amplitude
    	    		P = thresh;                            // reset these for next time
    	    		T = thresh;
    	    		System.out.println("Pulse Down");
    	    	}
    	  
    	    	if (N > 2500){                             // if 2.5 seconds go by without a beat
    	    		thresh = 512;                          // set thresh default
    	    		P = 512;                               // set P default
    	    		T = 512;                               // set T default
    	    		lastBeatTime = sampleCounter;          // bring the lastBeatTime up to date        
    	    		firstBeat = true;                      // set these to avoid noise
    	    		secondBeat = true;                     // when we get the heartbeat back
    	    	}
    	    
    	    return BPM;
    	}
 



		protected void onProgressUpdate(Integer... progress) {
			
			}

		@Override
		protected void onPostExecute(Integer i) {
			
			
			dialog();

			}
    	
    	}

class ThreadTest extends Thread {
    public ThreadTest(String str) {
        super(str);
    }
 
    public void run() {
        try {
			sleep(15000);
			
			runhttp = false;
			if (BPM > 100) {
				set_led("[\"0\", \"32\", \"0\", \"1000\", \"0\", \"0\", \"1023\", \"957\", \"95\", \"0\", \"0\", \"0\"]");
			} else if (BPM < 60) {
				set_led("[\"0\", \"32\", \"0\", \"1000\", \"0\", \"0\", \"1023\", \"957\", \"95\", \"0\", \"0\", \"0\"]");	
			} else {
				set_led("[\"0\", \"23\", \"0\", \"1000\", \"0\", \"1000\", \"1023\", \"964\", \"89\", \"0\", \"0\", \"0\"]}");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

class ThreadTest1 extends Thread {
    public ThreadTest1(String str) {
        super(str);
    }
 
    public void run() {
        try {
        	set_lcd("/kNNZWFzdXJpbmcuLi4=");
        	while (runhttp) {
        		sleep(500);
        		set_led("[\"0\", \"23\", \"0\", \"1000\", \"0\", \"1000\", \"1023\", \"964\", \"89\", \"0\", \"0\", \"0\"]}");
        		sleep(500);
        		set_led("[\"0\", \"33\", \"0\", \"0\", \"0\", \"0\", \"1023\", \"962\", \"99\", \"0\", \"0\", \"0\"]}");       		
        		
        		
        		
        	}
        	if (BPM > 100) {
    			
    			set_led("[\"0\", \"32\", \"0\", \"1000\", \"0\", \"0\", \"1023\", \"957\", \"95\", \"0\", \"0\", \"0\"]");
    		} else if (BPM < 60) {
    			
    			set_led("[\"0\", \"32\", \"0\", \"1000\", \"0\", \"0\", \"1023\", \"957\", \"95\", \"0\", \"0\", \"0\"]");	
    		} else {
    			
    			set_led("[\"0\", \"23\", \"0\", \"1000\", \"0\", \"1000\", \"1023\", \"964\", \"89\", \"0\", \"0\", \"0\"]}");
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

	void dialog() {
	AlertDialog.Builder alert = new AlertDialog.Builder(this);

	alert.setTitle("Alert");
	alert.setCancelable(false);
	alert.setMessage("Generating Graph");


	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Intent intent = new Intent(Graph.this, LiveGraph.class);
			startActivity(intent);
		}
	});

	alert.show();
}


}
