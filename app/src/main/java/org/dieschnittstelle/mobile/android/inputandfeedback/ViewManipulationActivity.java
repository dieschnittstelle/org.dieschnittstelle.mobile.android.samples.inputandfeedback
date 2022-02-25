package org.dieschnittstelle.mobile.android.inputandfeedback;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * Implements the dependencies on a form's ui elements as described on the
 * "Szenario" slide of the "Manipulation einer Ansicht" section
 * 
 * @author Joern Kreutel
 * 
 */
public class ViewManipulationActivity extends Activity {

	/**
	 * the logger (aka "tag" in android's diction)
	 */
	protected static final String logger = ViewManipulationActivity.class
			.getName();

	/**
	 * constants for doing some validation of input
	 */
	public static final String INVALID_TEXTINPUT1 = "0000";

	/**
	 * the ui elements
	 */
	private TextView textView;
	private EditText editText1;
	private EditText editText2;
	private CheckBox checkBox1;
	private CheckBox checkBox2;
	private Button okButton;

	// the model fields
	private String textInput1Value = null;
	private String textInput2Value = null;
	private String dynSpinnerValue = null;

	/**
	 * the dynamic spinner that will be inserted into the layout on checkBox1
	 * selection
	 */
	private Spinner dynSpinner;

	/**
	 * the layout that will be displayed while loading the dynSpinner
	 */
	private LinearLayout dynSpinnerPlaceholder;

	/**
	 * the layout to which the spinner will be attached
	 */
	private LinearLayout formFieldsLayout;

	/**
	 * keep the validity state of the editText1
	 */
	boolean invalidTextInput1 = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the list view as content view
		setContentView(R.layout.formview);

		// access the ui elements
		textView = (TextView) findViewById(R.id.textView);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		okButton = (Button) findViewById(R.id.okButton);

		/*
		 * constrain the input type on editText2
		 */
		editText2.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		/*
		 * initialise the spinner, using a layout inflater on the layout
		 * resource
		 */
		dynSpinner = (Spinner) getLayoutInflater().inflate(
				R.layout.dynamic_spinner, null);
		Log.d(logger, "got inflated spinner: " + dynSpinner);

		/*
		 * create a list adapter for the spinner which we can fill dynamically,
		 * given the input on textField1 (i.e. we add an empty list for the
		 * beginning) NOTE that we need to add an implementation of
		 * java.util.List rather than an array in order to be able to set
		 * elements dynamically!!!
		 */
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dynSpinner.setAdapter(adapter1);

