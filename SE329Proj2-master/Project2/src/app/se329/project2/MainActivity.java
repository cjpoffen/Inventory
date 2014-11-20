package app.se329.project2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import app.se329.project2.model.Inventory;
import app.se329.project2.model.InventoryItem;
import app.se329.project2.model.Navigation;
import app.se329.project2.util.MyJsonUtil;

public class MainActivity extends ActionBarActivity {

	// Drawer Layout Instance.
	static DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ListView navigationDrawerView;
	
	// User Environment Variables
	private String sessionUser = null;
	private int currentInventory = 0;
	private boolean newToDevice;
	private ArrayList<Inventory> inventories;

	// First Level Frag used to track what level the fragment transaction is
	// happening on.
	static public final String FIRST_LEVEL_FRAGMENT = "FirstLevelFrag";

	private ProjectFragment currentFragment;
	private int selectedItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setDefaultFragment();

		setupViews();
		setupNavigationDrawer();
		forceMenuOverflow();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (drawerToggle != null)
			drawerToggle.syncState();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (currentFragment.overrideOnKeyUp(keyCode, event)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		closeOptionsMenu();
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (drawerToggle.onOptionsItemSelected(item))
			return true;
		// Handle your other action bar items...
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		getSupportActionBar().setSubtitle(null);

		int bsCount = getSupportFragmentManager().getBackStackEntryCount();
		String pageTitle = getSupportActionBar().getTitle().toString();
		Log.i("Inventory Pal", "onBackPressed() on page: "+pageTitle+". Back-stack count: "+bsCount+".");
		
		if (bsCount == 0) {
			
			if (pageTitle.equals("Home") || pageTitle.equals("Login")){
				Log.e("Inventory Pal", "Closing the app.");
				moveTaskToBack(true);
			}

			Bundle bundle = new Bundle();
			bundle.putBoolean(FIRST_LEVEL_FRAGMENT, true);
			
			if(sessionUser != null)
				setContent(new HomeFragment(), bundle, "Home");
			else
				setContent(new LoginFragment(), bundle, "Login");
		} else if (currentFragment != null) {
			if (currentFragment.onBackPressed() == false){
				super.onBackPressed();
				Log.d("Inventory Pal", "called super.onBackPressed();");
			}
		} else
			super.onBackPressed();
	}

	/**
	 * Handles selecting and switching fragments for the given position in the
	 * navigation menu
	 * 
	 * @param position
	 * @return the selected position
	 */
	public int selectMenuItem(int position) {
		if (position != selectedItem) {
			selectedItem = position;
			String fragmentTitle = navigationDrawerView.getItemAtPosition(position).toString();
			getSupportActionBar().setTitle(fragmentTitle);

			ProjectFragment selectedFragment;
			Bundle selectedFragmentBundle = new Bundle();
			selectedFragmentBundle.putBoolean(FIRST_LEVEL_FRAGMENT, false);
			
			if (fragmentTitle.contentEquals("Home")) {
				selectedFragment = new HomeFragment();
				selectedFragmentBundle.putBoolean(FIRST_LEVEL_FRAGMENT, true);
			} 
			else if (fragmentTitle.contentEquals("Items")){
				selectedFragment = new ItemsFragment();
			} 
			else{
				selectedFragment = null;
			}

			if (selectedFragment != null)
				setContent(selectedFragment, selectedFragmentBundle, fragmentTitle);
		}
		// Highlight the selected item and close the drawer
		drawerLayout.closeDrawers();
		return position;
	}

