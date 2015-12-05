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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ChildEventListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import java.util.ArrayList;


public class DashboardActivity extends AppCompatActivity {
    Firebase ref;
    ArrayList<String> sessionList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ref = new Firebase(getResources().getString(R.string.firebase));
        AuthData authData = ref.getAuth();
        if(authData == null){
            finish();
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

        Button anchor = (Button) findViewById(R.id.manageSessions);

        final DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(this, anchor);

        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                Intent i = new Intent(DashboardActivity.this, CreateSessionActivity.class);
                i.putExtra(getResources().getString(R.string.session_id), sessionList.get(id));
                startActivity(i);
            }
        });

        ref.child("users").child(authData.getUid()).child("sessions").orderByValue().limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                droppyBuilder.addMenuItem(new DroppyMenuItem(dataSnapshot.getKey()));
                droppyBuilder.build();
                sessionList.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
    public int randomSessionID(){
        return (int)(Math.random()*99999)+1;
    }
    public void createSessionID(){
        final int sessionID = randomSessionID();
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

                    Firebase user = ref.child("users").child(ref.getAuth().getUid()).child("sessions");
                    user.child(Integer.toString(sessionID)).setValue(ts);

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
                    Intent i = new Intent(DashboardActivity.this, AnswerQuestionActivity.class);
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
