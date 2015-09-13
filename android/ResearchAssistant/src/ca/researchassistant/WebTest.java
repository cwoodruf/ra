package ca.researchassistant;

import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;

import org.apache.commons.io.IOUtils;

public class WebTest extends Activity {
	protected FlingView mWebView;
	protected WebTest me;
	protected String sectiondata;
	protected boolean finishedLoading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me = this;
		sectiondata = "";
		setContentView(R.layout.web_test);
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
//		new LoadPageTask().execute();
		
		sectiondata = Surveys.getSection(me, 1, 3);
		Log.v("WebTest","local:\n"+sectiondata);

		Saver saver = new Saver(me,"cal",1,3,sectiondata);
		Log.v("WebTest","from saver:\n"+saver.getSectiondata());
		
		mWebView.addJavascriptInterface(saver, "Saver");
		mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
	}
	
	protected void loadPage() {
		// test survey data access and network access
		Surveys.updateSurveys(me, 1, "local test");
		if (WebUtils.getNetworkInfo(me) != null) {
			
			try {
				Credentials.setLogin(me, "cal");
				Credentials.setPw(me, "evolernancul");
				Credentials.setSigkey(me, "03f32ab5ce20cc9881658cc43600b02b786e52a5");
				
				String uri = Credentials.defUrl(me);
				String userid = Credentials.getLogin(me);
				String pw = Credentials.getPw(me);
				String sigkey = Credentials.getSigkey(me);
				
				Log.v("WebTest", "uri "+uri+" userid "+userid+" pw "+pw+" sigkey "+sigkey);
				
				String websigkey = WebUtils.get(
						uri+"/profile/getkey?login="+userid+"&password="+pw
				);
				Log.v("WebTest", WebUtils.lastUri());
				
				if (websigkey != null && websigkey.length() > 0) {
					Credentials.setSigkey(me, websigkey);
				}
				String[] params = Credentials.encodedSig(me); 
				String nonce = params[1];
				String sig = params[0];
				Log.v("WebTest", "nonce "+nonce+" signature "+sig+" websigkey "+websigkey);

				String json = WebUtils.get(
						uri+"/data/section/3?userid="+userid+"&nonce="+nonce+"&sig="+sig
				);
				Log.v("WebTest", WebUtils.lastUri());
				Log.v("WebTest","JSON from website:\n"+json);
				Surveys.updateSection(me, 1, 3, 1, "test section", json);
				
				sectiondata = Surveys.getSection(me, 1, 3);
				Log.v("WebTest", "JSON in db:\n"+sectiondata);
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}		
	}
	// Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class LoadPageTask extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... urls) {
             
           // params comes from the execute() call: params[0] is the url.
           loadPage();
           return "Page loaded";
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
    	   finishedLoading = true;
           Log.v("WebTest onPostExecute", result);
       }
    }

}
