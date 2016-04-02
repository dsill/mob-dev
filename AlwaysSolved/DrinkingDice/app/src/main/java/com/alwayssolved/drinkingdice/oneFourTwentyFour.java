package com.alwayssolved.drinkingdice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//TODO: add number of players dialog to start of game

public class oneFourTwentyFour extends AppCompatActivity {
    int qtyDice = 6;                    //# of dice required for this game
    int qtyPlayers = 4;                 //# of players
    int currPlayer = 1;                 //current player
    int currPlayerScore;                //tally of current player's score

    boolean isQualifiedOne;             //0 if "One" is not locked yet
    boolean isQualifiedFour;            //0 if "Four" is not locked yet
    boolean isQualifiedOne_thisRoll;    //0 if "One" is not locked yet
    boolean isQualifiedFour_thisRoll;   //0 if "Four" is not locked yet
    int qtyOneSelected_thisRoll;        //# of 1's selected on the current roll
    int qtyFourSelected_thisRoll;       //# of 4's selected on the current roll

    int qtyPrevLocked;                  //qty dice locked as of last roll
    int qtyCurrLocked;                  //qty dice locked currently

    boolean isFirstRoll;                //set to false after first roll
    boolean rollContinue;               //roll validated and may continue

    boolean rolling;		            //Is die rolling?
    boolean isLastPlayer;               //Is this the final roller

    int rollError;                      //roll validation error id

    int diceValue1;                     //value of die at position 1
    int diceValue2;                     //value of die at position 2
    int diceValue3;                     //value of die at position 3
    int diceValue4;                     //value of die at position 4
    int diceValue5;                     //value of die at position 5
    int diceValue6;                     //value of die at position 5

    Random rng = new Random();	        //generate random numbers
    SoundPool dice_sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    int sound_id;		                //Used to control sound stream return by SoundPool
    Handler handler;	                //Post message to start roll
    Timer timer = new Timer();	        //Used to implement feedback to user

    //Declare Views
    TextView currPlayerText;            //reference to currPlayerText
    TextView isQualifiedText;           //reference to isQualifiedText
    TextView currPlayerScoreText;       //reference to currPlayerScoreText


    ImageView dicePicture1;		        //reference to dice picture
    ImageView dicePicture2;		        //reference to dice picture
    ImageView dicePicture3;		        //reference to dice picture
    ImageView dicePicture4;		        //reference to dice picture
    ImageView dicePicture5;		        //reference to dice picture
    ImageView dicePicture6;		        //reference to dice picture
    ImageView dicePicture;              //reference to passed imageView

    ImageView diceRoll;		            //reference to roll dice button
    ImageView scorePost;                //reference to post score button

