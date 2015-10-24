package com.cse110.clicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.Button;

import android.view.View;
import android.view.View.OnClickListener;

import com.firebase.client.Firebase;

public class LoginActivity extends AppCompatActivity {
    private EditText first, last, email, password, confirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);


        setContentView(R.layout.activity_login);
        first = (EditText) findViewById(R.id.editFirst);
        last = (EditText) findViewById(R.id.editLast);
        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPassword);
        confirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        Button register = (Button)findViewById(R.id.registerButton);
        register.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                attemptLogin();
            }
        });
    }
    private void attemptLogin(){
        Log.d("a", "b");
        Firebase myFirebaseRef = new Firebase("https://cse110clicker.firebaseio.com/");

        if(!password.getText().getString().equals(confirmPassword.getText().getString())){
            return;
        }
        String semail = email.getText().getString();
        String spassword = password.getText().getString();
        String sfirst = first.getText().getString();
        String slast = last.getText().getString();
        /*
        myFirebaseRef.createUser("bobtony@firebase.com", "correcthorsebatterystaple", new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
            }
        });
        */
    }
}
