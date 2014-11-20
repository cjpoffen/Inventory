package app.se329.project2;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import app.se329.project2.model.InventoryItem;

/**
 * Manages showing the low inventory items in the current inventory
 */
class LowInvPopup extends Popup {

	boolean isNewItem;
	boolean editing = true;
	int position;
	String items;
	
	public LowInvPopup(boolean isNew, String items, int pos) {
		position = pos;
		isNewItem = isNew;
		this.items = items;
	}

	@Override
	public View getPopupContentView() {
		View popupContent = LayoutInflater.from(popupActivity).inflate(R.layout.popup_low_inv, null, false);
		configureButtonPresses(popupContent);
		
		inflateTextFields(popupContent, false); 
		
		//hide soft keyboard
		popupActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		return popupContent;
	}
	
	//Add the desired text into a low inventory popup
	private void inflateTextFields(View popupContent, boolean enabled) {
		
		TextView itemsList = (TextView) popupContent.findViewById(R.id.low_inv_items);
		itemsList.setEnabled(enabled);
		itemsList.requestFocus();
		itemsList.setText(items);
		
	}
	
	//Add listeners to the Okay button on low inventory popups
	public void configureButtonPresses(View popupContent) {
		
		popupContent.findViewById(R.id.okay_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopup();
			}
		});
	}
	
	
	@Override
	public void popupIsShown(PopupActivity popupActivity) {
		popupActivity.getPopupContent().setVisibility(View.VISIBLE);
	}

	/**
	 * Displays an AlertDialog in the popup activity.
	 * @param title The title of the dialog.
	 * @param message The body text  for the dialog.
	 * @param closePopup Close the registration dialog upon dismissing alert.
	 */
	private void promptUser(String title, String message, final boolean closePopup){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(popupActivity, R.style.Theme_AppCompat));
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
