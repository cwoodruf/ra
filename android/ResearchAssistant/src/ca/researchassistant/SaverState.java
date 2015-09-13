package ca.researchassistant;
import java.sql.Timestamp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SaverState extends SQLiteOpenHelper {
	private static SQLiteDatabase db;
	private static SaverState dbhelper;
	private static String error;
	private static Context context;
	
	public static final String LOG_TAG = "SaverState";
	public static final String DBNAME = "saver";
	public static final int DBVER = 2;
	public static final String TABLE = "saverstate";
	
	// fields
	public static final String PARTID = "partid";
	public static final String SURVEY = "survey";
	public static final String SECTION = "section";
	public static final String LASTQ = "lastq";
	public static final String STATE = "state";
	public static final String MODIFIED = "modified";
	public static final String SENT = "sent";
	
	// table definition
	private static final String CREATE = 
			"create table "+TABLE+" ("
			+PARTID+" text not null, "
			+SURVEY+" integer not null, "
			+SECTION+" integer not null, "
			+LASTQ+" text not null, "
			+STATE+" text not null, "
			+MODIFIED+" datetime, "
			+SENT+" datetime, "
			+"primary key ("+PARTID+","+SURVEY+","+SECTION+")"
			+")";
	
	public SaverState(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(LOG_TAG, "creating "+DBNAME);
		try {
			db.execSQL(CREATE);
		} catch (SQLiteException e) {
			logError("error: "+e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(LOG_TAG, "upgrading "+DBNAME+" from v "+oldVersion+" to v "+newVersion);
		db.execSQL("drop table if exists "+TABLE);
		onCreate(db);
	}
	
	public static boolean open(Context ctx) 
			throws SQLiteException
	{
		if (db != null) return true;
		context = ctx;
		try {
			dbhelper = new SaverState(context, DBNAME, null, DBVER);
			db = dbhelper.getWritableDatabase();
			return unsetError();
		} catch (SQLiteException e) {
			// db = dbhelper.getReadableDatabase();
			return logError("open error: "+e.getMessage());
		}
	}
	
	public static boolean update(Context ctx, String partid, int survey, int section, int lastq, String state) {
		open(ctx);
		
		try {
			ContentValues row = new ContentValues();
			row.put(PARTID, partid);
			row.put(SURVEY, survey);
			row.put(SECTION, section);
			row.put(LASTQ, lastq);
			row.put(STATE, state);
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			row.put(MODIFIED, ts.toString());
			row.put(SENT, (String) null);

			Log.v(LOG_TAG,ts+": saving survey "
						+survey+" section "+section+" data for "+partid
						+" lastq was "+lastq+" state = "+state);
			
			db.insertWithOnConflict(TABLE, null, row, SQLiteDatabase.CONFLICT_REPLACE);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("error updating "+partid+","+survey+","+section+": "+e.getMessage());
		}
	}
	
	public static boolean setSent(Context me, Cursor c, String ts) {
		return setSent(
				me,
				c.getString(c.getColumnIndex(PARTID)),
				c.getInt(c.getColumnIndex(SURVEY)),
				c.getInt(c.getColumnIndex(SECTION)),
				ts
		);
	}
	
	public static boolean setSent(Context me, String partid, int surveyid, int sectionid, String ts) {
		try {
			ContentValues row = new ContentValues();
			row.put(SENT, ts);
			db.update(
					TABLE, 
					row, 
					PARTID+"= ? and "+SURVEY+"=? and "+SECTION+"=?", 
					new String[] { 
							partid, 
							Integer.toString(surveyid), 
							Integer.toString(sectionid) 
					}
			);
			return unsetError();
		} catch (SQLiteException e) {
			return logError(
					"error updating sent timestamp for "
					+partid+","+surveyid+","+sectionid+": "+ts+" "
					+e.getMessage()
			);
		}
	}
	
	public static String[] getState(Context ctx, String partid, int survey, int section) {
        open(ctx);
		Cursor c = db.query(
				TABLE, 
				null, 
				PARTID+"=? and "+SURVEY+"=? and "+SECTION+"=?", 
				new String[] { partid, Integer.toString(survey), Integer.toString(section) }, 
				null, 
				null, 
				MODIFIED+" desc",
				"1"
		);
		if (c.moveToFirst()) {
			String state = c.getString(c.getColumnIndex(STATE));
			String lastq = c.getString(c.getColumnIndex(LASTQ));
			Log.v(LOG_TAG,"found lastq "+lastq+" state="+state);
			return new String[] { lastq, state };
		}
		// defaults if you have no data
		return new String[] { "1", "{}" };
	}
	
	public static String getModified(Context ctx, String partid, int surveyid, int sectionid) {
		open(ctx);
		Cursor c = db.query(
				TABLE, 
				new String[] { MODIFIED }, 
				PARTID+"=? and "+SURVEY+"=? and "+SECTION+"=? ", 
				new String[] { partid, Integer.toString(surveyid), Integer.toString(sectionid) }, 
				null, 
				null, 
				null, 
				"1"
		);
		String modified = "";
		if (c.moveToFirst()) 
			modified = c.getString(c.getColumnIndex(MODIFIED));
		c.close();
		return modified;
	}
	
	public static Cursor getAll(Context ctx) {
		return getData(ctx, null, null);
	}
	
	public static Cursor getUnsent(Context ctx) {
		return getData(ctx, SENT+" is null ", null);
	}
	
	public static Cursor getSent(Context ctx) {
		return getData(ctx, SENT+" is not null ", null);
	}
	
	public static Cursor getData(Context ctx, String selector, String [] fields) {
		open(ctx);
		return db.query(
				TABLE, 
				null, 
				selector, 
				fields, 
				null, 
				null, 
				SENT
		);			
	}
	
	public static boolean del(Context ctx, String partid, int survey, int section) {
        open(ctx);
		try {
			db.delete(TABLE, 
					PARTID+"=? and "+SURVEY+"=? and "+SECTION+"=?",
					new String[] { partid, Integer.toString(survey), Integer.toString(section) }
			);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("error deleting partid "+
							partid+" survey "+survey+" section "+section+": "+e.getMessage());
		}
	}
	
	public static boolean delAll(Context ctx) {
		open(ctx);
		try {
			db.delete(TABLE,null,null);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("error deleting messages: "+e.getMessage());
		}
	}
	

	private static boolean unsetError() {
		error = null;
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
