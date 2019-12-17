package com.example.avocado1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ViewDatabase";
    private Toolbar toolbar;
    private TextView helloUser;
    private FirebaseAuth mAuth;
    private User muser;
    private User user;
    private TextView emailNav;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference moviesRef;
    private DatabaseReference TvShowsRef;
    private List<String> genresList = new ArrayList<>();
    private Button buttons[] = new Button[5];
    TextView textViewDisplayName;
    TextView textViewDisplayGenres;
    TextView textViewDisplayFollowingMovies;
    TextView textViewDisplayFollowingTvshows;
    private ImageView imageViewDisplayMoviePoster;
    private Button btnAddMovieToCal;


    private TMDBRecyclerViewAdapter mTMDBRecyclerViewAdapter;
    private GetTMDBJsonData getTMDBJsonData;
    private Map<String, Movie> userFollow;


    public static Map<String, Object> userMap;


    private static final String baseURL = "https://api.themoviedb.org/3/discover/movie?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-IL&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&year=2019";
    //private static final String baseURI ="https://api.themoviedb.org/3";
    //private static final String SearchURI ="https://api.themoviedb.org/3/search/movie?query=man in black&api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-IL";
    //private static final String GenresListURI ="https://api.themoviedb.org/3/genre/movie/list?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-il";
    //private static final String TopRated_TVShowsURI =" https://api.themoviedb.org/3/tv/top_rated?api_key=5ba2372e5f26794510a9b0987dddf17b&language=he-il&page=1";
    private static final String language = "he-IL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);




        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        textViewDisplayName = (TextView) findViewById(R.id.helloUser);
        textViewDisplayGenres = (TextView) findViewById(R.id.genres);
        //textViewDisplayFollowingTvshows = (TextView) findViewById(R.id.followingTvshows);
        imageViewDisplayMoviePoster= (ImageView) findViewById(R.id.moviePoster);



        toolbar = findViewById(R.id.toolbarId);
        emailNav = findViewById(R.id.emailNavBarId);
