package android.productdesignmobile;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    public static SessionManager session;

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        EditText username = findViewById(R.id.loginEditTextUsername);
        EditText password = findViewById(R.id.loginEditTextPassword);
        status = findViewById(R.id.loginTextViewStatus);

        Button login_button = findViewById(R.id.loginButtonLogin);
        login_button.setOnClickListener(v -> {
            final String login_username = username.getText().toString();
            final String login_password = password.getText().toString();
            LoginUser lu = new LoginUser(this,login_username,login_password);
            lu.execute();
        });

        TextView signup_button = findViewById(R.id.loginTextViewSignUp);
        signup_button.setOnClickListener(v -> {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            DialogFragment dialogFragment = new RegisterFragment();
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        });
    }

    private class LoginUser extends AsyncTask<String,String,String>{

        String username,password;
        String urlAddress = "https://facedatabasetest.azurewebsites.net/api/UserSignIn";
        Context context;

        public LoginUser(Context context, String username, String password){
            this.context = context;
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {
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
                try {
                    JSONObject json = new JSONObject(result);
                    String login = json.getString("login");
                    if ((!login.isEmpty()) && login.equals("Success")){
                        status.setText(R.string.loginStatusRetrievingUserData);
                        String temp = json.getString("data");
                        JSONObject result_json = new JSONObject(temp);
                        updateSession(result_json);
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                } catch (JSONException e) {
                    status.setText(R.string.loginStatusWrongCredentials);
                    status.setTextColor(Color.parseColor("#FF0000"));
                    e.printStackTrace();
                }
        }

        private void updateSession(JSONObject jsonObject) throws JSONException {
            session = new SessionManager(getApplicationContext());
            session.createLoginSession(jsonObject);
        }

        private String HttpPost(String urlAddress) throws IOException, JSONException {
            status.setTextColor(Color.parseColor("#3d5459"));
            status.setText(R.string.loginStatusConnecting);
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
                Log.d("LoginUser", "Reponse result: " + result.toString());
                status.setText(R.string.loginStatusConnectionSuccessful);
                return(result.toString());
            }
            else{
                status.setText(R.string.loginStatusConnectionFailed);
                return("unsuccessful");
            }
            //return conn.getResponseMessage()+"";
        }

        private JSONObject buildJsonObject() throws JSONException {
            JSONObject jo = new JSONObject();
            jo.accumulate("email", username);
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
