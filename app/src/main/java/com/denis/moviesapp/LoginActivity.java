package com.denis.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.denis.moviesapp.localAuth.LocalUser;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final LocalUser localUser = new LocalUser();
        final Context context = getApplicationContext();
        final Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);

        // Check if we are already logged in
        if (localUser.checkIfLoggedIn(context)) {
            startActivity(mainIntent);
            finish();
        }

        // Set the callback for when the Login button is pressed
        Button btnLogin = findViewById(R.id.btnLoginFromLogin);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean fieldsOk = true;

                EditText editTextUsername = findViewById(R.id.txtUsernameFromLogin);
                EditText editTextPassword = findViewById(R.id.txtPasswordFromLogin);
                final String username = editTextUsername.getText().toString();
                final String password = editTextPassword.getText().toString();

                // If the fields are empty then we won't do shit
                if (username.trim().equals("")) {
                    editTextUsername.setError("Username is required!");
                    fieldsOk = false;
                }

                if (password.trim().equals("")) {
                    editTextPassword.setError("Password is required!");
                    fieldsOk = false;
                }

                if (!fieldsOk) {
                    return;
                }

                localUser.login(context, username, password);
            }
        });

        // Set the callback for when the Register button is pressed
        Button btnRegister = findViewById(R.id.btnRegisterFromLogin);
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(registerIntent);
            }
        });
    }
}

