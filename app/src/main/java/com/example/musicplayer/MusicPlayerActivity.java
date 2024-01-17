package com.example.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class MusicPlayerActivity extends AppCompatActivity {

    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView artistTextView;
    private SeekBar seekBar;
    private ImageButton prevButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton loopButton;
    private ImageButton shuffleButton;

    private MediaPlayer mediaPlayer;
    private Handler seekBarHandler;

    private static final int STATE_STOPPED = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSED = 2;

    private int playerState = STATE_STOPPED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Initialize views
        coverImageView = findViewById(R.id.coverImageView);
        titleTextView = findViewById(R.id.titleTextView);
        artistTextView = findViewById(R.id.artistTextView);
        seekBar = findViewById(R.id.seekBar);
        prevButton = findViewById(R.id.prevButton);
        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        loopButton = findViewById(R.id.loopButton);
        shuffleButton = findViewById(R.id.shuffleButton);

        // Retrieve song data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String songId = intent.getStringExtra("SONG_ID");
            String songTitle = intent.getStringExtra("SONG_TITLE");
            String artistName = intent.getStringExtra("ARTIST_NAME");
            String imageUrl = intent.getStringExtra("IMAGE_URL");
            String songUrl = intent.getStringExtra("SONG_URL");

            // Update UI with song information
            titleTextView.setText(songTitle);
            artistTextView.setText(artistName);
            Glide.with(this).load(imageUrl).into(coverImageView);

            // Initialize MediaPlayer
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(mp -> {
                // Start playing when MediaPlayer is prepared
                mp.start();
                updateSeekBar();
                playerState = STATE_PLAYING;
            });

            playPauseButton.setOnClickListener(v -> {
                if (mediaPlayer != null) {
                    if (playerState == STATE_PLAYING) {
                        pauseMusic();
                    } else {
                        playMusic();
                    }
                }
            });
        } else {
            // Handle case when no song data is available
            finish();
        }
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playerState = STATE_PLAYING;
            updateSeekBar();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playerState = STATE_PAUSED;
            seekBarHandler.removeCallbacksAndMessages(null); // Dừng cập nhật thanh SeekBar
        }
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());

        seekBarHandler = new Handler();
        seekBarHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                seekBarHandler.postDelayed(this, 1000); // Cập nhật mỗi giây
            }
        }, 1000);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}
