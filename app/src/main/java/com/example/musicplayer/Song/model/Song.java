package com.example.musicplayer.Song.model;

import android.os.Parcel;
import android.os.Parcelable;

// Lớp biểu diễn đối tượng Song
public class Song implements Parcelable {
    private String songID;
    private String songTitle;
    private String artistName;
    private String imageUrl;
    private String roundImageUrl;
    private String songUrl;

    // Constructor
    public Song() {
    }

    // Constructor với tham số
    public Song(String songID, String songTitle, String artistName, String imageUrl, String roundImageUrl, String songUrl) {
        this.songID = songID;
        this.songTitle = songTitle;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.songUrl = songUrl;
        this.roundImageUrl = roundImageUrl;
    }

    // Getter
    public String getSongID() {
        return songID;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRoundImageUrl() {
        return roundImageUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    // Constructor để tạo đối tượng Song từ Parcel
    protected Song(Parcel in) {
        songID = in.readString();
        songTitle = in.readString();
        artistName = in.readString();
        imageUrl = in.readString();
        roundImageUrl = in.readString();
        songUrl = in.readString();
    }

    // Phương thức để ghi giá trị của đối tượng Song vào Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songID);
        dest.writeString(songTitle);
        dest.writeString(artistName);
        dest.writeString(imageUrl);
        dest.writeString(roundImageUrl);
        dest.writeString(songUrl);
    }

    // Phương thức mô tả nếu có các đối tượng đặc biệt trong Parcel
    @Override
    public int describeContents() {
        return 0;
    }

    // Creator để tạo đối tượng Song từ Parcel và mảng Song từ size (Đóng gói)
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}