	/**
	 * Switches the currently showing fragment. Must be a Mystate Fragment. Use
	 * FIRST_LEVEL_FRAGMENT as an argument if the fragment appears in the
	 * drawer.
	 * 
	 * @param selectedFragment
	 *            extended from MyState Fragment to be switched to
	 * @param arguments
	 *            Bundle of arguments to pass to Transaction
	 * @param title
	 *            String to show in Actionbar.
	 */
	public void setContent(ProjectFragment selectedFragment, Bundle arguments, String title) {
		getSupportActionBar().setSubtitle(null); // used to remove title errors when switching fragments
		/*
		 * Removes all previous fragments on the stack, if a back is called
		 * immediately after this a new MainFragment should open and then exit
		 * the app.
		 */
		if (arguments.containsKey(FIRST_LEVEL_FRAGMENT)) {
			arguments.remove(FIRST_LEVEL_FRAGMENT);
			selectedFragment.setArguments(arguments);
			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left).replace(R.id.content_frame, selectedFragment, title).commit();
		} else {
			selectedFragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left).replace(R.id.content_frame, selectedFragment, title)
					.addToBackStack(title).commit();
		}
		currentFragment = selectedFragment;
	}

	/**
	 * Used to set current selected fragment within Drawer Layout
	 * 
	 * @param fragment
	 *            that has already been created and is being shown.
	 */
	public void setCurrentFragment(ProjectFragment fragment) {
		setItemViewSelected(currentFragment = fragment);
	}

	public void setupNavigationDrawer() {
		// Basics and sliding stuff
		drawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {

				
				super.onDrawerSlide(drawerView, slideOffset);
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		drawerLayout.setDrawerShadow(null, GravityCompat.START);

		// Sets content
		navigationDrawerView.setAdapter(new ArrayAdapter<String>(this, R.layout.view_list_item_drawer, Navigation.getItems(this)) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView itemView = (TextView) getLayoutInflater().inflate(R.layout.view_list_item_drawer, null, false);
				itemView.setText(getItem(position));
				// itemView.setTypeface(Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto/Roboto-Light.ttf"));
				setSelectionOfItemView(itemView, position == selectedItem);

				return itemView;
			}
		});

		// Handling clicks
		navigationDrawerView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				if(sessionUser != null){
					TextView itemView = (TextView) view;
					setItemViewSelected(itemView, adapterView);
					// Switch content to proper view
					selectMenuItem(position);
				}
				else{
					Toast toast = Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});

	}

	private void forceMenuOverflow() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setItemViewSelected(ProjectFragment fragment) {
		for (int i = 0; i < navigationDrawerView.getChildCount(); i++) {
			View view = navigationDrawerView.getChildAt(i);
			if (view instanceof TextView) {
				TextView itemView = (TextView) navigationDrawerView.getChildAt(i);
				if (itemView != null && itemView.getText().toString().contentEquals(fragment.getTitle())) {
					setItemViewSelected(itemView, navigationDrawerView);
					selectedItem = i;
				}
			}
		}
	}

	private void setItemViewSelected(TextView itemView, AdapterView<?> adapterView) {
		
		// clear old selections
		for (int i = 0; i < adapterView.getChildCount(); i++) {
			setSelectionOfItemView(adapterView.getChildAt(i), false);
		}
		
		// select new view
		setSelectionOfItemView(itemView, true);
	}

	private void setSelectionOfItemView(View itemView, boolean selected) {
		if (selected) {
			itemView.setSelected(true);
			itemView.setBackgroundColor(getResources().getColor(R.color.selection));
		} else {
			itemView.setSelected(false);
			itemView.setBackgroundColor(getResources().getColor(R.color.transparent));
		}
	}

	/**
	 * Sets the fragment on initial starting and retrying
	 */
	private void setDefaultFragment() {
		(currentFragment = new LoginFragment()).setArguments(new Bundle());
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, currentFragment, "Login").commit();

		// Makes the first action bar title the app name
		getSupportActionBar().setTitle("Login");
	}

	private void setupViews() {
		navigationDrawerView = (ListView) findViewById(R.id.navigation_drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setSessionUser(String user, boolean isnewuser) {
		sessionUser = user;
		newToDevice = isnewuser;
		
		loadInventories();
	}
	
	private void loadInventories() {
		
		MyJsonUtil jsonUtil = new MyJsonUtil(this);
		inventories = jsonUtil.getInventories(sessionUser);
		
		if(inventories.size() == 0){
			Log.e("Inventory", "No Inventories were found. Creating default");
			Inventory inv = new Inventory(0, sessionUser, sessionUser+"'s Default Inventory");
			inv.saveInventoryObject(this);
			inv.saveInventoryItems(this);
			inventories.add(inv);
			
		}
		
		inventories.get(0).inflateInventory(this, null);
		
	}

	public String getSessionUser() {
		return sessionUser;
	}
	
	public void setInventory(int inven) {
		currentInventory = inven;
	}
	
	public Inventory getCurrentInventory() {
		return inventories.get(currentInventory);
	}
	
	public boolean isNewUser() {
		return newToDevice;
	}
	
	public void findLowInvItems() {

		String pageTitle = getSupportActionBar().getTitle().toString();
		
		String items = "";
		
		Log.i("Inventory Pal", "findLowInvItems() on page: "+pageTitle+".");
		
		int x = 0;
		
		Inventory inv = getCurrentInventory();
	
		for(x = 0; x < inv.getItems().size(); x++){
			if(inv.getItems().get(x).getQuantity() < 5){
				items += "Item: " + inv.getItems().get(x).getName() + " 		Quantity: " + inv.getItems().get(x).getQuantity() + "\n";
			}
		}
		System.out.println(items);
		launchLowInvPopup(false, items, x);
	}
	
	public void launchLowInvPopup(boolean isNew, String items, int pos) {
		PopupActivity.popup((ItemsFragment) currentFragment, currentFragment.getActivity(), 1, new LowInvPopup(false, items, pos));
	}
	
}