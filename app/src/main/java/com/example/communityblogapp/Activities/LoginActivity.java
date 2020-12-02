package com.example.communityblogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.communityblogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText login_mail,login_password;
    private Button btnLogin;
    private ProgressBar login_progress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView login_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_mail = findViewById(R.id.login_mail);
        login_password = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btnLogin);
        login_progress = findViewById(R.id.login_progress);
        login_photo = findViewById(R.id.login_photo);

        login_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        HomeActivity = new Intent(this, com.example.communityblogapp.Activities.Home.class);

        login_progress.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_progress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = login_mail.getText().toString();
                final String password = login_password.getText().toString();

                if (mail.isEmpty() || password.isEmpty()){
                    showMessage("Please verify all the fields");
                    btnLogin.setVisibility(View.VISIBLE);
                    login_progress.setVisibility(View.INVISIBLE);
                }
                else{
                    signIn(mail,password);
                }
            }
        });
    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    login_progress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();
                }
                else{
                    showMessage(task.getException().getMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    login_progress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUI() {
        startActivity(HomeActivity);
        finish();
    }

    private void showMessage(String text) {
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null){
            updateUI();
        }
    }
}