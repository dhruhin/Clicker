package com.cse110.clicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CreateSessionActivity extends AppCompatActivity {
    String sessionID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        Intent i = getIntent();
        sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        Log.d("a",sessionID);

        TextView session = (TextView) findViewById(R.id.sessionView);
        session.setText("Session ID: "+sessionID);
    }
    public void startSession(View view) {
        Intent i = new Intent(this, HandleSessionActivity.class);
        i.putExtra(getResources().getString(R.string.session_id), sessionID);
        startActivity(i);
    }
    public void editQuestions(View view){
        Intent i = new Intent(this, CreateQuestionActivity.class);
        i.putExtra(getResources().getString(R.string.session_id), sessionID);
        startActivity(i);
    }
}
