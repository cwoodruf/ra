package ca.researchassistant;

import java.util.ArrayList;

import ca.researchassistant.TreeAdapter.Entry;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class PeopleActivity extends Activity 
	implements OnChildClickListener, OnItemLongClickListener {

	protected PeopleActivity me;
	protected ArrayList<String> people;
	protected ArrayList<Participant> parts;
	protected Survey[] surveys;
	protected ArrayList<Section>[] sections;
	protected String partid;
	protected int partindex;
	protected int sectionid;
	protected int sectionindex;
	protected int surveyid;
	protected int surveyindex;
	protected final int SETTINGS = 1;
	protected final int EDITPART = 2;
	protected final int DOSURVEY = 3;
	public final String SAVE_INTERVIEWS = "Save Interviews";
	private final String LOG_TAG = "PeopleActivity";
	private int deleteAction;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me = this;
		setContentView(R.layout.activity_people);
		if (AlarmRefresh.wasStopped() == false) {
			AlarmRefresh.setAlarm(me, AlarmRefresh.SIXTYSECS);
		}
		refreshPeople();
		
	}

	public void saveInterviews(View v) {
		new GetPageTask().execute(SAVE_INTERVIEWS);
	}
	
	public void doRefresh(View v) {
		refreshPeople();
	}
	
	@SuppressWarnings("unchecked")
	protected void refreshPeople() {
		// EXAMPLE
		// from http://stackoverflow.com/questions/12569109/android-double-expandable-listview-hides-items-if-list-is-too-big
	    Tree root = new Tree("participants", 0);
	    Tree parttree;
	    Tree surveytree;
	    Tree sectiontree;
		
		people = new ArrayList<String>();
		parts = new ArrayList<Participant>();
		
		Cursor c = People.getParts(this);
		Cursor surveyc = Surveys.getSurveys(this);
		Cursor[] sectionc = new Cursor[surveyc.getCount()];
		surveys = new Survey[surveyc.getCount()];
		sections = (ArrayList<Section>[]) new ArrayList<?>[surveyc.getCount()];

		int partcount = 0;
		
		while (c.moveToNext()) {
		    String verylastinterview = null;
		    String lastsurvey = null;
			Participant p = new Participant(c);
			parts.add(p);
			
			String parttitle = p.partid;
			if (p.name.length() > 0) parttitle += " ["+p.name+"] ";
			if (p.inactive) parttitle += " (inactive) ";
			
			people.add(parttitle);
		    parttree = new Tree(parttitle, partcount++);
		    
		    // now add all the surveys to this participant
		    int surveycount = 0;
		    
		    surveyc.moveToFirst();
		    do {
			    String lastsection = null;
			    String lastinterview = null;
		    	Survey survey = new Survey(surveyc);

		    	if (surveys[surveycount] == null) 
		    		surveys[surveycount] = survey;
		    	String surveytitle = "Survey: "+survey.title;
	            surveytree = new Tree(surveytitle, survey.id);
		    	
		    	int sectioncount = 0;
		    	if (sectionc[surveycount] == null) {
		    		sectionc[surveycount] = Surveys.getSections(this, survey.id);
		    	}		    	
	            sectionc[surveycount].moveToFirst();
	            do {
	            	Section section = new Section(sectionc[surveycount]);
	            	
	            	if (sections[surveycount] == null)
	            		sections[surveycount] = new ArrayList<Section>();
	            	
	            	if (sections[surveycount].size() <= sectioncount)
	            		sections[surveycount].add(section);
	            	
	            	String modified = SaverState.getModified(this, p.partid, survey.id, section.id);
	            	if (modified.length() > 10) 
	            		modified = modified.substring(0,10);
	            	if (modified.length() > 0 && 
	            			(lastinterview == null || modified.compareTo(lastinterview) >= 0)) {
	            		verylastinterview = lastinterview = modified;
	            		lastsurvey = survey.title;
	            		lastsection = section.name;
	            		surveytree.setName(surveytitle+" ("+lastinterview+" "+lastsection+")");
	            	}
	            	sectiontree = new Tree(section.name+" "+modified, section.id);
	            	surveytree.addChild(sectiontree);
	            	sectioncount++;
	            	
	            } while (sectionc[surveycount].moveToNext());
	            
	            if (verylastinterview != null) {
	            	parttree.setName(parttitle+" ("+verylastinterview+" "+lastsurvey+")");
	            }
	            parttree.addChild(surveytree);
	            surveycount++;
	            
		    } while (surveyc.moveToNext());
		    
		    root.addChild(parttree);		    
		}
		
		c.close();

		// EXAMPLE Cont'd
	    ExpandableListView peoplelist = 
	    		(ExpandableListView)findViewById(R.id.peoplelist); 
	    final TreeAdapter treeAdapter = 
	    		new TreeAdapter(
	    				(Activity) this, 
	    				root, 
	    				(OnChildClickListener)this,
	    				(OnItemLongClickListener)this
	    		);
	    peoplelist.setAdapter(treeAdapter);
	    peoplelist.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

	    peoplelist.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
	        @Override
	        public boolean onGroupClick(ExpandableListView parent, View v,
	                int groupPosition, long id) {
	        	partindex = groupPosition;
	        	Participant p = parts.get(partindex);
	        	partid = p.partid;
	        	if (p.inactive) return true;
	        	return false;
	        }
	    });
	    peoplelist.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				surveyid = surveys[childPosition].id;
				surveyindex = childPosition;
				// Toast.makeText(me, "surveyid "+surveyid, Toast.LENGTH_LONG).show();
				return false;
			}
		});
	    
	    if (surveyid > 0 && sectionid > 0) {
	    	peoplelist.expandGroup(partindex);
	    	Entry e = treeAdapter.lsfirst[partindex];
	    	e.cls.expandGroup(surveyindex);
	    }
	    // END EXAMPLE
        
		registerForContextMenu(peoplelist);		
	}

	public void addPeople(View v) {
		Intent i = new Intent(this, PeopleEditActivity.class);
		i.putExtra("partid","");
		startActivityForResult(i,EDITPART);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Intent i = new Intent(this, SettingsActivity.class);
	        	this.startActivityForResult(i, SETTINGS);
	        	return true;
	        case R.id.action_upload:
	        	saveInterviews(item.getActionView());
	        	return true;
	        case R.id.action_del_interviews:
	        	delInterviews(item.getActionView());
	        	return true;
	        case R.id.action_add_part:
	        	addPeople(item.getActionView());
	        	return true;
	        case R.id.action_delete_parts:
	        	delAllParts(item.getActionView());
	        	return true;
	        case R.id.action_delete_inactive_parts:
	        	delInactiveParts(item.getActionView());
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void delInterviews(View v) {
		clean(v,R.string.action_del_interviews);
	}
	
	public void delAllParts(View v) {
		clean(v, R.string.action_delete_parts);
	}
	
	public void delInactiveParts(View v) {
		clean(v, R.string.action_delete_inactive_parts);
	}
	
	private void clean(View v, int string) {
		deleteAction = string;
		new AlertDialog.Builder(this).setTitle(deleteAction).setMessage(deleteAction)
		.setPositiveButton(R.string.delete, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String action = me.getString(deleteAction);
				boolean r = false;
				switch (deleteAction) {
				case R.string.action_del_interviews:
					r = SaverState.delAll(me);
					break;
				case R.string.action_delete_parts:
					r = People.delParts(me);
					break;
				case R.string.action_delete_inactive_parts:
					r = People.delInactiveParts(me);
					break;
				default: /* do nothing */ 
				}
				me.refreshPeople();
				Toast.makeText(me, action+(r?" succeeded":" failed"), Toast.LENGTH_LONG).show();
				dialog.dismiss();
		    } })
		.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			} })
		.show();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
		// will also work without this line
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		
		// only really needed if there are more than one view to choose from
		switch (v.getId()) {
		case R.id.peoplelist: 			
			// how to find out which menu item was selected after the fact
			ExpandableListView.ExpandableListContextMenuInfo info =
		            (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

	        int type =
	            ExpandableListView.getPackedPositionType(info.packedPosition);

	        int group =
	            ExpandableListView.getPackedPositionGroup(info.packedPosition);

	        int child =
	            ExpandableListView.getPackedPositionChild(info.packedPosition);
	        
		    Log.v(LOG_TAG, "create context menu: type "+type+" group "+group+" child "+child);
		    
		    partindex = group;
		    partid = parts.get(partindex).partid;
		    inflater.inflate(R.menu.context_people, menu);
		    break;
		default: /* do nothing */
		} 
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if (partid.length() == 0) return true;
		
		Cursor c = People.getPart(this, partid);
		if (!c.moveToFirst()) {
			c.close();
			Toast.makeText(this, "Cannot find participant "+partid, Toast.LENGTH_LONG).show();
			return true;
		}
		Participant p = new Participant(c);
		c.close();
		
		Intent i = new Intent();
		i.putExtra("partid", partid);
		
		switch (item.getItemId()) {
		/*
		case R.id.start_survey:
			i.setClass(this, ResearchAssistant.class);
			startActivity(i);
			break;
		 */
		case R.id.contact_info:
			new AlertDialog.Builder(this)
				.setTitle(R.string.contact_info)
				.setMessage(
					p.name+"\n\n"+
					p.email+"\n\n"+
					p.phone+"\n\n"+
					p.address
				).show();
			break;
		case R.id.email_part:
			if (p.email.length() == 0) {
				Toast.makeText(this, "No email for "+partid, Toast.LENGTH_LONG).show();
				return true;
			}
			// see http://www.mkyong.com/android/how-to-send-email-in-android/
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{p.email});		  
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, getText(R.string.email_client)));
			break;
		case R.id.phone_part:
			if (p.phone.length() == 0) {
				Toast.makeText(this, "No phone for "+partid, Toast.LENGTH_LONG).show();
				return true;
			}
			// see http://www.mkyong.com/android/how-to-make-a-phone-call-in-android/
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+p.phone));
			startActivity(callIntent);
			break;
		case R.id.edit_part:
			i.setClass(this, PeopleEditActivity.class);
			startActivityForResult(i,EDITPART);
			break;
		case R.id.del_part:
			new AlertDialog.Builder(this)
			.setTitle(R.string.delete_part_title)
			.setMessage(R.string.delete_part_confirm)
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				} })
			.setPositiveButton(R.string.delete, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					People.delPart(me, partid);
					me.refreshPeople();
					dialog.dismiss();
			    } }).show();
			break;
		default: /* empty */		
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    refreshPeople();
	}
	
	// Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    public class GetPageTask extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... what) {
             
           // params comes from the execute() call: params[0] is the url.
    	   boolean r = false;
    	   
    	   if (what[0] == SAVE_INTERVIEWS) {
    		   r = Saver.upload(me,true);
    		      
    	   } else {
    		   return "Error: don't understand what "+what+" means.";
    	   }
    	   
    	   return (r ? what[0] + " succeeded": what[0] + " failed");
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
    	   
    	   Toast.makeText(me, result, Toast.LENGTH_SHORT).show();
           Log.v(LOG_TAG, result);
       }
    }

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		Participant p = parts.get(partindex);
		String partid = p.partid;
		
		Class<?> c;
		int req;
		if (p.inactive) {
			c = PeopleEditActivity.class;
			req = EDITPART;
		} else {
			c = DisplaySection.class;
			req = DOSURVEY;
		}
		
		Intent i = new Intent(this, c);
		
		surveyindex = groupPosition;
		sectionindex = childPosition;
		
		sectionid = sections[groupPosition].get(childPosition).id;
		surveyid = surveys[groupPosition].id;
		
		i.putExtra("participant", partid);
		i.putExtra("partid", partid);
		i.putExtra("surveyid", surveyid);
		i.putExtra("sectionid", sectionid);
		
		startActivityForResult(i,req);
		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		TextView tv = (TextView)arg1;
		Tag tag = (Tag)tv.getTag();
		if (tag != null) {
			partid = parts.get(tag.partindex).partid;
			surveyid = tag.surveyid;
			sectionid = tag.sectionid;
			// Toast.makeText(this, "partid "+partid+" surveyid "+surveyid+" sectionid "+sectionid, Toast.LENGTH_LONG).show();
			
			new AlertDialog.Builder(this)
			.setTitle(R.string.delete_interview_title)
			.setMessage(R.string.delete_interview_confirm)
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				} })
			.setPositiveButton(R.string.delete, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SaverState.del(me, partid, surveyid, sectionid);
					me.refreshPeople();
					dialog.dismiss();
			    } }).show();
		}
		return true;
	}
}
