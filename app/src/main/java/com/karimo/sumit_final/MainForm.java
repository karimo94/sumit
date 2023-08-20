package com.karimo.sumit_final;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.app.AlertDialog;

public class MainForm extends Activity 
{
	private Summarizer s; 
	private AlertDialog.Builder aboutWindow; 
	private Spinner spn;
	private ProgressBar progressBar;
	private boolean apiUsedFlag = false;
	private Button summarizeButton;

	@SuppressLint("ClickableViewAccessibility")
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_form);
        
        s = new Summarizer();
        
        //set the default value for spinner to be 5
        Spinner selector = findViewById(R.id.spinner1);
        selector.setSelection(2);

        
        //prevent the keyboard from popping up when the app opens up (only when user clicks on edit text)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        
        //set the edittext to be scrollable
        EditText e =  findViewById(R.id.editext);
        e.setOnTouchListener(new OnTouchListener()
        {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent mEvent) {
				if(v.getId() == R.id.editext)
				{
					v.getParent().requestDisallowInterceptTouchEvent(true);
					switch(mEvent.getAction()&MotionEvent.ACTION_MASK)
					{
					case MotionEvent.ACTION_UP:
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
					}
				}
				return false;
			}
        });
        
        
        //set onclicklistener for summarize button so that progressbar fires during processing
        summarizeButton = (Button) findViewById(R.id.summarizeButton); 
        OnClickListener listener = new OnClickListener() {
    	    public void onClick(View view) {
    	    	showProgressBar();
    	        switch (view.getId()) {
	    	        case R.id.summarizeButton:
	    	            new DoProgressBarWhileProcessing().execute();
	    	            break;
    	        }
    	    }
        };
        summarizeButton.setOnClickListener(listener);
        
        PopulateSpinner();  
        AppRater.app_launched(this);
        
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void SendActivity(View v)
    {   
    	//when the button is clicked, 
    	//if there's a url, process it
    	//get the text from the url
    	//the logic behind that handles if there's just regular text (non-url)
    	//then, if an API was used to grab the text (in this case a full summary done)
    	//we can just go to next screen
    	//otherwise, call on the summarizer class to summarize the text
    	
        Intent sumScreen = new Intent(MainForm.this, SummaryScreen.class);
        
        //this runs on a separate thread from main
        String originalText = ParseArticleText(); 
        
        //if the api was not used, we can call on our summarizer algoritm
        String jobResultString = null;
        
        if(!apiUsedFlag) {
        	
        	//call on the summarizer class
        	jobResultString = SummarizeText(originalText); 
        }
        else {
        	
        	//we used the api
        	jobResultString = originalText;
        }

        
        //global variable prevents large transaction
        SummaryScreen.summarizedAllText = new StringBuilder(jobResultString); 

    	startActivity(sumScreen);
    }
    public void showProgressBar(){
    	progressBar = (ProgressBar) findViewById(R.id.progressBar1);
    	progressBar.setVisibility(View.VISIBLE);
    }
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private String ParseArticleText()
    {
    	//first parse and process the text from the url
    	EditText e = findViewById(R.id.editext);
    	
    	Spinner mainSpinner = findViewById(R.id.spinner1);
    	int summarySize = (Integer)mainSpinner.getSelectedItem();
    	
    	//set string to the text inside the main text box
    	String originalText = e.getText().toString();
    	
    	if(originalText.equals("") || originalText.equals(" "))
    	{
    		return s.Summarize("", 1);
    	}
    	
    	String testString = originalText;
    	

		//thread based processing
		DoURLParseThread parseUrlThread = null; 
		
        //this works for http/https, but if a url just has www.google.com, we need to account
        if(originalText.contains("www.") && originalText.contains(".com") && !originalText.contains("http")){
            originalText = "https://" + originalText;
            testString = originalText;
        }
    	if(isValidURL(originalText))
    	{
    		//thread call
    		parseUrlThread = new DoURLParseThread(testString, summarySize);
    		
    		Thread rc = new Thread(parseUrlThread);
    		rc.start();
    		try
			{
				rc.join();
			} catch (InterruptedException e1)
			{
				
				e1.printStackTrace();
			}
    		
    		originalText = parseUrlThread.getUrlText().toString();
    		apiUsedFlag = parseUrlThread.getApiUsedFlag();
    	}
    	
    	return originalText;
    }
	private String SummarizeText(String originalText) 
	{
		//call on the summarizer class through async thread 
		
		//set current value selected in spinner to integer
        Spinner mainSpinner = findViewById(R.id.spinner1);
    	int summarySize = (Integer)mainSpinner.getSelectedItem();
    	String jobResultString = null;

    	jobResultString = s.Summarize(originalText, summarySize);

		return jobResultString;
	}
    public void ClearText(View v)
    {
    	TextView mainText = findViewById(R.id.editext);
    	mainText.setText("");
    	return;
    }
    public void PasteText(View v) 
    {
    	//paste text from the clipboard
    	TextView mainText = findViewById(R.id.editext);
    	ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
		ClipData cData = clipboard.getPrimaryClip();
		ClipData.Item item;
		String text = "";
		
		if(cData != null)
		{
			//we can't be certain the clipboard has actual data
			item = cData.getItemAt(0);
			try 
			{
				text = item.getText().toString();
			} catch (Exception e)
			{
				
				e.printStackTrace();
			}
	    	mainText.append(text);
	    	return;
		}
		else
		{
			item = null;
			mainText.append(text);
			return;
		}
    }
    private void PopulateSpinner() 
    {
    	//populate the spinner which holds summary size (# of sentences)
        spn = findViewById(R.id.spinner1);
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 10; i < 80; i += 5)
        {
        	list.add(i);
        }
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(dataAdapter);
    	return;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_form, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        //here we implement what happens when either "About" or "Settings" is pressed...
        //respond to menu item selection
    	int id = item.getItemId();
    	if(id == R.id.about)
    	{
    		AboutWindow();
    		return true;
    	}
    	else
    	{
    		return super.onOptionsItemSelected(item);
    	}
    }
    private void AboutWindow()
    {
        //the code below is for the about window
        aboutWindow = new AlertDialog.Builder(this);
        final String website = " simpledevcode.wordpress.com";
        final String AboutDialogMessage = " SumIt! Version 3.0\n By Karim Oumghar\n\n Website for contact:\n";
        final TextView tx = new TextView(this);
        tx.setText(AboutDialogMessage + website);
        tx.setAutoLinkMask(RESULT_OK);
        tx.setTextColor(Color.BLACK);
        tx.setTextSize(15);
        tx.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(tx, Linkify.WEB_URLS);
        
        aboutWindow.setIcon(R.drawable.ic_launcher);
        aboutWindow.setTitle("About");
    	aboutWindow.setView(tx);
    	
    	aboutWindow.setPositiveButton("OK", new DialogInterface.OnClickListener() 
    	{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
    	});
    	aboutWindow.show();
    }
    public static boolean isValidURL(String urlString)
    {
	    try
	    {
	        URL url = new URL(urlString);
	        url.toURI();
	        return true;
	    } 
	    catch (Exception exception)
	    {
	        return false;
	    }
	}

    public class DoProgressBarWhileProcessing extends AsyncTask<Integer, Integer, String> {

		@RequiresApi(api = Build.VERSION_CODES.KITKAT)
		@Override
		protected String doInBackground(Integer... params)
		{
			SendActivity(getCurrentFocus());
			
			return null;
		}
		@Override
        protected void onPostExecute(String result) {
			if(progressBar != null)
				progressBar.setVisibility(View.GONE);

        }
        @Override
        protected void onPreExecute() {
        	
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    	
    }
}