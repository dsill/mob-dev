package com.alwayssolved.drinkingdice;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    ImageView dice_picture1;		//reference to dice picture
    ImageView dice_picture2;		//reference to dice picture
    Random rng=new Random();	//generate random numbers
    SoundPool dice_sound = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    int sound_id;		//Used to control sound stream return by SoundPool
    Handler handler;	//Post message to start roll
    Timer timer=new Timer();	//Used to implement feedback to user
    boolean rolling=false;		//Is die rolling?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //load dice sound
        sound_id=dice_sound.load(this,R.raw.shake_dice,1);
        //get reference to image widget
        dice_picture1 = (ImageView) findViewById(R.id.imageView1);
        dice_picture2 = (ImageView) findViewById(R.id.imageView2);
        //link handler to callback
        handler=new Handler(callback);
    }

    //Need to add support for multiple dice

    //User pressed dice, lets start
    public void HandleClick(View arg0) {
        if(!rolling) {
            rolling=true;
            //Show rolling image
            dice_picture1.setImageResource(R.drawable.dice3droll);
            dice_picture2.setImageResource(R.drawable.dice3droll);
            //Start rolling sound
            dice_sound.play(sound_id,1.0f,1.0f,0,0,1.0f);
            //Pause to allow image to update
            timer.schedule(new Roll(), 1000);
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
            switch(rng.nextInt(6)+1) {
                case 1:
                    dice_picture1.setImageResource(R.drawable.one);
                    dice_picture2.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dice_picture1.setImageResource(R.drawable.two);
                    dice_picture2.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dice_picture1.setImageResource(R.drawable.three);
                    dice_picture2.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dice_picture1.setImageResource(R.drawable.four);
                    dice_picture2.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dice_picture1.setImageResource(R.drawable.five);
                    dice_picture2.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dice_picture1.setImageResource(R.drawable.six);
                    dice_picture2.setImageResource(R.drawable.six);
                    break;
                default:
            }
            rolling=false;	//user can press again
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