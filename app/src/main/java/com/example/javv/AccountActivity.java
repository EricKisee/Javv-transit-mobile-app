package com.example.javv;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.javv.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "ACCOUNT_ACTIVITY";
    private static final String REQUIED = "Required";
    private DatabaseReference mDatabase;
    private  TextView textViewName,textViewEmail,textViewLocation,textViewGender,textViewPassword ,
            textViewMode;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            // Read from the database
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.w(TAG, "reading user data...");
                    user = dataSnapshot.getValue(User.class);
                    populate();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
            init();
        }else{
            finish();
            startActivity(new Intent(AccountActivity.this, LandingActivity.class));
        }

    }

    private void populate (){

        textViewName.setText(user.username);
        textViewEmail.setText(user.email);
        textViewGender.setText(user.gender);
        textViewLocation.setText(user.location);
        textViewMode.setText(user.mode);
    }

    private void init(){
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewGender = findViewById(R.id.textViewGender);
        textViewPassword = findViewById(R.id.textViewPassword);
        textViewMode = findViewById(R.id.textViewMode);


        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNameDialog();
            }
        });

        textViewGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditGenderDialog();
            }
        });

        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditLocationDialog();
            }
        });

        textViewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        textViewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditModeDialog();
            }
        });

    }

    private void showChangePasswordDialog (){
        final Dialog dialog = new Dialog(AccountActivity.this);
        dialog.setContentView(R.layout.dialog_change_password);
        final EditText editTextPassword = dialog.findViewById(R.id.editTextPassword);
        final EditText editTextNewPassword = dialog.findViewById(R.id.editTextNewPassword);
        Button buttonOk = (Button) dialog.findViewById(R.id.dialogButtonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialog.show();
        // show current values

        //Onclick
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                final String newPassword = editTextNewPassword.getText().toString();

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                // Get auth credentials from the user for re-authentication. The example below shows
                // email and password credentials but there are multiple possible providers,
                // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(firebaseUser.getEmail(), password);

                // Prompt the user to re-provide their sign-in credentials
                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Password updated");
                                                Toast.makeText(AccountActivity.this, "Password successfully changed.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(AccountActivity.this, "Unable to change password. Try again later.", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "Error password not updated");
                                            }
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "Error auth failed");
                                }
                            }
                        });
                dialog.dismiss();

            }
        });

    }

    private void showEditLocationDialog(){
        final Dialog dialog = new Dialog(AccountActivity.this);
        dialog.setContentView(R.layout.dialog_edit_location);
        final RadioGroup radioGroup = dialog.findViewById(R.id.radioLocation);
        Button buttonOk = (Button) dialog.findViewById(R.id.dialogButtonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialog.show();
        // show current values

        //Onclick
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = radioGroup.getCheckedRadioButtonId();
                switch (selected){
                    case R.id.radioButtonOn:
                        user.location="ON";
                        break;
                    case R.id.radioButtonOff:
                        user.location="OFF";
                        break;
                    default:
                        user.location="OFF";
                        break;
                }
                updateUser();
                dialog.dismiss();
            }
        });
    }

    private void showEditModeDialog(){
        final Dialog dialog = new Dialog(AccountActivity.this);
        dialog.setContentView(R.layout.dialog_edit_mode);
        final RadioGroup radioGroup = dialog.findViewById(R.id.radioMode);
        Button buttonOk = (Button) dialog.findViewById(R.id.dialogButtonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialog.show();
        // show current values

        //Onclick
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = radioGroup.getCheckedRadioButtonId();
                switch (selected){
                    case R.id.radioButtonPassenger:
                        user.mode="passenger";
                        break;
                    case R.id.radioButtonMotorist:
                        user.mode="motorist";
                        break;
                    default:
                        user.mode="passenger";
                        break;
                }
                updateUser();
                dialog.dismiss();
            }
        });
    }

    private void showEditGenderDialog (){
        final Dialog dialog = new Dialog(AccountActivity.this);
        dialog.setContentView(R.layout.dialog_edit_gender);
        final RadioGroup radioGroup = dialog.findViewById(R.id.radioGender);
        Button buttonOk = (Button) dialog.findViewById(R.id.dialogButtonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialog.show();
        // show current values

        //Onclick
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = radioGroup.getCheckedRadioButtonId();
                switch (selected){
                    case R.id.radioButtonFemale:
                        user.gender="FEMALE";
                        break;
                    case R.id.radioButtonMale:
                        user.gender="MALE";
                        break;
                    case R.id.radioButtonNeutral:
                        user.gender="Neutral";
                        break;
                    default:
                        user.gender="Not Set";
                        break;
                }
                updateUser();
                dialog.dismiss();
            }
        });
    }

    private void showEditNameDialog() {

        final Dialog dialog = new Dialog(AccountActivity.this);
        dialog.setContentView(R.layout.dialog_edit_name);
        final EditText editTextName = (EditText) dialog.findViewById(R.id.editTextName);
        Button buttonOk = (Button) dialog.findViewById(R.id.dialogButtonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialog.show();
        editTextName.setText(user.username);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                if (name.isEmpty()){
                    editTextName.setError("Required");
                } else {
                    user.username = name;
                    updateUser();
                    dialog.dismiss();
                }
            }
        });

    }

    private void updateUser(){
        mDatabase.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                    }
                });
    }

}
