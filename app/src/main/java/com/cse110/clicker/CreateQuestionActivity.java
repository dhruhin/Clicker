package com.cse110.clicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class CreateQuestionActivity extends AppCompatActivity {
    String sessionID;
    RadioGroup group;
    Firebase ref;
    int size, currentQuestion = 1;

    EditText a1, a2, a3, a4, a5, q;
    TextView progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        Intent i = getIntent();
        sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        TextView sessionView = (TextView) findViewById(R.id.progressText);
        sessionView.setText(sessionID);

        group = (RadioGroup) findViewById(R.id.radioGroup);

        ref = new Firebase(getResources().getString(R.string.firebase));
        loadSession();

        a1 = (EditText)findViewById(R.id.editText1);
        a2 = (EditText)findViewById(R.id.editText2);
        a3 = (EditText)findViewById(R.id.editText3);
        a4 = (EditText)findViewById(R.id.editText4);
        a5 = (EditText)findViewById(R.id.editText5);
        q = (EditText)findViewById(R.id.editQuestion);
        progress = (TextView) findViewById(R.id.progressText);
    }
    public void loadSession() {
        Firebase session = ref.child("questions").child(sessionID);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.exists()) {
                    //add after session stuff
                    size = (int) snapshot.getChildrenCount();
                    currentQuestion = size;
                    loadQuestion();
                } else {
                    //doesn't exist
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

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
                    q.setText(snapshot.child("q").getValue().toString());
                    a1.setText(snapshot.child("a1").getValue().toString());
                    a2.setText(snapshot.child("a2").getValue().toString());
                    a3.setText(snapshot.child("a3").getValue().toString());
                    a4.setText(snapshot.child("a4").getValue().toString());
                    a5.setText(snapshot.child("a5").getValue().toString());
                    group.check(group.getChildAt(Integer.parseInt(snapshot.child("a").getValue().toString())-1).getId());
                    updateProgress();
                } else {
                    //doesn't exist
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void updateProgress(){
        progress.setText("Question "+currentQuestion+" of "+size);
        if(currentQuestion==1&&size==1){
            Button next = (Button) findViewById(R.id.nextButton);
            next.setVisibility(View.INVISIBLE);
        }else if(currentQuestion==size){
            Button next = (Button) findViewById(R.id.nextButton);
            next.setText("x");
            next.setVisibility(View.VISIBLE);
        }else{
            Button next = (Button) findViewById(R.id.nextButton);
            next.setText("→");
            next.setVisibility(View.VISIBLE);
        }
        if(currentQuestion==1){
            Button previous = (Button) findViewById(R.id.previousButton);
            previous.setVisibility(View.INVISIBLE);
        }else{
            Button previous = (Button) findViewById(R.id.previousButton);
            previous.setVisibility(View.VISIBLE);
        }
    }
    public void newQuestion(View view) {
        saveQuestion(true, false);
    }
    public void doneEdit(View view){
        saveQuestion(false, true);
    }
    public void clearEditTexts(){
        q.getEditableText().clear();
        a1.getEditableText().clear();
        a2.getEditableText().clear();
        a3.getEditableText().clear();
        a4.getEditableText().clear();
        a5.getEditableText().clear();
    }
    public void saveQuestion(final boolean nextQuestion, final boolean end){
        int answer = 0;
        for(int i = 0; i < group.getChildCount();i++) {
            RadioButton button = (RadioButton)group.getChildAt(i);
            if(button.getId() == group.getCheckedRadioButtonId()){
                answer = i+1;
            }
        }
        final int ans = answer;
        final Firebase session = ref.child("questions").child(sessionID).child("question"+currentQuestion);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                session.child("q").setValue(q.getText().toString());
                session.child("a").setValue(ans);
                session.child("a1").setValue(a1.getText().toString());
                session.child("a2").setValue(a2.getText().toString());
                session.child("a3").setValue(a3.getText().toString());
                session.child("a4").setValue(a4.getText().toString());
                session.child("a5").setValue(a5.getText().toString());
                if(nextQuestion){
                    clearEditTexts();
                    currentQuestion++;
                    size++;
                    updateProgress();
                    saveQuestion(false, false);
                }
                if(end){
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void goLeft (View view){
        if(currentQuestion>1) {
            currentQuestion--;
            loadQuestion();
        }
    }
    public void goRight(View view){
        if(currentQuestion<size) {
            currentQuestion++;
            loadQuestion();
        }else if(currentQuestion==size&&size!=1){
            deleteQuestion();
        }
    }
    public void deleteQuestion(){
        Firebase question = ref.child("questions").child(sessionID).child("question"+currentQuestion);
        question.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                currentQuestion--;
                size--;
                loadQuestion();
            }
        });
    }
}
