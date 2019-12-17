package com.example.avocado1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;




public class SearchMoviesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetTMDBJsonData.OnDataReady {
    private static final String TAG = "ViewDatabase";

    private TMDBRecyclerViewAdapter mTMDBRecyclerViewAdapter;


    private static final String baseURL ="https://api.themoviedb.org/3/search/movie?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-IL&page=1&include_adult=false";
    //private static final String baseURI ="https://api.themoviedb.org/3";
    //private static final String SearchURI ="https://api.themoviedb.org/3/search/movie?query=man in black&api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-IL";
    //private static final String GenresListURI ="https://api.themoviedb.org/3/genre/movie/list?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-il";
    //private static final String TopRated_TVShowsURI =" https://api.themoviedb.org/3/tv/top_rated?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-il&page=1";
    private static final String language ="he-IL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movies);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTMDBRecyclerViewAdapter = new TMDBRecyclerViewAdapter(this, new ArrayList<Movie>());
        recyclerView.setAdapter(mTMDBRecyclerViewAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_homeId:
                Intent HomeIntent = new Intent(this, HomePage.class);
                startActivity(HomeIntent);
                return true;


            case R.id.action_accountId:
                Intent AccountIntent = new Intent(this, AccountPage.class);
                startActivity(AccountIntent);
                return true;


            case R.id.action_moviesId:
                Intent MovieIntent = new Intent(this, MovieDetailActivity.class);
                startActivity(MovieIntent);
                return true;

            case R.id.action_tvShowsId:
                Intent TvShowsIntent = new Intent(this, TvShowDetailActivity.class);
                startActivity(TvShowsIntent);
                return true;

            case R.id.search_movies:
                Intent searchMoviesIntent = new Intent(this, SearchMoviesActivity.class);
                startActivity(searchMoviesIntent);
                return true;

            case R.id.action_recommended:
                Intent recommendedMoviesIntent = new Intent(this, RecommendedMovies.class);
                startActivity(recommendedMoviesIntent);
                return true;
            case R.id.action_recommendedTv:
                Intent recommendedTvIntent = new Intent(this, RecommendedTvShows.class);
                startActivity(recommendedTvIntent);
                return true;

            case R.id.action_signOutId:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                            }
                        });
                Toast.makeText(this, "signed out", Toast.LENGTH_LONG).show();
                Intent signOutIntent = new Intent(this, LoginPage.class);
                startActivity(signOutIntent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        return true;

    }

    protected  void onResume(){
        Log.d(TAG, "onResume: starts");
        super.onResume();
        GetTMDBJsonData gTMDBdata = new GetTMDBJsonData(this,baseURL, language);
        //   gTMDBdata.excuteOnSameThread("");
        gTMDBdata.execute();
        Log.d(TAG, "onResume: ends");

    }


    @Override
    public void onDataReady(List list, DownloadStatus status){
        Log.d(TAG, "onDataReady: starts");



        if(status == DownloadStatus.OK){
            mTMDBRecyclerViewAdapter.loadNewData(list);
        }
        else {
            Log.e(TAG, "onDataReady failed with status " + status );
        }
        Log.d(TAG, "onDataReady: ends");
    }

}


