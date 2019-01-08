package com.denis.moviesapp.movies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denis.moviesapp.R;
import com.denis.moviesapp.localAuth.LocalUser;
import com.denis.moviesapp.networking.Api;
import com.denis.moviesapp.utils.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    private List<Movie> movies = new ArrayList<>();
    private Context context;

    private int recentlyDeletedMoviePosition;
    private Movie recentlyDeletedMovie;

    public MoviesAdapter(Context context) {
        this.context = context;
    }

    public void initMovies() {
        FileUtils fileUtils = new FileUtils();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            movies = objectMapper.readValue(fileUtils.read(context, "movies"), objectMapper.getTypeFactory().constructCollectionType(List.class, Movie.class));
        } catch (Exception ignored) {}
    }

    void updateMovies(String newMovies) {
        FileUtils fileUtils = new FileUtils();
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize first
        try {
            movies = objectMapper.readValue(newMovies, objectMapper.getTypeFactory().constructCollectionType(List.class, Movie.class));
        } catch (IOException ignored) {}


        // Sort them by title second
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie m1, Movie m2) {
                return m1.getTitle().compareToIgnoreCase(m2.getTitle());
            }
        });

        // Update in-memory third
        notifyDataSetChanged();

        // Update on-disk fourth
        try {
            fileUtils.write(context, "movies", newMovies);
        } catch (IOException ignored) {}
    }

    public void deleteMovie(int position) {
        // For undo
        recentlyDeletedMovie = movies.get(position);
        recentlyDeletedMoviePosition = position;
        showUndoSnackBar();

        // Remove from list
        movies.remove(position);
        notifyItemRemoved(position);

        /* Send request to remove from database */
        //We need a queue of requests to put our request in
        RequestQueue queue = Volley.newRequestQueue(context);

        // Basically, this is the request
        StringRequest request = new StringRequest(Request.Method.POST, Api.URL_REMOVE_MOVIE, null, null) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                LocalUser localUser = new LocalUser();

                params.put("token", localUser.getToken(context));
                params.put("title", recentlyDeletedMovie.getTitle());
                params.put("genre", recentlyDeletedMovie.getGenre());

                return params;
            }
        };

        queue.add(request);
    }

    private void showUndoSnackBar() {
        View viewRelativeLayoutMainActivity = ((Activity) context).findViewById(R.id.relativeLayoutMainActivity);
        Snackbar snackbar = Snackbar.make(viewRelativeLayoutMainActivity, R.string.snackbar_text, Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snackbar_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Re-add to list
                movies.add(recentlyDeletedMoviePosition, recentlyDeletedMovie);
                notifyItemInserted(recentlyDeletedMoviePosition);

                /* Send re-add to remove from database */
                //We need a queue of requests to put our request in
                RequestQueue queue = Volley.newRequestQueue(context);

                // Basically, this is the request
                StringRequest request = new StringRequest(Request.Method.POST, Api.URL_ADD_MOVIE, null, null) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        LocalUser localUser = new LocalUser();

                        params.put("token", localUser.getToken(context));
                        params.put("title", recentlyDeletedMovie.getTitle());
                        params.put("genre", recentlyDeletedMovie.getGenre());

                        return params;
                    }
                };

                queue.add(request);
            }
        });
        snackbar.show();
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MoviesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.title.setText(movies.get(position).getTitle());
        holder.genre.setText(movies.get(position).getGenre());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView genre;

        MoviesViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textViewMovieTitle);
            genre = view.findViewById(R.id.textViewMovieGenre);
        }
    }

}
