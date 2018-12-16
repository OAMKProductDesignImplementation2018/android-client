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
import android.widget.Toast;

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

public class RegisterFragment extends DialogFragment {

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
            if (!isEmailValid(em)){
                Toast.makeText(getContext(), "Not valid email", Toast.LENGTH_SHORT).show();
            } else{
                RegisterUser ru = new RegisterUser(getContext(),fn,ln,em,pw);
                ru.execute();
            }
        });
        return view;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class RegisterUser extends AsyncTask<String,Void,String>{
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
            Log.d("RegisterUser","Async started");
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
            dismiss();
            Toast.makeText(context,"User " + this.email + " created succesfully!",Toast.LENGTH_LONG).show();
        }

        private String HttpPost(String urlAddress) throws IOException, JSONException {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            JSONObject jsonObject = buildJsonObject();
            setPostRequestContent(conn, jsonObject);
            conn.connect();
            int response_code = conn.getResponseCode();
            Log.d("RegisterUser","response_code: "+ response_code);
            if (response_code == HttpURLConnection.HTTP_OK) {
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.d("'RegisterUser", "Response result: " + result.toString());
                return(result.toString());
            }
            else{
                return("unsuccessful");
            }
            //return conn.getResponseMessage()+"";
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
