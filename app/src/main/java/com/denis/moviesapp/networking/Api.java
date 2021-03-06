package com.denis.moviesapp.networking;

public class Api {

    private static final String ROOT_URL = "http://movies.go.ro/moviesapp/api/api.php?apicall=";

    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_LOGOUT = ROOT_URL + "logout";
    public static final String URL_ADD_USER = ROOT_URL + "addUser";
    public static final String URL_GET_MOVIES = ROOT_URL + "getMovies";
    public static final String URL_ADD_MOVIE = ROOT_URL + "addMovie";
    public static final String URL_REMOVE_MOVIE = ROOT_URL + "removeMovie";
}