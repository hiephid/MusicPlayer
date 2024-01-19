package com.example.musicplayer.Song.model;

import android.os.Parcel;
import android.os.Parcelable;

// Song.java
public class Song implements Parcelable {
    private String songID;
    private String songTitle;
    private String artistName;
    private String imageUrl;
    private String roundImageUrl;
    private String songUrl;

    public Song() {
        // Empty constructor needed for Firestore
    }

    public Song(String songID, String songTitle, String artistName, String imageUrl, String roundImageUrl, String songUrl) {
        this.songID = songID;
        this.songTitle = songTitle;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.songUrl = songUrl;
        this.roundImageUrl = roundImageUrl;
    }

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

    // Parcelable implementation
    protected Song(Parcel in) {
        songID = in.readString();
        songTitle = in.readString();
        artistName = in.readString();
        imageUrl = in.readString();
        roundImageUrl = in.readString();
        songUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songID);
        dest.writeString(songTitle);
        dest.writeString(artistName);
        dest.writeString(imageUrl);
        dest.writeString(roundImageUrl);
        dest.writeString(songUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

