package app.se329.project2.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Sends a query in the form of a string to the server php files
 */
public class DatabaseAccess {
	
	public DatabaseAccess(){} 
	
	public String query(String command){
		Log.i("Query", "Command: " + command);
		String result = "";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("query",command));
		InputStream is = null;
		//http post
		try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost("http://arlenburroughs.com/se_329_inventorypal/db_query.php");
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        HttpContext localContext = new BasicHttpContext();
		        HttpResponse response = httpclient.execute(httppost,localContext);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        
		        Log.i("Query", ".getEntity() successful");
		}catch(Exception e){
			Log.e("Query", "Error in http connection: "+e.toString());
			return "ER";
		}
		
		//convert response to string
		try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		        }
		        is.close();
		        result=sb.toString();
		        Log.d("Query", "FROM PHP ---> " + result);
		        
		}catch(Exception e){
		        Log.e("Query", "Error converting result "+e.toString());
		        return "ER";
		}
		return result;
	}

	public void writeToServer(String data, String filename) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("filename",filename+".json"));
		nameValuePairs.add(new BasicNameValuePair("data",data));
		InputStream is = null;
		//http post
		try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost("http://arlenburroughs.com/se_329_inventorypal/inventories/write_inventory.php");
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        HttpContext localContext = new BasicHttpContext();
		        HttpResponse response = httpclient.execute(httppost,localContext);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        
		        Log.i("Query", ".getEntity() successful");
		}catch(Exception e){
			Log.e("Query", "Error in http connection: "+e.toString());
			return;
		}
		
	}
	
	public String readFromServer(String filename) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("filename",filename));
		InputStream is = null;
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://arlenburroughs.com/se_329_inventorypal/inventories/read_inventory.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpContext localContext = new BasicHttpContext();
			HttpResponse response = httpclient.execute(httppost, localContext);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

			Log.i("Query", ".getEntity() successful");
		} catch (Exception e) {
			Log.e("Query", "Error in http connection: " + e.toString());
			return "ER";
		}
		String result;
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.d("Query", "FROM PHP ---> " + result);

		} catch (Exception e) {
			Log.e("Query", "Error converting result " + e.toString());
			return "ER";
		}
		return result;
		
	}
}
