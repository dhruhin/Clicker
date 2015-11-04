package com.cse110.clicker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ChildEventListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class DashboardActivity extends AppCompatActivity {
    Firebase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Firebase.setAndroidContext(this);
        ref = new Firebase("https://cse110clicker.firebaseio.com/");
        AuthData authData = ref.getAuth();
        if (authData == null) {
            // no user authenticated go to login page
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return;
        }

        ref.child("users").child(authData.getUid()).child("first").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    TextView name = (TextView) findViewById(R.id.welcomeView);
                    name.setText("Welcome " + snapshot.getValue() + "!");

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void createSession(View view){
        //generate random up to 5 digit number and store as key (string) in server and add timestamp
        createSessionID();
    }

    public void createSessionID(){
        final int sessionID = (int)(Math.random()*99999)+1;
        ref.child("sessions").child(Integer.toString(sessionID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    createSessionID();
                } else {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    Firebase session = ref.child("sessions").child(Integer.toString(sessionID));
                    session.child("timestamp").setValue(ts);
                    session.child("createdBy").setValue(ref.getAuth().getUid());

                    Intent i = new Intent(DashboardActivity.this, CreateSessionActivity.class);
                    i.putExtra(getResources().getString(R.string.session_id), Integer.toString(sessionID));
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



    public void joinSession(View view){
        final EditText input = (EditText) findViewById(R.id.inputID);
        input.setVisibility(View.VISIBLE);

        input.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    connectSession(input.getText().toString());
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
    public void connectSession(final String input){
        //check if it exists, then join host
        ref.child("sessions").child(input).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    Intent i = new Intent(DashboardActivity.this, JoinSessionActivity.class);
                    i.putExtra(getResources().getString(R.string.session_id), input);
                    startActivity(i);
                } else {
                    //doesn't exist
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void logUserOut(View view){
        ref.unauth();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
