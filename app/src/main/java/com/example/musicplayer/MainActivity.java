package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Song.adapter.SongAdapter;
import com.example.musicplayer.Song.model.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(songList, this, song -> {
            // Xử lý khi một item trong RecyclerView được click
            // Chuyển đến trang chơi nhạc và chuyển dữ liệu bài hát
            Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_ID", song.getSongID()); // Truyền ID của bài hát
            intent.putExtra("SONG_TITLE", song.getSongTitle());
            intent.putExtra("ARTIST_NAME", song.getArtistName());
            intent.putExtra("IMAGE_URL", song.getImageUrl());
            intent.putExtra("SONG_URL", song.getSongUrl());

            intent.putExtra("SONG_LIST", (Serializable) songList);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songAdapter);

        readSongsFromFirestore();

    }

    private void readSongsFromFirestore() {
        db.collection("Songs").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Song song = document.toObject(Song.class);
                        songList.add(song);
                        Log.d("Firestore", "Song Title: " + song.getSongTitle());
                    }
                    songAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting songs from Firestore", e);
                });
    }



}