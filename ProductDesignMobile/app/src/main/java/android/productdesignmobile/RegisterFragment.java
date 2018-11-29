package android.productdesignmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_activity, container, false);

        EditText first_name = view.findViewById(R.id.signUpEditTextFirstName);
        EditText last_name = view.findViewById(R.id.signUpEditTextLastName);
        EditText email = view.findViewById(R.id.signUpEditTextEmail);
        EditText password = view.findViewById(R.id.signUpEditTextPassword);

        final Button buttonRegister = view.findViewById(R.id.signUpButtonRegister);
        buttonRegister.setOnClickListener(v -> {
            String fn = first_name.getText().toString();
            String ln = last_name.getText().toString();
            String em = email.getText().toString();
            String pw = password.getText().toString();
            RegisterUser ru = new RegisterUser(getContext(),fn,ln,em,pw);
            ru.execute();
        });
        return view;
    }

    public class RegisterUser extends AsyncTask<String,Void,String>{

        String urlAddress = "https://facedatabasetest.azurewebsites.net/api/CreateUser";
        Context context;

        String first_name,last_name,email,password;

        public RegisterUser(Context context, String firstname, String lastname, String email, String password){
            this.context = context;
            this.first_name = firstname;
            this.last_name = lastname;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... voids) {
            try {
                try {
                return HttpPost(urlAddress);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("RegisterUser", "onPostExecute " + result);
        }

        private String HttpPost(String urlAddress) throws IOException, JSONException {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            JSONObject jsonObject = buildJsonObject();
            setPostRequestContent(conn, jsonObject);
            conn.connect();

            /*int responseCode=conn.getResponseCode();
            if(responseCode==conn.HTTP_OK){
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer response=new StringBuffer();
                String line;
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                //return response.toString();
            }*/

            return conn.getResponseMessage()+"";
        }

        private JSONObject buildJsonObject() throws JSONException {
            JSONObject jo = new JSONObject();
            jo.accumulate("firstname", first_name);
            jo.accumulate("lastname", last_name);
            jo.accumulate("email", email);
            jo.accumulate("password", password);
            Log.d("RegisterUser","JSONObject: " + jo.toString());
            return jo;
        }

        private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            Log.i(MainActivity.class.toString(), jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
        }
    }
}
