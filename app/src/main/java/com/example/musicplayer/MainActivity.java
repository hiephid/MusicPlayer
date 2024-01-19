package com.example.musicplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Song.adapter.SongAdapter;
import com.example.musicplayer.Song.model.Song;
import com.example.musicplayer.User.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    FirebaseAuth auth;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList;
    private FirebaseFirestore db;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các đối tượng và thiết lập RecyclerView
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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
            intent.putExtra("ROUND_IMAGE_URL", song.getRoundImageUrl());
            intent.putExtra("SONG_URL", song.getSongUrl());

            intent.putExtra("SONG_LIST", (Serializable) songList);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songAdapter);

        // Đọc danh sách bài hát từ Firestore và cập nhật RecyclerView
        readSongsFromFirestore();

        // Thiết lập thanh toolbar và điều hướng DrawerLayout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Danh sách bài hát");
        setSupportActionBar(toolbar); // Thiết lập toolbar như ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState(); // Đồng bộ trạng thái của DrawerLayout với ActionBarDrawerToggle
        navigationView.setNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.songlist) {
                    Toast.makeText(MainActivity.this, "SongList", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.logout) {
                    showLogoutConfirmationDialog();
                }

                drawerLayout.closeDrawer(GravityCompat.START); //Đóng DrawerLayout
                return true;
        });
    }

    // Đọc danh sách bài hát từ Firestore
    private void readSongsFromFirestore() {
        db.collection("Songs").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Song song = document.toObject(Song.class);
                        songList.add(song);
                        Log.d("Firestore", "Song Title: " + song.getSongTitle());
                    }
                    songAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting songs from Firestore", e);
                });
    }

    // Nút back
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // MenuItem được chọn
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng Xuất");
        builder.setMessage("Bạn có muốn đăng xuất không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performLogout();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn Không
                dialog.dismiss(); // Tắt hộp thoại
            }
        });
        builder.show();
    }

    private void performLogout() {
        if (auth != null) {
            auth.signOut();
        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}