package ca.researchassistant;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

import org.apache.commons.io.IOUtils;

public class DisplaySection extends Activity {
	protected FlingView mWebView;
	protected DisplaySection me;
	protected String sectiondata;
	protected boolean finishedLoading;
	protected int surveyid;
	protected int sectionid;
	protected String participant;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		me = this;
		Intent i = getIntent();
		
		surveyid = i.getExtras().getInt("surveyid");
		if (surveyid <= 0) 
			Toast.makeText(this, "Error: invalid surveyid", Toast.LENGTH_LONG).show();
		
		sectionid = i.getExtras().getInt("sectionid");
		if (sectionid <= 0)
			Toast.makeText(this, "Error: invalid sectionid", Toast.LENGTH_LONG).show();
		
		participant = i.getExtras().getString("participant");
		if (participant.length() == 0)
			Toast.makeText(this, "Error: need a participant", Toast.LENGTH_LONG).show();
		
		refreshSection();
	}
	
	protected void refreshSection() {
		if (surveyid > 0 && sectionid > 0 && participant.length() > 0) {
			setContentView(R.layout.display_section);

			Participant.setPartName(this, participant);
			
			TextView survey_name = (TextView)findViewById(R.id.survey_name);
			String surveytitle = Surveys.getSurveyTitle(this, surveyid);
			String sectionname = Surveys.getSectionName(this, surveyid, sectionid);
			survey_name.setText(surveytitle+": "+sectionname);
			
			
			sectiondata = "";
			mWebView = (FlingView)findViewById(R.id.flingView1);
			mWebView.getSettings().setJavaScriptEnabled(true);
			
			// http://stackoverflow.com/questions/4087674/android-read-text-raw-resource-file
			InputStream is = getResources().openRawResource(R.raw.question);
			String html = "";
			try {
				html = IOUtils.toString(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOUtils.closeQuietly(is); // don't forget to close your streams
	
			finishedLoading = false;
			
			sectiondata = Surveys.getSection(me, surveyid, sectionid);
			Log.v("WebTest","local:\n"+sectiondata);
	
			Saver saver = new Saver(me, participant, surveyid, sectionid, sectiondata);
			Log.v("WebTest","from saver:\n"+saver.getSectiondata());
			
			mWebView.addJavascriptInterface(saver, "Saver");
			mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
			
		} else {
			setContentView(R.layout.web_fail);
		}
	}	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_section, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.refresh_section:
	        	refreshSection();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
