package org.dieschnittstelle.mobile.android.inputandfeedback;

import org.dieschnittstelle.mobile.android.inputandfeedback.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Demonstrates usage of the 'back' button to return to the previous activity
 * 
 * @author Joern Kreutel
 * 
 */
public class ViewTransitionActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the list view as content view
		setContentView(R.layout.transtargetview);
	}

	/**
	 * override the handling method for 'back'
	 */
	@Override
	public void onBackPressed() {
		// display a toast
		Toast.makeText(this, "\'zurueck\' wurde gedrueckt!", Toast.LENGTH_SHORT)
				.show();

		super.onBackPressed();
	}

}
