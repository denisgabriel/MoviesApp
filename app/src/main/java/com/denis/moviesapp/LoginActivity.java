package com.denis.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denis.moviesapp.localAuth.LocalUsers;
import com.denis.moviesapp.networking.Api;
import com.denis.moviesapp.utils.NetworkingUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final LocalUsers localUsers = new LocalUsers();
        final Context context = getApplicationContext();
        final Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);

        // Check if we are already logged in
        if (localUsers.checkIfLoggedIn(context)) {
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

                // We need a queue of requests to put our request in
                RequestQueue queue = Volley.newRequestQueue(context);

                // Basically, this is the request
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.URL_GET_USERS, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Update the local list of users with the latest and greatest from server
                                try {
                                    localUsers.updateLocalUsers(context, response.getString("users"));
                                } catch (IOException | JSONException e) {
                                    Toast.makeText(context, "Failed to update local users", Toast.LENGTH_SHORT).show();
                                }

                                // Check local list of users
                                try {
                                    if (localUsers.checkUser(context, username, password)) {
                                        Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show();

                                        localUsers.login(context);

                                        startActivity(mainIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                // Check local list of users
                                try {
                                    if (localUsers.checkUser(context, username, password)) {
                                        Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show();

                                        localUsers.login(context);

                                        startActivity(mainIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    if ((new NetworkingUtils()).isOnline()) {
                                        Toast.makeText(context, "Server is down. Try again later!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "First login must be made online!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });

                // Add to the queue the request that will (hopefully) update our local list of users & try to login
                queue.add(request);
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

