package com.example.chleh.smart_pillow4;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicService extends Service {
   MediaPlayer mediaPlayer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate() {
        super.onCreate();


        mediaPlayer = new MediaPlayer();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
            {
                try {

                    Uri musicURI = Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + intent.getStringExtra("id"));

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


public void onDestory()
{




}





}
