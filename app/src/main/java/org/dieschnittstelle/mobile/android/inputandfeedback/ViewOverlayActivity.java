package org.dieschnittstelle.mobile.android.inputandfeedback;

import java.lang.reflect.Method;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Demonstrates various ways to overlay feedback messages over a view
 * 
 * @author Joern Kreutel
 * 
 */
public class ViewOverlayActivity extends Activity {

	/**
	 * the ID(s) for the dialog(s) to be displayed
	 */
	public static final int ALERT_DIALOG = 0;
	
	/**
	 * count how often a dialog is being opened
	 */
	public int dialogCount = 0;

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
				R.array.overlay_menu);
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
				/*
				 * different from the solution in InputAndFeedbackActivity, we
				 * read out the item name in the overlay_menu_methods array
				 * determining the index of the selected item in the menuItems
				 * array (this way we are independent of the ordering of the
				 * menu items in the view
				 */
				runMethod(getResources().getStringArray(
						R.array.overlay_menu_methods)[Arrays.asList(menuItems)
						.indexOf(adapter.getItem(arg2))]);
			}

		});
	}

	/**
	 * runs an activity given the activity's name
	 * 
	 * @param activityName
	 */
	public void runMethod(String methodName) {
		Log.i(this.getClass().getName(), "running method: " + methodName);

		try {
			// determine the method given its name (and argument types, which we
			// do not have here)
			Method meth = this.getClass().getDeclaredMethod(methodName);

			// invoke the method, passing myself as the object on which the
			// invocation is supposed to take place
			meth.invoke(this);

			// there are quite some exceptions that may occur here...
		} catch (Exception exp) {
			Log.e(this.getClass().getName(), "could not run method: "
					+ methodName + ". Got exception: " + exp, exp);
		}
	}

	/**
	 * display a toast
	 */
	public void runToast() {
		Toast.makeText(this, "Das ist ein Toast!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * display a dialog passing arguments
	 */
	public void runDialog() {
		Bundle args = new Bundle();
		args.putInt("dialogCount", dialogCount++);
		
		showDialog(ALERT_DIALOG, args);
	}

	/**
	 * this method will be called the first time a dialog for some id is being
	 * created. Whenever the dialog is *shown*, onPrepareDialog() will be called!
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Log.i(this.getClass().getName(),
				"onCreateDialog(): id of the dialog is: " + id + ", args are: " + args);
		switch (id) {
		case ALERT_DIALOG:
			return createAlertDialog();
		default:
			return new Dialog(this);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		Log.i(this.getClass().getName(),
				"onPrepareDialog(): id of the dialog is: " + id + ", dialog object is: " + dialog + ", args are: " + args);
		((AlertDialog)dialog).setMessage("Das ist ein AlertDialog (" + args.getInt("dialogCount") + ")!");
	}
	
	/**
	 * creates an alert dialog (surprise...) 
	 * The message of the dialog will be set dynamically in onPrepareDialog()
	 * Selecting "ok" will result in displaying the toast message
	 * 
	 * @return
	 */
	private AlertDialog createAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Das ist ein AlertDialog")
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								// show a toast
								runToast();
							}
						})
				.setNegativeButton("Aufhoeren", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		return builder.create();
	}

	/**
	 * run a dialog that is synchronised with some background process
	 */
	public void runSyncDialog() {

		new AsyncTask<Void, Void, Object>() {

			private ProgressDialog dialog = null;

			@Override
			protected void onPreExecute() {

				dialog = ProgressDialog.show(ViewOverlayActivity.this,
						"Bitte warten Sie...", "waehrend des Ladevorgangs.");
			}

			/*
			 * the "background process": sleep for 5 seconds
			 */
			@Override
			protected Object doInBackground(Void... arg) {
				try {
					// sleep and try it again...
					Thread.sleep(5000);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							runDialog();
						}												
					});
				} catch (Throwable t) {
					String err = "got exception on doInBackground(): " + t;
					Log.e(ViewOverlayActivity.this.getClass().getName(), err, t);
				}

				return "test";
			}

			@Override
			protected void onPostExecute(Object response) {
				Log.i(ViewOverlayActivity.this.getClass().getName(),
						"onPostExecute()...");
				dialog.cancel();
			}

		}.execute();

	}

}
