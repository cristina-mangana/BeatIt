package com.example.android.beatit;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Song> allMusicFiles = new ArrayList<>();
    List<Song> recordingsPlaylist = new ArrayList<>();
    List<Song> voiceNotesPlaylist = new ArrayList<>();
    List<Song> unknownArtistPlaylist = new ArrayList<>();
    List<Song> allSongsPlaylist = new ArrayList<>();
    ArrayList<Song> randomPlaylist = new ArrayList<>();
    List<Playlist> playlistsList = new ArrayList<>();
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore state from saved instance
        FloatingActionButton addNewFab = (FloatingActionButton) findViewById(R.id.add_fab);
        if (savedInstanceState != null) {
            Boolean isOpenSaved = savedInstanceState.getBoolean("isOpenSaved");
            randomPlaylist = savedInstanceState.getParcelableArrayList("randomList");
            // Apply stored items
            if (isOpenSaved) {
                addNewFab.callOnClick();
            }
        }

        //Toolbar settings
        Toolbar myToolbar = (Toolbar) findViewById(R.id.playlists_toolbar);
        myToolbar.setTitle(getString(R.string.playlists));
        setSupportActionBar(myToolbar);

        //Create playlistsList
        sortMusicInPlaylists();
        if (!recordingsPlaylist.isEmpty()) {
            playlistsList.add(new Playlist(getString(R.string.recordings), recordingsPlaylist));
        }
        if (!voiceNotesPlaylist.isEmpty()) {
            playlistsList.add(new Playlist(getString(R.string.voiceNotes), voiceNotesPlaylist));
        }
        if (!unknownArtistPlaylist.isEmpty()) {
            playlistsList.add(new Playlist(getString(R.string.unknownArtist), unknownArtistPlaylist));
        }
        if (!allSongsPlaylist.isEmpty()) {
            playlistsList.add(new Playlist(getString(R.string.allSongs), allSongsPlaylist));
        }
        if (!randomPlaylist.isEmpty()) {
            playlistsList.add(new Playlist(getString(R.string.random), randomPlaylist));
        }

        // Sort playlistsList alphabetically. From http://stackoverflow.com/questions/32419136/how-to-sort-strings-on-an-android-recyclerview
        if (playlistsList.size() > 1) {
            Collections.sort(playlistsList, new Comparator<Playlist>() {
                @Override
                public int compare(Playlist a, Playlist b) {
                    return a.getPlaylistName().compareTo(b.getPlaylistName());
                }
            });
        }

        //RecyclerView settings (from https://developer.android.com/training/material/lists-cards.html#RVExamples)
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.playlists_recycler_view);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // add divider decoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        // set animator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // specify an adapter
        PlaylistAdapter mAdapter = new PlaylistAdapter(playlistsList, this);
        mAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerView.indexOfChild(v);
                Playlist playlist = playlistsList.get(position);
                Intent openActivityDetail = new Intent(getApplicationContext(), PlaylistDetailActivity.class);
                openActivityDetail.putExtra("playlist", playlist);
                startActivity(openActivityDetail);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        //Listen to add new playlist button
        addNewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.not_add_playlist);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isOpen = false;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                isOpen = true;
            }
        });
    }

    // Fires when a configuration change occurs and fragment needs to save state
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("isOpenSaved",isOpen);
        savedInstanceState.putParcelableArrayList("randomList", randomPlaylist);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar_menu, menu);
        return true;
    }

    // Handle clicks on menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Intent openActivityDetail = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(openActivityDetail);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is a helper method that retrieves information from the audio files stored in the
     * device and sort data as a list in alphabetical order.
     * From https://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
     */
    public void getMusicFilesList() {
        // Retrieve the URI for external music files
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        // Iterate over the results, first checking that we have valid data
        if (musicCursor != null && musicCursor.moveToFirst()) {
            // Get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            // Add songs to list
            do {
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisData = musicCursor.getString(dataColumn);
                Uri songUri = Uri.parse("file:///" + thisData);
                allMusicFiles.add(new Song(getApplicationContext(), thisTitle, thisArtist, songUri));
            }
            while (musicCursor.moveToNext());
        } else {
            // Example list for testing purposes only, if there're no available songs on the device
            List<Uri> uriList = new ArrayList<>();
            Uri mediaPathTheSuburbs = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.af_thesuburbs);
            Uri mediaPathWannaKnow = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.am_doiwannaknow);
            Uri mediaPathUprising = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.muse_uprising);
            uriList.add(mediaPathTheSuburbs);
            uriList.add(mediaPathWannaKnow);
            uriList.add(mediaPathUprising);
            for (int i = 0; i < uriList.size(); i++) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(this, uriList.get(i));
                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                allMusicFiles.add(new Song(getApplicationContext(), title, artist, uriList.get(i)));
            }
        }
        musicCursor.close();

        // Sort alphabetically. From http://stackoverflow.com/questions/32419136/how-to-sort-strings-on-an-android-recyclerview
        Collections.sort(allMusicFiles, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    /**
     * This helper method sorts Music Files into different default playlists.
     */
    public void sortMusicInPlaylists() {
        getMusicFilesList();

        for (int i = 0; i < allMusicFiles.size(); i++) {
            Song song = allMusicFiles.get(i);
            String title = song.getTitle();
            String artist = song.getArtist();

            // Check if audio file names contains a keyword, filtering recordings and voice notes.
            // contains() method from http://stackoverflow.com/questions/17134773/to-check-if-string-contains-particular-word
            if (title.toLowerCase().contains(getString(R.string.record))
                    && artist.toLowerCase().contains(getString(R.string.unknown))) {
                recordingsPlaylist.add(song);
            } else if (title.toLowerCase().contains(getString(R.string.audio))
                    && artist.toLowerCase().contains(getString(R.string.unknown))) {
                voiceNotesPlaylist.add(song);
            } else if (artist.toLowerCase().contains(getString(R.string.unknown))) {
                unknownArtistPlaylist.add(song);
            } else {
                allSongsPlaylist.add(song);
            }
        }

        // Create random playlist with 60% of available songs. shuffle() method from
        // http://stackoverflow.com/questions/8115722/generating-unique-random-numbers-in-java
        if (randomPlaylist.isEmpty()) {
            long numberOfRandomSongs = Math.round(0.6 * allSongsPlaylist.size());
            ArrayList<Integer> randomNumbersList = new ArrayList<>();
            for (int i = 0; i < allSongsPlaylist.size(); i++) {
                randomNumbersList.add(i);
            }
            Collections.shuffle(randomNumbersList);
            for (int i = 0; i < numberOfRandomSongs; i++) {
                randomPlaylist.add(allSongsPlaylist.get(randomNumbersList.get(i)));
            }
            // Sort alphabetically
            Collections.sort(randomPlaylist, new Comparator<Song>() {
                @Override
                public int compare(Song a, Song b) {
                    return a.getTitle().compareTo(b.getTitle());
                }
            });
        }
    }
}
