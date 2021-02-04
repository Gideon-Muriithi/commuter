package com.the_commuter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.the_commuter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommuterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener {
    @BindView(R.id.spinner)
    Spinner spinner;
    String stage;
    @BindView(R.id.btn_submit_stage)
    Button submit_stage;

    String[] categories = {
            "Ruiru",
            "Kasarani Sunton",
            "Juja",
            "Ongata Rongai",
            "Athi River",
            "Mountain Mall"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commuter);

        ButterKnife.bind(this);
        AndroidNetworking.initialize(getApplicationContext());

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);

        submit_stage.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        // On selecting a spinner item
        stage = adapterView.getItemAtPosition(i).toString();
        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected Stage: " + stage, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(adapterView.getContext(), "Nothing has been selected" + stage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {

        if (view == submit_stage) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("bus_stop_name", stage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AndroidNetworking.post("https://event-tracker-system.herokuapp.com/api/create/bus_stop")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(CommuterActivity.this, "Submitted successfully", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(CommuterActivity.this, String.valueOf(anError), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

}