package org.dieschnittstelle.mobile.android.inputandfeedback;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMenusActivity extends Activity {

	protected static String logger = ViewMenusActivity.class.getSimpleName();

	private MenuItem option1;
	private View textViewContainer;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menusview);
		textView = (TextView) findViewById(R.id.textView);
		Log.i(logger, "found textViewContainer: " + textViewContainer);
		registerForContextMenu(textView);
	}

	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo info) {
		Log.i(logger, "onCreateContextMenu() for view: " + view);
		if (view == textView) {
			getMenuInflater().inflate(R.menu.contextmenu, menu);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(logger, "onCreateOptionsMenu()");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.optionsmenu, menu);

		option1 = menu.findItem(R.id.menu_option1);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.i(logger, "onPrepareOptionsMenu()");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(logger, "onOptionsItemSelected(): " + item);
		if (item.getItemId() == R.id.menu_option1) {
			Toast.makeText(this, "Option1 selected!", Toast.LENGTH_LONG).show();
		} else {
			option1.setEnabled(!option1.isEnabled());
		}
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(logger, "onContextItemSelected(): " + item);

		
		DisplayMetrics metrics;
		metrics = getApplicationContext().getResources().getDisplayMetrics();
		float currentSize = textView.getTextSize()/metrics.density;
		float newSize = 0.0f;
		
		if (item.getItemId() == R.id.ctxmenu_optionA) {
			newSize = currentSize * 2;
		} else {
			newSize = currentSize / 2;
		}
		Log.i(logger,
				"setting new size of textView from " + currentSize
						+ " to: " + newSize);

		textView.setTextSize(newSize);

		return true;
	}

}
