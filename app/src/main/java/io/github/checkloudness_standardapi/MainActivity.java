package io.github.checkloudness_standardapi;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    AudioRecorder audioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioRecorder = new AudioRecorder();
        audioRecorder.startRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRecorder.stopRecord();
    }
}
