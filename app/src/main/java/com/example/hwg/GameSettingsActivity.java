package com.example.hwg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameSettingsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private MediaPlayer mediaPlayer;
    private boolean playWithComputer = true;
    private String difficultyLevel = "Easy";
    private String backgroundImagePath; // File path for the selected background

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        RadioGroup playModeGroup = findViewById(R.id.play_mode_group);
        RadioGroup levelGroup = findViewById(R.id.level_group);
        Switch musicSwitch = findViewById(R.id.music_switch);
        Button selectBackgroundButton = findViewById(R.id.select_background_button);
        Button playNowButton = findViewById(R.id.play_now);

        // Initialize MediaPlayer for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        if (mediaPlayer == null) {
            Toast.makeText(this, "Error: Missing background music file!", Toast.LENGTH_SHORT).show();
        }

        // Music Toggle
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            } else if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        // Play Mode Selection
        playModeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            playWithComputer = (checkedId == R.id.play_with_computer);
        });

        // Difficulty Level Selection
        levelGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.level_easy) difficultyLevel = "Easy";
            else if (checkedId == R.id.level_medium) difficultyLevel = "Medium";
            else difficultyLevel = "Hard";
        });

        // Handle background selection
        selectBackgroundButton.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, PICK_IMAGE);
        });

        // Play Now Button
        playNowButton.setOnClickListener(v -> {
            if (playWithComputer && difficultyLevel.isEmpty()) {
                Toast.makeText(this, "Select difficulty level!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(GameSettingsActivity.this, GameActivity.class);
            intent.putExtra("playWithComputer", playWithComputer);
            intent.putExtra("difficultyLevel", difficultyLevel);
            intent.putExtra("backgroundImagePath", backgroundImagePath); // Pass the background image path
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                backgroundImagePath = saveImageToCache(bitmap); // Save the image and get the file path
                Toast.makeText(this, "Background selected successfully!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error selecting background!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToCache(Bitmap bitmap) {
        File cacheDir = getCacheDir();
        File file = new File(cacheDir, "background_image.png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