		// set a listener on the spinner
		dynSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				if (position == 0) {
					Log.i(logger,
							"got selection on 1st element, which may be the preselection. Ignore.");
				} else {
					String selectedItem = ((TextView) selectedItemView)
							.getText().toString();
					Log.i(logger, "got selected item on spinner: "
							+ selectedItem);
					processSpinner1Selection(selectedItem);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				dynSpinnerValue = null;
			}

		});

		/*
		 * the (loading...) view that will be displayed before the spinner will
		 * be shown
		 */
		dynSpinnerPlaceholder = (LinearLayout) getLayoutInflater().inflate(
				R.layout.loadingitemview, null);

		/*
		 * access the formfields layout object to which the spinner will be
		 * attached
		 */
		formFieldsLayout = (LinearLayout) findViewById(R.id.formfields);

		/*
		 * set two listeners on the editText1 which detect finalisation of
		 * input, indicated by IME_ACTION_DONE and also listen to any key press
		 */
		editText1.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
				Log.d(logger, "onEditorAction() for editText1: " + arg1
						+ " on textview " + view + ". KeyEvent is: " + arg2);
				if (arg1 == EditorInfo.IME_ACTION_DONE) {
					// Zugriff auf den eingegebenen Text
					String text = view.getText().toString();

					processTextInput1(text);

					return false;
				}
				return false;
			}

		});

		editText1.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d(logger, "okKey() for editText1: " + keyCode
						+ ". KeyEvent is: " + event);
				processTextInput1Changed();
				return false;
			}

		});

		/*
		 * set the same kind of listeners on editText2
		 */
		editText2.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
				Log.d(logger, "onEditorAction() for editText2: " + arg1
						+ " on textview " + view + ". KeyEvent is: " + arg2);
				if (arg1 == EditorInfo.IME_ACTION_DONE) {
					// Zugriff auf den eingegebenen Text
					String text = view.getText().toString();

					processTextInput2(text);

					return false;
				}
				return false;
			}

		});

		editText2.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d(logger, "okKey() for editText2: " + keyCode
						+ ". KeyEvent is: " + event);
				processTextInput2Changed();
				return false;
			}

		});

		/*
		 * set listeners on the check boxes which call the corresponding methods
		 * for further processing
		 */
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkbox,
					boolean checked) {
				Log.i(logger, "onCheckedChange() for checkBox1: checked is: "
						+ checked);
				processCheckBoxSelection1(checked);
			}
		});

		checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkbox,
					boolean checked) {
				Log.i(logger, "onCheckedChange() for checkBox2: checked is: "
						+ checked);
				processCheckBoxSelection2(checked);
			}
		});

		/*
		 * set an OnClickListener on the button
		 */
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(
						ViewManipulationActivity.this,
						"textInput1Value\t\t"
								+ textInput1Value
								+ (textInput2Value != null ? "\ntextInput2Value\t\t"
										+ textInput2Value
										: "")
								+ (dynSpinnerValue != null ? "\ndynSpinnerValue\t\t"
										+ dynSpinnerValue
										: ""), Toast.LENGTH_LONG).show();
			}
		});

	}

	/*********************************************
	 * the controller methods for the ui elements
	 *********************************************/

	/**
	 * here, we add/remove a ui element
	 * 
	 * @param selected
	 */
	private void processCheckBoxSelection1(boolean selected) {
		Log.i(logger, "processing selection of checkBox1: " + selected);
		// if selected is true, we activate the editText2, otherwise we hide it
		if (selected) {
			// we will asynchronously display the dynSpinnerPlaceholder
			new AsyncTask<Void, Void, Object>() {

				/*
				 * before executing, we will add the placeholder
				 */
				@Override
				protected void onPreExecute() {
					formFieldsLayout.addView(dynSpinnerPlaceholder,
							formFieldsLayout.indexOfChild(checkBox1) + 1);
				}

				@Override
				protected Object doInBackground(Void... arg) {
					try {
						// we sleep as long as we have set the textInput1...
						Thread.sleep(Integer.parseInt(textInput1Value));
					} catch (Throwable t) {
						String err = "got exception on doInBackground(): " + t;
						Log.e(logger, err, t);
					}

					return "test";
				}

				/*
				 * after executing, we will remove the placeholder and replace
				 * it with the spinner
				 */
				@Override
				protected void onPostExecute(Object response) {
					Log.i(logger, "will add spinner to layout...");
					formFieldsLayout.removeView(dynSpinnerPlaceholder);
					// we add the spinner right after the checkbox, after
					// updating its
					// values
					formFieldsLayout.addView(dynSpinner,
							formFieldsLayout.indexOfChild(checkBox1) + 1);
					updateSpinnerForInput();
				}

			}.execute();

		} else {
			Log.i(logger, "will remove spinner from layout...");
			formFieldsLayout.removeView(dynSpinner);
		}
		updateOkButtonState();
	}

	/**
	 * here, we simple change the visibility attribute of the dependent editText
	 * ui element
	 * 
	 * @param selected
	 */
	private void processCheckBoxSelection2(boolean selected) {
		Log.i(logger, "processing selection of checkBox1: " + selected);
		// if selected is true, we activate the editText2, otherwise we hide it
		if (selected) {
			editText2.setVisibility(View.VISIBLE);
		} else {
			editText2.setVisibility(View.INVISIBLE);
		}
		updateOkButtonState();
	}

	/**
	 * this is called when the user types in something in textInput1
	 */
	private void processTextInput1Changed() {
		// as soon as the user starts typing, we reset any existing value for
		// textInput1Value
		if (textInput1Value != null) {
			textInput1Value = null;
		}

		// we also reset the invalid flag
		if (invalidTextInput1) {
			resetInvalidTextInputState();
		}

		updateOkButtonState();
	}

	/**
	 * 
	 * @param text
	 */
	private void processTextInput1(String text) {
		Log.i(logger, "process textInput2: " + text);

		// if we have an invalid input, we display an error message...
		if (INVALID_TEXTINPUT1.equals(text)) {
			Log.i(logger, "text input is invalid");

			// textView.setTextColor(R.color.green);
			textView.setText("Diese Eingabe ist nicht gueltig.");

			// and we track that an invalid input has been entered
			invalidTextInput1 = true;
		}
		else if (text != null && text.trim().length() < 2) {
			Log.i(logger, "text input is invalid. At least two digits must be input");

			// textView.setTextColor(R.color.green);
			textView.setText("Es müssen mindestens 2 Ziffern einegegeben werden");

			// and we track that an invalid input has been entered
			invalidTextInput1 = true;
		} else {
			// if we have a valid input, the ok button is set to enabled
			Log.i(logger, "text input is valid");
			textInput1Value = text;
			// we set the checkBox1 as enabled
			checkBox1.setEnabled(true);
			// we also update the spinner
			updateSpinnerForInput();

			updateOkButtonState();
		}
	}

	/**
	 * methods for dealing with editText2
	 */
	private void processTextInput2Changed() {
		// as soon as the user starts typing, we reset any existing value for
		// textInput1Value
		if (textInput2Value != null) {
			textInput2Value = null;
		}

		// we also reset the invalid flag
		if (invalidTextInput1) {
			resetInvalidTextInputState();
		}

		updateOkButtonState();
	}

	/**
	 * 
	 * @param text
	 */
	private void processTextInput2(String text) {
		Log.i(logger, "process textInput2: " + text);

		Log.i(logger, "text input is valid");
		textInput2Value = text;

		updateOkButtonState();
	}

	/**
	 * process selection of a spinner element
	 * 
	 * @param selectedItem
	 */
	private void processSpinner1Selection(String selectedItem) {
		dynSpinnerValue = selectedItem;
		updateOkButtonState();
	}

	/**
	 * update the spinner
	 */
	private void updateSpinnerForInput() {

		// we will first check whether the spinner is being displayed or not
		if (dynSpinner.getParent() == null) {
			Log.i(logger,
					"no need to update spinner. It is not attached to the layout.");
		}
		// otherwise we will access the editText1 and read out its current value
		else {
			String[] spinnerValues = getPossibleValuesForSpinner(editText1
					.getText().toString());
			Log.i(logger,
					"will update spinner with values: "
							+ Arrays.toString(spinnerValues));
			((ArrayAdapter<String>) dynSpinner.getAdapter()).clear();
			for (int i = 0; i < spinnerValues.length; i++) {
				((ArrayAdapter<String>) dynSpinner.getAdapter()).add(spinnerValues[i]);
			}
			// trigger redraw of the spinner
			dynSpinner.invalidate();
			// we also reset any value that has been set before
			dynSpinnerValue = null;
		}
	}

	/**
	 * helper method: get possible values for the spinner given the input of
	 * editText1 (this is some arbitrary implementation which simply adds all
	 * substrings of the argument, starting with the first character)
	 */
	private String[] getPossibleValuesForSpinner(String editText1Value) {
		String[] values = new String[editText1Value.length()];

		for (int i = 0; i < editText1Value.length(); i++) {
			if (i < editText1Value.length() - 1) {
				values[i] = editText1Value.substring(0, i + 1);
			} else {
				values[i] = editText1Value;
			}
		}

		return values;
	}

	/**
	 * resetting the flag that marks the text of editText1 as invalid
	 */
	private void resetInvalidTextInputState() {
		Log.i(logger, "resetting invalid state for text input");
		invalidTextInput1 = false;
		textView.setText("");
	}

	/**
	 * recalculate the enabled state of the ok button
	 */
	private void updateOkButtonState() {
		okButton.setEnabled(textInput1Value != null
				&& (!checkBox1.isChecked() || dynSpinnerValue != null)
				&& (!checkBox2.isChecked() || textInput2Value != null));
	}

}
