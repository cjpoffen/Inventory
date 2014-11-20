package app.se329.project2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import app.se329.project2.tools.DatabaseAccess;

/**
 * Manages registering a new user.
 * @author Arlen
 *
 */
class RegisterPopup extends Popup {

	public void configureButtonPresses(View popupContent) {
		
		popupContent.findViewById(R.id.okay_butt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptRegister();
			}
		});
		
		popupContent.findViewById(R.id.cancel_butt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopup();
			}
		});
	}
	
	@Override
	public View getPopupContentView() {
		View popupContent = LayoutInflater.from(popupActivity).inflate(R.layout.popup_register, null, false);
		configureButtonPresses(popupContent);
		return popupContent;
	}

	@Override
	public void popupIsShown(PopupActivity popupActivity) {
		popupActivity.getPopupContent().setVisibility(View.VISIBLE);
	}
	
	/**
	 * Validates registration form fields. 
	 * Creates asynctask to attempt to register new user.
	 * Displays appropriate prompt following registration attempt.
	 */
	private void attemptRegister(){
		
		// Get text field values. Store in final strings.
		EditText username = (EditText) popupActivity.findViewById(R.id.reg_name_field);
		EditText pass = (EditText) popupActivity.findViewById(R.id.reg_pass_field);
		EditText passConf = (EditText) popupActivity.findViewById(R.id.reg_pass2_field);
		final String userToEnter = username.getText().toString();
		final String passToEnter = pass.getText().toString();
		final String passToConf = passConf.getText().toString();
		
		// ensure form has no errors. If it does, abort registration.
		if(checkFormForErrors(userToEnter, passToEnter, passToConf))return;// errors
		
		// task for validating user's desired username. Insertion if valid.
		new AsyncTask<String, Object, String>() 
		{
			DatabaseAccess dbAccess = new DatabaseAccess();
	
			protected void onPreExecute() {
				popupActivity.findViewById(R.id.reg_loading).setVisibility(View.VISIBLE);
			};
			
			@Override
			protected String doInBackground(String... params) {
				
				// query to see if username is available.
				String verifyUser = "Select id from Users where Username = '"+userToEnter+"';";
				String result = dbAccess.query(verifyUser);// should return null if no matches found.
				
				Log.i("Query", "Result: >"+result+"<");
				
				if(result.equals("null\n"))// No matches were found. Let's add the new user.
				{
					Log.i("Register", "---------- Adding User to Users table");
					String insertUser = "INSERT INTO `Users`(`Username`, `Password`)" +
											"VALUES ('"+userToEnter+"','"+passToEnter+"')";
					result = dbAccess.query(insertUser);
					return result;
				}
				if(result.contains("[{\"id\":")) return "UAE";// User Already Exists
				return result; // could have been an error.
			}
			
			protected void onPostExecute(String result) {
				popupActivity.findViewById(R.id.reg_loading).setVisibility(View.INVISIBLE);
				Log.i("Query Result", "Final Result: " + result);
				
				// display appropriate prompt following registration attempt.
				if(result.equals("ER"))		 promptUser("Network Error", "Please check network connection and try again.", false);
				else if(result.equals("UAE"))promptUser("Registration Error", "User already exists! Please select another username.", false);
				else 						 promptUser("Registration Success", "Please log in to continue.", true);
			}
		}.execute();
	}
	
	/**
	 * Checks standard form conditions.
	 * @return False if there are no found errors.
	 */
	private boolean checkFormForErrors(String user, String pass, String passConf) {
		
		if(user.length() < 5)
		{
			promptUser("Form Error", "Username must be at least 5 characters.", false);
			return true;
		}
		if(pass.length() < 6)
		{
			promptUser("Form Error", "Passwords must be at least 6 characters.", false);
			return true;
		}
		if(!pass.equals(passConf))
		{
			promptUser("Form Error", "Passwords do not match.", false);
			return true;
		}
		return false;
	}

	/**
	 * Displays an AlertDialog in the popup activity.
	 * @param title The title of the dialog.
	 * @param message The body text  for the dialog.
	 * @param closePopup Close the registration dialog upon dismissing alert.
	 */
	private void promptUser(String title, String message, final boolean closePopup){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				new ContextThemeWrapper(popupActivity, R.style.Theme_AppCompat));
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(closePopup)closePopup();
				}
			});
		alertDialogBuilder.create().show();
	}
}
