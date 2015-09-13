package ca.researchassistant;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.webkit.JavascriptInterface;

// originally in survey.php

public class Saver {
	private HashMap<String, JSONObject> store;
	private Context context;
	private int section;
	private int survey;
	private String participant;
	private String json;
	private String sectiondata;
	private int lastq;
	private static final String LOG_TAG = "Saver";
	
	public Saver(Context ctx, String part, int surv, int sect, String sdata) {
		Log.v(LOG_TAG,"initializing participant "+part+" survey "+surv+" section "+sect);
		context = ctx;
		survey = surv;
		section = sect;
		participant = part;
		json = "{}";
		lastq = 1;
		sectiondata = sdata;
		store = new HashMap<String, JSONObject>();
	}

	@JavascriptInterface
	public void setSectiondata(String section) 
	{
		sectiondata = section;
	}
	
	@JavascriptInterface
	public String getSectiondata() 
	{
		return sectiondata;
	}
	
	@JavascriptInterface
	public int survey_section()
	{
	    return section;
	}
	
	@JavascriptInterface
	public int survey() 
	{
		return survey;
	}
	
	@JavascriptInterface
	public int getLastq() 
	{
		return lastq;
	}
	
	@JavascriptInterface
	public void setLastq(int currquestion) 
	{
		if (lastq != currquestion) {
			Log.v(LOG_TAG,"setting lastq from "+lastq+" to "+currquestion);
			lastq = currquestion;
			sync();
		}
	}
	
	@JavascriptInterface
	public String participant()
	{
	    return participant;
	}

	@JavascriptInterface
	public String inflate() {
		String[] params = SaverState.getState(context,participant,survey,section);
		json = params[1];
		fromString(json);
		setLastq(Integer.parseInt(params[0]));
	    return json;
	}

	@JavascriptInterface
	public void del(String key, String idx) {
        if (store.get(key) != null) {
            try {
				if (store.get(key).get(idx) != null)
				        store.get(key).remove(idx);
			} catch (JSONException e) {
				e.printStackTrace();
			}
            if (store.get(key).length() == 0) store.remove(key);
			Log.v(LOG_TAG,"removed "+key+"/"+idx+" store = "+toString());
            sync();
        }
	}

	@JavascriptInterface
	public void delary(String key) {
	        if (store.get(key) != null) {
                store.remove(key);
                sync();
	        }
	}
	
	@JavascriptInterface
	public void clear () {
        store.clear();
        sync();
	}
	
	@JavascriptInterface
	public void sync() {
		SaverState.update(context,participant,survey,section,lastq,toString());
	}
	
	// run from a background task to keep the external website up to date
	public static boolean upload(Context me) {
		return upload(me, false);
	}
	
	public static boolean upload(Context me, boolean all) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		Cursor c = all ? SaverState.getAll(me): SaverState.getUnsent(me);
		
		boolean success = true;
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				if ( WebUtils.postSaverstate(me, c, ts.toString()) ) { 
					SaverState.setSent(me, c, ts.toString());
					Log.v(LOG_TAG, "uploaded data "+ts.toString());
				} else success = false;
				c.moveToNext();
			}
		}
		c.close();
		return success;
	}
	
	@JavascriptInterface
	public void save(String key, String idx, String val) {
		    if (!store.containsKey(key)) {
		    	store.put(key, new JSONObject());
		    }
	        try {
				store.get(key).put(idx, val);
				Log.v(LOG_TAG,"saving "+key+"/"+idx+" = "+val+" store = "+toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        sync();
	}
	
	@JavascriptInterface
	public String toString () {
		JSONObject jo = new JSONObject();
		
        for (String key: store.keySet().toArray(new String[0])) {
        	@SuppressWarnings("unchecked")
			Iterator<String> i = store.get(key).keys();
        	while (i.hasNext()) {
        		String idx = i.next();
        		try {
        			if (jo.has(key)) {
        				JSONObject ob = jo.getJSONObject(key);
        				ob.put(idx, store.get(key).get(idx));
        			} else {
            			JSONObject pair = new JSONObject();
            			pair.put(idx, store.get(key).get(idx));
    					jo.put(key, pair);
        			}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        Log.v(LOG_TAG,"toString state "+jo.toString());
        return jo.toString();
	}
	
	// reset store from a string
	@SuppressWarnings("unchecked")
	public void fromString(String state) {
		try {
			Log.v(LOG_TAG,"fromString state was "+toString());
			Log.v(LOG_TAG,"inflating new state "+state);
			store.clear();
			JSONObject jo = new JSONObject(state);
			Iterator<String> i = jo.keys();
			while (i.hasNext()) {
				String key = i.next();
				JSONObject vals = jo.getJSONObject(key);
				store.put(key, vals);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
