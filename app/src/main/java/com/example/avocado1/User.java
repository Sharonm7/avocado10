package com.example.avocado1;

import java.util.ArrayList;
import java.util.List;

public class User {
    public static User u;
    private String id;
    private String email;
    private String password;
    private String username;
    private ArrayList<String> Preferences;
    private List<Movie> followingMovies;
    private ArrayList<TvShow> followingTvShows ;
    private ArrayList<Integer> genresId;


//    public User(String id, String email, String password, String userName) {
//        this.id = id;
//        this.email = email;
//        this.username = userName;
//        this.password = password;
//    }

    public User(String id, String email,String password, String userName,ArrayList<String> preferences, ArrayList<Movie> followingMovies, ArrayList<TvShow> followingTvShows, ArrayList<Integer> genresId) {
        this.id = id;
        this.email = email;
        this.username = userName;
        this.Preferences = preferences;
        this.followingMovies= followingMovies;
        this.followingTvShows= followingTvShows;
        this.genresId= genresId;



    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getPreferences() {
        return Preferences;
    }

    public void setPreferences(ArrayList<String> preferences) {
        Preferences = preferences;
    }

    public List<Movie> getFollowingMovies() {
        return followingMovies;
    }

    public ArrayList<TvShow> getFollowingTvShows() {
        return followingTvShows;
    }

    public void setFollowingMovies(List<Movie> followingMovies) {
        this.followingMovies = followingMovies;
    }

    public void setFollowingTvShows(ArrayList<TvShow> followingTvShows) {
        this.followingTvShows = followingTvShows;
    }

    public ArrayList<Integer> getGenresId() {
        return genresId;
    }

    public void setGenresId(ArrayList<Integer> genresId) {
        this.genresId = genresId;
    }
}



