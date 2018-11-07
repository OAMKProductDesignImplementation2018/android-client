package android.productdesignmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

import static android.productdesignmobile.Connector.connect;

public class UpdateUserData extends AsyncTask<Void,Void,String> {

    private String urlAddress;
    EditText firstname, lastname, email;
    String FirstName,LastName,Email;
    Context c;
    int Gender;

    public UpdateUserData(Context c, String urlAddress, int gender, EditText... editTexts){
        this.c = c;
        this.urlAddress = urlAddress;

        this.firstname = editTexts[0];
        this.lastname = editTexts[1];
        this.email = editTexts[2];

        Gender = gender;
        FirstName = firstname.getText().toString();
        LastName = lastname.getText().toString();
        Email = email.getText().toString();
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d("Update user data", "Async task started");
        return this.send();
    }

    private String send(){
        HttpURLConnection con = connect(urlAddress);
        if (con == null){
            return null;
        }
        try{
            OutputStream os = con.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            bw.write(new DataPackager(FirstName,LastName,Email,Gender).packData());
            bw.flush();
            bw.close();
            os.close();;
            int responseCode=con.getResponseCode();
            if(responseCode==con.HTTP_OK){
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response=new StringBuffer();
                String line;
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                return response.toString();
            } else{
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(response != null)
        {
            Log.d("Update data onPostExeccute", "response successful");
            Toast.makeText(c,response,Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("Update data onPostExeccute", "response unsuccessful");
            Toast.makeText(c,"Unsuccessful "+response,Toast.LENGTH_LONG).show();
        }
    }

    public class DataPackager {
        String first_name,last_name,email;
        int gender;
        String user_id = "2";

        public DataPackager(String first_name, String last_name, String email, int gender) {
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.gender = gender;
        }

        public String packData()
        {
            JSONObject jo=new JSONObject();
            StringBuffer packedData=new StringBuffer();
            try
            {
                jo.put("user_id",user_id);
                jo.put("first_name",first_name);
                jo.put("last_name",last_name);
                jo.put("email",email);
                jo.put("gender",gender);
                Boolean firstValue=true;
                Iterator it=jo.keys();
                do {
                    String key=it.next().toString();
                    String value=jo.get(key).toString();
                    if(firstValue) {
                        firstValue=false;
                    }
                    else {
                        packedData.append("&");
                    }
                    packedData.append(URLEncoder.encode(key,"UTF-8"));
                    packedData.append("=");
                    packedData.append(URLEncoder.encode(value,"UTF-8"));
                }
                while (it.hasNext());

                return packedData.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
