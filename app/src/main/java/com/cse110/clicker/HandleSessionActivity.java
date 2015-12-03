package com.cse110.clicker;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.Console;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class HandleSessionActivity extends AppCompatActivity {
    String sessionID;
    Firebase ref;
    LinearLayout group;
    TextView progress, timer;
    int size = 1, currentQuestion = 1, highlight = 0;
    boolean isRunning = false;
    CountDownTimer ctimer;
    ColorStateList oldColors;
    GraphView graph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_session);

        ref = new Firebase(getResources().getString(R.string.firebase));

        Intent i = getIntent();
        sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        group = (LinearLayout) findViewById(R.id.answersView);
        progress = (TextView) findViewById(R.id.progressText);
        timer = (TextView) findViewById(R.id.timerView);
        oldColors =  ((TextView) findViewById(R.id.answerView1)).getTextColors();
        graph = (GraphView) findViewById(R.id.graph);
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
                    size = (int)snapshot.getChildrenCount();
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
                    TextView a1 = (TextView) findViewById(R.id.answerView1);
                    TextView a2 = (TextView) findViewById(R.id.answerView2);
                    TextView a3 = (TextView) findViewById(R.id.answerView3);
                    TextView a4 = (TextView) findViewById(R.id.answerView4);
                    TextView a5 = (TextView) findViewById(R.id.answerView5);
                    TextView q = (TextView)findViewById(R.id.questionView);
                    q.setText(snapshot.child("q").getValue().toString());
                    a1.setText(snapshot.child("a1").getValue().toString());
                    a2.setText(snapshot.child("a2").getValue().toString());
                    a3.setText(snapshot.child("a3").getValue().toString());
                    a4.setText(snapshot.child("a4").getValue().toString());
                    a5.setText(snapshot.child("a5").getValue().toString());
                    ((TextView)(group.getChildAt(highlight))).setTextColor(oldColors);
                    highlight = Integer.parseInt(snapshot.child("a").getValue().toString())-1;
                    ((TextView)(group.getChildAt(highlight))).setTextColor(Color.GREEN);
                    if(graph.getVisibility()==View.VISIBLE){
                        group.setVisibility(View.VISIBLE);
                        graph.setVisibility(View.GONE);
                    }
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

        if(currentQuestion==size){
            Button next = (Button) findViewById(R.id.nextButton);
            next.setVisibility(View.INVISIBLE);
        }else{
            Button next = (Button) findViewById(R.id.nextButton);
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
    public void goLeft (View view){
        if(isRunning)
            return;
        if(currentQuestion>1) {
            currentQuestion--;
            loadQuestion();
        }
    }
    public void goRight(View view){
        if(isRunning)
            return;
        if(currentQuestion<size) {
            currentQuestion++;
            loadQuestion();
        }
    }
    public void startTimer (long milliseconds){
        ctimer = new CountDownTimer(milliseconds-System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {
                updateTimer(millisUntilFinished/1000);
                isRunning = true;
            }

            public void onFinish() {
                emptyTimer();
            }
        }.start();
    }
    public void emptyTimer(){
        final Firebase session = ref.child("sessions").child(sessionID).child("timer");
        session.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                updateTimer(0);
                isRunning = false;
            }
        });
        ((Button) findViewById(R.id.timerButton)).setText("Start");
        ((Button) findViewById(R.id.extendButton)).setVisibility(View.GONE);
        openGraph();
    }
    public void updateTimer(long seconds){
        if(seconds<0) {
            seconds = 0;
        }
        timer.setText(String.format("%d:%02d Left", seconds / 60, seconds % 60));
    }
    public void startTimer(View view){
        if(isRunning){
            ctimer.cancel();
            emptyTimer();
            return;
        }
        if(graph.getVisibility()==View.VISIBLE){
            group.setVisibility(View.VISIBLE);
            graph.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.timerButton)).setText("Stop");
        ((Button) findViewById(R.id.extendButton)).setVisibility(View.VISIBLE);
        final Firebase session = ref.child("sessions").child(sessionID);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                session.child("currentQuestion").setValue(currentQuestion);
                final long val = System.currentTimeMillis() + 60*1000;
                session.child("timer").setValue(val, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        //update timer
                        startTimer(val);
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void extendTimer(View view){
        final Firebase session = ref.child("sessions").child(sessionID).child("timer");
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                final long val = (long)snapshot.getValue() + 15*1000;
                session.setValue(val, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        //update timer
                        ctimer.cancel();
                        startTimer(val);
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void openGraph(){
        Firebase session = ref.child("answers").child(sessionID).child("question"+currentQuestion);
        session.addListenerForSingleValueEvent(new ValueEventListener() {
            int a1 = 0, a2 = 0, a3 = 0, a4 = 0, a5 = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("a1").exists()){
                    a1 = (int)dataSnapshot.child("a1").getChildrenCount();
                }
                if(dataSnapshot.child("a2").exists()){
                    a2 = (int)dataSnapshot.child("a2").getChildrenCount();
                }
                if(dataSnapshot.child("a3").exists()){
                    a3 = (int)dataSnapshot.child("a3").getChildrenCount();
                }
                if(dataSnapshot.child("a4").exists()){
                    a4 = (int)dataSnapshot.child("a4").getChildrenCount();
                }
                if(dataSnapshot.child("a5").exists()){
                    a5 = (int)dataSnapshot.child("a5").getChildrenCount();
                }
                setupGraph(a1, a2, a3, a4, a5);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void loadGraph(List<int[]> data) {

        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(data.get(0)[0], data.get(0)[1]),
                new DataPoint(data.get(1)[0], data.get(1)[1]),
                new DataPoint(data.get(2)[0], data.get(2)[1]),
                new DataPoint(data.get(3)[0], data.get(3)[1]),
                new DataPoint(data.get(4)[0], data.get(4)[1]),
                new DataPoint(data.get(5)[0], data.get(5)[1]),
                new DataPoint(data.get(6)[0], data.get(6)[1])
        });
        graph.addSeries(series);
        series.setSpacing(20);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(6);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "A", "B", "C", "D", "E", ""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.valueOf("NONE"));
    }
    public void setupGraph(int a1, int a2, int a3, int a4, int a5){
        group.setVisibility(View.GONE);
        graph.setVisibility(View.VISIBLE);
        List<int[]> data = new ArrayList<int[]>();
        data.add(new int[]{0,0});

        // second element from a-e refers to value to be loaded from total from database
        data.add(new int[]{1,a1}); //a
        data.add(new int[]{2,a2}); //b
        data.add(new int[]{3,a3}); //c
        data.add(new int[]{4,a4}); //d
        data.add(new int[]{5,a5}); //e
        // end of a-e

        data.add(new int[]{6,0});
        graph.removeAllSeries();
        loadGraph(data);
    }
}
