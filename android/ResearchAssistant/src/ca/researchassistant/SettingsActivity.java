package ca.researchassistant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	protected final String LOG_TAG = "SettingsActivity";
	protected final String GET_SIGKEY = "Get sigkey"; 
	protected final String GET_SURVEYS = "Get surveys";
	protected SettingsActivity me;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		me = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		EditText login = (EditText)findViewById(R.id.edit_login);
		login.setText(Credentials.getLogin(this));
		
		EditText password = (EditText)findViewById(R.id.edit_password);
		password.setText(Credentials.getPw(this));
		
		EditText sigkey = (EditText)findViewById(R.id.edit_sigkey);
		sigkey.setText(Credentials.getSigkey(this));
		
		EditText uri = (EditText)findViewById(R.id.edit_uri);
		uri.setText(Credentials.getUrl(this));
		
		enableUpdateButtons();
		
	}

	protected void updateSigkey() {
 	   EditText sigkey = (EditText) findViewById(R.id.edit_sigkey);
 	   sigkey.setText(Credentials.getSigkey(this));
 	   enableUpdateButtons();
	}
	
	protected void enableUpdateButtons() {
		Button up = (Button)this.findViewById(R.id.update_surveys);
		Button sk = (Button)this.findViewById(R.id.getsigkey);
		if (Credentials.canUpdateSurveys(this)) {
			up.setEnabled(true);
			sk.setEnabled(true);
		} else {
			up.setEnabled(false);
			if (Credentials.canUpdateSigkey(me))
				sk.setEnabled(true);
			else sk.setEnabled(false);
		}
	}
	
	public void saveSettings(View v) {
		EditText login = (EditText)findViewById(R.id.edit_login);
		Credentials.setLogin(this, login.getText().toString());
		
		EditText password = (EditText)findViewById(R.id.edit_password);
		Credentials.setPw(this, password.getText().toString());
		
		EditText sigkey = (EditText)findViewById(R.id.edit_sigkey);
		Credentials.setSigkey(this, sigkey.getText().toString());
		
		EditText uri = (EditText)findViewById(R.id.edit_uri);
		Credentials.setUrl(this, uri.getText().toString());
		
		enableUpdateButtons();
		
		Toast.makeText(this, "Saved settings", Toast.LENGTH_SHORT).show();
	}
	
	public void getSigkey(View v) {
		new GetPageTask().execute(GET_SIGKEY);
		Toast.makeText(this, "Refreshing sigkey", Toast.LENGTH_SHORT).show();
	}
	
	public void updateSurveys(View v) {
		new GetPageTask().execute(GET_SURVEYS);
		Toast.makeText(this, "Updating all survey data", Toast.LENGTH_SHORT).show();
	}
	
	// Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class GetPageTask extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... what) {
             
           // params comes from the execute() call: params[0] is the url.
    	   boolean r = false;
    	   
    	   if (what[0] == GET_SIGKEY) {
    		   if (Saver.upload(me)) {
    			   r = WebUtils.getSigkey(me);
    		   }
    		   
    	   } else if (what[0] == GET_SURVEYS) {
    		   r = WebUtils.getSurveys(me);
    		   
    	   } else {
    		   return "Error: don't understand what "+what+" means.";
    	   }
    	   
    	   return (r ? what[0] + " succeeded": what[0] + " failed");
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
    	   me.updateSigkey();
    	   
    	   Toast.makeText(me, result, Toast.LENGTH_SHORT).show();
           Log.v(LOG_TAG, result);
       }
    }

}
