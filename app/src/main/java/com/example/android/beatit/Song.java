package com.example.android.beatit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

    private Context mContext;
    private String title, artist;
    private Uri uri;

    public Song(Context context, String songTitle, String songArtist, Uri songUri) {
        mContext = context;
        title = songTitle;
        artist = songArtist;
        uri = songUri;
    }

    public Bitmap getSongImage() {
        // Get the cover art if it's attached to the audio file
        // Help from http://stackoverflow.com/questions/15314617/get-cover-picture-by-song
        // and http://stackoverflow.com/questions/31178275/getting-audio-file-path-or-uri-from-mediastore
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        mmr.setDataSource(mContext, uri);
        rawArt = mmr.getEmbeddedPicture();

        // if rawArt is null, no cover art is embedded or is not recognized as such. Then,
        // a default cover is assigned.
        if (rawArt != null) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        } else {
            // Convert a drawable resource to bitmap http://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
            art = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.samplecover_300);
        }
        return art;
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
        title = in.readString();
        artist = in.readString();
        uri = (Uri) in.readValue(Uri.class.getClassLoader());
    }
}
