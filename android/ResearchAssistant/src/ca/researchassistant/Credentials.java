package ca.researchassistant;

import com.apache.commons.codec.digest.DigestUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Credentials {
	private static final String RA_PREFS = "ResearchAssistantLogin";
	private static final String RA_URL = "ra_url";
	private static final String RA_LOGIN = "ra_login";
	private static final String RA_PW = "ra_password";
	private static final String RA_SIGKEY = "ra_sigkey";
	private static final String LOG_TAG = "Creds";

	public static boolean canUpdateSigkey(Context me) {
		String pw = getPw(me);
		String login = getLogin(me);
		if (pw.length() > 0 && login.length() > 0) 
			return true;
		return false;
	}
	
	public static boolean canUpdateSurveys(Context me) {
		String sigkey = getSigkey(me);
		if (canUpdateSigkey(me) && sigkey.length() > 0)
			return true;
		return false;
	}
	
	// authentication string for most urls
	public static String sigUrlParams(Context me) {
		String userid = getLogin(me);
		
		String[] params = encodedSig(me); 
		String nonce = params[1];
		String sig = params[0];
		Log.v(LOG_TAG, "nonce "+nonce+" signature "+sig);
		
		return "userid="+userid+"&nonce="+nonce+"&sig="+sig;
	}
	
	// authentication string for getting sigkey
	public static String pwUrlParams(Context me) {
		String pw = getPw(me);
		String login = getLogin(me);
		
		if (pw.length() == 0 || login.length() == 0) return null;
		
		return "login="+login+"&password="+pw;
	}
	
	// signature used for data downloads
	public static String[] encodedSig(Context ctx) {
		String nonce = sha1(Double.toString(Math.random()));
		String creds = rawCreds(ctx, nonce);
		return new String[] { sha1(creds), nonce };
	}
	
	// public getters
	public static String getUrl(Context ctx) {
		return get(ctx, RA_URL, defUrl(ctx));
	}
	
	public static String getLogin(Context ctx) {
		return get(ctx, RA_LOGIN);
	}
	
	public static String getPw(Context ctx) {
		return get(ctx, RA_PW);
	}
	
	public static String getSigkey(Context ctx) {
		return get(ctx, RA_SIGKEY);
	}
	
	public static String defUrl(Context context) {
		return context.getString(R.string.ra_url);
	}

	// setters
	public static void setPw(Context context, String pw) {
		set(context, RA_PW, pw);
	}
	
	public static void setLogin(Context context, String login) {
		set(context, RA_LOGIN, login);
	}
	
	public static void setSigkey(Context context, String key) {
		set(context, RA_SIGKEY, key);
	}

	public static void setUrl(Context context, String uri) {
		set(context, RA_URL, uri);
	}

	public static void reset(Context context) {
		SharedPreferences up = context.getSharedPreferences(RA_PREFS, Context.MODE_PRIVATE);
		Editor ed = up.edit();
		ed.putString(RA_URL, defUrl(context));
		ed.putString(RA_LOGIN, "");
		ed.putString(RA_PW, "");
		ed.putString(RA_SIGKEY, "");
		ed.commit();
	}
	
	// private tools
	private static SharedPreferences prefs(Context context) {
		return context.getSharedPreferences(RA_PREFS, Context.MODE_PRIVATE);
	}
	
	private static Editor editor(Context context) {
		return prefs(context).edit();
	}
	
	private static void set(Context context, String field, String value) {
		Editor ed = editor(context);
		ed.putString(field, value);
		ed.commit();
	}
	
	private static String get(Context context, String field, String defValue) {
		return prefs(context).getString(field, defValue);
	}
	
	private static String get(Context context, String field) {
		return get(context,field,"");
	}

	// utilities to make a nonce based signature from saved preference data
	private static String rawCreds(Context ctx, String nonce) {
		try {
			String login = getLogin(ctx);
			String pw = getPw(ctx);
			String sigkey = getSigkey(ctx);
			
			if (!login.matches("[a-zA-Z0-9]+")) throw new Exception("invalid login "+login);
			if (!sigkey.matches("[a-zA-Z0-9]+")) throw new Exception("invalid sigkey "+sigkey);
			if (pw.length() == 0) throw new Exception("need a password!");
			if (nonce != null) {
				if (nonce.length() == 0) throw new Exception("missing nonce!");
				return nonce+sigkey+encodedPw(ctx)+login;
			} else {
				return pw+login+sigkey;
			}
			
		} catch (Exception e) {
			Log.w(LOG_TAG, e.getMessage());
		}
		return "";
	}
	
	private static String rawPw(Context ctx) {
		return rawCreds(ctx, null);
	}
	
	private static String sha1(String in) {
		if (in != null && in.length() > 0) 
			return DigestUtils.sha1Hex(in);
		return null;
	}	
	
	private static String encodedPw(Context ctx) {
		return sha1(rawPw(ctx));
	}

}