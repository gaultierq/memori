package com.qg.memori;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.logger.Logger;

public class SignupActivity extends AppCompatActivity {

    public static final int LAYOUT = R.layout.activity_signup;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Logger.d("signed_in:" + user.getUid());
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                }
                else {
                    Logger.d("signed is not signed in");
                }
            }
        };
    }

    public void gotoLogin(View v) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }

    public void signup(View v) {
        Logger.d("signing up");
        auth.createUserWithEmailAndPassword(
                ((TextView) findViewById(R.id.email)).getText().toString(),
                ((TextView) findViewById(R.id.password)).getText().toString()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Logger.d("signup successful:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w("signInWithEmail", task.getException());
                    Toast.makeText(SignupActivity.this, "Signing up failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
