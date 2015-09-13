package ca.researchassistant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

// see http://developer.android.com/training/basics/network-ops/connecting.html

public class WebUtils {
	private static final String LOG_TAG = "WebUtils";
	private static String lastUri = null;
	
	// check for a network connection
	public static NetworkInfo getNetworkInfo(Context ctx) {
	    ConnectivityManager connMgr = 
	    		(ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return networkInfo;
	    } else {
	        return null;
	    }
	}
	
	// post input data from an interview
	public static boolean postSaverstate(
			Context me,
			String partid,
			int surveyid,
			int sectionid,
			int lastq,
			String state,
			String modified,
			String sent
	) {
		Log.v(LOG_TAG, sent+" saving saverstate: "+partid+" survey "+surveyid+" section "+sectionid+" state "+state);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(5);
        params.add(new BasicNameValuePair(SaverState.PARTID, partid));
        params.add(new BasicNameValuePair(SaverState.SURVEY, Integer.toString(surveyid)));
        params.add(new BasicNameValuePair(SaverState.SECTION, Integer.toString(sectionid)));
        params.add(new BasicNameValuePair(SaverState.LASTQ, Integer.toString(lastq)));
        params.add(new BasicNameValuePair(SaverState.STATE, state));
        params.add(new BasicNameValuePair(SaverState.MODIFIED, modified));
        params.add(new BasicNameValuePair(SaverState.SENT, sent));
        
		String r = post(
				me,
				makeSigUri(me,"/saver/save"),
				params
		);
		if (r != null) {
			Log.v(LOG_TAG, r);
			return ( r.matches("OK.*") ? true : false );
		} 
		return false;
	}
	
	public static boolean postSaverstate(Context me, Cursor c, String ts) {
		return postSaverstate(
				me,
				c.getString(c.getColumnIndex(SaverState.PARTID)),
				c.getInt(c.getColumnIndex(SaverState.SURVEY)),
				c.getInt(c.getColumnIndex(SaverState.SECTION)),
				c.getInt(c.getColumnIndex(SaverState.LASTQ)),
				c.getString(c.getColumnIndex(SaverState.STATE)),
				c.getString(c.getColumnIndex(SaverState.MODIFIED)),
				ts
		);
	}
	
