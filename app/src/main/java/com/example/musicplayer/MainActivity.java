package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    boolean playing = false;
    int currentTrack=0;
    boolean sliding=false;
    public String searchName(){
        EditText searchName = (EditText) findViewById(R.id.searchMusic);
        return searchName.getText().toString();
    }

    public void playNext(View view) {
        SeekBar scrubSeekBar = (SeekBar) findViewById(R.id.scrubSeekBar);
        ListView myListView= (ListView) findViewById(R.id.musicList);
        ArrayAdapter<String> listAdapter= (ArrayAdapter<String>) myListView.getAdapter();
        int sound = 0;
        if(mediaPlayer!=null)
            mediaPlayer.reset();
        Resources res = getResources();
        currentTrack+=1;
        if(currentTrack< listAdapter.getCount()) {
            sound = res.getIdentifier(listAdapter.getItem(currentTrack), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.start();
            scrubSeekBar.setMax(mediaPlayer.getDuration());
            playing = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.playMusic);
            imageButton.setImageResource(android.R.drawable.ic_media_pause);
        }
        else
        {
            currentTrack=listAdapter.getCount()-1;
            sound = res.getIdentifier(listAdapter.getItem(currentTrack), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.start();
            scrubSeekBar.setMax(mediaPlayer.getDuration());
            playing = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.playMusic);
            imageButton.setImageResource(android.R.drawable.ic_media_pause);
        }
        changeText(listAdapter.getItem(currentTrack));
    }

    public void playPrev(View view){
        SeekBar scrubSeekBar = (SeekBar) findViewById(R.id.scrubSeekBar);
        ListView myListView= (ListView) findViewById(R.id.musicList);
        ArrayAdapter<String> listAdapter= (ArrayAdapter<String>) myListView.getAdapter();
        int sound = 0;
        if(mediaPlayer!=null)
            mediaPlayer.reset();
        Resources res = getResources();
        currentTrack -= 1;
        if(currentTrack< listAdapter.getCount() && currentTrack>-1) {
            sound = res.getIdentifier(listAdapter.getItem(currentTrack), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.start();
            scrubSeekBar.setMax(mediaPlayer.getDuration());
            playing = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.playMusic);
            imageButton.setImageResource(android.R.drawable.ic_media_pause);
        }
        else {
            currentTrack = 0;
            sound = res.getIdentifier(listAdapter.getItem(currentTrack), "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.start();
            scrubSeekBar.setMax(mediaPlayer.getDuration());
            playing = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.playMusic);
            imageButton.setImageResource(android.R.drawable.ic_media_pause);
        }
        changeText(listAdapter.getItem(currentTrack));
    }

    public void musicToggle(View view){
        if(mediaPlayer!=null) {
            ImageButton imageButton = (ImageButton) findViewById(R.id.playMusic);
            if (playing) {
                imageButton.setImageResource(android.R.drawable.ic_media_play);
                mediaPlayer.pause();
                playing = false;
            } else {
                imageButton.setImageResource(android.R.drawable.ic_media_pause);
                mediaPlayer.start();
                playing = true;
            }
        }
    }

    public void searchFilter(View view){
        ListView myListView= (ListView) findViewById(R.id.musicList);
        ArrayList<String> musicList=new ArrayList<String>();
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            if(fields[count].getName().contains(searchName()))
            musicList.add(fields[count].getName());
            Log.i("Raw Asset: ", fields[count].getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,musicList);
        myListView.setAdapter(arrayAdapter);

    }

    public void changeText(String songName){
        TextView searchName = (TextView) findViewById(R.id.nowPlaying);
        searchName.setText(songName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        //Music List
        ListView myListView= (ListView) findViewById(R.id.musicList);
        ArrayList<String> musicList=new ArrayList<String>();
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            musicList.add(fields[count].getName());
            Log.i("Raw Asset: ", fields[count].getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,musicList);
        myListView.setAdapter(arrayAdapter);

        //Song timing controller
        SeekBar scrubSeekBar = (SeekBar) findViewById(R.id.scrubSeekBar);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mediaPlayer!=null)
                mediaPlayer.reset();
                Resources res = getResources();
                int sound = res.getIdentifier(myListView.getAdapter().getItem(i).toString(),"raw",getPackageName());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),sound);
                scrubSeekBar.setMax(mediaPlayer.getDuration());
                changeText(myListView.getAdapter().getItem(i).toString());
                currentTrack=i;
                mediaPlayer.start();
                playing=true;
                ImageButton imageButton= (ImageButton) findViewById(R.id.playMusic);
                imageButton.setImageResource(android.R.drawable.ic_media_pause);
                //Toast.makeText(MainActivity.this, "Friend Name:  "+ musicList.get(i), Toast.LENGTH_SHORT).show();
            }
        });
        scrubSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("Scrub Value Changed",Integer.toString(i));
                //mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //mediaPlayer.pause();
                sliding=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if(playing)
                mediaPlayer.start();
                sliding=false;
            }
        });
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(playing && !sliding)
                scrubSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        },0,1);
    }
}