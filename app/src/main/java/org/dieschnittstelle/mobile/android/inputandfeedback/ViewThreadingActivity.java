package org.dieschnittstelle.mobile.android.inputandfeedback;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.dieschnittstelle.mobile.android.inputandfeedback.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Demonstrates triggering of view manipulation from an asynchronous process
 * (rather than from an AsyncTask), see section on "Technische Rahmenbedinungen"
 * 
 * @author Joern Kreutel
 * 
 */
public class ViewThreadingActivity extends Activity {

	private TextView feedbackTextView;

	private UIManipulationHandler handler = new UIManipulationHandler();

	/**
	 * constants for data to be put onto the messages to be passed to the
	 * handler
	 */
	public static final String MSGARG_MESSAGE_TYPE = "msgType";

	public static final int UI_UPDATE_REQUEST = 0;

	public static final String MSGARG_UPDATE_TEXT = "updateText";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the threadingview as content view
		setContentView(R.layout.threadingview);

		// set the textView for displaying feedbacks
		feedbackTextView = (TextView) findViewById(R.id.feedbackTextView);

		/*
		 * access the list view for the options to be displayed
		 */
		ListView listview = (ListView) findViewById(R.id.list);

		// read out the options
		final String[] menuItems = getResources().getStringArray(
				R.array.threading_menu);
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

				feedbackTextView.setText("");

				/*
				 * here, we create a thread that will use a callback on this
				 * activity
				 */
				CallbackRunnable runnable = new CallbackRunnable(
						ViewThreadingActivity.this,
						getResources().getStringArray(
								R.array.threading_menu_methods)[Arrays.asList(
								menuItems).indexOf(adapter.getItem(arg2))]);

				(new Thread(runnable)).start();
			}

		});

	}

	/*******************************************************
	 * the methods that will be called back from the thread
	 *******************************************************/

	/**
	 * this method will result in an error due to the attempt to initiate a ui
	 * change from a thread that is not the ui thread
	 */
	public void runThreadingError() {
		try {
			feedbackTextView.setText("runThreadingError()");
		} catch (Throwable t) {
			Log.e(ViewThreadingActivity.class.getName(),
					"got exception running runThreadingError: " + t, t);
		}
	}

	/**
	 * initiate a ui change by letting a Runnable run on the ui thread
	 */
	public void runUIThread() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				feedbackTextView.setText("runUIThread()");
			}
		});
	}

	/**
	 * initiate a ui change through a handler
	 */
	public void runHandler() {
		// create the message
		Message msg = new Message();
		msg.getData().putInt(MSGARG_MESSAGE_TYPE, UI_UPDATE_REQUEST);
		msg.getData().putSerializable(MSGARG_UPDATE_TEXT, "runHandler()");

		this.handler.sendMessage(msg);
	}

	/**
	 * the handler for receiving and processing messages
	 */
	private class UIManipulationHandler extends Handler {

		public void handleMessage(Message msg) {

			Log.i(this.getClass().getName(), "handling message: " + msg);

			int msgType = msg.getData().getInt(MSGARG_MESSAGE_TYPE);

			switch (msgType) {
			case UI_UPDATE_REQUEST:
				feedbackTextView.setText(String.valueOf(msg.getData()
						.getSerializable(MSGARG_UPDATE_TEXT)));
				break;
			default:
				Log.e(getClass().getName(), "got unknown message type: "
						+ msgType);
			}

		}

	}

	/**
	 * the runnable used for creating a new thread. Sleeps 1500ms and then calls
	 * back the activity given the method selected by the user
	 */
	private class CallbackRunnable implements Runnable {

		/**
		 * the activity to run on
		 */
		private ViewThreadingActivity activity;

		private String methodName;

		public CallbackRunnable(ViewThreadingActivity activity,
				String methodName) {
			this.activity = activity;
			this.methodName = methodName;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1500);
				Method meth = activity.getClass().getDeclaredMethod(
						this.methodName);
				meth.invoke(this.activity);
			} catch (Throwable t) {
				Log.e(ViewThreadingActivity.class.getName(),
						"got exception trying to run method " + methodName
								+ " on activity " + activity + ": " + t, t);
			}
		}

	}

}
