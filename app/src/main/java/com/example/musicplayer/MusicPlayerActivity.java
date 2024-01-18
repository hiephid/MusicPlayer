package com.example.musicplayer;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Song.model.Song;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView artistTextView;
    private TextView total_Time;
    private TextView start_Time;

    private SeekBar seekBar;
    private ImageButton prevButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton loopButton;
    private ImageButton shuffleButton;

    private MediaPlayer mediaPlayer;
    private Handler seekBarHandler;
    private List<Song> songList;
    //-----------nút ngau nhien
    private boolean shuffleMode = false;
    private static final int STATE_STOPPED = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSED = 2;

    private int playerState = STATE_STOPPED;

    private Object lock = new Object();

    // luu tru dung de tim kiem
    String songID;
    String songTitle;
    String artistName;
    String imageUrl;
    String songUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Anhxa();
        prepareMediaPlayer();
        // Retrieve song data from Intent
        Intent intent = getIntent();


        if (intent != null) {
            songID = intent.getStringExtra("SONG_ID");
//            currentSongId = songId;
            songTitle = intent.getStringExtra("SONG_TITLE");
            artistName = intent.getStringExtra("ARTIST_NAME");
            imageUrl = intent.getStringExtra("IMAGE_URL");
            songUrl = intent.getStringExtra("SONG_URL");

            songList = (List<Song>) getIntent().getSerializableExtra("SONG_LIST");
//            Toast.makeText(MusicPlayerActivity.this, "Length of songList: " + songList.size(), Toast.LENGTH_SHORT).show();


            updateUI();


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
                updateTimeSong();
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

        //        Toast.makeText(MusicPlayerActivity.this, "currentSongID oncreate: " + currentSongId, Toast.LENGTH_SHORT).show();

        nextButton.setOnClickListener(e -> playNextSong());
        prevButton.setOnClickListener(e -> playPreSong());


        //-----xu ly nut ngau nhien---
        shuffleButton.setOnClickListener(e -> toggleShuffleMode());




        //----su kien nguoi dung tuong tac voi seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });









        //---------------------------------------------

    }

    //---------ham xu ly nut ngau nhien
    private void toggleShuffleMode() {
        shuffleMode = !shuffleMode;
        // Update the UI to reflect the shuffle mode status (you can add an icon change or other visual indication)

        if (shuffleMode) {
            // Shuffle mode is enabled, update UI accordingly
            // You may also want to update the logic for the "Next" button when shuffle mode is enabled
            Toast.makeText(this, "Activate", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(false);
        } else {
            // Shuffle mode is disabled, update UI accordingly
            // Revert the logic for the "Next" button when shuffle mode is disabled
            Toast.makeText(this, "Deativate", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(true);
        }
    }
    private int getRandomPosition() {
        // Generate a random position within the songList size
        return (int) (Math.random() * songList.size());
    }








    // ---------------------------








    // -----------------------------------------------
    // Thêm vào class MusicPlayerActivity
    private void playNextSong() {
        if (songList != null && songList.size() > 0 && mediaPlayer != null) {
            int currentPosition = getCurrentSongPosition();
            int nextPosition;
            if (shuffleMode) {
                nextPosition = getRandomPosition();
            } else {
                nextPosition = (currentPosition + 1);
                if (currentPosition == songList.size() - 1) {
                    nextPosition = 0;
                }
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            // Lấy thông tin của bài hát kế tiếp
            Song nextSong = songList.get(nextPosition);

            //Toast.makeText(MusicPlayerActivity.this, "Next Song ID: " + nextSong.getSongID(), Toast.LENGTH_SHORT).show();

            // Phát bài hát kế tiếp
            playSong(nextSong);

            updateUI(nextPosition);


            // update current
            songID = nextSong.getSongID();
//            Toast.makeText(MusicPlayerActivity.this, "currentSongID" + currentSongId, Toast.LENGTH_SHORT).show();

        }
    }

    //--------------------------------------
    // xu ly prev song
    private void playPreSong() {
        if (songList != null && songList.size() > 0 && mediaPlayer != null) {
            int currentPosition = getCurrentSongPosition();
            int prePosition = (currentPosition - 1);

            if (currentPosition == 0) {
                prePosition = songList.size() - 1;
            }

            // Lấy thông tin của bài hát trước
            Song preSong = songList.get(prePosition);

            // Phát bài hát trước
            playSong(preSong);


            updateUI(prePosition);


            // update current
            songID = preSong.getSongID();
//            Toast.makeText(MusicPlayerActivity.this, "currentSongID" + currentSongId, Toast.LENGTH_SHORT).show();




        }

    }


    private int getCurrentSongPosition() {
        if (songList != null && songList.size() > 0 && mediaPlayer != null) {
            for (int i = 0; i < songList.size(); i++) {
                if (songList.get(i).getSongID().equals(songID)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(song.getSongUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    //--------------------------------------------

    private void prepareMediaPlayer() {

    }

    private void Anhxa() {
        // Initialize view(ánh xạ)
        coverImageView = findViewById(R.id.coverImageView);
        titleTextView = findViewById(R.id.titleTextView);
        artistTextView = findViewById(R.id.artistTextView);
        total_Time = findViewById(R.id.txt_totalTime);
        start_Time = findViewById(R.id.txt_timeStart);
        seekBar = findViewById(R.id.seekBar);
        prevButton = findViewById(R.id.prevButton);
        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        loopButton = findViewById(R.id.loopButton);
        shuffleButton = findViewById(R.id.shuffleButton);

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
            // seekBarHandler.removeCallbacksAndMessages(null); // Dừng cập nhật thanh SeekBar
        }
    }

    //----------lien quan xu ly seekbar-------------------
    private void updateTimeSong() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (mediaPlayer != null) {
                        SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                        start_Time.setText(dinhDangGio.format(mediaPlayer.getCurrentPosition()));

                        //update progress seekbar
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }

                // kiem tra thoi gian ket thuc bai hat->next.
                if (mediaPlayer != null) {
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playNextSong();
                        }
                    });
                }

                handler.postDelayed(this, 500); //1phan 10 giay
            }
        }, 100);
    }

    }








    //-----------------------------------------------








    private void updateSeekBar() {
        //
        SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
        total_Time.setText(dinhDangGio.format(mediaPlayer.getDuration()));
        // gan max tg cua bai hat = mediaplayer.getduration
        seekBar.setMax(mediaPlayer.getDuration());

//        seekBarHandler = new Handler();
//        seekBarHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mediaPlayer != null) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    seekBar.setProgress(currentPosition);
//                }
//                seekBarHandler.postDelayed(this, 1000); // Cập nhật mỗi giây
//            }
//        }, 1000);
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

    void updateUI() {
        // Update UI with song information
        titleTextView.setText(songTitle);
        artistTextView.setText(artistName);
        Glide.with(this).load(imageUrl).into(coverImageView);
    }

    private void updateUI(int i) {
        titleTextView.setText(songList.get(i).getSongTitle());
        artistTextView.setText(songList.get(i).getArtistName());
        Glide.with(MusicPlayerActivity.this).load(songList.get(i).getImageUrl()).into(coverImageView);
    }
}