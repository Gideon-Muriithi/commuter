package com.the_commuter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.the_commuter.R;
import com.the_commuter.dialogs.PasswordResetDialog;
import com.the_commuter.dialogs.ResendVerificationDialog;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;
    private Button signIn;
    private TextView register;
    private TextView resetPassword;
    private TextView resendEmailVerification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail =  findViewById(R.id.email);
        mPassword =  findViewById(R.id.password);
        mProgressBar = findViewById(R.id.progressBar);

        signIn = findViewById(R.id.email_sign_in_button);
        register =  findViewById(R.id.link_register);
        resetPassword = findViewById(R.id.forgot_password);
        resendEmailVerification = findViewById(R.id.resend_verification_email);

        signIn.setOnClickListener(this);
        register.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        resendEmailVerification.setOnClickListener(this);

        setupFirebaseAuth();
    }

    private void setupFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    if (user.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "Authenticated with: "
                                + user.getEmail(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, DriverToutActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                }
            }
        };

    }

    @Override
    public void onClick(View view) {

        if (view == signIn) {
            if (isEmpty(mEmail.getText().toString())
                    && isEmpty(mPassword.getText().toString())) {
                showDialog();

                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                hideDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
            }

        }

        if (view == register) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        if (view == resetPassword) {
            PasswordResetDialog dialog = new PasswordResetDialog();
            dialog.show(getSupportFragmentManager(), "dialog_password_reset");
        }

        if (view == resendEmailVerification) {
            ResendVerificationDialog dialog = new ResendVerificationDialog();
            dialog.show(getSupportFragmentManager(), "dialog_resend_email_verification");
        }

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private boolean isEmpty(String string) {
        return !string.equals("");
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}