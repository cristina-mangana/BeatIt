package com.example.android.beatit;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristina on 22/05/2017.
 * This class models the data for a single playlist.
 * This class implements parcelable in order to allow transfer data between activities. From
 * https://developer.android.com/reference/android/os/Parcelable.html
 * http://www.survivingwithandroid.com/2015/05/android-parcelable-tutorial-list-class-2.html
 * https://stackoverflow.com/questions/6300608/how-to-pass-a-parcelable-object-that-contains-a-list-of-objects
 */

public class Playlist implements Parcelable {

    private Bitmap cover;
    private String name, songSample;
    private List<Song> songs;

    public Playlist(String playlistName, List<Song> songList) {
        cover = songList.get(0).getSongImage();
        name = playlistName;
        songSample = songList.get(0).getTitle();
        songs = songList;
    }

    public Bitmap getPlaylistImage() {
        return cover;
    }

    public String getPlaylistName() {
        return name;
    }

    public String getPlaylistSongSample() {
        return songSample;
    }

    public List<Song> getSongs() {
        return songs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(songs);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    // De-parcel object
    // https://stackoverflow.com/questions/6300608/how-to-pass-a-parcelable-object-that-contains-a-list-of-objects
    public Playlist(Parcel in) {
        name = in.readString();
        songs = new ArrayList<>();
        in.readTypedList(songs, Song.CREATOR);
    }
}
