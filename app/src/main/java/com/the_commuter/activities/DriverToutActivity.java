package com.the_commuter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.JsonArray;
import com.the_commuter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriverToutActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = DriverToutActivity.class.getSimpleName();
    @BindView(R.id.bus_stop)
    EditText bus_stopName;
    @BindView(R.id.btn_search_stage)
    Button submit_btn;
    @BindView(R.id.return_stage)
    TextView setStage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tout);

        ButterKnife.bind(this);

        AndroidNetworking.initialize(getApplicationContext());
        submit_btn.setOnClickListener(this);

        setupFirebaseAuth();
//        getUserDetails();
        setUserDetails();
    }

    private void setupFirebaseAuth() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(DriverToutActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void setUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName("")
                    .setPhotoUri(Uri.parse("https://unsplash.com/photos/xP_AGmeEa6s")).build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                getUserDetails();
                            }
                        }
                    });
        }
    }

    private void getUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            String properties = "uid: " + uid + "\n" +
                    "name: " + name + "\n" +
                    "email: " + email + "\n" +
                    "photoUrl: " + photoUrl;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loggedin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            logout();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.optionAccountSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == submit_btn) {
            String bus_stop = bus_stopName.getText().toString().trim();
            if (bus_stop != null) {
                AndroidNetworking.get("https://event-tracker-system.herokuapp.com/api/get/{bus_stop}/bus_stop")
                        .addPathParameter("bus_stop", bus_stop)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int count = response.getInt("count");
//                                    Toast.makeText(DriverToutActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
                                    JSONArray jsonArray = response.getJSONArray("results");
                                    String stage = jsonArray.getJSONObject(0).getString("bus_stop_name");

                                    setStage.setText("Stage name: " + stage + "\n" + "Number of commuters: " + String.valueOf(count));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            } else {
                Toast.makeText(this, "Provide a bus stop name", Toast.LENGTH_SHORT).show();
            }
        }

    }
}