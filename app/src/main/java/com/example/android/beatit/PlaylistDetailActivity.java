package com.example.android.beatit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        final Playlist playlist = getIntent().getExtras().getParcelable("playlist");
        List<Song> songsList = playlist.getSongs();

        //Toolbar settings
        Toolbar myToolbar = (Toolbar) findViewById(R.id.playlist_detail_toolbar);
        myToolbar.setTitle(playlist.getPlaylistName());
        setSupportActionBar(myToolbar);
        //Add back button https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar-android
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Total number of songs header
        TextView textViewNumberOfSongs = (TextView) findViewById(R.id.numberOfSongs);
        int numberOfSongs = songsList.size();
        String stringAvailableSongs = getString(R.string.available_songs, numberOfSongs);
        //Look for numbers
        int positionMin = -1;
        int positionMax = -1;
        for (int i = 0; i < stringAvailableSongs.length(); i++){
            char c = stringAvailableSongs.charAt(i);
            if (c >= '0' && c <= '9') {
                if (positionMin == -1) {
                    positionMin = i;
                    positionMax = i;
                } else {
                    positionMax = i;
                }
            }
        }
        //Different style for numbers
        SpannableStringBuilder spannableNumberOfSongs = new SpannableStringBuilder(stringAvailableSongs);
        spannableNumberOfSongs.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), positionMin, positionMax + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewNumberOfSongs.setText(spannableNumberOfSongs);

        //RecyclerView settings
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.songs_recycler_view);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // add snap effect http://www.devexchanges.info/2016/09/android-tip-recyclerview-snapping-with.html
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        // specify an adapter
        SongsAdapter mAdapter = new SongsAdapter(songsList, new SongsAdapter.MyAdapterListener() {
            @Override
            public void playButtonOnClick(View v, int position) {
                Intent openActivityNowPlaying = new Intent(getApplicationContext(), NowPlayingActivity.class);
                openActivityNowPlaying.putExtra("playlist", playlist);
                openActivityNowPlaying.putExtra("position", position);
                startActivity(openActivityNowPlaying);
            }
        }, this);
        mRecyclerView.setAdapter(mAdapter);

        //Bottom toolbar settings
        ActionMenuView bottomToolbar = (ActionMenuView) findViewById(R.id.bottom_bar);
        Menu bottomMenu = bottomToolbar.getMenu();
        getMenuInflater().inflate(R.menu.bottom_bar_menu, bottomMenu);
        for (int i = 0; i < bottomMenu.size(); i++) {
            bottomMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.menu_playlists:
                            finish();
                            return true;
                        case R.id.menu_play:
                            Intent openActivityNowPlaying = new Intent(getApplicationContext(), NowPlayingActivity.class);
                            openActivityNowPlaying.putExtra("playlist", playlist);
                            openActivityNowPlaying.putExtra("position", 0);
                            startActivity(openActivityNowPlaying);
                            return true;
                        case R.id.menu_discover:
                            Intent openActivityDiscover = new Intent(getApplicationContext(), DiscoverActivity.class);
                            String songSample = playlist.getSongs().get(0).getTitle();
                            openActivityDiscover.putExtra("songSample", songSample);
                            startActivity(openActivityDiscover);
                            return true;
                    }
                    return onOptionsItemSelected(item);
                }
            });
        }
    }

    // Handle back button on toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar_menu, menu);
        menu.getItem(0).setIcon(R.drawable.ic_search_black_24dp);
        return true;
    }

    // Handle clicks on toolbar menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Intent openActivitySearch = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(openActivitySearch);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
