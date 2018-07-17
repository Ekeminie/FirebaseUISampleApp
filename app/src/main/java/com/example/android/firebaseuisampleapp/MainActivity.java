package com.example.android.firebaseuisampleapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.provider.IDPResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity {

   CoordinatorLayout constraintLayout;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    private TextView email_tv, displayName_tv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_tv = findViewById(R.id.emailView);
        displayName_tv = findViewById(R.id.displayName);
        constraintLayout = findViewById(R.id.root);
        mAuth = FirebaseAuth.getInstance();
        checkLogin();

    }

    /**
     * Check if user is Signend in or not using the authentication object listener
     */
    private void checkLogin(){

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //do nothing


                    return;
                }else {
                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(AuthUI.GOOGLE_PROVIDER, AuthUI.FACEBOOK_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    /**
     * This method handles the signing out when called from the overflow menu
     */
    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            showSnackbarMessage("User is succesfully Logged out");
                            checkLogin();
                        }else{
                            showSnackbarMessage("User is not Logged out");
                        }
                        // ...
                    }
                });
    }

    public void showSnackbarMessage (String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){

            if (resultCode == RESULT_OK){
                //User successfully Signed in
                showSnackbarMessage("You are Succesfully Logged in");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String displayName = user.getDisplayName();
                String email = user.getEmail();

                displayName_tv.setText(displayName);
                email_tv.setText(email);


            }else {

                checkLogin();

            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                signOut();
                return true;

            default:
                return false;


        }
    }
}
