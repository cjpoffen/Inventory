package app.se329.project2.tools;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

public class SharedPreferencesArray{
	
	static private String TOKEN = ",";
	
	public static Integer[] getIntegerArray(SharedPreferences sharedPreferences,String key){
		LinkedList<Integer> tokens = getIntegerList(sharedPreferences,key);
		Integer[] result = new Integer[tokens.size()];
		tokens.toArray(result);
		return result;
	}
	
	public static void putIntegerArray(SharedPreferences sharedPreferences,String key,Integer... array){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
		    str.append(array[i]).append(TOKEN);
		}
		sharedPreferences.edit().putString(key, str.toString()).apply();
	}
	
	public static LinkedList<Integer> getIntegerList(SharedPreferences sharedPreferences,String key){
		String unparsedArray = sharedPreferences.getString(key,"");
		StringTokenizer st = new StringTokenizer(unparsedArray, TOKEN);
		LinkedList<Integer> tokens = new LinkedList<Integer>();
		while(st.hasMoreTokens()){
		    tokens.add(Integer.parseInt(st.nextToken()));
		}
		return tokens;
	}
	
	public static void putIntegerList(SharedPreferences sharedPreferences,String key,LinkedList<Integer> array){
		Integer[] result = new Integer[array.size()];
		array.toArray(result);
		putIntegerArray(sharedPreferences, key, result);
	}
	
	public static void putStringArray(SharedPreferences sharedPreferences,String key,String... array){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
		    str.append(array[i]).append(TOKEN);
		}
		sharedPreferences.edit().putString(key, str.toString()).apply();
	}
	public static String[] getStringArray(SharedPreferences sharedPreferences,String key){
		LinkedList<String> tokens = getStringList(sharedPreferences,key);
		String[] result = new String[tokens.size()];
		tokens.toArray(result);
		return result;
	}

	public static LinkedList<String> getStringList(SharedPreferences sharedPreferences,String key){
		String unparsedArray = sharedPreferences.getString(key,"");
		StringTokenizer st = new StringTokenizer(unparsedArray, TOKEN);
		LinkedList<String> tokens = new LinkedList<String>();
		while(st.hasMoreTokens()){
		    tokens.add(st.nextToken());
		}
		return tokens;
	}
	
	public static boolean deletePref(SharedPreferences sharedPreferences,String key)
	{
		sharedPreferences.edit().remove(key).apply();
		
		return false;
	}
	
	public static boolean putHashMap(SharedPreferences sharedPreferences, HashMap<String, String> urls){
		Set<String> keys = urls.keySet();
		Editor prefs = sharedPreferences.edit();
		for(String key : keys){
			prefs.putString(key, urls.get(key));
		}
		return prefs.commit();
	}
	
	public static  HashMap<String, String>  getHashMap(SharedPreferences sharedPreferences, String[] keySet){
		 HashMap<String, String> configs = new HashMap<String, String>();
		 
		 for(String key : keySet){
			 configs.put(key, sharedPreferences.getString(key, null));
		 }
		 
		return configs;
	}
}
