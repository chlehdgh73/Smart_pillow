package com.example.chleh.smart_pillow4;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicService extends Service {
   MediaPlayer mediaPlayer;
    private final IBinder mBinder = new LocalBinder();
    private int oncreate;
    private  int time;
    public MusicService() {
    }
    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    public void onRebind(Intent intent){
        Log.i("정보 : ","onRebind 호출");
    }

    @Override
        public boolean onUnbind(Intent intent){
        Log.i("정보 : ","onUnbind 호출");
       // mediaPlayer.pause();
        return true;
    }
    @Override
               public IBinder onBind(final Intent intent) {
                // TODO: Return the communication channel to the service.
                try {
                    if(intent.getIntExtra("restart",0)==0) {
                        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + intent.getStringExtra("id"));
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(this, musicURI);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                    else//다시시작
           {
                    Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + intent.getStringExtra("id"));
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(this, musicURI);
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(intent.getIntExtra("restart",0));
                    mediaPlayer.start();
                }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(intent.getIntExtra("position",0)<intent.getIntExtra("size",0)) {
                       try {
                           Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + intent.getStringExtra("id"));
                           mediaPlayer.reset();
                           mediaPlayer.setDataSource(getApplicationContext(), musicURI);
                           mediaPlayer.prepare();
                           mediaPlayer.start();
                       }
                       catch (Exception e)
                       {

                       }
                    }
                }
            });
        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
        oncreate=1;
        return mBinder;
    }
    public void onCreate() {
        super.onCreate();
    mediaPlayer = new MediaPlayer();
    oncreate=0;
    // 서비스에서 가장 먼저 호출됨(최초에 한번만)
}
    public int getoncreate()
    {
        return oncreate;
    }
    public int getcur()
    {
        return mediaPlayer.getCurrentPosition();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
            {
                try {
                    Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + intent.getStringExtra("id"));
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(this, musicURI);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch (Exception e) {
                    Log.e("SimplePlayer", e.getMessage());
                }
        return START_STICKY;
    }

    public MediaPlayer player()
{
    return mediaPlayer;
}


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("정보 : ","onDestroy 호출");
    }





}
