package com.cse110.clicker;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateSessionActivity extends AppCompatActivity {
    String sessionID;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        Intent i = getIntent();
        sessionID = i.getStringExtra(getResources().getString(R.string.session_id));
        Log.d("a", sessionID);

        TextView session = (TextView) findViewById(R.id.sessionView);
        session.setText("Session ID: " + sessionID);

        loadButtons();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, getIntent());
        loadButtons();
    }

    public void loadButtons() {
        ref = new Firebase(getResources().getString(R.string.firebase));
        final Button session = (Button)findViewById(R.id.sessionButton);
        final Button export = (Button)findViewById(R.id.exportButton);
        ref.child("questions").child(sessionID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ((Button)findViewById(R.id.edit)).setText("Edit Questions");
                    session.setVisibility(View.VISIBLE);
                }else
                    ((Button)findViewById(R.id.edit)).setText("Add Questions");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        ref.child("answers").child(sessionID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    export.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void startSession(View view) {
        Intent i = new Intent(this, HandleSessionActivity.class);
        i.putExtra(getResources().getString(R.string.session_id), sessionID);
        startActivityForResult(i, 1);
    }

    public void editQuestions(View view) {
        Intent i = new Intent(this, CreateQuestionActivity.class);
        i.putExtra(getResources().getString(R.string.session_id), sessionID);
        startActivityForResult(i, 1);
    }

    public void exportData(View view) {
        Firebase questions = ref.child("questions").child(sessionID);
        questions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int num = (int)dataSnapshot.getChildrenCount();
                    findCorrectAnswers(num);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void findCorrectAnswers(final int num){
        Firebase questions = ref.child("questions").child(sessionID);
        questions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int i = 0;
                    int[] correctAnswers = new int[num];
                    String[] questions = new String[num];
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        correctAnswers[i] = (int)((long)postSnapshot.child("a").getValue());
                        questions[i] = (String) postSnapshot.child("q").getValue();
                        i++;
                    }
                    findStudentAnswers(num, correctAnswers, questions);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void findStudentAnswers(final int num, final int[] correctAnswers, final String[] questions){
        Firebase users = ref.child("answers").child(sessionID).child("users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<int[]> data = new ArrayList<int[]>();
                List<String> uids = new ArrayList<String>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String uid = postSnapshot.getKey();
                    uids.add(uid);
                    int[] responses = new int[num];
                    for(int j = 0; j < num; j++){
                        if(postSnapshot.child("question"+(j+1)).exists())
                            responses[j] = (int)((long)postSnapshot.child("question"+(j+1)).getValue());
                        else
                            responses[j] = 0;
                    }
                    data.add(responses);
                }
                parseUIDS(uids, data, correctAnswers, questions, num);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void parseUIDS(final List<String> users, final List<int[]> responses, final int[]correctAnswers, final String[] questions, final int num){

        Firebase user = ref.child("users");
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> firsts = new ArrayList<String>();
                List<String> lasts = new ArrayList<String>();
                List<String> ids = new ArrayList<String>();
                for(String s: users){
                    firsts.add((String)dataSnapshot.child(s).child("first").getValue());
                    lasts.add((String)dataSnapshot.child(s).child("last").getValue());
                    ids.add((String)dataSnapshot.child(s).child("id").getValue());
                }
                export(firsts, lasts, ids, responses, correctAnswers, questions, num);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void export(List<String>firsts, List<String>lasts, List<String>ids, List<int[]> responses, int[] correctAnswers, String[] questions, int num) {
        Context context = getApplicationContext();
        CharSequence text = "Exporting...";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        try {
        /*    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;

            requestPermissions(perms, permsRequestCode); */
            //boolean check = Context#checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                    PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                    PackageManager.PERMISSION_GRANTED) {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "data.csv");
                file.createNewFile();
                CSVWriter writer = new CSVWriter(new FileWriter(file));

                List<String[]> data = new ArrayList<String[]>();
                String[] topRow = new String[questions.length+4];
                topRow[0] = "First Name";
                topRow[1] = "Last Name";
                topRow[2] = "Student ID";
                topRow[topRow.length-1] = "% Correct";
                for(int i = 0; i < questions.length; i++)
                    topRow[i+3] = questions[i];
                data.add(topRow);
                // each add is basically a row, so can increment accordingly
                String[] answerRow = new String[correctAnswers.length+4];
                answerRow[0] = "*Answer";
                answerRow[1] = "*Key";
                answerRow[2] = "*0";
                for(int i = 0; i < correctAnswers.length; i++)
                    answerRow[i+3] = convertIntToChar(correctAnswers[i])+"";
                answerRow[answerRow.length-1] = "100.00";
                data.add(answerRow); //load answer key
                //start getting values from students and adding rows here
                for(int i = 0; i < firsts.size(); i++){
                    String[] results = new String[correctAnswers.length+4];
                    results[0]=firsts.get(i);
                    results[1]=lasts.get(i);
                    results[2]=ids.get(i);
                    int[] anss = responses.get(i);
                    for(int j = 0; j < anss.length; j++){
                        results[j+3] = convertIntToChar(anss[j])+"";
                    }
                    results[correctAnswers.length+3] = String.format("%.2f",returnPercentage(correctAnswers, anss));
                    data.add(results);
                }

                writer.writeAll(data);
                writer.close();
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.addCompletedDownload("data.csv", "Results from Session", true, "text/csv",
                        file.getPath(), file.length(), true);


                CharSequence saveText = "Saved";
                Toast saveToast = Toast.makeText(context, saveText, duration);
                saveToast.show();
            }

          /*  Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent); */
        } catch (IOException e) {
            //error
            CharSequence errText = "Error Exporting";
            Toast errToast = Toast.makeText(context, errText, duration);
            errToast.show();
            e.printStackTrace();
        }
    }
    public float returnPercentage(int[] correctAnswers, int[] studentResponses){
        int correct = 0;

        if(correctAnswers.length != studentResponses.length || correctAnswers.length <= 0)
            return 0.0f;
        for(int i = 0; i < correctAnswers.length; i++)
            if(correctAnswers[i]==studentResponses[i])
                correct++;
        return 100*((float)correct)/((float)correctAnswers.length);
    }
    public char convertIntToChar(int i){
        if(i==1)
            return 'A';
        else if(i==2)
            return 'B';
        else if(i==3)
            return 'C';
        else if(i==4)
            return 'D';
        else if(i==5)
            return 'E';
        return '?';
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateSession Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cse110.clicker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateSession Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cse110.clicker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
