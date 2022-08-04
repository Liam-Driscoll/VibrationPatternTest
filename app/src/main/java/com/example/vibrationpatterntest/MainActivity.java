package com.example.vibrationpatterntest;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    int initial = 2000;
    int pulse = 30;
    int pulseDelay = 300;
    int initialInhaleIntervalDelay = initial - (2 * pulse + pulseDelay);
    int initialExhaleIntervalDelay = initial - (3 * pulse + pulseDelay);
    int inhaleIntervalDelay = initialInhaleIntervalDelay;
    int exhaleIntervalDelay = initialExhaleIntervalDelay;

    int buttonPressCounter = 0;

    long[] vibrationPattern = {0, pulse, pulseDelay, pulse, inhaleIntervalDelay, pulse, pulseDelay, pulse, pulseDelay, pulse, exhaleIntervalDelay};

    int inhaleDuration = 2*pulse+pulseDelay+inhaleIntervalDelay;
    int exhaleDuration = 3*pulse+2*pulseDelay+exhaleIntervalDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keeps screen on while in application
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        TextView breathAction = findViewById(R.id.breathAction);
        Button vibrateButton = findViewById(R.id.vibrateButton);

        breathAction.setVisibility(View.GONE);

        vibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressCounter++;
                if (buttonPressCounter % 2 == 1) {
                    vibrate(vibrationPattern, 0);
                    vibrateButton.setText("Stop");
                }
                else{
                    vibrate(null, 1);
                    vibrateButton.setText("Vibrate");
                }
            }
        });
    }

    public void vibrate(long[] pattern, int stop){
        TextView breathAction = findViewById(R.id.breathAction);
        TextView instructions = findViewById(R.id.instructions);

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (stop == 1){
            instructions.setVisibility(View.VISIBLE);
            breathAction.setVisibility(View.GONE);
            vibrator.cancel();
        }
        else{
            final int indexInPatternToRepeat = 0; //-1: don't repeat pattern, 0: repeat pattern
            vibrator.vibrate(pattern, indexInPatternToRepeat);
            timer(inhaleDuration);
            breathAction.setVisibility(View.VISIBLE);
            breathAction.setText("Inhale");
            instructions.setVisibility(View.GONE);
        }
    }

    public void timer(long duration) {
        TextView breathAction = findViewById(R.id.breathAction);
        long timerDuration = duration;

        CountDownTimer countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (buttonPressCounter % 2 == 0) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                    if (timerDuration == inhaleDuration) {
                        timer(exhaleDuration);
                        breathAction.setText("Exhale");
                    } else {
                        timer(inhaleDuration);
                        breathAction.setText("Inhale");
                    }
            }
        }.start();
    }
}