//        setSupportActionBar(toolbar);

        loadUserInformation();
       updateGenres(genresList);
         DisplayGenres(buttons);
       setOnMoviesListener();
        setOnTvShowsListener();





    }

    @Override
    public void onStart() {
        super.onStart();
        // checkAuth();
//        mAuth.addAuthStateListener(mAuthListener);
    }

    private void loadUserInformation() {

        final FirebaseUser fbUser = mAuth.getCurrentUser();


        if (fbUser.getDisplayName() != null) {
            textViewDisplayName.setText("ברוכים הבאים,  " + fbUser.getDisplayName() + "!");
            Log.d("ggg", fbUser.getDisplayName());
        }


    }

        private void updateGenres(final List<String> genres) {

        moviesRef = FirebaseDatabase.getInstance().getReference("Users");
        final String userName = mAuth.getCurrentUser().getDisplayName();

            moviesRef.child(userName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    String genres = dataSnapshot.child("preferences").getValue().toString();

                    genres = genres.replace("[", "");
                    genres = genres.replace("]", "");

                    textViewDisplayGenres.setText(genres);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
    }

    private void DisplayGenres(final Button buttons[]) {
        moviesRef = FirebaseDatabase.getInstance().getReference("Users");
        final String userName = mAuth.getCurrentUser().getDisplayName();


        moviesRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String genresStr = textViewDisplayGenres.getText().toString();

                long numOfGenres = dataSnapshot.child("preferences").getChildrenCount();

                createButtons(genresStr, numOfGenres);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void checkAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(HomePage.this, "Successfully signed in with: " + user.getEmail(), Toast.LENGTH_LONG).show();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(HomePage.this, "Successfully signed out " + user.getEmail(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void createButtons(String genreStr, long numOfGenres) {
        Log.d(TAG, "string: " + genreStr);


        List<String> genresList = Arrays.asList(genreStr.split("\\s*,\\s*"));

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.buttonsLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        for (int i = 0; i < numOfGenres; i++) {

            Button genresBtn = new Button(this);
            linearLayout.addView(genresBtn);
            genresBtn.setText(genresList.get(i));
            genresBtn.setBackgroundColor(Color.parseColor("#227a70"));


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    public void setOnTvShowsListener(){



        mAuth = FirebaseAuth.getInstance();
        TvShowsRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getDisplayName()).child("followingTvShows");


        TvShowsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();


                final ArrayList<TvShow> tvShowArrayList = new ArrayList<>();
                if (td.entrySet() != null) {


                    Iterator it = td.entrySet().iterator();

                    while (it.hasNext()) {

                        Map.Entry pair = (Map.Entry) it.next();

                        System.out.println(pair.getKey() + " = " + pair.getValue());
                        DatabaseReference TvShowRef = TvShowsRef.child((String) pair.getKey());

                        TvShowRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                TvShow tvShow = dataSnapshot.getValue(TvShow.class);
                                if (tvShow != null) {

                                    tvShowArrayList.add(tvShow);

                                    System.out.println("tvArray:" + tvShowArrayList.toString());

                                    System.out.println(tvShow.toString());
                                    System.out.println(tvShow.getTitle());
                                    dispalyFollowingTvShows(tvShow);
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }


                        });

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


       // DeleteEmptyMovie();
    }





    public void setOnMoviesListener() {

            mAuth = FirebaseAuth.getInstance();
            moviesRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getDisplayName()).child("followingMovies");
//        final String userName = mAuth.getCurrentUser().getDisplayName();


        moviesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Movie> td = (HashMap<String, Movie>) dataSnapshot.getValue();
//                List<Object> values = (List<Object>) td.values();
                final ArrayList<Movie> movieArrayList = new ArrayList<>();
               if (td.entrySet() != null) {


                Iterator it = td.entrySet().iterator();

                while (it.hasNext()) {

                    Map.Entry pair = (Map.Entry) it.next();

                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    DatabaseReference movieRef = moviesRef.child((String) pair.getKey());

                    movieRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Movie movie = dataSnapshot.getValue(Movie.class);
                            if (movie != null) {

                                movieArrayList.add(movie);

                                System.out.println("movieArray:" + movieArrayList.toString());

                                System.out.println(movie.toString());
                                System.out.println(movie.getTitle());
                                dispalyFollowingMovies(movie);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });

                }


            }

       }


                @Override
                public void onCancelled (DatabaseError databaseError){
                }

        });
    }

    private void dispalyFollowingMovies(final Movie movie){


        LinearLayout linearLayout = findViewById(R.id.followingMoviesLayout);
//            createMovieContainer();
            TextView textViewDisplayMovies= new TextView(this);
            textViewDisplayMovies.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            textViewDisplayMovies.setText(movie.getTitle());
            textViewDisplayMovies.setGravity(Gravity.CENTER);
            textViewDisplayMovies.setTextSize(20);
            linearLayout.addView(textViewDisplayMovies);


            ImageView imageViewDisplayMoviesPoster= new ImageView(this);
        imageViewDisplayMoviesPoster.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Picasso.get().load(movie.getPoster_path()).into(imageViewDisplayMoviesPoster);
        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(250,250);
        layoutParams.gravity=Gravity.CENTER;
        imageViewDisplayMoviesPoster.setLayoutParams(layoutParams);
        linearLayout.addView(imageViewDisplayMoviesPoster);



        Button unfollowBtn= new Button(this);
        unfollowBtn.setBackgroundResource(R.drawable.follow_btn_shape);
        unfollowBtn.setText("הסר");
        unfollowBtn.setTextColor(Color.WHITE);
        unfollowBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams layoutParamsBtn =new LinearLayout.LayoutParams(100,70);
        layoutParamsBtn.gravity=Gravity.CENTER;
        unfollowBtn.setLayoutParams(layoutParamsBtn);
        linearLayout.addView(unfollowBtn);



        unfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteFollowingMovie(movie);

            }
        });





      imageViewDisplayMoviesPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseUser fbUser = mAuth.getCurrentUser();
                Intent intent = new Intent(HomePage.this, CalendarActivity.class);
                //intent.putExtra("moviedetails", new String[]{movie.getTitle(),movie.getRelease_date()

                intent.putExtra("MovieTitle", movie.getTitle());
                intent.putExtra("MovieDate",movie.getRelease_date() );
                intent.putExtra("email",fbUser.getEmail() );
                startActivity(intent);

            }
        });



    }

    private void dispalyFollowingTvShows(final TvShow tvShow) {

        LinearLayout linearLayout = findViewById(R.id.followingTvShowsLayout);
//            createMovieContainer();
        TextView textViewDisplayTvShows= new TextView(this);
        textViewDisplayTvShows.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textViewDisplayTvShows.setText(tvShow.getTitle());
        textViewDisplayTvShows.setGravity(Gravity.CENTER);
        textViewDisplayTvShows.setTextSize(20);
        linearLayout.addView(textViewDisplayTvShows);




        ImageView imageViewDisplayTvShowsPoster= new ImageView(this);
        imageViewDisplayTvShowsPoster.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Picasso.get().load(tvShow.getPoster_path()).into(imageViewDisplayTvShowsPoster);
        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(250,250);
        layoutParams.gravity=Gravity.CENTER;
        imageViewDisplayTvShowsPoster.setLayoutParams(layoutParams);
        linearLayout.addView(imageViewDisplayTvShowsPoster);







        Button unfollowBtn= new Button(this);
        unfollowBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        unfollowBtn.setBackgroundResource(R.drawable.follow_btn_shape);
        unfollowBtn.setText("הסר");
        unfollowBtn.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams layoutParamsBtn =new LinearLayout.LayoutParams(100,70);
        layoutParamsBtn.gravity=Gravity.CENTER;
        unfollowBtn.setLayoutParams(layoutParamsBtn);
        linearLayout.addView(unfollowBtn);







        unfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteFollowingTvShow(tvShow);

            }
        });

        imageViewDisplayTvShowsPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseUser fbUser = mAuth.getCurrentUser();
                Intent intent = new Intent(HomePage.this, CalendarActivity.class);
                //intent.putExtra("moviedetails", new String[]{movie.getTitle(),movie.getRelease_date()

                intent.putExtra("TvShowTitle", tvShow.getTitle());
                intent.putExtra("TvShowDate",tvShow.getRelease_date() );
                intent.putExtra("email",fbUser.getEmail() );
                startActivity(intent);

            }
        });




    }



    public void DeleteEmptyMovie(){
        mAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Users").child(mAuth.getCurrentUser().getDisplayName()).child("followingTvShows").child("0");

        query.getRef().removeValue();
        finish();
        startActivity(getIntent());



    }


    public void DeleteFollowingMovie(Movie movie){

        mAuth = FirebaseAuth.getInstance();
        String Title= movie.getTitle();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Users").child(mAuth.getCurrentUser().getDisplayName()).child("followingMovies").orderByChild("title").equalTo(Title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }




    public void DeleteFollowingTvShow(TvShow tvShow){

        mAuth = FirebaseAuth.getInstance();
        String Title= tvShow.getTitle();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Users").child(mAuth.getCurrentUser().getDisplayName()).child("followingTvShows").orderByChild("title").equalTo(Title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }








    @Override
    public void onBackPressed() {
     /*   DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
       */
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        return true;


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
}