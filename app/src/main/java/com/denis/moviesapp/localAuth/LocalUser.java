package com.denis.moviesapp.localAuth;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denis.moviesapp.networking.Api;
import com.denis.moviesapp.utils.FileUtils;
import com.denis.moviesapp.utils.NetworkingUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

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

    public void login(final Context context, final String username, final String password) {
        // Let's ask DB for a token
        // We need a queue of requests to put our request in
        RequestQueue queue = Volley.newRequestQueue(context);

        // Basically, this is the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Api.URL_LOGIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        FileUtils file = new FileUtils();

                        try {
                            file.write(context, filenameLogStatus, response.getString("token"));
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkingUtils networkingUtils = new NetworkingUtils();

                if(networkingUtils.isOnline()){
                    Toast.makeText(context, "Server is down. Try again later!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You MUST be online to login!", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("hash", BCrypt.hashpw(username + password, BCrypt.gensalt()));

                return params;
            }
        };

        // Add to the queue the request that will (hopefully) work
        queue.add(request);
    }

    public void logout(final Context context) {
        FileUtils file = new FileUtils();

        // Firstly, let's take care of it local
        try {
            file.write(context, filenameLogStatus, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Now let's tell the DB too
        // We need a queue of requests to put our request in
        RequestQueue queue = Volley.newRequestQueue(context);

        // Basically, this is the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Api.URL_LOGOUT, null, null, null) {
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
