

package ca.georgebrown.game2011.card_memory_game_midterm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Memory Match Game Midterm
 * Created by Diego Eduardo Camacho(STU ID# 101090467) and Cory Ronald
 * On March 16 2018
 * You have 6 seconds to memorize as many cards as you can
 * Match as many pairs as you can before the time runs out!
 *
 * @Variables NUMBEROFPAIRS,NUMBEROFROWS, NUMBEROFCOLUMS can all be changed to modify the current game.
 * @NumberofPairs: can change the amount of pairs in the current game.
 * @NumberofRows: can change the number of rows in the current game.
 * @NumberofColums: can change the number of columns in the current game.
 */

public class MainActivity extends Activity {
    //Global Variables
    int NUMBEROFPAIRS = 4;
    int NUMBEROFROWS = 4;
    int NUMBEROFCOLUMNS = 4;

    //Context
    Context activity;

    //Card ID List
    List<Integer> cardImageIndex = new ArrayList<Integer>();

    //Current play cards list.
    List<Card> playCards = new ArrayList<Card>();

    //Random Generator
    Random randomGenerator;

    //UI Elements
    TextView timerText;
    TextView instructionText;
    TextView scoreText;

    //UI Variables
    int timePerRound = 30;
    int score = 0;


    //Cards Selected
    int[] CardValues;
    int id = 0;


    //Selected Cards
    boolean ClickAvailable = false;
    Card firstCardSelected = null;
    Card secondCardSelected = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;

        //Find References
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        timerText = findViewById(R.id.TimerID);
        instructionText = findViewById(R.id.InstructionID);
        scoreText = findViewById(R.id.ScoreID);

        //Text Initialize;
        timerText.setText(Integer.toString(timePerRound));
        scoreText.setText(Integer.toString(score));

        //Initialization Variables
        randomGenerator = new Random();
        CardValues = new int[NUMBEROFCOLUMNS * NUMBEROFROWS];


        // Generate Card ID's (Separate thread.)
        new CardIDGenRunnable().run();

        //Generate the cards that will be placed (Separate Thread.)
        new CardValueGenRunnable().run();

        //Load play cards into List
        new LoadCardRunnable().run();

        //Display all play cards.
        int count = 0;
        for (int x = 0; x < NUMBEROFROWS; x++) {
            TableRow tablerow = new TableRow(activity);
            for (int y = 0; y < NUMBEROFCOLUMNS; y++) {
                tablerow.addView(playCards.get(count));
                count++;
            }
            tableLayout.addView(tablerow);
        }

        //Gives the player 10 seconds before the cards flip and the game begins
        new CountDownTimer(7000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {

                for (int x = 0; x < playCards.size(); x++) {
                    Log.d("Flip", "onFinish: FlipCard");

                    playCards.get(x).FlipCard();
                    ClickAvailable = true;

                    new CounterClass().run();


                }

            }
        }.start();
    }

    //Display GAME OVER alert
    private void ShowAlert(String dialogTitle) {
        AlertDialog.Builder alertBox;
        alertBox = new AlertDialog.Builder(activity);
        alertBox.setTitle(dialogTitle)
                .setMessage("Your score: " + Integer.toString(score) + ". \n Would you like to restart the game?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.recreate();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Gets the Resource ID for each Card PNG and passes it to an array to be randomly picked from.
     */

    public class CardIDGenRunnable implements Runnable {

        @Override
        public void run() {
            GenerateCardID();
        }

        private void GenerateCardID() {
            //Get reference to all the cards, place them in an array.
            String cardType = "";
            for (int x = 1; x < 14; x++) {
                for (int i = 0; i < 4; i++) {
                    switch (i) {
                        case 0:
                            cardType = "c";
                            break;
                        case 1:
                            cardType = "d";
                            break;
                        case 2:
                            cardType = "h";
                            break;
                        case 3:
                            cardType = "s";
                            break;
                    }
                    int Imageresource = getResources().getIdentifier("card_" + Integer.toString(x) + cardType, "drawable", activity.getPackageName());
                    cardImageIndex.add(Imageresource);
                }
            }
        }
    }

    /**
     * Randomly generates the cards that will be displayed and places them in a array.
     * Values inside the array are card numbers between 0 and 56.
     * each number being a specific card
     */

    public class CardValueGenRunnable implements Runnable {
        @Override
        public void run() {
            InitializeCardValue();
        }

        private void InitializeCardValue() {
            int count = 0;
            int random;
            int maxCards = NUMBEROFROWS * NUMBEROFCOLUMNS;
            while (count < maxCards) {
                random = randomGenerator.nextInt(cardImageIndex.size());
                if (count < NUMBEROFPAIRS * 2) {
                    for (int x = 0; x < 2; x++) {
                        CardValues[count] = random;
                        count++;
                    }
                } else {
                    CardValues[count] = random;
                    count++;
                }
            }
            for (int x = 0; x < maxCards; x++) {
                int index = randomGenerator.nextInt(x + 1);
                int temp = CardValues[index];
                CardValues[index] = CardValues[x];
                CardValues[x] = temp;
            }
        }
    }

    /**
     *Initializes and loads all the active cards into the playCards List.
     * Creates a object of type Class
     * Sets its front face to one of the values given by cardValues array.
     * Pushes the card into the playCard List
     **/

    public class LoadCardRunnable implements Runnable {

        @Override
        public void run() {
            LoadCards();
        }

        private void LoadCards() {
            for (int x = 0; x < CardValues.length; x++) {
                Card currentCard = new Card(activity);
                currentCard.setId(id);
                int card = CardValues[x];
                currentCard.setImageResource(cardImageIndex.get(card));
                currentCard.setOnClickListener(new EventListener());
                playCards.add(currentCard);
                id++;
            }
        }
    }


    /**
     * Keeps Track of current timer and enables a Alert if the game is over.
     */

    public class CounterClass implements Runnable {

        @Override
        public void run() {
            new CountDownTimer(30000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    timerText.setText("" + (millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {

                    ShowAlert("Times Up!");

                }
            }.start();
        }
    }




    private class EventListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //Checks if the player can click on a buttom
            if (ClickAvailable) {
                //Checks if the player has selected the first card.
                if (firstCardSelected == null) {
                    firstCardSelected = (Card) view;
                    firstCardSelected.FlipCard();

                } else {
                    //allows the player to deselect card if already selected
                    if (view.getId() == firstCardSelected.getId()) {
                        firstCardSelected.FlipCard();
                        firstCardSelected = null;
                        return;

                    }
                }
                if (secondCardSelected == null && firstCardSelected != null) {
                    //Lets the player pick a second card making sure it isn't the first one he picked.
                    if (view.getId() != firstCardSelected.getId()) {
                        secondCardSelected = (Card) view;
                        secondCardSelected.FlipCard();

                    }

                }

                // If 2 cards have been selected
                if (firstCardSelected != null && secondCardSelected != null) {
                    secondCardSelected.FlipCard();
                    ClickAvailable = false;
                    //Checks if the first card selected and the second card selected are the same
                    if (firstCardSelected.getImageFrontID() == secondCardSelected.getImageFrontID()) {
                        secondCardSelected.FlipCard();
                        instructionText.setText("Correct!");
                        score++;
                        scoreText.setText(Integer.toString(score));


                        new CountDownTimer(1000, 100) {

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                firstCardSelected.setVisibility(View.INVISIBLE);
                                secondCardSelected.setVisibility(View.INVISIBLE);
                                firstCardSelected = null;
                                secondCardSelected = null;
                                instructionText.setText("Pick a card any card!");
                                ClickAvailable = true;

                            }
                        }.start();


                    } else {
                        secondCardSelected.FlipCard();
                        instructionText.setText("Wrong!");


                        new CountDownTimer(1000, 100) {

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                firstCardSelected.FlipCard();
                                secondCardSelected.FlipCard();
                                firstCardSelected = null;
                                secondCardSelected = null;
                                instructionText.setText("Pick a card any card!");
                                ClickAvailable = true;

                            }
                        }.start();
                    }


                }
            }
        }


    }
}








