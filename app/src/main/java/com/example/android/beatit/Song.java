package com.example.android.beatit;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cristina on 21/05/2017.
 * From https://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
 * This class models the data for a single audio file.
 * This class implements parcelable in order to allow transfer data between activities
 * https://7lanterns.wordpress.com/2013/02/23/parcelable-in-android-for-data-passing-between-activities/
 */

public class Song implements Parcelable {

    private Bitmap image;
    private String title, artist;
    private Uri uri;

    public Song(Bitmap songImage, String songTitle, String songArtist, Uri songUri) {
        image = songImage;
        title = songTitle;
        artist = songArtist;
        uri = songUri;
    }

    public Bitmap getSongImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(image);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeValue(uri);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    // De-parcel object
    public Song(Parcel in) {
        image = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        title = in.readString();
        artist = in.readString();
        uri = (Uri) in.readValue(Uri.class.getClassLoader());
    }
}
