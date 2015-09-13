package ca.researchassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Surveys extends SQLiteOpenHelper {

	private static SQLiteDatabase db;
	private static Surveys dbhelper;
	private static String error;
	private static Context context;
	
	public static final String LOG_TAG = "Surveys";
	public static final String DBNAME = "surveys";
	public static final int DBVER = 1;
	public static final String SURVEYS = "surveys";
	public static final String SECTIONS = "sections";
	
	// fields
	public static final String SURVEYID = "surveyid";
	public static final String SURVEYTITLE = "surveytitle";
	public static final String SURVEYLIST = "surveylist";
	public static final String SURVEYSECTIONS = "surveysections";
	public static final String SECTIONID = "sectionid";
	public static final String SECTIONNAME = "sectionname";
	public static final String ORD = "ord";
	public static final String SECTION = "section";
	
	// table definitions
	private static final String CREATESURVEYS = 
			"create table "+SURVEYS+" ("
			+SURVEYID+" integer not null primary key, "
			+SURVEYTITLE+" text not null "
			+")";
	
	private static final String CREATESECTIONS = 
			"create table "+SECTIONS+" ("
			+SECTIONID+" integer not null, "
			+SURVEYID+" integer not null, "
			+ORD+" integer not null, "
			+SECTIONNAME+" text not null, "
			+SECTION+" text, "
			+"primary key ("+SECTIONID+","+SURVEYID+")"
			+")";
	
	public Surveys(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(LOG_TAG, "creating "+DBNAME);
		try {
			Log.v(LOG_TAG, CREATESURVEYS);
			db.execSQL(CREATESURVEYS);
			Log.v(LOG_TAG, CREATESECTIONS);
			db.execSQL(CREATESECTIONS);
			Log.v(LOG_TAG, "success");
		} catch (SQLiteException e) {
			logError("error: "+e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(LOG_TAG, "upgrading "+DBNAME+" from v "+oldVersion+" to v "+newVersion);
		db.execSQL("drop table if exists "+SURVEYS);
		db.execSQL("drop table if exists "+SECTIONS);
		onCreate(db);
	}
	
	public static boolean open(Context ctx) 
			throws SQLiteException
	{
		Log.v(LOG_TAG,"trying to open");
		if (db != null) return true;
		context = ctx;
		try {
			dbhelper = new Surveys(context, DBNAME, null, DBVER);
			db = dbhelper.getWritableDatabase();
			Log.v(LOG_TAG,"db "+DBNAME+" now open");
			return unsetError();
		} catch (SQLiteException e) {
			// db = dbhelper.getReadableDatabase();
			return logError("open error: "+e.getMessage());
		}
	}
	
	public static boolean updateSurveys(Context ctx, int surveyid, String title) {
		open(ctx);
		try {
			ContentValues row = new ContentValues();
			row.put(SURVEYID,surveyid);
			row.put(SURVEYTITLE,title);
			db.insertWithOnConflict(SURVEYS, null, row, SQLiteDatabase.CONFLICT_REPLACE);			
			return unsetError();
		} catch (SQLiteException e) {
			return logError("surveys update error: "+e.getMessage());
		}
	}
	
	public static boolean clearSurveys(Context ctx) {
		open(ctx);
		try {
			db.delete(SURVEYS, null, null);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("surveys delete all error: "+e.getMessage());
		}
	}
	
	public static boolean updateSection(
			Context ctx, 
			int surveyid, 
			int sectionid, 
			int ord,
			String name
	) {
		return updateSection(ctx,surveyid,sectionid,ord,name,null);
	}
	
	public static boolean updateSection(
			Context ctx, 
			int surveyid, 
			int sectionid, 
			int ord,
			String name, 
			String section 
	) {
		try {
			open(ctx);
			ContentValues row = new ContentValues();
			row.put(SURVEYID,surveyid);
			row.put(SECTIONID, sectionid);
			row.put(SECTIONNAME, name);
			row.put(ORD, ord);
			
			if (section != null) row.put(SECTION, section);
			
			db.insertWithOnConflict(SECTIONS, null, row, SQLiteDatabase.CONFLICT_REPLACE);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("section update error for survey "+
						surveyid+" section "+sectionid+": "+e.getMessage());
		}
	}
	
	public static boolean clearSections(Context ctx) {
		open(ctx);
		try {
			db.delete(SECTIONS, null, null);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("surveys delete all error: "+e.getMessage());
		}		
	}
	
	public static Cursor getSurveys(Context ctx) {
		open(ctx);
		return db.query(
				SURVEYS, null, 
				null, null, 
				null, null, SURVEYID
		);
	}
	
	public static Cursor getSurvey(Context ctx, int surveyid) {
		open(ctx);
		return db.query(
				SURVEYS, null, 
				SURVEYID+"=? ", new String[] { Integer.toString(surveyid) }, 
				null, null, SURVEYID, "1"
		);
	}
	
	public static String getSurveyTitle(Context ctx, int surveyid) {
		String title = "";
		Cursor c = getSurvey(ctx, surveyid);
		if (c.moveToFirst()) {
			title = c.getString(c.getColumnIndex(SURVEYTITLE));
		}
		c.close();
		return title;
	}
	
	public static Cursor getSections(Context ctx, int surveyid) {
		open(ctx);
		return db.query(
				SECTIONS, null, 
				SURVEYID+"=? ", new String[] { Integer.toString(surveyid) }, 
				null, null, ORD
		);
	}

	public static Cursor getSectionRow(Context ctx, int surveyid, int sectionid) {
		open(ctx);
		Cursor c = db.query(
				SECTIONS, null, 
				SURVEYID+"=? and "+SECTIONID+"=? ", 
				new String[] { Integer.toString(surveyid), Integer.toString(sectionid) }, 
				null, null, ORD
		);
		return c;
	}

	public static String getSectionName(Context ctx, int surveyid, int sectionid) {
		String title = "";
		Cursor c = getSectionRow(ctx, surveyid, sectionid);
		if (c.moveToFirst()) {
			title = c.getString(c.getColumnIndex(SECTIONNAME));
		}
		c.close();
		return title;
	}
	
	public static String getSection(Context ctx, int surveyid, int sectionid) {
		String section = "";
		Cursor c = getSectionRow(ctx, surveyid, sectionid);
		if (c.moveToFirst()) {
			section = c.getString(c.getColumnIndex(SECTION));
		}
		c.close();
		return section;
	}

	private static boolean unsetError() {
		error = "";
		return true;
	}
	
	private static boolean logError(String msg) {
		error = msg;
		Log.e(LOG_TAG, msg);
		return false;
	}
	
	public static String getError() {
		return error;
	}
}

