package ca.researchassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This broadcast receiver turns on the periodic task that checks to see if we need 
 * bug the participant to do something.
 * To do something it sends a broadcast which can then be picked up by DrdatGUI's
 * TaskBroadcast to make a notification.
 * 
 * @author cal
 *
 */
public class AlarmRefresh extends BroadcastReceiver {
	protected Context me;
	private static String TAG = "ALARM REFRESH";
	private final String LOG_TAG = TAG;
	private static AlarmManager dailyCron;
	private static PendingIntent dailyOp;
	public static long SIXTYSECS = 60;
	public static long ONEDAY = 86400000; 
	public static long ONEHOUR = 3600000;
	public static long ONEMINUTE = 60000;

	public static int UNSET = 0;
	public static int STARTED = 1;
	public static int STOPPED = 2;
	private static int state = UNSET;
	protected final String SAVE_INTERVIEWS = "Save interviews";
	
	@Override
	/**
	 * use DrdatSmi2TaskList to figure out if we need to bug the participant
	 * send broadcast to DrdatGUI's TaskBroadcast if we do
	 * 
	 * @param context - our context (mainly needed for db access)
	 * @param intent - intent we were called with (not used)
	 */
	public void onReceive(Context context, Intent intent) {
		me = context;
		Log.v(LOG_TAG, "starting Saver.upload check ...");
		new GetPageTask().execute(SAVE_INTERVIEWS);
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
    		   r = Saver.upload(me);
    		      
    	   } else {
    		   return "Error: don't understand what "+what+" means.";
    	   }
    	   
    	   return (r ? what[0] + " succeeded": what[0] + " failed");
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
           Log.v(LOG_TAG, result);
       }
    }

	/**
	 * gets the alarm service and sets it to a repeating alarm that pings itself (see onReceive) periodically
	 * 
	 * @param me - our context
	 * @param checkevery - how often to check in seconds
	 */
	public static void setAlarm(Context me, long checkevery) {
		Intent i = new Intent(me, AlarmRefresh.class);
		AlarmRefresh.dailyOp = PendingIntent.getBroadcast(me, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmRefresh.dailyCron = (AlarmManager) me.getSystemService(Context.ALARM_SERVICE);
		AlarmRefresh.dailyCron.setRepeating(
				AlarmManager.RTC_WAKEUP, // how to interpret next arguments 
				System.currentTimeMillis(), // start right away
				(checkevery * 1000), 
				AlarmRefresh.dailyOp // what to do
			);
		state = STARTED;
		Log.v(TAG, "started alarm state="+state);
	}

	public static void clearAlarm() {
		if (AlarmRefresh.dailyCron != null) {
			AlarmRefresh.dailyCron.cancel(AlarmRefresh.dailyOp);
		}
		state = STOPPED;
	}
	
	public static PendingIntent getDailyOp() {
		return dailyOp;
	}

	public static AlarmManager getDailyCron() {
		return dailyCron;
	}
	
	/**
	 * check whether the alarm was deliberately stopped
	 * we can use this to decide whether to leave the alarm off
	 * when the task manager gets started: 
	 * by default it will try and start notifications
	 * @return true if we deliberately stopped the alarm false otherwise
	 */
	public static boolean wasStopped() {
		boolean stopped = (state == STOPPED ? true : false);
		Log.d(TAG, "alarm state = "+state+", alarm deliberately stopped = "+stopped);
		return stopped;
	}
}
