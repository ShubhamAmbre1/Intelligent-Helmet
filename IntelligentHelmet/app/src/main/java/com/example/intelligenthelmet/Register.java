package com.example.intelligenthelmet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private TextInputEditText username, email, password, phone;
    private Button buttonSignUp;
    private ProgressBar progressBar;
    private TextView loginText;

    private DatabaseReference mDatabase;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.rusername);
        email = findViewById(R.id.remail);
        password = findViewById(R.id.rpassword);
        phone = findViewById(R.id.phone);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        signUp();
        clickableSignUpText();
    }

    private void clickableSignUpText() {
        loginText = findViewById(R.id.loginText);

        String text = "Already have an account? Login";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        };

        ss.setSpan(clickableSpan, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginText.setText(ss);
        loginText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void signUp(){
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_ = email.getText().toString().trim();
                String password_ = password.getText().toString().trim();

                if(TextUtils.isEmpty(email_)){
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password_)){
                    password.setError("Password is required");
                    return;
                }
                if(password_.length() < 6){
                    password.setError("Password must be greater 5");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.createUserWithEmailAndPassword(email_, password_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            createUser();
                            Toast.makeText(Register.this, "User Register", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void createUser(){
        NewUser user = new NewUser();
        mDatabase.setValue(user);
    }
}