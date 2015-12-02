package com.cse110.clicker;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    TextView progress, timer;
    int size = 1, currentQuestion = 1;
    CountDownTimer ctimer;
    RadioGroup group;
    TextView questionView;
    RadioButton a1,a2,a3,a4,a5;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        ref = new Firebase("https://cse110clicker.firebaseio.com/");

        Intent i = getIntent();
        sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        group = (RadioGroup) findViewById(R.id.radioGroup);
        questionView = (TextView) findViewById(R.id.questionView);
        timer = (TextView)findViewById(R.id.timerView);
        submit = (Button)findViewById(R.id.submitAnswer);
        progress = (TextView) findViewById(R.id.progressText);
        loadSession();
    }
    public void loadSession() {

        Firebase session = ref.child("questions").child(sessionID);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    size = (int)dataSnapshot.getChildrenCount();
                updateProgress();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        session = ref.child("sessions").child(sessionID);

        session.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child("currentQuestion").exists()){
                    currentQuestion = (int)((long)snapshot.child("currentQuestion").getValue());
                    updateProgress();
                    loadQuestion();
                }
                if(snapshot.child("timer").exists()){
                    if(ctimer!=null)
                        ctimer.cancel();
                    startTimer((long)snapshot.child("timer").getValue());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
    public void loadQuestion(){
        Firebase session = ref.child("questions").child(sessionID).child("question"+currentQuestion);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    //add after session stuff
                    Log.d("a", snapshot.getValue().toString());
                     a1 = (RadioButton)findViewById(R.id.answerButton1);
                     a2 = (RadioButton)findViewById(R.id.answerButton2);
                     a3 = (RadioButton)findViewById(R.id.answerButton3);
                     a4 = (RadioButton)findViewById(R.id.answerButton4);
                     a5 = (RadioButton)findViewById(R.id.answerButton5);
                    questionView.setText(snapshot.child("q").getValue().toString());
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

    public void startTimer (long milliseconds){
        questionView.setVisibility(View.VISIBLE);
        group.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
        ctimer = new CountDownTimer(milliseconds-System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {
                updateTimer(millisUntilFinished/1000);
            }

            public void onFinish() {
                emptyTimer();
                submitAns(null);
            }
        }.start();
    }

    public void updateTimer(long seconds){
        if(seconds<0) {
            seconds = 0;
        }
        timer.setText(String.format("%d:%02d Left", seconds / 60, seconds % 60));
    }
    public void emptyTimer(){
        questionView.setVisibility(View.INVISIBLE);
        group.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);
        timer.setText("Waiting for a question to be selected...");
    }
    public void submitAns(View view){
        int answer = 0;
        for(int i = 0; i < group.getChildCount();i++) {
            RadioButton button = (RadioButton)group.getChildAt(i);
            if(button.getId() == group.getCheckedRadioButtonId()){
                answer = i+1;
            }
        }
        final int ans = answer;
        final Firebase answers = ref.child("answers").child(sessionID);
        answers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                answers.child("question"+currentQuestion).child("a"+ans).child(ref.getAuth().getUid()).setValue(0);
                answers.child("users").child(ref.getAuth().getUid()).child("question"+currentQuestion).setValue(ans);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void updateProgress(){
        progress.setText("Question "+currentQuestion+" of "+size);
    }
}
