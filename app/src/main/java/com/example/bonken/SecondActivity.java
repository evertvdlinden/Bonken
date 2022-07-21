package com.example.bonken;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    //Initialize variables and string arrays
    public static final String NAMES = "com.example.example.NAME";
    private int roundCount;
    private final Integer[] pointLimit = {13, 13, 24, 24, 10, 10, 10, 26, 26, 26, 26};
    private final Integer[] warningStrings = {R.string.score_not_13, R.string.score_not_13,
            R.string.score_not_24, R.string.score_not_24, R.string.score_not_10,
            R.string.score_not_10, R.string.score_not_10, R.string.score_not_26,
            R.string.score_not_26, R.string.score_not_26, R.string.score_not_26};
    private final Integer[] roundNames = {R.string.ducking, R.string.no_hearts, R.string.no_men,
            R.string.no_queens, R.string.no_king_of_hearts, R.string.last_trick, R.string.domino,
            R.string.trumps1, R.string.trumps2, R.string.trumps3, R.string.trumps4};
    private List<String> trumps = new ArrayList<>();
    private String removedTrump;

    private TextView[] playerNames = new TextView[4];
    private EditText[] tempScores = new EditText[4];
    private Integer[]  tempScoresInt = new Integer[4];
    private TextView[] totalScores = new TextView[4];
    private Integer[] totalScoresInt = new Integer[4];

    private Button previousButton;
    private Button calculateButton;
    private Button undoButton;
    private TextView warningText;
    private TextView endOfGameText;
    private Button newGameButton;
    private TextView roundName;
    private Spinner dropDown;

    //Functionality for everything that happens when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //Get names from main activity
        Intent intent = getIntent();
        final String[] names = intent.getStringArrayExtra(MainActivity.NAMES);
        roundCount = 0;
        trumps.addAll(List.of("Choose Trump", "Hearts", "Clubs", "Diamonds", "Spades", "No Trump"));

        //Dropdown menu for troeven
        dropDown = findViewById(R.id.spinner);
        dropDown.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, trumps);
        dropDown.setAdapter(adapter);

        //Layout declarations
        newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setVisibility(View.INVISIBLE);

        playerNames[0] = findViewById(R.id.namePlayer1);
        playerNames[1] = findViewById(R.id.namePlayer2);
        playerNames[2] = findViewById(R.id.namePlayer3);
        playerNames[3] = findViewById(R.id.namePlayer4);

        tempScores[0] = findViewById(R.id.scoreToAddPlayer1);
        tempScores[1] = findViewById(R.id.scoreToAddPlayer2);
        tempScores[2] = findViewById(R.id.scoreToAddPlayer3);
        tempScores[3] = findViewById(R.id.scoreToAddPlayer4);

        totalScores[0] = findViewById(R.id.totalScorePlayer1);
        totalScores[1] = findViewById(R.id.totalScorePlayer2);
        totalScores[2] = findViewById(R.id.totalScorePlayer3);
        totalScores[3] = findViewById(R.id.totalScorePlayer4);

        for (int i = 0; i < playerNames.length; i++) {
            playerNames[i].setText(names[i] + ":");
            tempScores[i].setText("0");
            totalScores[i].setText("0");
        }

        warningText = findViewById(R.id.warningText);
        endOfGameText = findViewById(R.id.endOfGameText);
        roundName = findViewById(R.id.textview_roundName);
        roundName.setText(getResources().getString(R.string.ducking));

        //Functionality for back button
        previousButton = findViewById(R.id.button_previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //Functionality for undo button
        undoButton = findViewById(R.id.button_undo);
        undoButton.setVisibility(View.INVISIBLE);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Calculates previous scores based on the tempscores of last round
                    for (int i = 0; i < tempScoresInt.length; i++) {
                        if (roundCount < 8) {
                            convertIntToTextView(totalScoresInt[i] - tempScoresInt[i], totalScores[i]);
                        } else {
                            convertIntToTextView(totalScoresInt[i] + tempScoresInt[i], totalScores[i]);
                        }
                        convertIntToEditText(tempScoresInt[i], tempScores[i]);
                    }
                    //Toggles buttons
                    undoButton.setVisibility(View.INVISIBLE);
                    endOfGameText.setText("");
                    calculateButton.setVisibility(View.VISIBLE);
                    newGameButton.setVisibility(View.INVISIBLE);
                    if (roundCount == 7 || roundCount == 11) { //Dropdown should be set to invisible
                        dropDown.setVisibility(View.INVISIBLE);
                    }
                    if (roundCount > 6) {//Removed troef should be added to list again
                        trumps.add(removedTrump);
                    }
                    roundCount--;
                    roundName.setText(getResources().getString(roundNames[roundCount]));
                } catch (NumberFormatException e) {}
            }
        });

        //Functionality for calculating new scores
        calculateButton = findViewById(R.id.calculateScore);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int count = 0;
                    for (int i = 0; i < tempScoresInt.length; i++) {
                        count = count + convertEditTextToInt(tempScores[i]);
                    }
                    //Checks if total score input is correct
                    if (count != pointLimit[roundCount]) {
                        warningText.setText(getResources().getString(warningStrings[roundCount]));
                        return;
                    }
                    //Checks if troef is selected
                    if (roundCount > 6 && dropDown.getSelectedItem().equals("Choose Trump")) {
                        warningText.setText(getResources().getString(R.string.trump_not_selected));
                        return;
                    }
                    warningText.setText("");

                    //Converts temp scores to integers
                    for (int i = 0; i < tempScores.length; i++) {
                        tempScoresInt[i] = convertEditTextToInt(tempScores[i]);
                    }

                    //Calculate new scores and updates them
                    for (int i = 0; i < totalScoresInt.length; i++) {
                        if (roundCount < 7) {//First 7 rounds
                            totalScoresInt[i] = convertTextViewToInt(totalScores[i]) + tempScoresInt[i];
                        } else {//Troef rounds
                            totalScoresInt[i] = convertTextViewToInt(totalScores[i]) - tempScoresInt[i];
                        }
                        convertIntToTextView(totalScoresInt[i], totalScores[i]);
                        tempScores[i].setText("0");
                    }

                    //Updates for the next round
                    undoButton.setVisibility(view.VISIBLE);
                    roundCount++;
                    if (roundCount < 7) {//First 7 rounds
                        roundName.setText(getResources().getString(roundNames[roundCount]));
                    } else if (roundCount < 11) {//Troef rounds
                        roundName.setText(getResources().getString(roundNames[roundCount]));
                        dropDown.setVisibility(View.VISIBLE);
                        if (dropDown.getSelectedItem().equals("Choose Trump")) {//Troef should be selected
                            return;
                        } else {//Troef is selected
                            removedTrump = dropDown.getSelectedItem().toString();
                            trumps.remove(dropDown.getSelectedItem());
                            dropDown.setAdapter(adapter);
                        }
                    } else {//End of game
                        endOfGameText.setText(getResources().getString(R.string.end_of_game));
                        calculateButton.setVisibility(view.INVISIBLE);
                        newGameButton.setVisibility(view.VISIBLE);
                        dropDown.setVisibility(View.INVISIBLE);
                        newGameButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra(NAMES, names);
                                startActivity(intent);
                            }
                        });
                    }
                } catch (NumberFormatException e) {}
            }
        });
    }

    //Converts textview to integer
    public Integer convertTextViewToInt(TextView s) {
        return Integer.parseInt(s.getText().toString());
    }

    //Converts edittext to integer
    public Integer convertEditTextToInt(EditText s) {
        return Integer.parseInt(s.getText().toString());
    }

    //Converts integer to textview
    public void convertIntToTextView(Integer n, TextView s) {
        s.setText(String.valueOf(n));
    }

    //Converts integer to edittext
    public void convertIntToEditText(Integer n, EditText s) {
        s.setText(String.valueOf(n));
    }
}

