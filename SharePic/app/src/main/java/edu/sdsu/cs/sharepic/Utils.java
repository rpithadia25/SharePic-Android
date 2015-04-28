package edu.sdsu.cs.sharepic;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Horsie on 4/28/15.
 */
public class Utils {

    public static final String PREFERENCE_NAME = "SharePic";

    public static void storeInSharedPreferences(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(key,value);
        prefEditor.apply();
    }

    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences shared = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return (shared.getString(key, ""));
    }
}
