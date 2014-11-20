package app.se329.project2;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import app.se329.project2.model.InventoryItem;
import app.se329.project2.views.ListItemView;


public class HomeFragment extends ProjectFragment{

    View rootView;
    MainActivity mainActivity;
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		mainActivity = getSupportActivity();
		setupInitial();
		return rootView;
    }
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.findItem(R.id.go_back).setVisible(false);
		menu.findItem(R.id.action_export).setVisible(false);
	}

	private void setupInitial() {
		TextView tv = (TextView) rootView.findViewById(R.id.user_name_text);
		tv.setText(mainActivity.getSessionUser());
		
		attemptNetInventoryPull();
	}
	
	/**
	 * Attempts to pull sessionUser's inventory from the web.
	 */
    private void attemptNetInventoryPull() {
		
		
	}

	@Override
    public int getActionBarColor() {
        return Color.TRANSPARENT;
    }
	
	private void promptUser(String title, String message){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Okay", null)
			.setNegativeButton("Okay", null);
		alertDialogBuilder.create().show();
	}
}
