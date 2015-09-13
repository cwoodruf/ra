package ca.researchassistant;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Participant {
	public String partid;
	public String name;
	public String email;
	public String phone;
	public String address;
	public String notes;
	public boolean inactive;
	
	public Participant(Cursor c) {
		partid = c.getString(c.getColumnIndex(People.PARTID));
		if (partid == null) partid = "";
		name = c.getString(c.getColumnIndex(People.NAME));
		if (name == null) name = "";
		email = c.getString(c.getColumnIndex(People.EMAIL));
		if (email == null) email = "";
		phone = c.getString(c.getColumnIndex(People.PHONE));
		if (phone == null) phone = "";
		address = c.getString(c.getColumnIndex(People.ADDRESS));
		if (address == null) address = "";
		notes = c.getString(c.getColumnIndex(People.NOTES));		
		if (notes == null) notes = "";
		int i = c.getInt(c.getColumnIndex(People.INACTIVE));
		inactive = ( i == 0 ? false : true );
	}
	
	public Participant(Activity c) {
		EditText epartid = (EditText)c.findViewById(R.id.edit_partid);
		partid = epartid.getText().toString();
		EditText ename = (EditText)c.findViewById(R.id.edit_name);
		name = ename.getText().toString();
		EditText eemail = (EditText)c.findViewById(R.id.edit_email);
		email = eemail.getText().toString();
		EditText ephone = (EditText)c.findViewById(R.id.edit_phone);
		phone = ephone.getText().toString();
		EditText eaddress = (EditText)c.findViewById(R.id.edit_address);
		address = eaddress.getText().toString();
		EditText enotes = (EditText)c.findViewById(R.id.edit_notes);
		notes = enotes.getText().toString();
		CheckBox cinactive = (CheckBox)c.findViewById(R.id.check_inactive);
		inactive = cinactive.isChecked();
	}
	
	public void setView(Activity c) {
		EditText epartid = (EditText)c.findViewById(R.id.edit_partid);
		epartid.setText(partid);
		EditText ename = (EditText)c.findViewById(R.id.edit_name);
		ename.setText(name);
		EditText eemail = (EditText)c.findViewById(R.id.edit_email);
		eemail.setText(email);
		EditText ephone = (EditText)c.findViewById(R.id.edit_phone);
		ephone.setText(phone);
		EditText eaddress = (EditText)c.findViewById(R.id.edit_address);
		eaddress.setText(address);
		EditText enotes = (EditText)c.findViewById(R.id.edit_notes);
		enotes.setText(notes);
		CheckBox cinactive = (CheckBox)c.findViewById(R.id.check_inactive);
		
if (inactive) Toast.makeText(c, "inactive", Toast.LENGTH_LONG).show();
		
		cinactive.setChecked(inactive);		
	}
	
	public ContentValues makeRow() {
		ContentValues row = new ContentValues();
		row.put(People.PARTID, partid);
		row.put(People.NAME, name);
		row.put(People.EMAIL, email);
		row.put(People.PHONE, phone);
		row.put(People.ADDRESS, address);
		row.put(People.NOTES, notes);
		row.put(People.INACTIVE, ( inactive ? 1 : 0 ));
		return row;
	}
	
	// View must be set in "me" before this function is run
	public static void setPartName(Activity me, String partid) {
		
		TextView part_name = (TextView)me.findViewById(R.id.part_name);
		
		Cursor c = People.getPart(me, partid);
		
		if (c.moveToFirst()) {
			Participant part = new Participant(c);
			
			if (part.partid == null)
				part.partid = "found partid is null for "+partid;
			if (part.name == null)
				part.name = "name is null";
			if (part_name == null)
				Toast.makeText(me, "Can't find part_name!", Toast.LENGTH_LONG).show();
			else part_name.setText(part.partid+" / "+part.name);
		} else {
			part_name.setText("Error: no participant selected!");
		}
		c.close();
	}
}
