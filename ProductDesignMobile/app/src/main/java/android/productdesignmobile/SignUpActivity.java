package android.productdesignmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        firstName = findViewById(R.id.signUpEditTextFirstName);
        firstName = findViewById(R.id.signUpEditTextLastName);
        password = findViewById(R.id.loginEditTextPassword);
    }
}
