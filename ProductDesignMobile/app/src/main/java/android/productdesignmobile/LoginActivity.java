package android.productdesignmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        username = findViewById(R.id.loginEditTextUsername);
        password = findViewById(R.id.loginEditTextPassword);

        Button login_button = findViewById(R.id.loginButtonLogin);
        login_button.setOnClickListener(v -> {
            final String login_username = username.getText().toString();
            final String login_password = password.getText().toString();
            new AsyncLogin().execute(login_username,login_password);
        });

        Button signup_button = findViewById(R.id.loginButtonSignUp);
        signup_button.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
        });
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://productdesign.westeurope.cloudapp.azure.com/android_api/login.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(Objects.requireNonNull(query));
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }
            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("Login query", "result " + result.toString());
                    return(result.toString());
                }
                else{
                    return("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("true"))
            {
                //save user_id so proper data can be fetched later

                SharedPreferences prefs = LoginActivity.this.getSharedPreferences(
                        "product_design_shared", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("user_id_key", 2);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else if (result.equalsIgnoreCase("false")){
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(LoginActivity.this, "Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
