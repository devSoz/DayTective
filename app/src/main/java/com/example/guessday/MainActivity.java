package com.example.guessday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        //highScore - Holds highest score of all games and save using shared preferences
        //Score - Score of current game
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Integer highScore = getScore(); //Get the highest score from shared preferences
        TextView score = (TextView) findViewById(R.id.txtScore);
        score.setText(String.valueOf(highScore));
        clickListener(); //On click of start button, vibrate and go to next activity
    }

    private void clickListener()
    {
        // get the VIBRATOR_SERVICE system service
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        ImageButton b1 = (ImageButton) findViewById(R.id.btn1);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final VibrationEffect vibrationEffect1;

                // this is the only type of the vibration which requires system version Oreo (API 26)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    // this effect creates the vibration of default amplitude for 1000ms(1 sec)
                    vibrationEffect1 = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
                    Log.d(TAG, "Vibrated");
                    //Toast.makeText(MainActivity.this, "frst vibrate", Toast.LENGTH_SHORT).show();
                    // it is safe to cancel other vibrations currently taking place
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }


                Intent intent2 = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent2);
            }
        });

    }
   
    private Integer getScore()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore","0")) ;
        return highScore;
    }
}