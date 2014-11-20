package app.se329.project2.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

public class InternetUtil {

    private static InternetUtil internetUtil = null;

	public static InternetUtil getInstanceOfInternetUtil(){
		return internetUtil==null ? internetUtil = new InternetUtil() : internetUtil;
	}
	
	
	/* ***********************  Internet Helper Methods ***********************/
	public  JSONObject sendJSONRequest(String url, JSONObject obj) throws JSONException {
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		HttpConnectionParams.setSoTimeout(myParams, 10000);
		HttpClient httpclient = new DefaultHttpClient();
		
		JSONObject jsonResponse = new JSONObject();
		
		try {
			HttpPost httppost = new HttpPost(url.toString());
			httppost.setHeader("Content-type", "application/json");

			StringEntity se = new StringEntity(obj.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
			httppost.setEntity(se);

			HttpResponse response = httpclient.execute(httppost);
			String responseString = EntityUtils.toString(response.getEntity());
			jsonResponse = new JSONObject("{\"data\":"+responseString+"}");

		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		} catch (JSONException e) {
			jsonResponse = null;
		}			
		return jsonResponse;
	}
	
	/**
	 * Call get Request for JSONOBject of the given URL. 
	 * @param url String of URL to be queried with GET Request.
	 * @return JSON object from get Request
	 * @throws JSONException if JSON object cannot be created/parsed
	 */
	public  JSONObject sendGetRequest(String url) throws JSONException {
		return sendGetRequest(url, true);
	}
	
	public  JSONObject sendGetRequest(String url, boolean wrapResponseInData) throws JSONException {
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		HttpConnectionParams.setSoTimeout(myParams, 10000);
		HttpClient httpclient = new DefaultHttpClient();

		JSONObject jsonResponse = new JSONObject();
		try {

			HttpGet httpget = new HttpGet(url.toString());
			httpget.setHeader("Content-type", "application/json");
			HttpResponse response = httpclient.execute(httpget);
			String responseString = EntityUtils.toString(response.getEntity());
			if(wrapResponseInData) jsonResponse = new JSONObject("{\"data\":"+responseString+"}");
			else jsonResponse = new JSONObject(responseString);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		return jsonResponse;
	}
	
	public   boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public  Bitmap getBitmapForUrl(String url){
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    InputStream imageStream = new URL(url).openStream();
		    BitmapFactory.decodeStream(imageStream,null,options);

		    // Calculate inSampleSize

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    options.inPreferredConfig = Bitmap.Config.RGB_565;
		    imageStream = new URL(url).openStream();
			return BitmapFactory.decodeStream(imageStream,null,options);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public  Bitmap getBitmapForUrl(String url,int reqWidth,int reqHeight){
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    InputStream imageStream = new URL(url).openStream();
		    BitmapFactory.decodeStream(imageStream,null,options);

		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    options.inPreferredConfig = Bitmap.Config.RGB_565;
		    imageStream = new URL(url).openStream();
			return BitmapFactory.decodeStream(imageStream,null,options);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * Source: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 **/
	public  int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}
}