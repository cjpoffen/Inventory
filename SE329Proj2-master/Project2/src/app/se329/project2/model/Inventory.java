package app.se329.project2.model;

import java.util.ArrayList;

import android.content.Context;
import app.se329.project2.util.MyJsonUtil;

public class Inventory {

	private int id;
	private String name;
	private String descr;
	private ArrayList<InventoryItem> items;
	private String userName;
	
	
	public Inventory(int id, String username, String inventoryName){
		this.id = id;
		userName = username;
		name = inventoryName;
	}
	
	/**
	 * 
	 * @return the Items in the inventor to be used by exportCSV
	 */
	public ArrayList<InventoryItem> getInventory(){
		return items;
	}
	
	public boolean inflateInventory(Context cntxt, String result){
		MyJsonUtil jsonUtil = new MyJsonUtil(cntxt);
		items = jsonUtil.getInventoryItems(this, result);
		
		return true;
	}
	
	public boolean saveInventoryObject(Context cntxt){
		MyJsonUtil jsonUtil = new MyJsonUtil(cntxt);
		jsonUtil.writeInventory(this);
		return true;
	}
	
	public boolean saveInventoryItems(Context cntxt){
		MyJsonUtil jsonUtil = new MyJsonUtil(cntxt);
		jsonUtil.saveInventoryItems(this);
		return true;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public ArrayList<InventoryItem> getItems() {
		if(items==null)items = new ArrayList<InventoryItem>();
		return items;
	}
	public void setItems(ArrayList<InventoryItem> items) {
		this.items = items;
	}
	
	public String getUser() {
		return userName;
	}

	public void setUser(String user) {
		this.userName = user;
	}
}
