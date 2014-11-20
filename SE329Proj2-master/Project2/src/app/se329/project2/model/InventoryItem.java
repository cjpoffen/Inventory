package app.se329.project2.model;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class InventoryItem implements Serializable {

	private String name;
	private String descr;
	private int quantity;
	private double unitWeight;
	private String weightUnits;
	private String totalWeight;
	private String picName;
	private String picPath;
	private Bitmap bitmap;

	public InventoryItem(String itemName, String description, int quantity, double weight, String weightU, String bmpName, String bmpPath){
		setName(itemName);
		setDescr(description);
		setQuantity(quantity);
		setUnitWeight(weight);
		weightUnits = weightU;
		setTotalWeight();
		picName = bmpName;
		picPath = bmpPath;
	}

	public InventoryItem() {

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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quan) {
		quantity = quan;
	}

	public double getUnitWeight() {
		return unitWeight;
	}

	public void setUnitWeight(double weight) {
		this.unitWeight = weight;
	}
	
	public String getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight() {
		this.totalWeight = ""+quantity*unitWeight;
	}
	
	public String getPicName() {
		return picName;
	}

	public void setPicName(String name) {
		picName = name;
	}
	
	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String path) {
		picPath = path;
	}

	public String getWeightUnits() {
		return weightUnits;
	}
	
	public Bitmap getBitmap() {
		
		if(picName.equals("default"))return null;
		
		String fullPath = picPath+"/"+picName;
		if(bitmap == null){
			Log.i("Image", "----------------- Getting bitmap from: " + fullPath);
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			bitmap = BitmapFactory.decodeFile(fullPath, options);
		}
		
		return bitmap;
	}

	public void setBitmap(Bitmap bmp) {
		bitmap = bmp;
	}
	
}
