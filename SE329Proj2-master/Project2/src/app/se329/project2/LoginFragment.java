package app.se329.project2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import app.se329.project2.tools.DatabaseAccess;
import app.se329.project2.util.MyJsonUtil;

public class LoginFragment extends ProjectFragment{

    View rootView;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
		rootView = inflater.inflate(R.layout.fragment_login, container, false);
		setupInitial(rootView);
		
		return rootView;
    }

    private void attemptLogin() {
    	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    	
    	EditText username = (EditText) rootView.findViewById(R.id.username_field);
		EditText pass = (EditText) rootView.findViewById(R.id.pass_field);
		final String userToVerify = username.getText().toString();
		final String passToVerify = pass.getText().toString();

		new AsyncTask<String, Object, String>() {
			DatabaseAccess dbAccess = new DatabaseAccess();
			
			protected void onPreExecute() {
				rootView.findViewById(R.id.reg_loading_home).setVisibility(View.VISIBLE);
			};
			
			@Override
			protected String doInBackground(String... params) {
				
				String verifyUser = "Select Password from Users where Username = '"+userToVerify+"';";
				String result = dbAccess.query(verifyUser);// should return null if no matches found.

				return result;
			}
			
			protected void onPostExecute(String result) {
				rootView.findViewById(R.id.reg_loading_home).setVisibility(View.INVISIBLE);
				boolean loginSuccess = false;
				try {
					JSONArray jArr = new JSONArray(result);
					JSONObject jObj = jArr.getJSONObject(0);
					result = jObj.getString("Password");
					
					if(result.equals(passToVerify)) loginSuccess = true;
					
				} catch (JSONException e) {  e.printStackTrace();  }
				
				if(loginSuccess)
				{
					boolean newToDevice = new MyJsonUtil(rootView.getContext()).verifyLocalUser(userToVerify, passToVerify);
					launchUserHome(userToVerify, newToDevice);
				}
				else if(result.equals("ER"))promptUser("Network Error", "Please check network connection and try again.");
				else promptUser("Login Failure", "Incorrect username or password.");
			}
		}.execute();
		
	}

    private void launchUserHome(String userToVerify, boolean newToDevice) {
    	
    	MainActivity activity = (MainActivity) getSupportActivity();
    	activity.setSessionUser(userToVerify, newToDevice);
    	HomeFragment homeFragment = new HomeFragment();
    	Bundle arguments = new Bundle();
    	activity.setContent(homeFragment, arguments, "Home");
	}
    
	private void launchRegisterPopup() {
		PopupActivity.closable = true;
		PopupActivity.popup((FragmentActivity) getActivity(),new RegisterPopup(), false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.findItem(R.id.go_back).setVisible(false);
		menu.findItem(R.id.action_export).setVisible(false);
	}

    @Override
    public int getActionBarColor() {
        return Color.TRANSPARENT;
    }
	
	@Override
	protected boolean overrideOnKeyUp(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			if(MainActivity.drawerLayout.isDrawerOpen(Gravity.LEFT))
			{
				MainActivity.drawerLayout.closeDrawers();
				return true;
			}
			
		MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
		return true;
		}
		
		return super.overrideOnKeyUp(keyCode, event);
	}
	
	private void setupInitial(View root){
		((TextView) root.findViewById(R.id.title2)).setTypeface(Typeface.createFromAsset(root.getContext().getAssets(),"fonts/Roboto/Roboto-Light.ttf"));
		root.findViewById(R.id.context_bar).setAnimation(AnimationUtils.loadAnimation(root.getContext(), R.animator.home_screen_animation));
		setHasOptionsMenu(true);
		
		Button localLogin = (Button) root.findViewById(R.id.locallogin_butt);
		localLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attemptLogin();
			}
		});
		
		Button registerButt = (Button) root.findViewById(R.id.signup_butt);
		registerButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchRegisterPopup();
			}
		});
	}
	
	private void promptUser(String title, String message){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Okay", null);
		alertDialogBuilder.create().show();
	}
}
