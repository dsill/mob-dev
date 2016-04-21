package com.alwayssolved.drinkingdice;

import java.util.Random;


public class DiceRoll {


    Random rng = new Random();	        //generate random numbers
    int diceValue;


    public static DiceRoll from(Integer diePosition) {return new DiceRoll(diePosition);}

    private final Integer diePosition;
    private DiceRoll(Integer diePosition) {
        this.diePosition = diePosition;
    }

    //called to begin the roll and returns the rolled value
    public int BeginRoll() {

        diceValue = rng.nextInt(6) + 1;
        return diceValue;

    }




}
