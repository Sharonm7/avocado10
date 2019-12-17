package com.example.avocado1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterPage extends AppCompatActivity {

    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    Button submitBtn;
    User user;
    FirebaseDatabase databaseRef;
    private DatabaseReference userRef;
    ProgressBar progressBar;
    TextView LoginBtn;
    private FirebaseAuth mAuth;
    private static final String TAG = RegisterPage.class.getName();






    private void AddUserToDb(String Email, String Password, String UserName) {
        Movie m= new Movie();
        TvShow tv= new TvShow();
        ArrayList<String> Preferences = new ArrayList<>();
        Preferences.add("");
        ArrayList<Integer> genreId= new ArrayList<>();
        genreId.add(0);
        ArrayList<Movie> followingMovies = new ArrayList<>();
        ArrayList<TvShow> followingTvShows = new ArrayList<>();
        followingMovies.add(m);
        followingTvShows.add(tv);


        String id = userRef.push().getKey();

        userRef = FirebaseDatabase.getInstance().getReference("Users");
//        final String userName = mAuth.getCurrentUser().getDisplayName();



        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        User user = new User(id, Email, Password, UserName, Preferences,followingMovies,followingTvShows, genreId);


        userRef.child(UserName);
        userRef.child(UserName).setValue(user);
        userRef.child(UserName).child("preferences").setValue(Preferences);
        userRef.child(UserName).child("followingMovies").setValue(followingMovies);
        userRef.child(UserName).child("followingTvShows").setValue(followingTvShows);
        userRef.child(UserName).child("genreIDs").setValue(genreId);


        progressBar.setVisibility(View.GONE);
        //Toast.makeText(RegisterPage.this, "user added to db", Toast.LENGTH_LONG).show();


    }


    private void UserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest ProfileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(editTextUsername.getText().toString().trim()).build();

            user.updateProfile(ProfileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //Toast.makeText(getApplicationContext(), "user profile updated", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        editTextEmail = findViewById(R.id.emailRegId);
        editTextPassword = findViewById(R.id.passRegId);
        editTextUsername = findViewById(R.id.usernameId);
        progressBar = findViewById(R.id.progressBarId);
        LoginBtn = findViewById(R.id.LoginId);
//        user = new User(id, Email, Password, UserName);
        submitBtn = findViewById(R.id.submitId);


        databaseRef = FirebaseDatabase.getInstance();
        userRef = databaseRef.getReference("Users");
        mAuth = FirebaseAuth.getInstance();


        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginPage.class);
                startActivityForResult(myIntent, 0);
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = editTextEmail.getText().toString().trim();
                String Password = editTextPassword.getText().toString().trim();
                String UserName = editTextUsername.getText().toString().trim();


                if (Email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    return;
                }

                if (Password.isEmpty()) {
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }
                if (editTextPassword.length() < 6) {
                    editTextPassword.setError("Minimum lenght of password should be 6");
                    editTextPassword.requestFocus();
                    return;
                }

                if (UserName.isEmpty()) {
                    editTextUsername.setError("User Name is required");
                    editTextUsername.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                AddUserToDb(Email,Password,UserName);


                mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            //Toast.makeText(RegisterPage.this, "User auth was created!", Toast.LENGTH_LONG).show();
                            UserProfile();
                            startActivity(new Intent(RegisterPage.this, ChooseGenresPage.class));

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                              //  Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterPage.this, ChooseGenresPage.class));

                            } else {
                                Toast.makeText(getApplicationContext(), "User Creation Failed due to: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

            }


        });

    }
}