    //Declare HashMaps
    HashMap<Integer, Integer> diceImages = new HashMap<>();
    HashMap<Integer, Integer> diceValues = new HashMap<>();
    HashMap<Integer, Integer> playerScores = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_four_twenty_four);

        //load dice sound
        sound_id = dice_sound.load(this, R.raw.shake_dice, 1);

        dicePicture1 = (ImageView) findViewById(R.id.imageView1);
        dicePicture2 = (ImageView) findViewById(R.id.imageView2);
        dicePicture3 = (ImageView) findViewById(R.id.imageView3);
        dicePicture4 = (ImageView) findViewById(R.id.imageView4);
        dicePicture5 = (ImageView) findViewById(R.id.imageView5);
        dicePicture6 = (ImageView) findViewById(R.id.imageView6);

        scorePost = (ImageView) findViewById(R.id.imageViewSubmit);
        diceRoll = (ImageView) findViewById(R.id.imageViewRoll);

        //populate hashMap of dice sides
        diceImages.put(1, R.drawable.one);
        diceImages.put(2, R.drawable.two);
        diceImages.put(3, R.drawable.three);
        diceImages.put(4, R.drawable.four);
        diceImages.put(5, R.drawable.five);
        diceImages.put(6, R.drawable.six);

        //populate hashMap of player scores
        for (int i = 1; i <= qtyPlayers; i++) {
            playerScores.put(i, 0);
        }

        currPlayerText = (TextView) findViewById(R.id.currPlayerText);
        isQualifiedText = (TextView) findViewById(R.id.isQualifiedText);
        currPlayerScoreText = (TextView) findViewById(R.id.currPlayerScoreText);


        //link handler to callback
        handler = new Handler(callback);

        //call method to initialize turn-specific values
        turnInitialize();
    }

    //Reset turn-specific values for next player's roll
    public void turnInitialize() {

        currPlayerScore = 0;                    //set new player's score to 0

        isQualifiedOne = false;                 //0 if "One" is not locked yet
        isQualifiedFour = false;                //0 if "Four" is not locked yet
        isQualifiedOne_thisRoll = false;        //0 if "One" is not locked yet
        isQualifiedFour_thisRoll = false;       //0 if "Four" is not locked yet
        qtyOneSelected_thisRoll = 0;            //# of 1's selected on the current roll
        qtyFourSelected_thisRoll = 0;           //# of 4's selected on the current roll

        qtyPrevLocked = 0;

        isFirstRoll = true;             //set to false after first roll
        rollContinue = true;            //roll validated and may continue
        rollError = 0;

        diceValue1 = 0;                 //value of die at position 1
        diceValue2 = 0;                 //value of die at position 2
        diceValue3 = 0;                 //value of die at position 3
        diceValue4 = 0;                 //value of die at position 4
        diceValue5 = 0;                 //value of die at position 5
        diceValue6 = 0;                 //value of die at position 6

        rolling = false;		        //Is die rolling?

        //create hashMap of dice values
        diceValues.put(R.id.imageView1, 0);
        diceValues.put(R.id.imageView2, 0);
        diceValues.put(R.id.imageView3, 0);
        diceValues.put(R.id.imageView4, 0);
        diceValues.put(R.id.imageView5, 0);
        diceValues.put(R.id.imageView6, 0);

        dicePicture1.setClickable(false);
        dicePicture2.setClickable(false);
        dicePicture3.setClickable(false);
        dicePicture4.setClickable(false);
        dicePicture5.setClickable(false);
        dicePicture6.setClickable(false);

        dicePicture1.setSelected(false);
        dicePicture2.setSelected(false);
        dicePicture3.setSelected(false);
        dicePicture4.setSelected(false);
        dicePicture5.setSelected(false);
        dicePicture6.setSelected(false);

        scorePost.setClickable(false);
        scorePost.setImageResource(R.drawable.postscorebutton_off_down);

        dicePicture1.setImageResource(R.drawable.dice3droll);
        dicePicture2.setImageResource(R.drawable.dice3droll);
        dicePicture3.setImageResource(R.drawable.dice3droll);
        dicePicture4.setImageResource(R.drawable.dice3droll);
        dicePicture5.setImageResource(R.drawable.dice3droll);
        dicePicture6.setImageResource(R.drawable.dice3droll);

        dicePicture1.setColorFilter(null);
        dicePicture2.setColorFilter(null);
        dicePicture3.setColorFilter(null);
        dicePicture4.setColorFilter(null);
        dicePicture5.setColorFilter(null);
        dicePicture6.setColorFilter(null);

        //call method to build and replace player token
        buildPlayerString();

        //call method to build and replace score token
        buildScoreString();

        //reset qualified string
        isQualifiedText.setText(R.string.not_qualified);

    }


    //User clicked roll button, lets start
    public void HandleRollClick(View v) {
        //make sure at least one die was selected if not first roll
        if(isFirstRoll) {
            //turn has started so make dice clickable
            dicePicture1.setClickable(true);
            dicePicture2.setClickable(true);
            dicePicture3.setClickable(true);
            dicePicture4.setClickable(true);
            dicePicture5.setClickable(true);
            dicePicture6.setClickable(true);

            //first roll completed
            isFirstRoll = false;
        }
        else {
            isContinue();
        }

        if(rollContinue) {
            //save values from previous roll
            if(!isQualifiedOne && isQualifiedOne_thisRoll) {isQualifiedOne = true;}
            if(!isQualifiedFour && isQualifiedFour_thisRoll) {isQualifiedFour = true;}

            //reset roll based values
            qtyOneSelected_thisRoll = 0;
            qtyFourSelected_thisRoll = 0;

            if (!rolling) {
                if (!dicePicture1.isSelected()) {
                    //Show rolling image
                    dicePicture1.setImageResource(R.drawable.dice3droll);
                    rolling = true;
                } else {
                    //do not allow more clicks on this die
                    dicePicture1.setClickable(false);
                }

                if (!dicePicture2.isSelected()) {
                    //Show rolling image
                    dicePicture2.setImageResource(R.drawable.dice3droll);
                    rolling = true;
                } else {
                    //do not allow more clicks on this die
                    dicePicture2.setClickable(false);
                }

                if (!dicePicture3.isSelected()) {
                    //Show rolling image
                    dicePicture3.setImageResource(R.drawable.dice3droll);
                    rolling = true;
                } else {
                    //do not allow more clicks on this die
                    dicePicture3.setClickable(false);
                }

                if (!dicePicture4.isSelected()) {
                    //Show rolling image
                    dicePicture4.setImageResource(R.drawable.dice3droll);
                    rolling = true;
                } else {
                    //do not allow more clicks on this die
                    dicePicture4.setClickable(false);
                }

                if (!dicePicture5.isSelected()) {
                    //Show rolling image
                    dicePicture5.setImageResource(R.drawable.dice3droll);
                    rolling = true;
                } else {
                    //do not allow more clicks on this die
                    dicePicture5.setClickable(false);
                }

                if (!dicePicture6.isSelected()) {
                    //Show rolling image
                    dicePicture6.setImageResource(R.drawable.dice3droll);
                    rolling = true;
                } else {
                    //do not allow more clicks on this die
                    dicePicture6.setClickable(false);
                }

                if (rolling) {
                    //Start rolling sound
                    dice_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);

                    //Pause to allow image to update
                    timer.schedule(new Roll(), 600);
                }
            }
        }
        else if(rollError == 1){
            //Display noneSelected error message
            Snackbar noneSelected = Snackbar.make(findViewById(R.id.oneFourTwentyFourCoordinator), R.string.none_selected, Snackbar.LENGTH_LONG);
            noneSelected.show();
        }
    }

    //User clicked roll button, lets start
    public void isContinue() {
        //calculate current # of locked dice
        qtyLocked();

        if(qtyCurrLocked > qtyPrevLocked){
            qtyPrevLocked = qtyCurrLocked;
            rollContinue = true;
        }
        else {
            rollContinue = false;
            rollError = 1;
        }


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

            //validate qualifiers and calculate score
            calcScore(v);

            //check if turn can be ended
            isSubmit();
        }
    }

    public void isSubmit() {

        qtyLocked();

        if(qtyCurrLocked == 6){
            scorePost.setClickable(true);
            scorePost.setImageResource(R.drawable.postscorebutton_on_up);
        }
        else{
            scorePost.setClickable(false);
            scorePost.setImageResource(R.drawable.postscorebutton_off_down);
        }

    }

    public void qtyLocked() {
        qtyCurrLocked = 0;

        if(dicePicture1.isSelected()){qtyCurrLocked++;}
        if(dicePicture2.isSelected()){qtyCurrLocked++;}
        if(dicePicture3.isSelected()){qtyCurrLocked++;}
        if(dicePicture4.isSelected()){qtyCurrLocked++;}
        if(dicePicture5.isSelected()){qtyCurrLocked++;}
        if(dicePicture6.isSelected()){qtyCurrLocked++;}

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
                diceValue1 = rng.nextInt(6) + 1;
                //dicePicture1.setImageResource(diceImages.get(diceValue1));
                diceValues.put(R.id.imageView1, diceValue1);
            }

            if (!dicePicture2.isSelected()) {
                //Show roll result
                diceValue2 = rng.nextInt(6) + 1;
                //dicePicture2.setImageResource(diceImages.get(diceValue2));
                diceValues.put(R.id.imageView2, diceValue2);
            }

            if (!dicePicture3.isSelected()) {
                //Show roll result
                diceValue3 = rng.nextInt(6) + 1;
                //dicePicture3.setImageResource(diceImages.get(diceValue3));
                diceValues.put(R.id.imageView3, diceValue3);
            }

            if (!dicePicture4.isSelected()) {
                //Show roll result
                diceValue4 = rng.nextInt(6) + 1;
                //dicePicture4.setImageResource(diceImages.get(diceValue4));
                diceValues.put(R.id.imageView4, diceValue4);
            }

            if (!dicePicture5.isSelected()) {
                //Show roll result
                diceValue5 = rng.nextInt(6) + 1;
                //dicePicture5.setImageResource(diceImages.get(diceValue5));
                diceValues.put(R.id.imageView5, diceValue5);
            }

            if (!dicePicture6.isSelected()) {
                //Show roll result
                diceValue6 = rng.nextInt(6) + 1;
                //dicePicture6.setImageResource(diceImages.get(diceValue6));
                diceValues.put(R.id.imageView6, diceValue6);
            }

            displayDice();      //display the dice for the values rolled and check post-roll rules
            rolling = false;	//user can press again
            return true;


        }
    };

    public void displayDice() {

        dicePicture1.setImageResource(diceImages.get(diceValue1));
        dicePicture2.setImageResource(diceImages.get(diceValue2));
        dicePicture3.setImageResource(diceImages.get(diceValue3));
        dicePicture4.setImageResource(diceImages.get(diceValue4));
        dicePicture5.setImageResource(diceImages.get(diceValue5));
        dicePicture6.setImageResource(diceImages.get(diceValue6));

        //post-role rules
        checkCanQualify();

        if(rollError == 2){
            //call function to display non-qualify dialog and end turn
            nonQualifyDialog();
        }
    }

    public void checkCanQualify() {

        boolean canQualify = false;
        int activeDieValue1 = 0;
        int activeDieValue2 = 0;

        //end turn if it is no longer possible to qualify
        if(qtyPrevLocked >=4) {
            if(dicePicture1.isClickable()){
                activeDieValue1 = diceValue1;
            }

            //determine values of remaining dice
            if(dicePicture2.isClickable() && activeDieValue1 == 0){
                activeDieValue1 = diceValue2;
            }
            else {
                activeDieValue2 = diceValue2;
            }

            if(dicePicture3.isClickable() && activeDieValue1 == 0){
                activeDieValue1 = diceValue3;
            }
            else {
                activeDieValue2 = diceValue3;
            }

            if(dicePicture4.isClickable() && activeDieValue1 == 0){
                activeDieValue1 = diceValue4;
            }
            else {
                activeDieValue2 = diceValue4;
            }

            if(dicePicture5.isClickable() && activeDieValue1 == 0){
                activeDieValue1 = diceValue5;
            }
            else {
                activeDieValue2 = diceValue5;
            }

            if(dicePicture6.isClickable() && activeDieValue1 == 0){
                activeDieValue1 = diceValue6;
            }
            else {
                activeDieValue2 = diceValue6;
            }


            if (!isQualifiedOne && !isQualifiedFour) {
                if (activeDieValue1 == 1 || activeDieValue1 == 4 || activeDieValue2 == 1 || activeDieValue2 == 4) {
                    canQualify = true;
                }
            }
            else if (isQualifiedOne && !isQualifiedFour) {
                if (activeDieValue1 == 4 || activeDieValue2 == 4) {
                    canQualify = true;
                }
            }
            else if (!isQualifiedOne && isQualifiedFour) {
                if (activeDieValue1 == 1 || activeDieValue2 == 1) {
                    canQualify = true;
                }
            }

            //set roll error
            if(!canQualify) {rollError = 2;}
        }


    }


    //User clicked roll button, lets start
    public void calcScore(View v) {
        //TODO: do not allow selection of non-qualifier when qualification is needed
        //TODO: auto end turn by either non-qualification or last die rolled
        dicePicture = (ImageView) findViewById(v.getId());

        //get numeric value of the selected die
        int diceValue = diceValues.get(v.getId());

        //determine if selected die is a qualifier or if it is a score  modifier
        if (dicePicture.isSelected()) {
            if(diceValue == 1){qtyOneSelected_thisRoll++;}  //count # of 1's selected this roll
            if(diceValue == 4){qtyFourSelected_thisRoll++;} //count # of 4's selected this roll

            if((!isQualifiedOne || !isQualifiedOne_thisRoll) && diceValue == 1){
                //not "1" qualified and selected die is a 1
                isQualifiedOne_thisRoll = true;
            }
            else if ((!isQualifiedFour || !isQualifiedFour_thisRoll) && diceValue == 4) {
                //not "4" qualified and selected die is a 4
                isQualifiedFour_thisRoll = true;
            }
            else {
                //add value to total
                currPlayerScore = currPlayerScore + diceValue;
            }
        }
        else {
            if(diceValue == 1){qtyOneSelected_thisRoll--;}
            if(diceValue == 4){qtyFourSelected_thisRoll--;}

            if(isQualifiedOne_thisRoll && diceValue == 1 && qtyOneSelected_thisRoll == 0){
                //not "1" qualified and de-selected die is a 1
                isQualifiedOne_thisRoll = false;
            }
            else if (isQualifiedFour_thisRoll && diceValue == 4 && qtyFourSelected_thisRoll == 0) {
                //not "4" qualified and de-selected die is a 4
                isQualifiedFour_thisRoll = false;
            }
            else {
                //subtract value from total
                currPlayerScore = currPlayerScore - diceValue;
            }
        }

        //set qualified text
        if( (isQualifiedOne && isQualifiedFour) ||
            (isQualifiedOne && isQualifiedFour_thisRoll) ||
            (isQualifiedOne_thisRoll && isQualifiedFour) ||
            (isQualifiedOne_thisRoll && isQualifiedFour_thisRoll)){

                isQualifiedText.setText(R.string.is_qualified);
        }
        else {
            isQualifiedText.setText(R.string.not_qualified);
        }

        //call method to build and replace score token
        buildScoreString();
    }

    //User clicked roll button, lets start
    public void updateScore(Integer scoreValue) {
        //add or subtract scoreValue from score
    }


    //build and replace score string token
    public void buildScoreString() {
        String currScoreString = TagFormat.from(getString(R.string.currPlayerScore))
                .with("playerScore", currPlayerScore)
                .format();

        currPlayerScoreText.setText(currScoreString);
    }

    //build and replace player string token
    public void buildPlayerString(){
        String currPlayerString = TagFormat.from(getString(R.string.currPlayer))
                .with("playerNumber", currPlayer)
                .format();

        currPlayerText.setText(currPlayerString);
    }


    //User clicked submit button
    public void HandleSubmitScore(View v) {
        //TODO: check if qualified and start next turn
        if(isQualifiedOne && isQualifiedFour){
            playerScores.put(currPlayer, currPlayerScore);
        }
        else{
            playerScores.put(currPlayer, -1);
        }


        //go to next player or end round if last player has rolled
        if(currPlayer < qtyPlayers){
            //display score submit dialog
            submitDialog();
        }
        else{
            isLastPlayer = true;
            //end round
            endRound();
        }

    }

    //All users have completed their rolls
    public void submitDialog() {

        //Build nextPlayer dialog
        //Declare dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your score: " + Integer.toString(currPlayerScore));
        //.setTitle(R.string.dialog_title);
        builder.setPositiveButton("next player", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //increment current player
                currPlayer++;
                //initialize values for next player
                turnInitialize();
            }
        });

        //Display nextPlayer dialog
        AlertDialog nextPlayerDialog = builder.create();
        nextPlayerDialog.show();
    }

    //All users have completed their rolls
    public void nonQualifyDialog() {

        //Build nextPlayer dialog
        //Declare dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.not_qualified_msg);
        //.setTitle(R.string.dialog_title);
        builder.setPositiveButton("next player", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //increment current player
                currPlayer++;
                //initialize values for next player
                turnInitialize();
            }
        });

        //Display nextPlayer dialog
        AlertDialog nextPlayerDialog = builder.create();
        nextPlayerDialog.show();
    }


    //All users have completed their rolls
    public void endRound() {
        //TODO: display scores and determine winner when round is completed
        //Build endRound dialog
        //Declare dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Round completed");
        //.setTitle(R.string.dialog_title);
        builder.setNegativeButton("game menu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked negative button
            }
        });
        builder.setPositiveButton("new game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked positive button
            }
        });

        //Display nextPlayer dialog
        AlertDialog nextPlayerDialog = builder.create();
        nextPlayerDialog.show();
    }

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

