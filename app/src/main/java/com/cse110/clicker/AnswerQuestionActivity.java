package com.cse110.clicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.Console;
import java.util.Dictionary;

public class AnswerQuestionActivity extends AppCompatActivity {
    String sessionID;
    Firebase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        ref = new Firebase("https://cse110clicker.firebaseio.com/");

        Intent i = getIntent();
        sessionID = i.getStringExtra(getResources().getString(R.string.session_id));

        loadSession();
    }
    public void loadSession() {
        Firebase session = ref.child("questions").child(sessionID);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    //add after session stuff
                    loadQuestion(1);
                } else {
                    //doesn't exist
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void loadQuestion(int i){
        Firebase session = ref.child("questions").child(sessionID).child("question"+i);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    //add after session stuff
                    Log.d("a", snapshot.getValue().toString());
                    RadioButton a1 = (RadioButton)findViewById(R.id.answerButton1);
                    RadioButton a2 = (RadioButton)findViewById(R.id.answerButton2);
                    RadioButton a3 = (RadioButton)findViewById(R.id.answerButton3);
                    RadioButton a4 = (RadioButton)findViewById(R.id.answerButton4);
                    RadioButton a5 = (RadioButton)findViewById(R.id.answerButton5);
                    TextView q = (TextView)findViewById(R.id.questionView);
                    q.setText(snapshot.child("q").getValue().toString());
                    a1.setText(snapshot.child("a1").getValue().toString());
                    a2.setText(snapshot.child("a2").getValue().toString());
                    a3.setText(snapshot.child("a3").getValue().toString());
                    a4.setText(snapshot.child("a4").getValue().toString());
                    a5.setText(snapshot.child("a5").getValue().toString());

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
