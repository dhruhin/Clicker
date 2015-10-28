package com.cse110.clicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText first, last, email, password, confirmPassword, schoolID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        first = (EditText) findViewById(R.id.editFirst);
        last = (EditText) findViewById(R.id.editLast);
        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPassword);
        confirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        schoolID = (EditText) findViewById(R.id.editID);
    }
    public void register(View view){
        final String semail = email.getText().toString();
        final String spassword = password.getText().toString();
        final String scpassword = confirmPassword.getText().toString();
        final String sfirst = first.getText().toString();
        final String slast = last.getText().toString();
        final String sid = schoolID.getText().toString();
        if(semail.isEmpty() || spassword.isEmpty() || sfirst.isEmpty() || slast.isEmpty()){
            alertError("Not all required fields are filled out.");
            return;
        }
        if(!spassword.equals(scpassword)){
            alertError("Passwords don't match.");
            return;
        }

        final Firebase ref = new Firebase("https://cse110clicker.firebaseio.com");
        ref.createUser(semail, spassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                //stored
                ref.authWithPassword(semail, spassword, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Firebase user = ref.child("users").child(authData.getUid());
                        user.child("first").setValue(sfirst);
                        user.child("last").setValue(slast);
                        user.child("id").setValue(sid);
                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // there was an error
                        alertError("Unable to Login, try again");
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
                alertError("Registration Error!");
            }
        });
    }

    public void alertError(String message){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing when clicked
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
