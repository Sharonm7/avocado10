package com.example.avocado1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChooseGenresPage extends AppCompatActivity {


    private List<String> selectedGenres;
    private List<Integer> genredIDs;
    private CheckBox check_action;
    private CheckBox check_drama;
    private CheckBox check_comedy;
    private CheckBox check_horror;
    private CheckBox check_scifi;
    private Button chooseBtn;
    private User user;
    private TextView goToHome;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_genres_page);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        chooseBtn= findViewById(R.id.chooseBtnId);




        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedGenres= new ArrayList<>();
                genredIDs= new ArrayList<>();

                check_action = findViewById(R.id.check_action);
                check_comedy = findViewById(R.id.check_comedy);
                check_drama = findViewById(R.id.check_drama);
                check_horror = findViewById(R.id.check_horror);
                check_scifi = findViewById(R.id.check_scifi);

                if (check_action.isChecked()) {
                    selectedGenres.add("אקשן");
                    genredIDs.add(28);
                    genredIDs.add(80);
                    genredIDs.add(10759);

                }
                if (check_comedy.isChecked()){
                    selectedGenres.add("קומדיה");
                    genredIDs.add(35);
                }
                if (check_drama.isChecked()){
                    selectedGenres.add("דרמה");
                    genredIDs.add(18);
                    genredIDs.add(10749);

                }
                if (check_horror.isChecked()){
                    selectedGenres.add("אימה");
                    genredIDs.add(53);
                    genredIDs.add(27);

                }
                if (check_scifi.isChecked()){
                    selectedGenres.add("מדע-בדיוני");
                    genredIDs.add(878);
                    genredIDs.add(10765);
                }


                updateToDateBase(selectedGenres, genredIDs);
                startActivity(new Intent(ChooseGenresPage.this, AccountPage.class));



            }
        });


    }

    private void updateToDateBase(final List <String> selectedGenres, final List <Integer> genredIDs) {



        myRef = FirebaseDatabase.getInstance().getReference("Users");

        final String userName = mAuth.getCurrentUser().getDisplayName().toString();



        myRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                myRef.child(userName).child("preferences").setValue(selectedGenres);
                myRef.child(userName).child("genresId").setValue(genredIDs);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





}