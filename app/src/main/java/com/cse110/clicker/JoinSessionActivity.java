package com.cse110.clicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class JoinSessionActivity extends AppCompatActivity {
    Firebase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_session);
        Intent i = getIntent();
        String sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        TextView sessionView = (TextView) findViewById(R.id.sessionIDView);
        sessionView.setText(sessionID);

        ref = new Firebase("https://cse110clicker.firebaseio.com/");
        loadSession(sessionID);
    }
    private void loadSession(final String sessionID){
        ref.child("sessions").child(sessionID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    String user = snapshot.child("createdBy").getValue().toString();
                    loadUser(user);
                } else {
                    //doesn't exist
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    private void loadUser(final String user){
        ref.child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    TextView creatorView = (TextView) findViewById(R.id.creator);
                    String name = "Created by " + snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                    creatorView.setText(name);
                } else {
                    //doesn't exist
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
