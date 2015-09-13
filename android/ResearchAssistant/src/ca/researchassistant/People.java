package ca.researchassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class People extends SQLiteOpenHelper {
	private static SQLiteDatabase db;
	private static People dbhelper;
	private static String error;
	private static Context context;
	
	public static final String LOG_TAG = "People";
	public static final String DBNAME = "people";
	public static final int DBVER = 1;
	public static final String TABLE = "participants";
	
	// fields
	public static final String PARTID = "partid";
	public static final String INACTIVE = "inactive";
	public static final String NAME = "name";
	public static final String EMAIL = "email";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String NOTES = "notes";
	
	// table definition
	private static final String CREATE = 
			"create table "+TABLE+" ("
			+PARTID+" text primary key, "
			+INACTIVE+" integer not null default 0, "
			+NOTES+" text, "
			+NAME+" text, "
			+EMAIL+" text, "
			+PHONE+" text, "
			+ADDRESS+" text "
			+")";
	
	public People(Context context, String name, CursorFactory factory,
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
			dbhelper = new People(context, DBNAME, null, DBVER);
			db = dbhelper.getWritableDatabase();
			return unsetError();
		} catch (SQLiteException e) {
			// db = dbhelper.getReadableDatabase();
			return logError("open error: "+e.getMessage());
		}
	}
	
	public static boolean updateParts(Context ctx, Participant part) {
		open(ctx);		
		try {
			ContentValues row = part.makeRow();
			db.insertWithOnConflict(TABLE, null, row, SQLiteDatabase.CONFLICT_REPLACE);
			return unsetError();
		} catch (SQLiteException e) {
			return logError(part.toString()+e.getMessage());
		}
		
	}
	
	public static boolean updateParts(
			Context ctx, 
			String partid,
			boolean inactive,
			String name,
			String email, 
			String phone, 
			String address,
			String notes
	) {
		open(ctx);		
		try {
			ContentValues row = new ContentValues();
			row.put(PARTID, partid);
			row.put(INACTIVE, (inactive ? 1:0));
			row.put(NAME, notes);
			row.put(EMAIL, email);
			row.put(PHONE, phone);
			row.put(ADDRESS, address);
			row.put(NOTES, notes);
			
			db.insertWithOnConflict(TABLE, null, row, SQLiteDatabase.CONFLICT_REPLACE);
			return unsetError();
		} catch (SQLiteException e) {
			return logError(
					"error updating "+
					partid+","+
					notes+","+
					email+","+
					phone+","+
					address+","+
					(inactive?"inactive":"active")+": "+
					e.getMessage()
			);
		}
	}
	
	public static Cursor getParts(Context ctx) {
        open(ctx);
		Cursor c = db.query(
				TABLE, 
				null, 
				null, 
				null, 
				null, 
				null, 
				INACTIVE+","+PARTID
		);
		return c;
	}
	
	public static Cursor getPart(Context ctx, String partid) {
        open(ctx);
		Cursor c = db.query(
				TABLE, 
				null, 
				PARTID+"=? ", 
				new String[] { partid }, 
				null, 
				null, 
				null,
				"1"
		);
		return c;
	}
	
	public static boolean delPart(Context ctx, String partid) {
        open(ctx);
		try {
			db.delete(TABLE, 
					PARTID+"=? ",
					new String[] { partid }
			);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("error deleting partid "+partid+e.getMessage());
		}
	}

	public static boolean delInactiveParts(Context ctx) {
		open(ctx);
		try {
			db.delete(TABLE,INACTIVE+"=1 ",null);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("error deleting inactive: "+e.getMessage());
		}
	}
	

	public static boolean delParts(Context ctx) {
		open(ctx);
		try {
			db.delete(TABLE,null,null);
			return unsetError();
		} catch (SQLiteException e) {
			return logError("error deleting: "+e.getMessage());
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
