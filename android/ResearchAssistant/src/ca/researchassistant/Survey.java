package ca.researchassistant;

import android.database.Cursor;

public class Survey {
	public int id;
	public String title;
	
	public Survey (Cursor c) {
		id = c.getInt(c.getColumnIndex(Surveys.SURVEYID));
		title = c.getString(c.getColumnIndex(Surveys.SURVEYTITLE));
	}
}
