package com.cse110.clicker;

import android.content.Context;
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

public class CreateSessionActivity extends AppCompatActivity {
    String sessionID;
    RadioGroup group;
    Firebase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        Intent i = getIntent();
        sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        TextView sessionView = (TextView) findViewById(R.id.sessionIDView);
        sessionView.setText(sessionID);

        group = (RadioGroup) findViewById(R.id.radioGroup);

        ref = new Firebase("https://cse110clicker.firebaseio.com/");
    }

    public void saveQuestion(View view){
        int answer = 0;
        for(int i = 0; i < group.getChildCount();i++) {
            RadioButton button = (RadioButton)group.getChildAt(i);
            if(button.getId() == group.getCheckedRadioButtonId()){
                answer = i+1;
            }
        }
        final int ans = answer;
        final int sessionID = (int)(Math.random()*99999)+1;
        final Firebase session = ref.child("questions").child(Integer.toString(sessionID)).child("question1");
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                EditText q = ((EditText)findViewById(R.id.editQuestion));
                EditText a1 = ((EditText)findViewById(R.id.editText1));
                EditText a2 = ((EditText)findViewById(R.id.editText2));
                EditText a3 = ((EditText)findViewById(R.id.editText3));
                EditText a4 = ((EditText)findViewById(R.id.editText4));
                EditText a5 = ((EditText)findViewById(R.id.editText5));
                session.child("q").setValue(q.getText().toString());
                session.child("a").setValue(ans);
                session.child("a1").setValue(a1.getText().toString());
                session.child("a2").setValue(a2.getText().toString());
                session.child("a3").setValue(a3.getText().toString());
                session.child("a4").setValue(a4.getText().toString());
                session.child("a5").setValue(a5.getText().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
