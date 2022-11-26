package com.myapps.majorprojectversion2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText email, password;
    private Button login;
    private TextView registerHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();






        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        registerHere = findViewById(R.id.textViewRegisterHere);

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailArg = Objects.requireNonNull(email.getText()).toString();
                String passwordArg = Objects.requireNonNull(password.getText()).toString();
                if (emailArg.length() >= 11) {
                    if (passwordArg.length() >= 8) {
                        signIn(emailArg, passwordArg);
                    } else {
                        password.setError("password should be at least 8 chars");
                    }

                } else {
                    email.setError("Invalid email");
                }
            }
        });


    }

    private void signIn(String emailArg, String passwordArg) {

        mAuth.signInWithEmailAndPassword(emailArg, passwordArg).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(getApplicationContext(), PublisherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", String.valueOf(user));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Error logging in User", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), PublisherActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user", String.valueOf(currentUser));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {

    }
}