package com.alwayssolved.drinkingdice;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game_twoDice extends AppCompatActivity {
    int qtyDice = 2;                //# of dice required for this game

    ImageView dicePicture1;		//reference to dice picture
    ImageView dicePicture2;		//reference to dice picture
    Random rng = new Random();	    //generate random numbers
    SoundPool dice_sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    int sound_id;		            //Used to control sound stream return by SoundPool
    Handler handler;	            //Post message to start roll
    Timer timer = new Timer();	    //Used to implement feedback to user
    boolean rolling = false;		//Is die rolling?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_two_dice);
        //load dice sound
        sound_id = dice_sound.load(this, R.raw.shake_dice, 1);

/*        HashMap<Integer, Integer> diceImages = new HashMap<>();
        diceImages.put(1, R.drawable.one);
        diceImages.put(2, R.drawable.two);
        diceImages.put(3, R.drawable.three);
        diceImages.put(4, R.drawable.four);
        diceImages.put(5, R.drawable.five);
        diceImages.put(6, R.drawable.six);

        for (int i = 1; i < qtyDice; i++) {
            //do something looped
        }*/

        dicePicture1 = (ImageView) findViewById(R.id.imageView1);
        dicePicture2 = (ImageView) findViewById(R.id.imageView2);
        //link handler to callback
        handler = new Handler(callback);
    }

    //Need to add support for multiple dice

    //User pressed dice, lets start
    public void HandleClick(View arg0) {
        if(!rolling) {
            rolling = true;
            //Show rolling image
            dicePicture1.setImageResource(R.drawable.dice3droll);
            dicePicture2.setImageResource(R.drawable.dice3droll);
            //Start rolling sound
            dice_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);
            //Pause to allow image to update
            timer.schedule(new Roll(), 600);
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
            //Remember nextInt returns 0 to 5 for argument of 6
            //hence + 1
            switch(rng.nextInt(6) + 1) {
                case 1:
                    dicePicture1.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dicePicture1.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dicePicture1.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dicePicture1.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dicePicture1.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dicePicture1.setImageResource(R.drawable.six);
                    break;
                default:
            }

            switch(rng.nextInt(6) + 1) {
                case 1:
                    dicePicture2.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dicePicture2.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dicePicture2.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dicePicture2.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dicePicture2.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dicePicture2.setImageResource(R.drawable.six);
                    break;
                default:
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