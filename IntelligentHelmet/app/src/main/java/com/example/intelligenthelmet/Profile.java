package com.example.intelligenthelmet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {
    private DatabaseReference mDatabase;
    FirebaseAuth fAuth;
    private TextInputEditText emergency;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        update = findViewById(R.id.profileButtonUpdate);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmergency();
            }
        });

    }

    public void changeEmergency(){
        emergency = findViewById(R.id.profileEmergencyPhoneNumber);
        emergency.setText(mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("emergency_number").setValue());
    }
}