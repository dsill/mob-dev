package com.alwayssolved.drinkingdice;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game_twoDice extends AppCompatActivity {
    int qtyDice = 2;                //# of dice required for this game

    ImageView dicePicture1;		    //reference to dice picture
    ImageView dicePicture2;		    //reference to dice picture
    ImageView dicePicture;
    Random rng = new Random();	    //generate random numbers
    SoundPool dice_sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    int sound_id;		            //Used to control sound stream return by SoundPool
    Handler handler;	            //Post message to start roll
    Timer timer = new Timer();	    //Used to implement feedback to user
    boolean rolling = false;		//Is die rolling?
    boolean turnStarted = false;	//has the user started their turn

    HashMap<Integer, Integer> diceImages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_two_dice);

        //load dice sound
        sound_id = dice_sound.load(this, R.raw.shake_dice, 1);

        //create hashMap of dice sides
        diceImages.put(1, R.drawable.one);
        diceImages.put(2, R.drawable.two);
        diceImages.put(3, R.drawable.three);
        diceImages.put(4, R.drawable.four);
        diceImages.put(5, R.drawable.five);
        diceImages.put(6, R.drawable.six);

//        for (int i = 1; i < qtyDice; i++) {
//            //do something looped
//        }

        dicePicture1 = (ImageView) findViewById(R.id.imageView1);
        dicePicture2 = (ImageView) findViewById(R.id.imageView2);

        dicePicture1.setClickable(false);
        dicePicture2.setClickable(false);

        //link handler to callback
        handler = new Handler(callback);
    }


    //User clicked dice, lets start
    public void HandleDiceClick(View v) {
        if(!rolling) {
            dicePicture = (ImageView) findViewById(v.getId());

            if (!dicePicture.isSelected()) {
                dicePicture.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                dicePicture.setSelected(true);
            }
            else {
                if(dicePicture.isClickable()) {
                    dicePicture.setColorFilter(null);
                    dicePicture.setSelected(false);
                }
            }
        }
    }

    //User clicked roll button, lets start
    public void HandleRollClick(View v) {
        //turn has started so make dice clickable
        dicePicture1.setClickable(true);
        dicePicture2.setClickable(true);

        if (!rolling) {

            if (!dicePicture1.isSelected()) {
                //Show rolling image
                dicePicture1.setImageResource(R.drawable.dice3droll);
                rolling = true;
            }
            else{
                //do not allow more clicks on this die
                dicePicture1.setClickable(false);
            }

            if (!dicePicture2.isSelected()) {
                //Show rolling image
                dicePicture2.setImageResource(R.drawable.dice3droll);
                rolling = true;
            }
            else{
                //do not allow more clicks on this die
                dicePicture2.setClickable(false);
            }

            if (rolling) {
                //Start rolling sound
                dice_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);

                //Pause to allow image to update
                timer.schedule(new Roll(), 600);
            }
        }
    }



    //When pause completed message sent to callback
    class Roll extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);

        }
    }

    //Receives message from timer to start dice roll
    Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            //Get roll result
            //nextInt starts at 0 hence + 1
            if (!dicePicture1.isSelected()) {
                //Show roll result
                dicePicture1.setImageResource(diceImages.get(rng.nextInt(6) + 1));
            }

            if (!dicePicture2.isSelected()) {
                //Show roll result
                dicePicture2.setImageResource(diceImages.get(rng.nextInt(6) + 1));
            }

            rolling = false;	//user can press again
            return true;
        }
    };

    //Clean up
    @Override
    protected void onPause() {
        super.onPause();
        dice_sound.pause(sound_id);
    }
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}

