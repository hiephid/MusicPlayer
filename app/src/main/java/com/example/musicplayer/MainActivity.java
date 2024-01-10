package com.example.musicplayer;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Song.adapter.SongAdapter;
import com.example.musicplayer.Song.model.Song;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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