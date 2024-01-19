package com.example.musicplayer.Song.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Song.model.Song;

import com.bumptech.glide.Glide;

import java.util.List;

// Lớp adapter cho RecyclerView để hiển thị danh sách bài hát
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private List<Song> songList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Constructor khởi tạo dữ liệu cho adapter
    public SongAdapter(List<Song> songList, Context context, OnItemClickListener onItemClickListener) {
        this.songList = songList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    // Tạo mới ViewHolder (đối tượng chứa view item)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ layout item_song.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    // Phương thức này gắn dữ liệu từ đối tượng Song vào ViewHolder tương ứng
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);

        // Gán dữ liệu từ đối tượng Song vào các thành phần trong ViewHolder
        holder.titleTextView.setText(song.getSongTitle());
        holder.artistTextView.setText(song.getArtistName());

        // Sử dụng thư viện Glide để tải ảnh từ URL và hiển thị trong ImageView
        Glide.with(context)
                .load(song.getImageUrl())
                .into(holder.imageView);

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(song));
    }

    // Số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Lớp ViewHolder định nghĩa cấu trúc của một item trong RecyclerView
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, artistTextView;
        ImageView imageView;

        // Constructor của ViewHolder, ánh xạ các thành phần từ layout item_song.xml
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    // Interface định nghĩa sự kiện nghe khi item được click
    public interface OnItemClickListener {
        void onItemClick(Song song);
    }
}

