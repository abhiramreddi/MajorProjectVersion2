package com.myapps.majorprojectversion2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText email, password, username;
    private Button register;
    private final DatabaseReference root = FirebaseDatabase.getInstance("https://majorprojectv2-65c20-default-rtdb.firebaseio.com/").getReference("usersdata");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.etEmailRegister);
        username = findViewById(R.id.etUsernameRegister);
        password = findViewById(R.id.etPasswordRegister);

        register = findViewById(R.id.buttonRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailArg = Objects.requireNonNull(email.getText()).toString();
                String passwordArg = Objects.requireNonNull(password.getText()).toString();
                String usernameArg = Objects.requireNonNull(username.getText()).toString();
                if (emailArg.length() >= 11) {
                    if (passwordArg.length() >= 8) {
                        if (usernameArg.length() >= 8) {
                            createUserAccount(emailArg, passwordArg,usernameArg);
                            uploadDetailsToFirebase(usernameArg,emailArg,passwordArg);
                        } else {
                            Toast.makeText(RegisterActivity.this, "username should be at least 8 chars", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        password.setError("password should be at least 8 chars");
                    }

                } else {
                    email.setError("Invalid Email");
                }
            }
        });


//        firebaseDatabase = FirebaseDatabase.getInstance("https://majorprojectv2-65c20-default-rtdb.firebaseio.com/");
//        databaseReference = firebaseDatabase.getReference("users");
//
//        DataHelperRegister dataHelperRegister =new DataHelperRegister(emailArg,passwordArg,usernameArg);
//        if(databaseReference.child(usernameArg).setValue(dataHelperRegister).isSuccessful()){
//            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(this, "Error in Realtime database", Toast.LENGTH_SHORT).show();
//        }


    }

    private void uploadDetailsToFirebase(String usernameArg, String emailArg, String passwordArg) {
            DataHelperRegister dataHelperRegister = new DataHelperRegister(emailArg,passwordArg,usernameArg);
            if(root.child(usernameArg).setValue(dataHelperRegister).isSuccessful()){
                Toast.makeText(getApplicationContext(), "Welcome!!", Toast.LENGTH_SHORT).show();
            }
//            else{
//                Toast.makeText(getApplicationContext(), "Error in saving details", Toast.LENGTH_SHORT).show();
//            }

    }

    private void createUserAccount(String emailArg, String passwordArg, String usernameArg) {

        mAuth.createUserWithEmailAndPassword(emailArg, passwordArg).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Account Created Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), PublisherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", usernameArg);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(RegisterActivity.this, "Error Logging In User", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {

    }
}