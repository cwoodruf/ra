package ca.researchassistant;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

public class SectionDump extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		int surveyid = i.getExtras().getInt("surveyid");
		int sectionid = i.getExtras().getInt("sectionid");
		
		setContentView(R.layout.activity_section_dump);
		String sectiondata = Surveys.getSection(this, surveyid, sectionid);
		TextView sd = (TextView)findViewById(R.id.sectiondata);
		sd.setText(sectiondata);
		
	}
}
