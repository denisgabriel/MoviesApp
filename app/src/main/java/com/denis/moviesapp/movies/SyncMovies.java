package com.denis.moviesapp.movies;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denis.moviesapp.localAuth.LocalUser;
import com.denis.moviesapp.networking.Api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SyncMovies {
    private MoviesAdapter moviesAdapter;

    private Context context;
    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            // We need a queue of requests to put our request in
            RequestQueue queue = Volley.newRequestQueue(context);

            // Basically, this is the request
            StringRequest request = new StringRequest(Request.Method.POST, Api.URL_GET_MOVIES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JsonNode jsonResponse = (new ObjectMapper()).readTree(response);
                                if (jsonResponse.get("error").booleanValue()){
                                    Toast.makeText(context, "Error occured while syncing", Toast.LENGTH_SHORT).show();
                                }else{
                                    moviesAdapter.updateMovies(jsonResponse.get("movies").toString());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, null) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    LocalUser localUser = new LocalUser();

                    params.put("token", localUser.getToken(context));

                    return params;
                }
            };

            queue.add(request);
        }
    };
    private Timer timer = new Timer();

    public SyncMovies(Context context, MoviesAdapter moviesAdapter) {
        this.context = context;
        this.moviesAdapter = moviesAdapter;
    }

    public void begin() {
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void end(){
        timer.cancel();
        timer.purge();
    }
}
