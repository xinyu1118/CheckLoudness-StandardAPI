package io.github.checkloudness_standardapi;


import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Record audio data from microphone, duration and interval could be set.
 */
public class AudioRecorder {
    private MediaRecorder mediaRecorder;
    public static final int DURATION = 1000;
    private String filePath;
    List<Integer> amplitudes = new ArrayList<>();

    public AudioRecorder() {
        //this.filePath = "temp/audio" + System.currentTimeMillis();
        this.filePath = "/dev/null";
    }

    private long startTime;
    private long stopTime;


    public void startRecord() {
        if (mediaRecorder == null)
            mediaRecorder = new MediaRecorder();

        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.setOutputFile(filePath);
            //mediaRecorder.setMaxDuration(DURATION);
            mediaRecorder.prepare();
            mediaRecorder.start();
            startTime = System.currentTimeMillis();
            Log.d("Log", "startTime: "+startTime);

            while (true) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime > DURATION) {
                    break;
                }
                amplitudes.add(mediaRecorder.getMaxAmplitude());
            }
            updateMicStatus();

        } catch (IllegalStateException e) {
            Log.d("Log", e.getMessage());
        } catch (IOException e) {
            Log.d("Log", e.getMessage());
        }
    }

    public void stopRecord() {
        if (mediaRecorder == null)
            return;
        stopTime = System.currentTimeMillis();
        Log.d("Log", "stopTime: "+stopTime);

        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        Log.d("Log", "Time: "+ (stopTime-startTime));
    }

    private final Handler handler = new Handler();
    private Runnable updateMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };

    private int BASE = 1;
    private int INTERVAL = 1000*10;

    private void updateMicStatus() {
        if (mediaRecorder != null) {
            double rms = 0;

            // calculate average loudness in dB.
            if (amplitudes.size() == 0) return;
            for (Integer amplitude : amplitudes) {
                rms += amplitude.doubleValue() / amplitudes.size() * amplitude.doubleValue();
            }
            rms = Math.sqrt(rms);

            // calculate maximum loudness in dB.
            //double ratio = (double)mediaRecorder.getMaxAmplitude() /BASE;
            //double db = 0;
            //if (ratio > 1)
            //    db = 20 * Math.log10(ratio);

            double db = 20 * Math.log10(rms/BASE);
            Log.d("Log", "loudness: "+db);

            if (db <= 30)
                Log.d("Log", "Loudness is lower than or equal to the threshold.");

            handler.postDelayed(updateMicStatusTimer, INTERVAL);
        }
    }
}
