package com.denis.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.denis.moviesapp.localAuth.LocalUsers;
import com.denis.moviesapp.movies.MoviesAdapter;
import com.denis.moviesapp.movies.SyncMovies;
import com.denis.moviesapp.utils.SwipeToDeleteCallback;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find our view
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBooks);

        // We need a layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        // We need an adapter
        MoviesAdapter moviesAdapter = new MoviesAdapter(this);
        recyclerView.setAdapter(moviesAdapter);

        // We need an animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // We need this for the swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(moviesAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Let's try to init the list with something from disk
        moviesAdapter.initMovies();

        // Let's sync them with the database
        SyncMovies syncMovies = new SyncMovies(this, moviesAdapter);
        syncMovies.begin();

        // Set the callback for our logout button
        FloatingActionButton btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalUsers localUsers = new LocalUsers();
                final Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);

                localUsers.logout(getApplicationContext());
                finish();
                startActivity(loginIntent);
            }
        });
    }
}
