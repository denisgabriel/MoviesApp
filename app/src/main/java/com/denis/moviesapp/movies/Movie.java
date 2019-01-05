package com.denis.moviesapp.movies;

class Movie {
    private String title = null;
    private String genre = null;

    String getTitle() {
        return title;
    }

    String getGenre() {
        return genre;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}