package com.example.avocado1;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginPage extends BaseActivity {


    TextView signUp;
    User user;
     private FirebaseAuth mAuth;
     EditText editTextPassword, editTextEmail;
     Button loginBtn;
     ProgressBar progressBar;
    DatabaseReference myRef;
  //  Button googleSignInBtn;
   // private static final String TAG2 = "GoogleActivity";
  //  private static final int RC_SIGN_IN = 9001;
  //  private static final String EMAIL = "email";
  //  private GoogleSignInClient mGoogleSignInClient;



    private static final String TAG= LoginPage.class.getName();






     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        signUp= findViewById(R.id.signUpId);
//         user= new User(id, Email, Password, UserName);
         editTextEmail = findViewById(R.id.emailId);
         editTextPassword = findViewById(R.id.passId);
         loginBtn= findViewById(R.id.loginId);
         progressBar= findViewById(R.id.progressBarId);
         mAuth= FirebaseAuth.getInstance();
       //  googleSignInBtn= findViewById(R.id.sign_in_button);




         signUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent myIntent = new Intent(view.getContext(), RegisterPage.class);
               startActivityForResult(myIntent, 0);
           }
       });


         loginBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 progressBar.setVisibility(View.VISIBLE);

                 String Email = editTextEmail.getText().toString().trim();
                 String Password= editTextPassword.getText().toString().trim();

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

                 if (Password.length() < 6) {
                     editTextPassword.setError("Minimum length of password should be 6");
                     editTextPassword.requestFocus();
                     return;
                 }

                 mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()){
                             Log.d(TAG, "success");
                             FirebaseUser currentUser = mAuth.getCurrentUser();

                             String userEmail;
                             userEmail=currentUser.getEmail();

                             myRef= FirebaseDatabase.getInstance().getReference("Users");

                             myRef.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                     }
                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {
                                         Toast.makeText(LoginPage.this, "failed to read"+ databaseError.toException(),Toast.LENGTH_SHORT).show();
                                     }
                             });

                             //Toast.makeText(LoginPage.this,"Hello "+userEmail,Toast.LENGTH_LONG).show();


                             if(currentUser != null)
                             {
                                 Intent intent = new Intent(LoginPage.this, AccountPage.class);
                                 startActivity(intent);
                             }
                         }
                         else {
                             Toast.makeText(LoginPage.this, "Authentication failed."+ task.getException().getMessage(),
                                     Toast.LENGTH_SHORT).show();

                         }

                     }
                 });



             }
         });
/*
         googleSignInBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

             //    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
               //  startActivityForResult(signInIntent, RC_SIGN_IN);




             }
         });

*/

            }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();


    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(LoginPage.this, LoginPage.class);
        startActivity(intent);
        finish();

    }
/*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG2, "Google sign in failed", e);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
//        callbackManager.onActivityResult(requestCode, resultCode, data);





    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG2, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG2, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), HomePage.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG2, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                       // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]




*/

}



