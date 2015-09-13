package ca.researchassistant;

import android.database.Cursor;

public class Section {
	public int id;
	public String name;
	public int ord;
	public String data;
	
	public Section(Cursor c) {
		id = c.getInt(c.getColumnIndex(Surveys.SECTIONID));
		ord = c.getInt(c.getColumnIndex(Surveys.ORD));
		name = c.getString(c.getColumnIndex(Surveys.SECTIONNAME));
		data = c.getString(c.getColumnIndex(Surveys.SECTION));
	}
}
