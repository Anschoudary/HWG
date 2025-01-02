// src/java/com/example/hollywoodgame/GameActivity.java
package com.example.hwg;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private String word = "";
    private StringBuilder hiddenWord = new StringBuilder();
    private StringBuilder mistakes = new StringBuilder();
    private int mistakesCount = 0;
    private boolean playWithComputer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout gameLayout = findViewById(R.id.screen_layout);

        // Retrieve background image path
        String backgroundImagePath = getIntent().getStringExtra("backgroundImagePath");
        if (backgroundImagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(backgroundImagePath);
            Drawable background = new BitmapDrawable(getResources(), bitmap);
            gameLayout.setBackground(background);
        }

        playWithComputer = getIntent().getBooleanExtra("playWithComputer", true);
        String difficultyLevel = getIntent().getStringExtra("difficultyLevel");

        TextView wordDisplay = findViewById(R.id.word_display);
        TextView mistakesField = findViewById(R.id.mistakes_field);
        TextView chancesRemaining = findViewById(R.id.chances_remaining);
        EditText inputWord = findViewById(R.id.input_word);
        EditText guessInput = findViewById(R.id.guess_input);
        Button startGameButton = findViewById(R.id.start_game_button);
        Button guessButton = findViewById(R.id.guess_button);

        startGameButton.setOnClickListener(v -> {
            if (playWithComputer) {
                word = getRandomWord(difficultyLevel).toLowerCase();
                inputWord.setVisibility(View.GONE); // Hide input for computer mode
            } else {
                word = inputWord.getText().toString().toLowerCase();
            }

            if (word.isEmpty()) {
                Toast.makeText(this, "Enter a valid word!", Toast.LENGTH_SHORT).show();
                return;
            }

            hiddenWord.setLength(0);
            for (int i = 0; i < word.length(); i++) hiddenWord.append("_");
            wordDisplay.setText(hiddenWord.toString());
            mistakes.setLength(0);
            mistakesCount = 0;
            mistakesField.setText("Mistakes: ");
            chancesRemaining.setText("Chances left: 5");
        });

        guessButton.setOnClickListener(v -> {
            String guess = guessInput.getText().toString().toLowerCase();
            guessInput.setText("");

            if (guess.isEmpty() || guess.length() > 1) {
                Toast.makeText(this, "Enter a single letter!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (word.contains(guess)) {
                for (int i = 0; i < word.length(); i++) {
                    if (word.charAt(i) == guess.charAt(0)) {
                        hiddenWord.setCharAt(i, guess.charAt(0));
                    }
                }
                wordDisplay.setText(hiddenWord.toString());
            } else {
                mistakesCount++;
                mistakes.append(guess).append(" ");
                mistakesField.setText("Mistakes: " + mistakes);
                chancesRemaining.setText("Chances left: " + (5 - mistakesCount));
            }

            if (hiddenWord.toString().equals(word) || mistakesCount >= 5) {
                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                intent.putExtra("winner", mistakesCount < 5 ? "Player 2" : "Player 1");
                intent.putExtra("actualWord", word);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getRandomWord(String difficultyLevel) {
        String[] easyWords = {"apple", "ball", "cat", "dog", "fish", "guitar", "house", "ice", "jacket", "table", "water", "zebra"};
        String[] mediumWords = {"orange", "guitar", "pencil", "brain", "star", "door", "football", "stop", "lunch", "grapes", "duck", "mouse"};
        String[] hardWords = {"elephant", "umbrella", "chocolate", "xylophone", "nasty", "museum", "alphabet", "daffodil", "basketball", "storm"};

        String[] words;
        if (difficultyLevel.equals("Easy")) {
            words = easyWords;
        } else if (difficultyLevel.equals("Medium")) {
            words = mediumWords;
        } else {
            words = hardWords;
        }

        return words[new Random().nextInt(words.length)];
    }
}