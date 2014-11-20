package app.se329.project2.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import app.se329.project2.MainActivity;
import app.se329.project2.R;

public class Navigation {

    public static String NAVIGATION_ITEMS = "Navigation items";

    public static String[] getItems(Context context) {
        return getDefaultItems(context);
    }

    private static String[] getDefaultItems (Context context){
        return context.getResources().getStringArray(R.array.menu_items);
    }

}
