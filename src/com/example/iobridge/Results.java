package com.example.iobridge;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Results extends Activity {
	
	int bmi;
	int bpm;
	TextView t1,t2;
	Button email,sms,hospital;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		t1 = (TextView)findViewById(R.id.textView1);
		t2 = (TextView)findViewById(R.id.textView2);
		bmi = BmiIndex.bmi;
		bpm = (int) (Graph.BPM);
		
		t1.setText("Your BPM is "+Integer.toString(bpm));
		t2.setText("Your BMI indes is "+Integer.toString(bmi));
		
		email = (Button)findViewById(R.id.button1);
		email.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendEmail();
				
			}
		});
		
		sms = (Button)findViewById(R.id.button2);
		sms.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendSms();
				
			}
		});
		hospital = (Button)findViewById(R.id.button3);
		hospital.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Results.this, Hospitals.class);
				startActivity(intent);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
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
    	    Toast.makeText(Results.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void sendSms(){
    	Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
    			Uri.parse("sms:4088399161")); 
    			smsIntent.putExtra("sms_body", "Hello");
    			startActivity(smsIntent);
    }

}
