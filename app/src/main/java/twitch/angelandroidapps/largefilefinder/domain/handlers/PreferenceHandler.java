package twitch.angelandroidapps.largefilefinder.domain.handlers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * PROJECT: Flutter4d
 * angelandroidapps.twitch.flutter4d.domain.handler
 * Created by Angel on 12/6/2015.
 */
public class PreferenceHandler {

    //DROPBOX RELATED
    //================
    public static String getAccessToken(Context context) {
        return getPref(context).getString("pref_access_token", null);
    }

    public static void setAccessToken(Context context, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString("pref_access_token", value);
        editor.commit();
    }

    public static String getUserId(Context context) {
        return getPref(context).getString("pref_user_id", null);
    }

    public static void setUserId(Context context, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString("pref_user_id", value);
        editor.commit();
    }


    //BASE METHODS
    //============
    private static SharedPreferences getPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext()).edit();
    }

}
