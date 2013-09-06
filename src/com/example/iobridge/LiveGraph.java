package com.example.iobridge;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

import com.androidplot.series.XYSeries;
import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class LiveGraph extends Activity  {
	
	private XYPlot mySimpleXYPlot;
	ArrayList<Integer> series1Numbers2 = new ArrayList<Integer>();
	String bpm;
	TextView showBpm;
	Button res;
	 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_graph);
        
        showBpm = (TextView)findViewById(R.id.textView2);
        
        // initialize our XYPlot reference:
        mySimpleXYPlot = (XYPlot) findViewById(R.id.aprLevelsPlot1);
 
        // Create a couple arrays of y-values to plot:
       series1Numbers2 = Graph.series1Numbers1;
       bpm = Long.toString(Graph.BPM);
       showBpm.setText("Your BPM is "+bpm);
        
 
        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
        		series1Numbers2,          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series
 
        // same as above
        XYSeries series2 = new SimpleXYSeries(series1Numbers2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                Color.rgb(0, 100, 0),                   // point color
                null,                                   // fill color (none)
                new PointLabelFormatter(Color.WHITE));                           // text color
 
        // add a new series' to the xyplot:
       
 
        // same as above:
        mySimpleXYPlot.addSeries(series2,
                new LineAndPointFormatter(
                        Color.rgb(0, 0, 200),
                        Color.rgb(0, 0, 100),
                        null,
                        new PointLabelFormatter(Color.TRANSPARENT)));
 
        // reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(3);
        
        res = (Button)findViewById(R.id.button1);
        res.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LiveGraph.this, Results.class);
				startActivity(intent);
				
			}
		});
    }
	
}
