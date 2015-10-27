package com.cse110.clicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.Button;

import android.content.Intent;

import android.view.View;
import android.view.View.OnClickListener;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
    }
    public void login(View view){
        Firebase ref = new Firebase("https://cse110clicker.firebaseio.com/");
        ref.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.e("d","User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                loginError();
            }
        });
    }
    public void goToRegister(View view){
        //load Registration view
        startActivity(new Intent(this, RegisterActivity.class));
    }
    public void loginError(){
        new AlertDialog.Builder(this)
                .setTitle("Login Error")
                .setMessage("Username/password not found")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing when clicked
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
