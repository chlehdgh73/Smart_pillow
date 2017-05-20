package com.example.chleh.smart_pillow4;

import android.app.Service;
import android.content.Intent;
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
    public Alram_Service() {


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


        mediaPlayer =new MediaPlayer();
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
        /*
        while(무한)
        {
            if(배개값 함수로 일어났다!)
            {
            mediaplayer.stop();
            break;

            }



        }*/
        stopSelf();

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
}
