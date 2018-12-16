package android.productdesignmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.productdesignmobile.LoginActivity.session;

public class UpdateUserData extends AsyncTask<Void,Void,String> {

    Context context;
    String urlAddress = "https://facedatabasetest.azurewebsites.net/api/UpdateUser";
    JSONObject userdata;

    public UpdateUserData(Context context) throws JSONException {
        this.context = context;
        this.userdata = session.updateUserData();
        Log.d("UpdateUserData", "JSON: " + this.userdata.toString());
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            try {
                Log.d("UpdateUserData", "JSONdata: " + userdata.toString());
                return HttpPost(urlAddress);
            } catch (JSONException e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String HttpPost(String urlAddress) throws IOException, JSONException {
        URL url = new URL(urlAddress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        setPostRequestContent(conn, userdata);
        conn.connect();
        int response_code = conn.getResponseCode();
        Log.d("UpdateUserData","response_code: "+ response_code);
        if (response_code == HttpURLConnection.HTTP_OK) {
            InputStream input = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("UpdateUser", "Response result: " + result.toString());
            return(result.toString());
        }
        else{
            return("unsuccessful");
        }
        //return conn.getResponseMessage()+"";
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
        Log.d("setPOST: ", "" + os.toString());
    }


    @Override
    protected void onPostExecute(String response) {

    }
}
