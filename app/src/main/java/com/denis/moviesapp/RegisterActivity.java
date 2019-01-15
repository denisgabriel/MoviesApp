package com.denis.moviesapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denis.moviesapp.networking.Api;
import com.denis.moviesapp.utils.Encryption;
import com.denis.moviesapp.utils.NetworkingUtils;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set the callback for when the Register button is pressed
        Button btnRegister = findViewById(R.id.btnRegisterFromRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextUsername = findViewById(R.id.txtUsernameFromRegister);
                EditText editTextPassword = findViewById(R.id.txtPasswordFromRegister);
                EditText editTextPasswordCheck = findViewById(R.id.txtPasswordCheckFromRegister);
                final String username = editTextUsername.getText().toString();
                final String password = editTextPassword.getText().toString();
                String passwordCheck = editTextPasswordCheck.getText().toString();
                Boolean fieldsOk = true;

                // If the fields are empty then we won't do shit
                if (username.trim().equals("")) {
                    editTextUsername.setError("Username is required!");
                    fieldsOk = false;
                }

                if (password.trim().equals("")) {
                    editTextPassword.setError("Password is required!");
                    fieldsOk = false;
                }

                if (passwordCheck.trim().equals("")) {
                    editTextPasswordCheck.setError("Password is required!");
                    fieldsOk = false;
                }

                // If passwords don't match we won't do shit
                if (!passwordCheck.equals(password)) {
                    editTextPasswordCheck.setError("Password doesn't match!");
                    editTextPassword.setError("Password doesn't match!");
                    fieldsOk = false;
                }

                if (!fieldsOk) {
                    return;
                }

                final Context context = getApplicationContext();

                // We need a queue of requests to put our request in
                RequestQueue queue = Volley.newRequestQueue(context);

                // Basically, this is the request
                StringRequest request = new StringRequest(Request.Method.POST, Api.URL_ADD_USER,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(context, "Registration success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkingUtils networkingUtils = new NetworkingUtils();

                                if (networkingUtils.isOnline()) {
                                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "You must be online in order to register!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();

                        params.put("hash", Encryption.encryptThisString(username + password));

                        return params;
                    }
                };

                // Add to the queue the request that will (hopefully) update our local list of users
                queue.add(request);
            }
        });

        // Set the callback for when the Register button is pressed
        Button btnLogin = findViewById(R.id.btnLoginFromRegister);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