	// get list of surveys
	// then get the sections for the surveys - basically rebuilds all survey related data
	// will delete all existing survey data - won't delete input data
	public static boolean getSurveys(Context me) {
		String json = null;
		try {
			json = get(me, makeSigUri(me, "/data/surveys"));
			if (json == null) return false;
			
			JSONArray ja = new JSONArray(json);
			Surveys.clearSurveys(me);
			Surveys.clearSections(me);
			for (int i=0; i<ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				String title = jo.getString("title");
				int surveyid = Integer.parseInt(jo.getString("surveyid"));
				
				Surveys.updateSurveys(me, surveyid, title);
				
				getSections(me, surveyid);
			}
			return true;
			
		} catch (IOException e) {
			Log.e(LOG_TAG, lastUri());
			e.printStackTrace();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "error parsing: "+json);
			e.printStackTrace();
		}
		return false;
	}
	
	// get list of sections for a survey and save skeleton record
	// then try and get the section data for that section
	public static boolean getSections(Context me, int surveyid) {
		String json = null;
		try {
			json = get(me, makeSigUri(me, "/data/survey/"+surveyid));
			if (json == null) return false;
			
			JSONArray ja = new JSONArray(json);
			for (int i=0; i<ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				/* {"surveyid":"1",
				 * "title":"survey test",
				 * "sectionid":"1",
				 * "name":"Prenatal Service Usage",
				 * "userid":"cal","role":"",
				 * "visible":"1",
				 * "ord":"0",
				 * "hide":"0"} 
				 **/
				
				int sectionid = Integer.parseInt(jo.getString("sectionid"));
				int ord = Integer.parseInt(jo.getString("ord"));
				String name = jo.getString("name");
				
				// not absolutely essential as getSection will do this
				Surveys.updateSection(me, surveyid, sectionid, ord, name);
				
				getSection(me, surveyid, sectionid, ord);
			}
			return true;
			
		} catch (IOException e) {
			Log.e(LOG_TAG, lastUri());
			e.printStackTrace();
			
		} catch (NumberFormatException e) {
			Log.e(LOG_TAG, "error parsing number in "+json);
			e.printStackTrace();
			
		} catch (JSONException e) {
			Log.e(LOG_TAG, "error parsing "+json);
			e.printStackTrace();
		}
		return false;
	}

	// get section data for a single section and save to db
	public static boolean getSection(Context me, int surveyid, int sectionid, int ord) {
		String json = null;
		
		try {
			json = get(me, makeSigUri(me, "/data/section/"+sectionid));
			if (json == null) return false;
			
			Log.v(LOG_TAG, lastUri());
			
			JSONObject jo = new JSONObject(json);
			String name = jo.getString("sectionname");
			Surveys.updateSection(me, surveyid, sectionid, ord, name, json);
			return true;
			
		} catch (IOException e) {
			Log.e(LOG_TAG, lastUri());
			e.printStackTrace();
			
		} catch (JSONException e) {
			Log.e(LOG_TAG, "json: error parsing "+json);
			e.printStackTrace();
		}
		return false;
	}
	
	// get the signature key from the ra site
	// may want to disable this at some point?
	public static boolean getSigkey(Context me) {
		try {
			String websigkey = get(me, makePwUri(me,"/profile/getkey"));
			if (websigkey == null) return false;
			
			// looking for something like 03f32ab5ce20cc9881658cc43600b02b786e52a5 
			if (websigkey.matches("[a-f0-9]+")) {
				Credentials.setSigkey(me, websigkey);
				return true;
			} else throw new Exception("bad sigkey "+websigkey);
			
		} catch (IOException e) {
			Log.e(LOG_TAG, lastUri());
			e.printStackTrace();
			
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	public static String get(Context ctx, String myurl) throws IOException {
		if (getNetworkInfo(ctx) == null) return null;
		
    	lastUri = myurl;
        URL url = new URL(myurl);
        return stream2string(url.openStream());
	}
	
	private static String stream2string(InputStream is) {		
		StringBuffer result = new StringBuffer();
		
        BufferedReader in = new BufferedReader(
                new InputStreamReader(is));
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result.append(inputLine);
            in.close();
    	    return result.toString();
    	    
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return null;
	}
	
	// see http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
	// and http://www.androidsnippets.com/get-the-content-from-a-httpresponse-or-any-inputstream-as-a-string
	public static String post(Context ctx, String myurl, List<NameValuePair> nameValuePairs) {
		
		if (getNetworkInfo(ctx) == null) return null;

    	lastUri = myurl;
    	// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(myurl);

	    try {
	        // Add your data
	    	/*
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("id", "12345"));
	        nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
	        */
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        return stream2string(response.getEntity().getContent());
	        
	    } catch (ClientProtocolException e) {
	    	Log.e(LOG_TAG,"protocol exception for "+myurl+": "+e.getMessage());
	    } catch (IOException e) {
	    	Log.e(LOG_TAG,"io exception for "+myurl+": "+e.getMessage());
	    }
	    return null;
	} 
	
	public static String lastUri() {
		return lastUri;
	}
	
	// tools for making urls
	private static String makeUri(Context me, String uri, String creds) {
		String baseuri = Credentials.getUrl(me);
		return baseuri+uri+"?"+creds;
	}
	
	private static String makeSigUri(Context me, String uri) {
		String sig = Credentials.sigUrlParams(me);		
		return makeUri(me,uri,sig);
	}
	
	private static String makePwUri(Context me, String uri) {
		String sig = Credentials.pwUrlParams(me);		
		return makeUri(me,uri,sig);
	}
	
}
