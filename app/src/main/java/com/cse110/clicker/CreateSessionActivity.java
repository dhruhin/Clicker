package com.cse110.clicker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CreateSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        Intent i = getIntent();
        String sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        TextView sessionView = (TextView) findViewById(R.id.sessionIDView);
        sessionView.setText(sessionID);
    }
}
