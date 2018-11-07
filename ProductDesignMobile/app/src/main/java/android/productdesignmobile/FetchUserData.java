package android.productdesignmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class FetchUserData extends AsyncTask<Void, Void, String> {
    Context c;
    private String urlAddress;
    private String user_id;
    private String first_name;
    private String last_name;
    private String first_name_json;
    private String last_name_json;
    private String email_json;
    private int gender_json;

    private JSONObject result_json = null;
    private JSONObject user_info_json = null;

    private UserDataInterface testInterface;

    public void setTestInterface(UserDataInterface testInterface) {
        this.testInterface = testInterface;
    }

    public FetchUserData(Context c, String urlAddress, String user_id) {
        this.c = c;
        this.urlAddress = urlAddress;
        this.user_id = user_id;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d("Fetch user data","Async task started");
        return this.fetch();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        //send userdata through interface
        testInterface.setUserData(first_name_json, last_name_json, email_json, gender_json);
    }

    private String fetch(){
        HttpURLConnection con=Connector.connect(urlAddress);
        if(con==null)
        {
            return null;
        }
        try
        {
            int responseCode=con.getResponseCode();
            if(responseCode==con.HTTP_OK)
            {
                Log.d("Fetch user data", "HTTP_OK");
                InputStream input = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.d("JSONobject",result.toString());
                result_json = new JSONObject(result.toString());
                Log.d("jiison", result_json.toString());
                String temp = result_json.getString("user_info");

                Log.d("jiison", temp);
                user_info_json = new JSONObject(temp);
                first_name_json = user_info_json.getString("first_name");
                Log.d("Parse JSONobject", "first_name " + first_name_json);
                last_name_json = user_info_json.getString("last_name");
                Log.d("Parse JSONobject", "last_name " + last_name_json);
                email_json = user_info_json.getString("email");
                Log.d("Parse JSONobject", "email " + email_json);
                gender_json = user_info_json.getInt("gender");
                Log.d("Parse JSONobject", "gender " + gender_json);




                return result.toString();
            }else
                Log.d("Fetch user data", "HTTP_ERROR");
            {
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
