package app.se329.project2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import app.se329.project2.model.Inventory;
import app.se329.project2.model.InventoryItem;
import app.se329.project2.tools.DatabaseAccess;

public class MyJsonUtil {

	static Context cntxt;
	
	public MyJsonUtil(Context context){
		cntxt = context;
	}
	
	public static JSONObject readFromFile(String filename){
		String jsonText = null;
		JSONObject jObj = null;
		try{
			FileInputStream is = cntxt.openFileInput(filename);
			byte [] buffer = new byte[is.available()];
			while (is.read(buffer) != -1);
			jsonText = new String(buffer);
			Log.i("JSON", filename+" contents: " + jsonText);
		}
		catch(FileNotFoundException e){
			Log.e("JSON", filename+" not found. Creating template.");
			jsonText = "{\""+filename+"\":[]}";
			try {
				saveToFile(filename, new JSONObject(jsonText));
			} catch (JSONException e1) {e1.printStackTrace();}
		}
		catch(IOException e){
			e.printStackTrace();
		}

		try { jObj = new JSONObject(jsonText); } 
		catch (JSONException e){e.printStackTrace(); }

		return jObj;
	}
	
	public static boolean saveToFile(String filename, JSONObject fullDataObj){
		try
		{
			FileOutputStream fos = cntxt.openFileOutput(filename, Context.MODE_PRIVATE);
		    fos.write(fullDataObj.toString().getBytes());
		    fos.close();
		    Log.i("JSON", "File: " +filename+" updated.\nNew Contents: " + fullDataObj.toString());
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Writes the user credentials to local_users.json file, for local log on
	 * if the user is not already in the file.
	 * @param userToEnter
	 * @param passToEnter
	 * @return True if user is new to this device.
	 */
	public boolean verifyLocalUser(String userToEnter, String passToEnter){
		String filename = "local_users";
		
		//get current file contents
		JSONObject fullDataObj = readFromFile(filename);
		
		// create new user entry
		JSONArray usersArr = null;
		JSONObject newUserObj = null;
		try {
			usersArr = fullDataObj.getJSONArray(filename);
			for(int i = 0 ; i < usersArr.length(); i++)
			{
				newUserObj = usersArr.getJSONObject(i);
				if(newUserObj.getString("username").equals(userToEnter))
				{
					Log.d("JSON", "Local User Found.");
					return false;
				}
			}
			newUserObj = new JSONObject();
			newUserObj.put("username", userToEnter);
			newUserObj.put("password", passToEnter); 
			
			// place new entry into array
			usersArr.put(newUserObj);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// save all the contents
		saveToFile(filename, fullDataObj);
		return true;
	}

	public ArrayList<InventoryItem> getInventoryItems(Inventory inventory, String data) {
		
		Log.d("JSON", "Loading inventory items for: " + inventory.getName());
		
		ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
		
		String filename = inventory.getUser() + "_inv_" + inventory.getId();
		
		JSONObject inventJson;
		JSONArray itemsJsonArr = null;
		JSONObject itemJson = null;
		InventoryItem item = null;
		try {
			
			if(data == null)inventJson = readFromFile(filename);
			else {
				Log.i("JSON", "Inflating from result file.");
				inventJson = new JSONObject(data);
			}
			
			itemsJsonArr = inventJson.getJSONArray("items");
			for(int i = 0; i < itemsJsonArr.length(); i++){
				item = new InventoryItem();
				itemJson = new JSONObject();
				itemJson = itemsJsonArr.getJSONObject(i);
				item.setName(itemJson.getString("item_name"));
				item.setDescr((itemJson.getString("item_desc")));
				item.setQuantity(Integer.parseInt(itemJson.getString("item_quan")));
				item.setUnitWeight(Double.parseDouble(itemJson.getString("item_weight")));
				item.setPicName(itemJson.getString("pic_name"));
				item.setPicPath(""+cntxt.getFilesDir());
				
				Log.i("Item", "Added item: " + item.getName());
				items.add(item);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return items;
	}
	
	public void saveInventoryItems(Inventory inventory) {
		
		Log.d("JSON", "Saving inventory items for: " + inventory.getName());
		
		String filename = inventory.getUser() + "_inv_" + inventory.getId();
		ArrayList<InventoryItem> items = inventory.getItems();
		
		// create new user entry
		JSONObject pushInventory = null;
		JSONObject putItem = null;
		JSONArray itemsList = null;
		try {
			pushInventory = new JSONObject();
			itemsList = new JSONArray();
			
			pushInventory.put("id", inventory.getId());
			pushInventory.put("name", inventory.getName());
			pushInventory.put("desc", inventory.getDesc());
			for(int i = 0 ; i < items.size(); i ++)
			{
				putItem = new JSONObject();
				putItem.put("item_name", items.get(i).getName());
				putItem.put("item_desc", items.get(i).getDesc());
				putItem.put("item_quan", items.get(i).getQuantity());
				putItem.put("item_weight", items.get(i).getUnitWeight());
				putItem.put("item_weigh_unit", items.get(i).getWeightUnits());
				putItem.put("pic_name", items.get(i).getPicName());
				
				itemsList.put(i, putItem);
			}
			pushInventory.put("items", itemsList);
			
			saveToFile(filename, pushInventory);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Inventory> getInventories(String userName) {
		//TODO
		Log.i("Inventory", "Loading list of inventories...");
		ArrayList<Inventory> inventories = new ArrayList<Inventory>();
		String filename = userName + "_inventories";
		
		//get current file contents
		JSONObject fullData = readFromFile(filename);
		
		JSONArray invArr = null;
		try {
			invArr = fullData.getJSONArray(filename);
			Log.i("Inventory", "Inventory Array grabbed. Size: " + invArr.length());
			
			for(int i = 0; i < invArr.length() ; i++){
				JSONObject invObj = invArr.getJSONObject(i);
				int id = invObj.getInt("id");
				String name = invObj.getString("name");
				Inventory inv = new Inventory(id, userName, name);
				inventories.add(inv);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return inventories;
	}

	public void writeInventory(Inventory inventory) {
		Log.i("Inventory","Saving inventory: " +inventory.getName());
		
		String filename = inventory.getUser() + "_inventories";
		
		JSONObject fullDataObj = readFromFile(filename);
		JSONArray inventoriesArr = null;

		try {
			JSONObject inventoryJson = new JSONObject();
			inventoryJson.put("id", inventory.getId());
			inventoryJson.put("name", inventory.getName());
			
			inventoriesArr = fullDataObj.getJSONArray(inventory.getUser() + "_inventories");
			inventoriesArr.put(inventoryJson);
			
			saveToFile(filename, fullDataObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		
		
	}

	public static String saveBitmap(Bitmap image) {

		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
	    String mImageName="IP_"+ timeStamp +".jpg";
		
	    try {
	        FileOutputStream fos = cntxt.openFileOutput(mImageName, Context.MODE_PRIVATE);
	        image.compress(Bitmap.CompressFormat.PNG, 90, fos);
	        fos.close();
	    } catch (FileNotFoundException e) {
	        Log.d("image", "File not found: " + e.getMessage());
	    } catch (IOException e) {
	        Log.d("image", "Error accessing file: " + e.getMessage());
	    }
	    
	    return mImageName;
	}

	public static void uploadInventory(Inventory inventory) {
		
		final String filename = inventory.getUser() + "_inv_" + inventory.getId();
		
		//get current file contents
		final JSONObject inventoryJson = readFromFile(filename);
		
		Log.i("Upload", "Uploading "+filename+" to server...");
		new AsyncTask<String, Object, String>() {
			DatabaseAccess dbAccess = new DatabaseAccess();
			
			protected void onPreExecute() {

			};
			
			@Override
			protected String doInBackground(String... params) {
				dbAccess.writeToServer(""+inventoryJson, filename);
				return "";
			}
			
			protected void onPostExecute(String result) {
				
			}
		}.execute();
	}
	
	public void downloadInventory(Inventory inventory) {
		
		final String filename = inventory.getUser() + "_inv_" + inventory.getId();
		
		Log.i("Download", "Downloading "+filename+" from server...");
		new AsyncTask<String, Object, String>() {
			DatabaseAccess dbAccess = new DatabaseAccess();
			
			protected void onPreExecute() {

			};
			
			@Override
			protected String doInBackground(String... params) {
				return dbAccess.readFromServer(filename);
			}
			
			protected void onPostExecute(String result) {
				Log.i("Result", "Response from server: " + result);
				
			}
		}.execute();
	}
	
}
