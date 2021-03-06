package com.example.android.beatit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cristina on 23/05/2017.
 * This adapter provides access to the items in the data set, creates views for items, and replaces
 * the content of some of the views with new data items when the original item is no longer visible.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder>{
    private Context mContext;
    private List<Song> songsList;
    public static MyAdapterListener onClickListener;

    // Handle button click inside the recyclerView: https://stackoverflow.com/questions/30284067/handle-button-click-inside-a-row-in-recyclerview
    public interface MyAdapterListener {
        void playButtonOnClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cover;
        public TextView title, artist;

        public ViewHolder(View view) {
            super(view);
            cover = (ImageView) view.findViewById(R.id.song_image);
            title = (TextView) view.findViewById(R.id.song_title);
            artist = (TextView) view.findViewById(R.id.artist_name);
            FloatingActionButton playButton = (FloatingActionButton) itemView.findViewById(R.id.play_fab);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.playButtonOnClick(v, getAdapterPosition());
                }
            });
        }
    }

    public SongsAdapter(List<Song> songsList, MyAdapterListener listener, Context context) {
        this.songsList = songsList;
        onClickListener = listener;
        mContext = context;
    }

    @Override
    public SongsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_cardview, parent, false);
        // Set the view's parameters
        //Get the device screen dimensions https://stackoverflow.com/questions/14267582/get-screen-dimensions-inside-page-adapter
        int screenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = parent.getContext().getResources().getDisplayMetrics().heightPixels;
        //Find the Layout
        ImageView imageViewCover = (ImageView) itemView.findViewById(R.id.song_image);
        //Set the dimensions of the image based on the screen dimensions
        if (screenWidth < screenHeight) {
            imageViewCover.setMinimumHeight((int) (0.7 * screenWidth));
            imageViewCover.setMinimumWidth((int) (0.7 * screenWidth));
        } else {
            imageViewCover.setMinimumHeight((int) (0.3 * screenHeight));
            imageViewCover.setMinimumWidth((int) (0.3 * screenHeight));
        }
        // Set the font's path
        String fontPathRobotoRegular = "fonts/Roboto-Regular.ttf";
        // Get the id TextView
        TextView textViewTitle = (TextView) itemView.findViewById(R.id.song_title);
        TextView textViewArtist = (TextView) itemView.findViewById(R.id.artist_name);
        // Load the font as a TypeFace object
        Typeface typeFaceRobotoRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), fontPathRobotoRegular);
        // Set the extra fancy font in the customFont TextView
        textViewTitle.setTypeface(typeFaceRobotoRegular);
        textViewArtist.setTypeface(typeFaceRobotoRegular);

        return new SongsAdapter.ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SongsAdapter.ViewHolder holder, int position) {
        Song song = songsList.get(position);
        // Get the cover art if it's attached to the audio file
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        mmr.setDataSource(mContext, song.getUri());
        rawArt = mmr.getEmbeddedPicture();

        // if rawArt is null, no cover art is embedded or is not recognized as such. Then,
        // a default cover is assigned.
        if (rawArt != null) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        } else {
            // Convert a drawable resource to bitmap http://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
            art = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.samplecover_300);
        }
        holder.cover.setImageBitmap(art);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return songsList.size();
    }
}
