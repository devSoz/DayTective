package com.example.guessday;
import java.time.DayOfWeek;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import java.util.Calendar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GameActivity extends AppCompatActivity
{
    private String[] strArray;
    private String strDate;
    private int f=1;
    private int counter=100; //countdown and includes penalty as well
    private Integer intScore = 0;
    private static final String TAG = "GameActivity";
    private static String dayOfTheWeek;
    private CountDownTimer timer1;
    private Integer bgColor = R.color.backgnd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        strArray = new String[4];
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        final TextView counttime = findViewById(R.id.txtTimer);


        //Timer that runs 5 mins and closes the game automatically when time elaspsed
        //Penalty of 10 seconds will be deducted if guess is wrong
        timer1= new CountDownTimer (100000, 1000)
        {   //timer loop
            @Override
            public void onTick(long millisUntilFinished)
            {
                counttime.setText(String.valueOf(counter));
                counter--;
                if (counter<=0)
                {
                    cancel(); //counter overrides system timer as it includes penalty, so cancel timer and close
                    //stores the highest score in shared preferences to show in main screen
                    storeScore();
                }
            }

            @Override
            public void onFinish()
            {
                counttime.setText("Round over");
                storeScore(); //stores the highest score in shared preferences to show in main screen

            }
        }.start();

        //When orientation is changed, restore the variables to keep activity intact
        if (savedInstanceState!= null)
        {
            f=0;
            intScore = savedInstanceState.getInt("Score");
            counter = savedInstanceState.getInt("counter");
            setScore(intScore);
            bgColor = savedInstanceState.getInt("colour");
            setBackground(bgColor);
            strArray[0] = savedInstanceState.getString("Rb1");
            strArray[1] = savedInstanceState.getString("Rb2");
            strArray[2] = savedInstanceState.getString("Rb3");
            strArray[3] = savedInstanceState.getString("Rb4");
            strDate = savedInstanceState.getString("date");

            initialiseRb();
        }
        //Set the random date and random days(as options) for the user to guess correct day.
        if(f==1)
        { initialiseNormal(f);}
        //Button onclick listener is added here
        listenerNormal();
    }

    private void initialiseRb()
    {

        RadioButton[] rb = new RadioButton[4];

        rb[0]= (RadioButton) findViewById(R.id.rbNormal1);
        rb[1]= (RadioButton) findViewById(R.id.rbNormal2);
        rb[2]= (RadioButton) findViewById(R.id.rbNormal3);
        rb[3]= (RadioButton) findViewById(R.id.rbNormal4);

        for (int i=0; i<4; i++)
        {
            Log.d(TAG,"9");
            rb[i].setText(strArray[i]);
        }
        TextView tvDate = (TextView) findViewById(R.id.date1);
        tvDate.setText(strDate);
    }
    private void initialiseNormal(int f)
    {
        f=1;
        setScore(intScore);
        Calendar calenderNormal =  new GregorianCalendar();
        //Generate random date between 2000 and 2050 years
        int month = createRandomIntBetween(1, 12);
        int year = createRandomIntBetween(2000, 2050);
        int day = randomDate(month,year);

        calenderNormal.set(year,month,day);

        //Get day of the date . "EEEE" format returns day in full
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        dayOfTheWeek = sdf.format(calenderNormal.getTime());
        //Get the date to show in date1 textview control in dd-MMM-yyyy format
        sdf = new SimpleDateFormat("dd-MMM-yyyy");
        strDate = sdf.format(calenderNormal.getTime());

        TextView tvDate = (TextView) findViewById(R.id.date1);
        tvDate.setText(strDate);

        List<String> list = new ArrayList<>();
        //Randomly get 4 options, one of it should have correct day in random position in radio group
        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        list.add("Thursday");
        list.add("Friday");
        list.add("Saturday");
        list.add("Sunday");
        list.remove(new String(dayOfTheWeek));

        int temp = (int)(Math.random() * (4));
        //Log.d(TAG, String.valueOf(temp));
        //set correct day
        strArray[temp] = dayOfTheWeek;

        //set rest of 3 days in random position
        for (int j=0; j<4; j++)
        {
            if(j!=temp)
            {
                String option = getRandomElement(list);
                strArray[j]=option;
                list.remove(new String(option)); //remove the one set already
            }
        }
        initialiseRb();
         }
    //Function to generate day from the list
    private String getRandomElement(List<String> list)
    {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    //Show the score in text view
    private void setScore(Integer i)
    {
        TextView tvScore = (TextView) findViewById(R.id.scoreNormal);
        tvScore.setText(String.valueOf(i));
    }

    //Onclick listener or button
    private void listenerNormal()
    {
        Button b1 = (Button)findViewById(R.id.btnSubmitNormal1);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Get the user selected option
                RadioGroup Rg1 = (RadioGroup)findViewById(R.id.radioGroupNormal);

                int selectedOption = Rg1.getCheckedRadioButtonId();
                //If user does not select any, notify user
                if(selectedOption==(-1))
                {
                    Toast.makeText(GameActivity.this, "Select an option", Toast.LENGTH_SHORT).show();
                }
                else {
                    //If selected option is correct day, add 10 to the score, else add penalty
                    RadioButton rbSelected = (RadioButton) findViewById(selectedOption);
                    String strSelected = rbSelected.getText().toString();

                    if (strSelected.equals(dayOfTheWeek))
                    {
                        intScore += 10; //add score
                        setBackground(R.color.clr2); //set background to green
                        Toast.makeText(GameActivity.this, "Your next challenge is here....", Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                            //Penalty is -5 score and overall time is reduced by 10 seconds
                            counter -= 10;
                            intScore -= 5;
                            if (counter<=0)
                            {
                                timer1.cancel();
                                storeScore();

                            }
                            else {
                                setBackground(R.color.clr1); //set background to red
                                Toast.makeText(GameActivity.this, "Oops...You are losing 5 points..Your next challenge is here...", Toast.LENGTH_SHORT).show();

                            }

                    }
                    Rg1.clearCheck();
                    initialiseNormal(1);
                }
        }
        });
    }

    //stores the highest score in shared preferences to show in main screen
    //This function is called when the time elapses or counter becomes 0
    private void storeScore()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore","0")) ;

        if (intScore>highScore)
        {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("highScore", String.valueOf(intScore));
            myEdit.commit();
        }
        //add alert here
        setScore(intScore);
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(GameActivity.this);
        aBuilder.setMessage(R.string.dialog_message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent2 = new Intent(GameActivity.this, MainActivity.class);
                        startActivity(intent2);
                    }
                });

        AlertDialog alert=aBuilder.create();
        alert.show();


    }

    @SuppressLint("ResourceAsColor")

    private void setBackground(Integer c)
    {
        bgColor = c;

        ScrollView layout1 = (ScrollView) findViewById(R.id.layout1);
        layout1.setBackgroundResource(c);
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.lay2);
        layout2.setBackgroundResource(c);
    }

    private static int createRandomIntBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    private int randomDate(int month, int year)
    {
        int day;
        if(month==2)
        {
            if(((year%4==0)&&(year%100 !=0))||(year%400==0))
            {
                day = createRandomIntBetween(1, 29);
            }
            else {
                day = createRandomIntBetween(1, 28);
            }
        }
        else if (month%2==0)
        {
            day = createRandomIntBetween(1, 30);
        }
        else {
            day = createRandomIntBetween(1, 30);
        }
        return day;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("counter",counter);
        outState.putInt("Score",intScore);
        outState.putString("Rb1",strArray[0]);
        outState.putString("Rb2",strArray[1]);
        outState.putString("Rb3",strArray[2]);
        outState.putString("Rb4",strArray[3]);
        outState.putString("date",strDate);
        outState.putInt("colour",bgColor);
    }
}