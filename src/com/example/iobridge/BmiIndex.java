package com.example.iobridge;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BmiIndex extends Activity {
	
	EditText height,weight,age,gender;
	Button calculate;
	TextView result;
	static int bmi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bmi_index);
		 height = (EditText)findViewById(R.id.editText2);
		 weight = (EditText)findViewById(R.id.editText3);
		 age = (EditText)findViewById(R.id.editText1);
		 gender = (EditText)findViewById(R.id.editText4);
		 result = (TextView)findViewById(R.id.textView4);
		 
		 calculate =(Button)findViewById(R.id.button1);
		 
		 calculate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String i = height.getText().toString();
				int h = Integer.parseInt(i);
				int w = Integer.parseInt(weight.getText().toString());
				bmi=calculateBMI(h,w);
				result.setText("Your BMI is "+ Integer.toString(bmi));
		        
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bmi_index, menu);
		return true;
	}
	
	 static int heightInInches( int f,int i){
	        int in=f*12;
	        int inches=i+in;
	        return inches;
	    }
	    static int weightInPounds(int stone, int p){
	        int po=stone*14;
	        int pounds=p+po;
	        return pounds;
	    }
	    static int calculateBMI(int height,int weight ){
	        int w=weight*703;
	        int h=height*height;
	        int bmi = (int) ((double)w/(double)h);
	        return bmi;
	    }


}
