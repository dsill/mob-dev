package com.alwayssolved.drinkingdice;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;



public class Game_mainMenu extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.alwayssolved.drinkingdice.MESSAGE";

    /** Called when the user clicks the send button */
    public void selectGame(View view) {
        Intent intent;

        switch(view.getId())
        {
            case R.id.btn_Game_twoDice:
                intent = new Intent(this, Game_twoDice.class);
                break;
            case R.id.btn_Game_oneFourTwentyFour:
                intent = new Intent(this, oneFourTwentyFour.class);
                break;
            default:
                throw new RuntimeException("Invalid button ID");
        }

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}