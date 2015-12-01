package com.cse110.clicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        ref = new Firebase("https://cse110clicker.firebaseio.com/");
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
                    loadQuestion(size);
                } else {
                    //doesn't exist
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void loadQuestion(final int i){
        Firebase session = ref.child("questions").child(sessionID).child("question"+i);
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
                    currentQuestion = i;
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
    }
    public void newQuestion(View view) {
        saveQuestion(true);
    }
    public void doneEdit(View view){
        saveQuestion(false);
    }
    public void clearEditTexts(){
        q.getEditableText().clear();
        a1.getEditableText().clear();
        a2.getEditableText().clear();
        a3.getEditableText().clear();
        a4.getEditableText().clear();
        a5.getEditableText().clear();
    }
    public void saveQuestion(final boolean clear){
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
                if(clear){
                    clearEditTexts();
                    currentQuestion++;
                    size++;
                    updateProgress();
                }else{

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
