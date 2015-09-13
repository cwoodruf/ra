package ca.researchassistant;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PeopleEditActivity extends Activity {
	protected String partid;
	protected Participant part;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people_edit);
		Intent i = getIntent();
		partid = i.getExtras().getString("partid");
		if (partid.length() > 0) {
			Cursor c = People.getPart(this, partid);
			if (c.moveToFirst()) {
				part = new Participant(c);
				part.setView(this);
				setPartname(part);
			} else {
				part = null;
			}
			c.close();
		}
	}
	
	public void savePart(View v) {
		Participant newp = new Participant(this);
		
		if (!partid.equals(newp.partid)) {
			Cursor c = People.getPart(this, newp.partid);
			if (c.moveToFirst() && partid.length() > 0) {
				// show an error dialog ?
				Toast.makeText(this, 
					"Error: cannot change id for "+partid+": "
						+newp.partid+" already exists!", 
					Toast.LENGTH_LONG).show();
			} else {
				saveNewPart(newp);
			}
			c.close();
		} else if (newp.partid.length() == 0) {
			Toast.makeText(this, 
					"Error: need a participant id!", 
					Toast.LENGTH_LONG).show();			
		} else {
			saveNewPart(newp);
		}
	}
	
	private void saveNewPart(Participant newp) {
		if (People.updateParts(this, newp)) {
			Toast.makeText(this, 
					"Saved "+newp.partid, 
					Toast.LENGTH_SHORT).show();
			setPartname(newp);
		} else {
			Toast.makeText(this, 
					"Error saving "+newp.partid, 
					Toast.LENGTH_LONG).show();
		}
	}
	
	private void setPartname(Participant part) {
		TextView n = (TextView)findViewById(R.id.partname);
		n.setText(part.partid+" / "+part.name);
	}
}
