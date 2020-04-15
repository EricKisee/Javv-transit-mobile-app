package com.example.javv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.javv.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

//    Declaring global variables

    private FirebaseAuth mAuth;
    private String TAG = "SIGN_UP_ACTIVITY";
    private EditText editTextName,editTextEmail,editTextPassword,editTextPassword2;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//         Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onStart() {
        super.onStart();
//         Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
        else
            init();
    }

    private void init(){
//        Initializing the view
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword2 = findViewById(R.id.editTextPassword2);
        progressBar = findViewById(R.id.progressBar);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String password2 = editTextPassword2.getText().toString();

//                checking for errors in the input fields
                if(name.isEmpty()){
                    editTextName.setError("required");
                    progressBar.setVisibility(View.GONE);
                }else if(email.isEmpty()){
                    editTextEmail.setError("required");
                    progressBar.setVisibility(View.GONE);
                }else if (password.isEmpty()){
                    editTextPassword.setError("required");
                    progressBar.setVisibility(View.GONE);
                }else if (password2.isEmpty()){
                    editTextPassword2.setError("required");
                    progressBar.setVisibility(View.GONE);
                }else if (!password.equalsIgnoreCase(password2)){
                    editTextPassword2.setError("passwords don't match");
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    signUp(name,email,password);
                }


            }
        });
    }

    private void signUp(final String fullName , final String email , String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user.getUid(),fullName,email,"...","off" , "passenger" , 0,0);
                            Toast.makeText(SignUpActivity.this, "Signup Successfull", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(SignUpActivity.this, AccountActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Signup failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void writeNewUser(String userId, String name, String email, String gender, String location , String mode , double lat, double lng) {
//        write a new record in the firebase database.
        User user = new User(name, email, gender , location , mode , lat, lng);
        mDatabase.child("users").child(userId).setValue(user);
    }
}
