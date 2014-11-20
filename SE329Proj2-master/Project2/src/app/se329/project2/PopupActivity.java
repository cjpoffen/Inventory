package app.se329.project2;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import app.se329.project2.model.InventoryItem;
import app.se329.project2.tools.AnimationEndListener;
import app.se329.project2.util.MyJsonUtil;
/**
 * Popup activity is an activity class that allows the easy creation of views as popups.
 * @author wdian
 */
public class PopupActivity extends ActionBarActivity implements OnClickListener{
	
	static int REQUEST_CODE_MISSING = 212131231;
	static Popup popupAdapter;
	FrameLayout popupRoot;
	View popupContent;
	static boolean closable = true;
	String bmpName = "default";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup);
		
		popupAdapter.popupActivity = this;
		popupContent = popupAdapter.getPopupContentView();
		popupRoot = (FrameLayout) findViewById(R.id.popup_root);
		popupRoot.setOnClickListener(this);
		popupRoot.addView(popupContent);
		popupAdapter.popupIsShown(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	/**
	 * Creates a popup using a request code and adapter for handling activity results
	 * @param itemsFragment 
	 * @param activity The root activity the popup show appear on
	 * @param requestCode The given request code to handle results
	 * @param popup The popup that will be shown
	 */
	public static void popup(ItemsFragment itemsFragment, FragmentActivity activity, int requestCode, Popup adapter) {
		Intent intent = new Intent(activity, PopupActivity.class);
		if(REQUEST_CODE_MISSING==requestCode) activity.startActivity(intent);
		else {
			Log.i("Activity", "Starting activity for result. Request Code: " + requestCode);
			itemsFragment.startActivityForResult(intent,requestCode);
		}
		
		activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		popupAdapter = adapter;
	}
	
	/**
	 * Creates a popup using just a popup
	 * @param activity The root activity the popup show appear on
	 * @param popup The popup that will be shown
	 * @param canClose Whether the popup can close or not
	 */
	public static void popup(FragmentActivity activity, Popup adapter, boolean canClose) {
		popup(null, activity, REQUEST_CODE_MISSING, adapter);
		closable = canClose;
	}

	@Override
	public void onClick(View v) {
		if(closable)
			closePopup();
	}
	
	@Override
	public void onBackPressed() {
		closePopup();
	}

	public View getPopupContent(){
		return popupContent;
	}

	public void closePopup(){
		Animation fadeOut = AnimationUtils.loadAnimation(this, R.animator.fade_out);
		fadeOut.setAnimationListener(new AnimationEndListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				popupRoot.setVisibility(View.INVISIBLE);
				finish();
			}
		});
		popupRoot.startAnimation(fadeOut);
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("Result", "@@@ Returned with resultCode: " + resultCode + ", requestCode: " + requestCode);
		if((requestCode == 44 || requestCode == 15) && resultCode == -1){
			Uri selectedImage = data.getData();
            InputStream imageStream = null;
			try {
				imageStream = getContentResolver().openInputStream(selectedImage);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
            Bitmap toSave = BitmapFactory.decodeStream(imageStream);
            ImageView iv = (ImageView) findViewById(R.id.reg_photo);
            iv.setImageBitmap(toSave);
            MyJsonUtil jUtil = new MyJsonUtil(getApplicationContext());
            bmpName = jUtil.saveBitmap(toSave);
            Log.i("Image","Bitmap saved as: " + bmpName+"!");
		}
	}
}

/**
 *  Popup class to be used with a popup activity
 */
abstract class Popup{
	
	PopupActivity popupActivity;
	
	/**
	 * Define view to be used as popup in this method
	 */
	abstract public View getPopupContentView();
	
	/**
	 * Called after a the popup has be created and displayed
	 */
	public void popupIsShown(PopupActivity popupActivity){
		
	}
	
	public void closePopup(){
        if(popupActivity != null)
	    	popupActivity.closePopup();
	}
	
	/**
	 * Closes the popup and notifies the root activity of result
	 * @param resultCode The result code of the popup activity
	 * @param data Intent data returned by popup activity
	 */
	public void closePopup(int resultCode, Intent data){
		Log.i("Result", "--- Closing Popup. Result Code: " + resultCode);
		popupActivity.setResult(resultCode, data);
		popupActivity.closePopup();
	}
}