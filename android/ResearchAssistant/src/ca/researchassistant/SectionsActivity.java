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
import android.widget.TextView;

public class SectionsActivity extends Activity {
	protected SectionsActivity me;
	protected ArrayList<String> sectionnames;
	protected ArrayList<Integer> sectionids;
	protected int surveyid;
	protected String surveytitle;
	protected String partid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me = this;
		Intent i = getIntent();
		surveyid = i.getExtras().getInt("surveyid");
		surveytitle = i.getExtras().getString("surveytitle");
		partid = i.getExtras().getString("partid");
		
		setContentView(R.layout.activity_sections);
		refreshSections();
	}
	
	
	public void doRefresh(View v) {
		refreshSections();
	}
	
	protected void refreshSections() {
		Participant.setPartName(this, partid);
		
		ListView surveylist = (ListView)findViewById(R.id.sectionlist);
		
		// TODO: refresh dynamically?
		TextView title = (TextView)findViewById(R.id.surveytitle);
		title.setText("Survey: "+surveytitle);
		
		sectionnames = new ArrayList<String>();
		sectionids = new ArrayList<Integer>();

		Cursor c = Surveys.getSections(this,surveyid);
		
		while (c.moveToNext()) {
			sectionnames.add(c.getString(c.getColumnIndex(Surveys.SECTIONNAME)));
			sectionids.add(c.getInt(c.getColumnIndex(Surveys.SECTIONID)));
		}
		c.close();
		surveylist.setAdapter(new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				(String[])sectionnames.toArray(new String[sectionnames.size()]))
			);
		registerForContextMenu(surveylist);
		
		surveylist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				//Intent i = new Intent(me, SectionDump.class);
				Intent i = new Intent(me, DisplaySection.class);
				i.putExtra("surveyid", surveyid);
				i.putExtra("sectionid", sectionids.get(position));
				i.putExtra("participant", partid);
				me.startActivity(i);
			}
		});		
	}

}
