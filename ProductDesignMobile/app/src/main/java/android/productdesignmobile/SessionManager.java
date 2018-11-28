package android.productdesignmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "FaceRekPref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";

    public static final String KEY_DIETARY_GLUTEN = "gluten";
    public static final String KEY_DIETARY_LACTOSEFREE = "lactosefree";
    public static final String KEY_DIETARY_LOWLACTOSE = "lowlactose";
    public static final String KEY_DIETARY_MILKLESS = "milkless";
    public static final String KEY_DIETARY_VEGAN = "vegan";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession() {
        //TODO get userinfo from database
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FIRSTNAME, "Henry");
        editor.putString(KEY_LASTNAME, "Väisänen");
        editor.putString(KEY_EMAIL, "t5vahe01@students.oamk.fi");
        editor.putBoolean(KEY_DIETARY_GLUTEN, true);
        editor.putBoolean(KEY_DIETARY_LACTOSEFREE, true);
        editor.putBoolean(KEY_DIETARY_LOWLACTOSE, true);
        editor.putBoolean(KEY_DIETARY_MILKLESS, true);
        editor.putBoolean(KEY_DIETARY_VEGAN, true);
        editor.commit();
    }

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_FIRSTNAME, pref.getString(KEY_FIRSTNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    public HashMap<String, Boolean> getDietaryDetails() {
        HashMap<String, Boolean> dietary = new HashMap<String, Boolean>();
        dietary.put(KEY_DIETARY_GLUTEN, pref.getBoolean(KEY_DIETARY_GLUTEN, false));
        dietary.put(KEY_DIETARY_LACTOSEFREE, pref.getBoolean(KEY_DIETARY_LACTOSEFREE, false));
        dietary.put(KEY_DIETARY_LOWLACTOSE, pref.getBoolean(KEY_DIETARY_LOWLACTOSE, false));
        dietary.put(KEY_DIETARY_MILKLESS, pref.getBoolean(KEY_DIETARY_MILKLESS, false));
        dietary.put(KEY_DIETARY_VEGAN, pref.getBoolean(KEY_DIETARY_VEGAN, false));
        return dietary;
    }

    public void setDietaryDetails(HashMap<String, Boolean> dietary) {
        editor.putBoolean(KEY_DIETARY_GLUTEN, dietary.get("gluten"));
        editor.putBoolean(KEY_DIETARY_LACTOSEFREE, dietary.get("lactosefree"));
        editor.putBoolean(KEY_DIETARY_LOWLACTOSE, dietary.get("lowlactose"));
        editor.putBoolean(KEY_DIETARY_MILKLESS, dietary.get("milkless"));
        editor.putBoolean(KEY_DIETARY_VEGAN, dietary.get("vegan"));
        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        //Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        //_context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
