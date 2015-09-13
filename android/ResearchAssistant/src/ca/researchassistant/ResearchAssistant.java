package ca.researchassistant;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ResearchAssistant extends Activity {
	
	protected ResearchAssistant me;
	protected ArrayList<String> surveytitles;
	protected ArrayList<Integer> surveyids;
	protected String partid;
	private static final int STARTSETTINGS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		me = this;
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		partid = i.getExtras().getString("partid");
		
		setContentView(R.layout.activity_main);
		refreshSurveys();
	}
	
	public void doRefresh(View v) {
		refreshSurveys();
	}
	
	protected void refreshSurveys() {
		Participant.setPartName(this, partid);
		
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
				i.putExtra("surveytitle", surveytitles.get(position));
				i.putExtra("partid", partid);
				me.startActivity(i);
			}
		});		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (requestCode == STARTSETTINGS) {
		  refreshSurveys();
	  }
	} 
}
