package com.example.android.beatit;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import info.abdolahi.CircularMusicProgressBar;

public class NowPlayingActivity extends AppCompatActivity {
    Song songPlaying;
    boolean repeat = false;
    boolean shuffle = false;
    boolean isFavorite = false;
    boolean isPlaying = false;
    Playlist playlist;
    int position;
    MediaPlayer mediaPlayer;
    int currentPosition = 0;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        playlist = getIntent().getExtras().getParcelable("playlist");
        position = getIntent().getExtras().getInt("position");
        if (getIntent().hasExtra("repeat")) {
            repeat = getIntent().getExtras().getBoolean("repeat");
        }
        if (getIntent().hasExtra("shuffle")) {
            shuffle = getIntent().getExtras().getBoolean("shuffle");
        }
        if (getIntent().hasExtra("isPlaying")) {
            isPlaying = getIntent().getExtras().getBoolean("isPlaying");
        }
        //TODO improve shuffle
        if (shuffle) {
            Collections.shuffle(playlist.getSongs());
        } else {
            Collections.sort(playlist.getSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song a, Song b) {
                    return a.getTitle().compareTo(b.getTitle());
                }
            });
        }
        songPlaying = playlist.getSongs().get(position);

        //Toolbar settings
        Toolbar myToolbar = (Toolbar) findViewById(R.id.now_playing_toolbar);
        myToolbar.setTitle(getString(R.string.playing));
        setSupportActionBar(myToolbar);
        //Add back button https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar-android
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Import song data
        CircularMusicProgressBar circularCover = (CircularMusicProgressBar) findViewById(R.id.circular_cover);
        circularCover.setImageBitmap(songPlaying.getSongImage());
        TextView textViewTitle = (TextView) findViewById(R.id.title);
        textViewTitle.setText(songPlaying.getTitle());
        TextView textViewArtist = (TextView) findViewById(R.id.artist);
        textViewArtist.setText(songPlaying.getArtist());

        //Buttons appearance when passing from one song to other
        final FloatingActionButton floatingActionButtonPlay = (FloatingActionButton) findViewById(R.id.play_pause);
        if (isPlaying) {
            floatingActionButtonPlay.setImageResource(R.drawable.ic_pause_white_24px);
            play();
        }
        final ImageView imageViewRepeat = (ImageView) findViewById(R.id.repeat);
        if (repeat) {
            imageViewRepeat.setImageResource(R.drawable.ic_repeat_tinted);
        }
        final ImageView imageViewShuffle = (ImageView) findViewById(R.id.shuffle);
        if (shuffle) {
            imageViewShuffle.setImageResource(R.drawable.ic_shuffle_tinted);
        }

        //Handle button clicks on music flow
        imageViewRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!repeat) {
                    imageViewRepeat.setImageResource(R.drawable.ic_repeat_tinted);
                    repeat = true;
                } else {
                    imageViewRepeat.setImageResource(R.drawable.ic_repeat);
                    repeat = false;
                }
            }
        });
        ImageView imageViewPrevious = (ImageView) findViewById(R.id.previous);
        imageViewPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    position = playlist.getSongs().size() - 1;
                } else {
                    position--;
                }
                if (isPlaying) {
                    pause();
                }
                refresh();
            }
        });
        floatingActionButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    floatingActionButtonPlay.setImageResource(R.drawable.ic_pause_white_24px);
                    play();
                    isPlaying = true;
                } else {
                    floatingActionButtonPlay.setImageResource(R.drawable.ic_play_arrow_white_24px);
                    pause();
                    isPlaying = false;
                }
            }
        });
        ImageView imageViewNext = (ImageView) findViewById(R.id.next);
        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == playlist.getSongs().size() - 1) {
                    position = 0;
                } else {
                    position++;
                }
                if (isPlaying) {
                    pause();
                }
                refresh();
            }
        });
        imageViewShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!shuffle) {
                    imageViewShuffle.setImageResource(R.drawable.ic_shuffle_tinted);
                    shuffle = true;
                } else {
                    imageViewShuffle.setImageResource(R.drawable.ic_shuffle);
                    shuffle = false;
                }
            }
        });

        // Restore state from saved instance
        if (savedInstanceState != null) {
            Boolean isOpenSaved = savedInstanceState.getBoolean("isOpenSaved");
            Boolean isFavoriteSaved = savedInstanceState.getBoolean("isFavoriteSaved");
            Boolean repeatSaved = savedInstanceState.getBoolean("repeatSaved");
            Boolean shuffleSaved = savedInstanceState.getBoolean("shuffleSaved");
            Boolean isPlayingSaved = savedInstanceState.getBoolean("isPlayingSaved");
            int currentPositionSaved = savedInstanceState.getInt("currentPositionSaved");
            // Apply stored items
            if (isOpenSaved) {
                onBackPressed();
            }
            isFavorite = isFavoriteSaved;
            if (repeatSaved) {
                imageViewRepeat.setImageResource(R.drawable.ic_repeat_tinted);
                repeat = true;
            }
            if (shuffleSaved) {
                imageViewShuffle.setImageResource(R.drawable.ic_shuffle_tinted);
                shuffle = true;
            }
            if (isPlayingSaved) {
                floatingActionButtonPlay.setImageResource(R.drawable.ic_pause_white_24px);
                isPlaying = true;
                currentPosition = currentPositionSaved;
                play();
            }
        }
    }

    // Fires when a configuration change occurs and fragment needs to save state
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("isOpenSaved",isOpen);
        savedInstanceState.putBoolean("isFavoriteSaved",isFavorite);
        savedInstanceState.putBoolean("repeatSaved",repeat);
        savedInstanceState.putBoolean("shuffleSaved",shuffle);
        savedInstanceState.putBoolean("isPlayingSaved",isPlaying);
        //TODO keep playing when device rotates
        savedInstanceState.putInt("currentPositionSaved",currentPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.now_playing_menu, menu);
        if (isFavorite) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_favorite_black_24dp_tinted);
            isFavorite = true;
        }
        return true;
    }

    // Handle clicks on menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_text,
                        songPlaying.getTitle(), getString(R.string.app_name)));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.action_favorite:
                if (!isFavorite) {
                    item.setIcon(R.drawable.ic_favorite_black_24dp_tinted);
                    isFavorite = true;
                    return true;
                } else {
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                    isFavorite = false;
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle back button on toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * This helper method refreshes the activity when playing a different song
     */
    public void refresh() {
        finish();
        overridePendingTransition(0, 0);
        Intent openActivityNowPlaying = new Intent(this, NowPlayingActivity.class);
        openActivityNowPlaying.putExtra("playlist", playlist);
        openActivityNowPlaying.putExtra("position", position);
        openActivityNowPlaying.putExtra("repeat", repeat);
        openActivityNowPlaying.putExtra("shuffle", shuffle);
        openActivityNowPlaying.putExtra("isPlaying", isPlaying);
        openActivityNowPlaying.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(openActivityNowPlaying);
    }

    /**
     * This helper method plays a song and keep playing the playlist until it finished or pause
     * button is clicked
     * https://developer.android.com/guide/topics/media/mediaplayer.html?utm_source=udacity&utm_medium=course&utm_campaign=android_basics
     */
    public void play() {
        Uri songUri = playlist.getSongs().get(position).getUri();
        //Check if it has been paused
        if (currentPosition != 0) {
            mediaPlayer.seekTo(currentPosition);
        } else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), songUri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.e("IOException", "No Song Uri", e);
            }
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (position == playlist.getSongs().size() - 1) {
                    if (repeat) {
                        position = 0;
                    } else {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                } else {
                    position++;
                }
                refresh();
            }
        });
    }

    /**
     * This helper method pauses the song
     */
    public void pause() {
        currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.want_close);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isOpen = false;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isOpen = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        isOpen = true;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
