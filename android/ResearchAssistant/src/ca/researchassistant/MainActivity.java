package ca.researchassistant;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	protected MainActivity me;
	protected ArrayList<String> surveytitles;
	protected ArrayList<Integer> surveyids;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		me = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		refreshSurveys();
	}
	
	public void doRefresh(View v) {
		refreshSurveys();
	}
	
	protected void refreshSurveys() {
		ListView surveylist = (ListView)findViewById(R.id.surveyList);
		
		surveytitles = new ArrayList<String>();
		surveyids = new ArrayList<Integer>();

		Cursor c = Surveys.getSurveys(this);
		
		while (c.moveToNext()) {
			surveytitles.add(c.getString(c.getColumnIndex(Surveys.SURVEYTITLE)));
			surveyids.add(c.getInt(c.getColumnIndex(Surveys.SURVEYID)));
		}
		c.close();
		surveylist.setAdapter(new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				(String[])surveytitles.toArray(new String[surveytitles.size()]))
			);
		registerForContextMenu(surveylist);
		
		surveylist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				Intent i = new Intent(me, SectionsActivity.class);
				i.putExtra("surveyid", surveyids.get(position));
				me.startActivity(i);
			}
		});		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Intent i = new Intent(this, SettingsActivity.class);
	        	this.startActivity(i);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
