package org.dieschnittstelle.mobile.android.inputandfeedback;

import org.dieschnittstelle.mobile.android.inputandfeedback.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Start activity of the app. Allows to select between a range of scenarios
 * specified in the res/values/strings.xml file in the "main_menu" array.
 * 
 * @author Joern Kreutel
 * 
 */
public class InputAndFeedbackActivity extends Activity {

	/**
	 * a suffix that will be appended to an activity named configured in a
	 * string array
	 */
	private static final String ACTIVITY_NAME_SUFFIX = "Activity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the list view as content view
		setContentView(R.layout.listview);

		/*
		 * access the list view for the options to be displayed
		 */
		ListView listview = (ListView) findViewById(R.id.list);

		// read out the options
		final String[] menuItems = getResources().getStringArray(
				R.array.main_menu);

		/*
		 * create an adapter that allows for the view to access the list's
		 * content and that holds information about the visual representation of
		 * the list items
		 */
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menuItems);

		// set the adapter on the list view
		listview.setAdapter(adapter);

		// set a listener that reacts to the selection of an element
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.i(this.getClass().getName(), "got item selected: "
						+ adapter.getItem(arg2));

				// we read out the position of the activity name in the
				// main_menu_activities array
				runActivity(getResources().getStringArray(
						R.array.main_menu_activities)[arg2]);
			}

		});
	}

	/**
	 * runs an activity given the activity's name
	 * 
	 * @param activityName
	 */
	public void runActivity(String activityName) {
		Log.i(this.getClass().getName(), "running activity: " + activityName);

		// determine the activity class given the activity's name and our own
		// package
		// NOTE that this demonstrates usage of the java "reflection"
		// functionality, e.g. the dynamic loading of classes
		try {
			Class<?> activityClass = Class.forName(this.getClass().getPackage()
					.getName()
					+ "." + activityName + ACTIVITY_NAME_SUFFIX);
			Log.i(this.getClass().getName(), "will use activity class: "
					+ activityClass);

			// we create an intent given the activitie's name and our own
			// package
			startActivity(new Intent(this, activityClass));
		} catch (ClassNotFoundException exp) {
			Log.e(this.getClass().getName(), "could not start activity: "
					+ activityName + ". Got exception: " + exp, exp);
		}
	}
}