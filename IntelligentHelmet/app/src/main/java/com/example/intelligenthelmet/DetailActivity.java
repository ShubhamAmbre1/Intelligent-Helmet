package com.example.intelligenthelmet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private DatabaseReference mDatabase;
    private TextView one, two, three, four, five, six, seven, eight, nine, ten, one1, two1, three1, four1, five1, six1, seven1, eight1, nine1, ten1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        setData();
    }

    private void setData(){
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        ten = findViewById(R.id.ten);


        one1 = findViewById(R.id.one1);
        two1 = findViewById(R.id.two1);
        three1 = findViewById(R.id.three1);
        four1 = findViewById(R.id.four1);
        five1 = findViewById(R.id.five1);
        six1 = findViewById(R.id.six1);
        seven1 = findViewById(R.id.seven1);
        eight1 = findViewById(R.id.eight1);
        nine1 = findViewById(R.id.nine1);
        ten1 = findViewById(R.id.ten1);

        String[] a = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mq3").child("detected").child("1").getKey().split(" ");
        String[] b = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mq3").child("detected").child("2").getKey().split(" ");
        String[] c = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mq3").child("detected").child("3").getKey().split(" ");
        String[] d = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mq3").child("detected").child("4").getKey().split(" ");
        String[] e = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mq3").child("detected").child("5").getKey().split(" ");
        String[] f = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mpu").child("detected").child("1").getKey().split(" ");
        String[] g = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mpu").child("detected").child("2").getKey().split(" ");
        String[] h = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mpu").child("detected").child("3").getKey().split(" ");
        String[] i = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mpu").child("detected").child("4").getKey().split(" ");
        String[] j = mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("mpu").child("detected").child("5").getKey().split(" ");

        one.setText(a[1]);
        two.setText(a[1]);
        three.setText(a[1]);
        four.setText(a[1]);
        five.setText(a[1]);
        six.setText(a[1]);
        seven.setText(a[1]);
        eight.setText(a[1]);
        nine.setText(a[1]);
        ten.setText(a[1]);

        one1.setText(a[0]);
        two1.setText(a[0]);
        three1.setText(a[0]);
        four1.setText(a[0]);
        five1.setText(a[0]);
        six1.setText(a[0]);
        seven1.setText(a[0]);
        eight1.setText(a[0]);
        nine1.setText(a[0]);
        ten1.setText(a[0]);
    }
}