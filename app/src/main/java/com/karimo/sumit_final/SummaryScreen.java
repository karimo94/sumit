package com.karimo.sumit_final;



//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;


public class SummaryScreen extends Activity 
{
	EditText e;
	static StringBuilder summarizedAllText;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary_screen);
		
		//set the action bar text to SumtIt!
		setTitle("SumIt!");
		
		//assign the text of the summary textbox to the summary text
		e = (EditText) findViewById(R.id.summaryTextBox);
		
    	//first clear the summary screen text box
    	e.setText("");
		

		e.setText(summarizedAllText);
    	e.setScroller(new Scroller(this));
    	
		//loading advert
		//AdView banner = (AdView) findViewById(R.id.adView);

		//AdRequest ad_req = new AdRequest.Builder().build();

		//banner.loadAd(ad_req);
	}
	@Override
	protected void onStart(){
		super.onStart();
		e.requestFocus(View.FOCUS_UP);

    	e.setKeyListener(null);//make the textbox readonly
	}
	public void Share(View v)
	{
		String shareBody = e.getText().toString();
	    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	        sharingIntent.setType("text/plain");
	        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
	        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	        startActivity(Intent.createChooser(sharingIntent, "Share using:"));
	}
	public void SaveSummary(Context c)
	{
		//IMPLEMENT
		//save the current text
		//give each save name as the date like so: summary of Dec. 1, 2015...
		//when its saved, go to the save screen
		
		return;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary_screen, menu);
		return true;
	}
}
