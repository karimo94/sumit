package com.karimo.sumit_final;

//import java.sql.SQLException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.ListView;

public class SavesScreen extends Activity 
{
	//private NotesDBAdapter ndb;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saves_screen);
//		String[] phrases = {"aomar", "kiko", "el wayway", "ey ey", "stik tik tik"};
//		ListView lv = (ListView)findViewById(R.id.listView1);
//		SavesArrayAdapter s = new SavesArrayAdapter(phrases, this);
//		lv.setAdapter(s);
//		ndb = new NotesDBAdapter(this);
//		try 
//		{
//			ndb.open();
//		} 
//		catch (SQLException e) 
//		{
//			e.printStackTrace();
//		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.saves_screen, menu);
		return true;
	}
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        //here we implement what happens when either "About" or "Settings" is pressed...
        //respond to menu item selection
//    	switch(item.getItemId())
//    	{
//    		case R.id.back_to_main: 
//    			GoToMain();
//    			return true;
//    		case R.id.delete_all_sums:
//    			deleteAll();
//    			return true;
//    		default:
//    			return super.onOptionsItemSelected(item);
//    	}
    	int id = item.getItemId();
    	if(id == R.id.back_to_main)
    	{
    		GoToMain();
    		return true;
    	}
    	if(id == R.id.delete_all_sums)
    	{
    		deleteAll();
    		return true;
    	}
    	else
    	{
    		return super.onOptionsItemSelected(item);
    	}
    }
	public void openSummary()
	{
		//to open a summary, read in from the saved text file
		//and display the text to the summary screen
	}
	@SuppressWarnings("unused")
	private void deleteSummary()
	{
		//just remove a selected summary from memory
		//swipe left
		//or long click
	}
	private void deleteAll()
	{
		//go thru and remove all summaries
		//
	}
	private void GoToMain()
	{
		//go back to the main screen
		finish();
	}
}
