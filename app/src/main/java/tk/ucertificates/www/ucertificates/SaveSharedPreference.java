package tk.ucertificates.www.ucertificates;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by DELL STORE on 5/17/2017.
 */
//Shared Preferences allow you to save and retrieve data in the form of key,value pair.
//Use Shared Preference , When user logged into your application store login status into
// sharedPreference and clear sharedPreference when user click on logged Out.
//Check every time when user enter into application
// if user status from shared Preference is true then no need to login otherwise move to login logic.

public class SaveSharedPreference {
    static final String PREF_USER_NAME= "username";

    static SharedPreferences getSharedPreferences(Context ctx) {
        // getDefltSharedPreferences method ,Gets a SharedPreferences instance that points to the default file
        // that is used by the preference framework in the given context.
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

// In Login activity if user login successful then set UserName using setUserName() function.
    public static void setUserName(Context ctx, String userName)
    {
        //You can save something in the sharedpreferences by using SharedPreferences.Editor class.
        // You will call the edit method of SharedPreference instance and will receive it in an editor object.
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    //get the current user name
    public static String getUserName(Context ctx)
    {
        //In order to use shared preferences, you have to call a method getSharedPreferences()
        // that returns a SharedPreference instance pointing to the file
        // that contains the values of preferences.

        //initially username key has value of length zero
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    //clear all stored data if user log out
    public static void clearUserName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

}


