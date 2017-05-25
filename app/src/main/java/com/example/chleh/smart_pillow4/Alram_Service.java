package com.example.chleh.smart_pillow4;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Alram_Service extends Service {
    private MediaPlayer mediaPlayer;
    private  int Id;
    private BroadcastReceiver setting;
    public static final int STATE_INIT = 0;//초기상태
    public static final int STATE_LAIN = 1;//누운상태
    public static final int STATE_DEEP = 2;//완전수면상태
    public static final int STATE_SHALLOW = 3;//뒤척임상태
    public static final int STATE_TEMP_AWAKE = 4;//잠깐깬상태
    public static final int STATE_RE_LAIN = 5;//다시누운상태
    public static final int STATE_COMPLETE_AWAKE = 6;//기상상태
    public Alram_Service() {


    }
    @Override
    public void onCreate()
    {
        mediaPlayer=new MediaPlayer();
        setting =new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(action.equals("STATE_CHANGE_NOTIFY"))
                {
                    int state=intent.getIntExtra("NOTIFY_STATE",1);
                    switch(state)
                    {
                        case STATE_INIT:
                            break;
                        case STATE_LAIN:
                            break;
                        case STATE_DEEP:
                            break;
                        case STATE_SHALLOW:
                            break;
                        case STATE_TEMP_AWAKE:
                            break;
                        case STATE_RE_LAIN:
                            break;
                        case STATE_COMPLETE_AWAKE:
                            break;
                    }
                }
            }
        };
        setReceiver();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        FileInputStream fis;
        BufferedReader bufferReader;
        try {
            fis = openFileInput("Alram_infor.txt");

            bufferReader = new BufferedReader(new InputStreamReader(fis));
            String content="", temp="";
            temp = bufferReader.readLine();
            StringTokenizer tokens= new StringTokenizer(temp);
            Id=Integer.parseInt(tokens.nextToken(","));
            fis.close();
            bufferReader.close();
        }
        catch (Exception e)
        {
        }
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        try {
            Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" +Id );
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
        // 무한 반복재생
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               mediaPlayer.start();
            }
        });
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    void setReceiver()
    {
        IntentFilter filter =new IntentFilter();
        filter.addAction("STATE_CHANGE_NOTIFY");
        registerReceiver(setting,filter);
    }




}
