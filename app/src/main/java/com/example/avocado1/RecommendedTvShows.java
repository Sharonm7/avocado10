package com.example.avocado1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class RecommendedTvShows extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetJSONDataByGenreTv.OnDataReadyTv {
    private static final String TAG = "ViewDatabase";
    private FirebaseAuth mAuth;
    private DatabaseReference moviesRef;
    private TextView textViewEmptyListError;

    private TMDBTVRecyclerViewAdapter mTMDBTVRecyclerViewAdapter;
    //List<?> list= new ArrayList<>();


    private static final String baseURL ="https://api.themoviedb.org/3/tv/popular?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-IL&page=1";

    //  private static final String baseURL ="https://api.themoviedb.org/3/discover/movie?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-IL&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&year=2019";
    //private static final String baseURI ="https://api.themoviedb.org/3";
    //private static final String SearchURI ="https://api.themoviedb.org/3/search/movie?query=man in black&api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-IL";
    //private static final String GenresListURI ="https://api.themoviedb.org/3/genre/movie/list?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-il";
    //private static final String TopRated_TVShowsURI =" https://api.themoviedb.org/3/tv/top_rated?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-il&page=1";
    private static final String language ="he-IL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTMDBTVRecyclerViewAdapter = new TMDBTVRecyclerViewAdapter(this, new ArrayList<TvShow>());
        recyclerView.setAdapter(mTMDBTVRecyclerViewAdapter);
        textViewEmptyListError= findViewById(R.id.emptyList);

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
        GetJSONDataByGenreTv gTMDBdata = new GetJSONDataByGenreTv(this,baseURL, language);
        //   gTMDBdata.excuteOnSameThread("");
        gTMDBdata.execute();
        Log.d(TAG, "onResume: ends");

    }


    @Override
    public void onDataReadyTv(List list, DownloadStatus status){
        Log.d(TAG, "onDataReady: starts");



        if(status == DownloadStatus.OK){
            filterByGenere(list);

        }
        else {
            Log.e(TAG, "onDataReadyTv failed with status " + status );
        }
        Log.d(TAG, "onDataReady: ends");
    }

    private void filterByGenere(final List list) {



        mAuth = FirebaseAuth.getInstance();

        moviesRef = FirebaseDatabase.getInstance().getReference("Users");
        final String userName = mAuth.getCurrentUser().getDisplayName();



        moviesRef.child(userName).child("genresId").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Integer> genresIdArray;
                ArrayList<TvShow> filteredShows= new ArrayList<>();
                genresIdArray = ((ArrayList<Integer>)dataSnapshot.getValue());

                //iterate on each movie
                for( int i = 0; i < list.size() ; i++ ){

                    TvShow tvShowFromList = (TvShow) list.get(i);
                    //get his genere
                    ArrayList<Integer> generesFromList = tvShowFromList.getGenre_ids();
                    System.out.println("genresFromList" + generesFromList);
                    System.out.println("genresIdArray" + genresIdArray);




                    // if genere is in Genere Array add to new list
                    for (int j = 0 ; j < generesFromList.size() ; j ++){
                        for(int k=0; k<genresIdArray.size();k++){
                            System.out.println("generesFromList-j: " + generesFromList.get(j));
                            System.out.println("genresIdArray-k: " + genresIdArray.get(k));



                            String a = String.valueOf(generesFromList.get(j));
                            String b = String.valueOf(genresIdArray.get(k));

                            System.out.println(a==b);

                            if(a.equals(b)){

                                filteredShows.add(tvShowFromList);
                                System.out.println("TvShowFromListINNN" + tvShowFromList);
                                System.out.println("filteredShowINNN" + filteredShows);
                                System.out.println("filteredShowSIZE" + filteredShows.size());


                            }
                            break;
                        }
//                            if(genresIdArray.contains(generesFromList.get(j)));
//                                filteredMovie.add(movieFromList);
//                                continue;
                        System.out.println("filteredshows" + filteredShows);
                        break;





                    }
                    // System.out.println("filteredMovie" + filteredMovie);




                }

                //display new list
                if(filteredShows.isEmpty())
                    Toast.makeText(RecommendedTvShows.this, "אין תכן להציג על פי הז'אנרים הנבחרים", Toast.LENGTH_LONG).show();
                mTMDBTVRecyclerViewAdapter.loadNewData(filteredShows);
                // genresIdArray = ((ArrayList<Integer>) dataSnapshot.getValue());

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

}




