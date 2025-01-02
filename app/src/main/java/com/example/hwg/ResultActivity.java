// src/java/com/example/hollywoodgame/ResultActivity.java
package com.example.hwg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView winnerText = findViewById(R.id.winner_text);
        TextView actualWordText = findViewById(R.id.actual_word_text);
        Button replayButton = findViewById(R.id.replay_button);
        Button exitButton = findViewById(R.id.exit_button);

        String winner = getIntent().getStringExtra("winner");
        String actualWord = getIntent().getStringExtra("actualWord");

        winnerText.setText("Winner: " + winner);
        actualWordText.setText("Actual Word: " + actualWord);

        replayButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GameSettingsActivity.class);
            startActivity(intent);
            finish();
        });

        exitButton.setOnClickListener(v -> {
            // Exit the app
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity(); // Close all activities
            System.exit(0); // Ensure the app is terminated
        });

    }
}
