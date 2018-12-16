package android.productdesignmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "FaceRekPref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FIRST_NAME = "firstname";
    public static final String KEY_LAST_NAME = "lastname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_GROUP_ID = "groupid";
    public static final String KEY_RESTAURANT_ID = "restaurant_id";

    public static final String KEY_DIETARY_GLUTEN = "gluten";
    public static final String KEY_DIETARY_LACTOSEFREE = "lactosefree";
    public static final String KEY_DIETARY_LOWLACTOSE = "lowlactose";
    public static final String KEY_DIETARY_MILKLESS = "milkless";
    public static final String KEY_FEEL_WELL = "feel_well";
    public static final String KEY_DIETARY_VEGAN = "vegan";
    public static final String KEY_DIETARY_GARLIC = "garlic";
    public static final String KEY_DIETARY_ALLERGEN = "allergen";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(JSONObject user_json, JSONObject dietary_json) throws JSONException {
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, user_json.getInt("id"));
        editor.putString(KEY_FIRST_NAME, user_json.getString("firstname"));
        editor.putString(KEY_LAST_NAME, user_json.getString("lastname"));
        editor.putString(KEY_GROUP_ID, user_json.getString("groupid"));
        editor.putString(KEY_GENDER, user_json.getString("gender"));
        editor.putString(KEY_EMAIL, user_json.getString("email"));
        editor.putString(KEY_IMAGE_URL, user_json.getString("imageurl"));
        //editor.putInt(KEY_RESTAURANT_ID, user_json.getInt("restaurantid"));
        editor.putBoolean(KEY_DIETARY_GLUTEN, dietary_json.getBoolean("G"));
        editor.putBoolean(KEY_DIETARY_LACTOSEFREE, dietary_json.getBoolean("L"));
        editor.putBoolean(KEY_DIETARY_LOWLACTOSE, dietary_json.getBoolean("VL"));
        editor.putBoolean(KEY_DIETARY_MILKLESS, dietary_json.getBoolean("M"));
        editor.putBoolean(KEY_FEEL_WELL, dietary_json.getBoolean("VH"));
        editor.putBoolean(KEY_DIETARY_VEGAN, dietary_json.getBoolean("VEG"));
        editor.putBoolean(KEY_DIETARY_GARLIC, dietary_json.getBoolean("VS"));
        editor.putBoolean(KEY_DIETARY_ALLERGEN, dietary_json.getBoolean("A"));
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

    public int getUserID(){
        return pref.getInt(KEY_USER_ID, 0);
    }

    public JSONObject updateUserData() throws JSONException{
        JSONObject json = new JSONObject();
        json.accumulate("user_id", pref.getInt(KEY_USER_ID, 0));
        json.accumulate("first_name", pref.getString(KEY_FIRST_NAME, null));
        json.accumulate("last_name", pref.getString(KEY_LAST_NAME, null));
        json.accumulate("groupid", pref.getString(KEY_GROUP_ID, null));
        json.accumulate("gender", pref.getString(KEY_GENDER, null));
        json.accumulate("restaurant_id", pref.getString(KEY_RESTAURANT_ID, null));

        json.accumulate("g", parseBoolean(pref.getBoolean(KEY_DIETARY_GLUTEN, false)));
        json.accumulate("l", parseBoolean(pref.getBoolean(KEY_DIETARY_LACTOSEFREE, false)));
        json.accumulate("vl", parseBoolean(pref.getBoolean(KEY_DIETARY_LOWLACTOSE, false)));
        json.accumulate("m", parseBoolean(pref.getBoolean(KEY_DIETARY_MILKLESS, false)));
        json.accumulate("vh", parseBoolean(pref.getBoolean(KEY_FEEL_WELL, false)));
        json.accumulate("veg", parseBoolean(pref.getBoolean(KEY_DIETARY_VEGAN, false)));
        json.accumulate("vs", parseBoolean(pref.getBoolean(KEY_DIETARY_GARLIC, false)));
        json.accumulate("a", parseBoolean(pref.getBoolean(KEY_DIETARY_ALLERGEN, false)));
        return json;
    }

    private int parseBoolean(Boolean x){
        int y;
        if (x){
            y = 1;
        } else {
            y = 0;
        }
        return y;
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));
        user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
        user.put(KEY_GROUP_ID, pref.getString(KEY_GROUP_ID, null));
        return user;
    }

    public void setUserDetails(JSONObject jsonObject) throws JSONException{
        Log.d("SessionManager", "setUserDetails JsonObject" + jsonObject.toString());
        editor.putString(KEY_FIRST_NAME, jsonObject.getString("first_name"));
        editor.putString(KEY_LAST_NAME, jsonObject.getString("last_name"));
        editor.putString(KEY_GROUP_ID, jsonObject.getString("groupid"));
        editor.putString(KEY_RESTAURANT_ID, jsonObject.getString("restaurant_id"));
        if (jsonObject.getInt("gender") == 0) {
            editor.putString(KEY_GENDER, "F");
        } else if (jsonObject.getInt("gender") == 1) {
            editor.putString(KEY_GENDER, "M");
        }
        editor.commit();
        Log.d("SessionManager", "setUserDetails after: " + pref.getString(KEY_GROUP_ID, "testi"));
    }

    public HashMap<String, Boolean> getDietaryDetails() {
        HashMap<String, Boolean> dietary = new HashMap<String, Boolean>();
        dietary.put(KEY_DIETARY_GLUTEN, pref.getBoolean(KEY_DIETARY_GLUTEN, false));
        dietary.put(KEY_DIETARY_LACTOSEFREE, pref.getBoolean(KEY_DIETARY_LACTOSEFREE, false));
        dietary.put(KEY_DIETARY_LOWLACTOSE, pref.getBoolean(KEY_DIETARY_LOWLACTOSE, false));
        dietary.put(KEY_DIETARY_MILKLESS, pref.getBoolean(KEY_DIETARY_MILKLESS, false));
        dietary.put(KEY_FEEL_WELL, pref.getBoolean(KEY_FEEL_WELL, false));
        dietary.put(KEY_DIETARY_VEGAN, pref.getBoolean(KEY_DIETARY_VEGAN, false));
        dietary.put(KEY_DIETARY_GARLIC, pref.getBoolean(KEY_DIETARY_GARLIC, false));
        dietary.put(KEY_DIETARY_ALLERGEN, pref.getBoolean(KEY_DIETARY_ALLERGEN, false));
        return dietary;
    }

    public void setDietaryDetails(HashMap<String, Boolean> dietary) {
        editor.putBoolean(KEY_DIETARY_GLUTEN, dietary.get("gluten"));
        editor.putBoolean(KEY_DIETARY_LACTOSEFREE, dietary.get("lactosefree"));
        editor.putBoolean(KEY_DIETARY_LOWLACTOSE, dietary.get("lowlactose"));
        editor.putBoolean(KEY_DIETARY_MILKLESS, dietary.get("milkless"));
        editor.putBoolean(KEY_FEEL_WELL, dietary.get("feel_well"));
        editor.putBoolean(KEY_DIETARY_VEGAN, dietary.get("vegan"));
        editor.putBoolean(KEY_DIETARY_GARLIC, dietary.get("garlic"));
        editor.putBoolean(KEY_DIETARY_ALLERGEN, dietary.get("allergen"));
        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
