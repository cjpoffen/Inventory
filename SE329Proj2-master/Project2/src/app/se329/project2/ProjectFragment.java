 package app.se329.project2;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Custom class for all Fragments in this MyState app to extend. Allows for extra customizations to all fragments of app 
 * and the ability for modularity.
 *
 */
public abstract class ProjectFragment extends Fragment{
	
	protected AsyncTask<?, ?, ?> currentAsyncTask;
	static float lastT = 0.0f;
	
	public String activeUser = "";
	
	boolean allowed = true;
	
	/**
	 * Handler for back pressed. Usually called by the activity the fragment is attached to.
	 * @return false if onBackPressed was not handled and needs to be handle by caller; true if back pressed was handled
	 */
	public boolean onBackPressed(){
		return false;
	}
	
	public String getTitle(){
		return getTag();
	}
	
	public MainActivity getSupportActivity(){
		return ((MainActivity) getActivity());
	}
	
	public ActionBar getSupportActionBar(){
		return getSupportActivity().getSupportActionBar();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		
		
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.back_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()== R.id.go_back){
			getSupportActivity().onBackPressed();
			return true;
		}
		if(item.getItemId() == R.id.action_low_inv){
			getSupportActivity().findLowInvItems();
		}
		return false;
	}
	
	@Override
	public void onResume() {
		getSupportActivity().setCurrentFragment(this);
		getSupportActionBar().setTitle(getTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActionBarColor()));
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if(currentAsyncTask!=null) currentAsyncTask.cancel(true);
		super.onPause();
	}
	  
	public float dip(int value){
		return value * getResources().getDisplayMetrics().density;
	}
	
	public float sp(int value){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
	}
	
	public void protectAsyncTask(AsyncTask<?, ?, ?> task){
		currentAsyncTask = task;
	}
	
	public int getActionBarColor(){
		return isAdded() ? getActivity().getResources().getColor(R.color.blue) : -1;
	}
	
	/**
	 * Sets the permission for whether or not analytics
	 * is allowed to report data.
	 * @param isAllowed
	 */
	public void setDataReportingAllowed(boolean isAllowed)
	{
		allowed = isAllowed;
	}
	
	public void setActionBarColor(int color){
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
	}

	protected boolean overrideOnKeyUp(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_MENU){
			if(MainActivity.drawerLayout.isDrawerOpen(Gravity.LEFT)){
				MainActivity.drawerLayout.closeDrawers();
				return true;
			}
			MainActivity.drawerLayout.openDrawer(Gravity.LEFT);
			return true;
		}
		
		if(keyCode == KeyEvent.KEYCODE_BACK && MainActivity.drawerLayout.isDrawerOpen(Gravity.LEFT))
		{
			MainActivity.drawerLayout.closeDrawers();
			return true;
		}
		return false;
	}
}
