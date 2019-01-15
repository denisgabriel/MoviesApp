package com.denis.moviesapp.localAuth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denis.moviesapp.networking.Api;
import com.denis.moviesapp.utils.Encryption;
import com.denis.moviesapp.utils.FileUtils;
import com.denis.moviesapp.utils.NetworkingUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocalUser {
    private String filenameLogStatus;

    public LocalUser() {
        this.filenameLogStatus = "logStatus";
    }

    public String getToken(Context context){
        FileUtils file = new FileUtils();
        String token = null;

        try {
            token = file.read(context, filenameLogStatus);
        } catch (Exception ignored) {}

        return token;
    }

    public void login(final Context context, final String username, final String password, final Intent mainIntent) {
        // Let's ask DB for a token
        // We need a queue of requests to put our request in
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Api.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            FileUtils file = new FileUtils();
                            JsonNode jsonResponse = (new ObjectMapper()).readTree(response);

                            if(!jsonResponse.get("error").booleanValue()){
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();

                                file.write(context, filenameLogStatus, jsonResponse.get("token").asText());

                                context.startActivity(mainIntent);
                                ((Activity)context).finish();
                            } else {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkingUtils networkingUtils = new NetworkingUtils();

                        if(networkingUtils.isOnline()){
                            Toast.makeText(context, "Server is down. Try again later!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "You must be online to login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String hash = Encryption.encryptThisString(username + password);

                params.put("hash", hash);

                return params;
            }
        };

        // Add to the queue the request that will (hopefully) work
        queue.add(request);
    }

    public void logout(final Context context, final Intent loginIntent) {
        // Let's tell the DB too
        // We need a queue of requests to put our request in
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Api.URL_LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JsonNode jsonResponse = (new ObjectMapper()).readTree(response);

                            if(!jsonResponse.get("error").booleanValue()){
                                FileUtils file = new FileUtils();

                                // update it local
                                file.write(context, filenameLogStatus, "");

                                // close this screen
                                ((Activity)context).finish();

                                // open login screen
                                context.startActivity(loginIntent);

                                Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Some error occurred. Try again later!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkingUtils networkingUtils = new NetworkingUtils();

                        if(networkingUtils.isOnline()){
                            Toast.makeText(context, "Server is down. Try again later!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "You must be online to logout!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("token", getToken(context));

                return params;
            }
        };

        // Add to the queue the request that will (hopefully) work
        queue.add(request);
    }

    // Logged in - True
    // Logged out - False
    public Boolean checkIfLoggedIn(Context context) {
        FileUtils file = new FileUtils();
        String data;

        try {
            data = file.read(context, filenameLogStatus);
        } catch (Exception e) {
            return false;
        }

        return !data.equals("");
    }
}
