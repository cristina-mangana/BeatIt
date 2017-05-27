package com.example.android.beatit;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cristina on 20/05/2017.
 * From https://developer.android.com/training/material/lists-cards.html#RVExamples
 * This adapter provides access to the items in the data set, creates views for items, and replaces
 * the content of some of the views with new data items when the original item is no longer visible.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{
    private Context mContext;
    private List<Playlist> playlistsList;
    private View.OnClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView playlistImage;
        public TextView playlistName, playlistSongs;

        public ViewHolder(View view) {
            super(view);
            playlistImage = (ImageView) view.findViewById(R.id.playlist_image);
            playlistName = (TextView) view.findViewById(R.id.playlist_name);
            playlistSongs = (TextView) view.findViewById(R.id.playlist_songs);
        }
    }

    public PlaylistAdapter(List<Playlist> playlistsList, Context context) {
        this.playlistsList = playlistsList;
        mContext = context;
    }

    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_row, parent, false);
        // Set the view's parameters
        // Set the font's path
        String fontPathRobotoRegular = "fonts/Roboto-Regular.ttf";
        // Get the id TextView
        TextView textViewName = (TextView) itemView.findViewById(R.id.playlist_name);
        TextView textViewSongs = (TextView) itemView.findViewById(R.id.playlist_songs);
        // Load the font as a TypeFace object
        Typeface typeFaceRobotoRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), fontPathRobotoRegular);
        // Set the extra fancy font in the customFont TextView
        textViewName.setTypeface(typeFaceRobotoRegular);
        textViewSongs.setTypeface(typeFaceRobotoRegular);

        // Manage Item Click Listener. From https://newfivefour.com/android-item-click-listener-recyclerview.html
        PlaylistAdapter.ViewHolder holder = new ViewHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(v);
            }
        });
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Playlist playlist = playlistsList.get(position);
        holder.playlistImage.setImageBitmap(playlist.getPlaylistImage());
        holder.playlistName.setText(playlist.getPlaylistName());
        holder.playlistSongs.setText(mContext.getString(R.string.others, playlist.getPlaylistSongSample()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return playlistsList.size();
    }

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }
}
