package com.example.intelligenthelmet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private CardView profile, location, emergency, activity;

    private Button logout;
    private DatabaseReference mDatabase;

    private TextView status, lastWorn;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        getHelmetStatus();
        logout();
        profile();
        location();
        activity();


    }

    private void getHelmetStatus(){
        status = findViewById(R.id.helmet);
        lastWorn = findViewById(R.id.lastWorn);

        status.setText(mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("status").getKey());
        status.setText(mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("last_worn").getKey());
    }

    private void activity(){
        activity = findViewById(R.id.activity);
        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DetailActivity.class));
            }
        });
    }

    private void logout(){
        logout = findViewById(R.id.buttonLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    private void profile(){
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });
    }

    private void location(){
        location = findViewById(R.id.location_button_dashboard);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ShowLocation.class));
            }
        });
    }
}