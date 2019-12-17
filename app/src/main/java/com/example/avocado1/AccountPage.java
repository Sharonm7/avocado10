package com.example.avocado1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccountPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        private Toolbar toolbar;
        TextView textViewEmail;
        Button ButtonEditGenres;
        TextView textViewDisplayName;
        TextView textViewSelectedGenres;
        CalendarView calendarView;
        Button btnSaveDate;
        FirebaseAuth mAuth;
        private FirebaseDatabase mFirebaseDatabase;
        private FirebaseAuth.AuthStateListener mAuthListener;
        private DatabaseReference myRef;
        private List<String> genres = new ArrayList<>();


        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        toolbar = findViewById(R.id.toolbarId);
//        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        textViewDisplayName = (TextView) findViewById(R.id.accountName);
        textViewSelectedGenres = (TextView) findViewById(R.id.selectedGenres);
        textViewEmail = (TextView) findViewById(R.id.accountEmail);
        ButtonEditGenres = (Button) findViewById(R.id.editGenres);
        calendarView= (CalendarView) findViewById(R.id.calendarView);
        btnSaveDate= (Button) findViewById(R.id.btnSaveDate);


        loadUserInformation(genres);

        ButtonEditGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountPage.this, ChooseGenresPage.class));
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                String date= i2 + "/" +(i1+1) + "/" +i;
                Toast.makeText(getApplicationContext(), "date:"+ date, Toast.LENGTH_LONG).show();


            }
        });


    }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return true;


    }

        private void loadUserInformation (List<String> genres) {


        final FirebaseUser user = mAuth.getCurrentUser();
        if (user.getDisplayName() != null) {
            textViewDisplayName.setText("החשבון של " + user.getDisplayName());
        }
        textViewEmail.setText(mAuth.getCurrentUser().getEmail());

        updateGenres(genres);

    }


        private List<String> updateGenres ( final List<String> genres){

        myRef = FirebaseDatabase.getInstance().getReference("Users");
        final String userName = mAuth.getCurrentUser().getDisplayName().toString();



            myRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                genres.add(dataSnapshot.child("preferences").getValue().toString());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setSelected(genres);


        return genres;


    }


        private void setSelected ( final List<String> genres){

        myRef = FirebaseDatabase.getInstance().getReference("Users");
        final String userName = mAuth.getCurrentUser().getDisplayName().toString();
        myRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String genres = dataSnapshot.child("preferences").getValue().toString();
                genres = genres.replace("[", "");
                genres = genres.replace("]", "");

                textViewSelectedGenres.setText(genres);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
        public boolean onNavigationItemSelected (MenuItem item){
       ///
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

