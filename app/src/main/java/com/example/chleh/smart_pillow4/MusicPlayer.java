package com.example.chleh.smart_pillow4;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<MusicDto> list;
    private MediaPlayer mediaPlayer;
    private TextView title;
    private ImageView album,previous,play,pause,next;
    private SeekBar seekBar;
    boolean isPlaying = true;
    private ContentResolver res;
    private ProgressUpdate progressUpdate;
    private int position;
    private play play2;
    private int oncreate=0;
    private  int time;
    private  boolean end;
    //
     MusicService ms; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
// 서비스와 연결되었을 때 호출되는 메서드
// 서비스 객체를 전역변수로 저장
            MusicService.LocalBinder mb = (MusicService.LocalBinder) service;
            ms = mb.getService(); // 서비스가 제공하는 메소드 호출하여
// 서비스쪽 객체를 전달받을수 있슴
            isService = true;
        }
        public void onServiceDisconnected(ComponentName name) {
// 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        end=false;
        setContentView(R.layout.activity_musicplayer);
        Intent intent = getIntent();
        mediaPlayer = new MediaPlayer();
        title = (TextView)findViewById(R.id.title);
        album = (ImageView)findViewById(R.id.album);
        seekBar = (SeekBar)findViewById(R.id.seekbar);

        position = intent.getIntExtra("position",0);
        list = (ArrayList<MusicDto>) intent.getSerializableExtra("playlist");
        res = getContentResolver();

        previous = (ImageView)findViewById(R.id.pre);
        play = (ImageView)findViewById(R.id.play);
        pause = (ImageView)findViewById(R.id.pause);
        next = (ImageView)findViewById(R.id.next);

        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        playMusic(list.get(position));



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ms.mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //ms.player().seekTo(seekBar.getProgress());
                if(seekBar.getProgress()>=0 && play.getVisibility()== View.GONE){

                    time=seekBar.getProgress();
                    ms.player().seekTo(time);
                    ms.player().start();


                }
            }
        });


    }



    public void playMusic(MusicDto musicDto) {

        try {
            seekBar.setProgress(0);
            title.setText(musicDto.getArtist() + " - " + musicDto.getTitle());
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicDto.getId());

              play2 = new play();
              play2.start();
              play2.join();
            oncreate=0;

        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
        UiControl(musicDto);
    }

    public void UiControl(MusicDto musicDto)
    {
        seekBar.setMax(mediaPlayer.getDuration());
        if(oncreate==0||ms.player().isPlaying()){
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);

        }else{
            play.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
        }

        Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicDto.getAlbumId()),getApplication()));
        album.setImageBitmap(bitmap);
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();

    }

    //앨범이 저장되어 있는 경로를 리턴합니다.
    private static String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
             try {
                 Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + list.get(position).getId());
                 ms.player().reset();
                 ms.player().setDataSource(this, musicURI);
                 ms.player().prepare();
                 ms.player().seekTo(time);
                 ms.player().start();
             }catch (Exception e)
             {

             }
          //      Intent intent2 = new Intent(MusicPlayer.this, MusicService.class);
          //      intent2.putExtra("id",list.get(position).getId());
         //       intent2.putExtra("restart",time);
          //      bindService(intent2, // intent 객체
         //               conn, // 서비스와 연결에 대한 정의
        //                Context.BIND_AUTO_CREATE);


                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                ms.player().pause();

    //            unbindService(conn); // 서비스 종료
                break;
            case R.id.pre:
                if(position-1>=0 ){
                    position--;
                    ms.player().pause();
                    try {
                        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + list.get(position).getId());
                        ms.player().reset();
                        ms.player().setDataSource(this, musicURI);
                        ms.player().prepare();
                        ms.player().seekTo(time);
                        ms.player().start();
                    }catch (Exception e)
                    {

                    }
                 //   unbindService(conn); // 서비스 종료


                }
                break;
            case R.id.next:
                if(position+1<list.size()){
                    position++;
                    ms.player().pause();
                    try {
                        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + list.get(position).getId());
                        ms.player().reset();
                        ms.player().setDataSource(this, musicURI);
                        ms.player().prepare();
                        ms.player().seekTo(time);
                        ms.player().start();
                    }catch (Exception e)
                    {

                    }
                  // unbindService(conn); // 서비스 종료
                    //  end=true;//작업을 종료시켜야해!
                 //   playMusic(list.get(position));


                }

                break;
        }
    }


    class ProgressUpdate extends Thread {
        @Override
        public void run() {
            while(oncreate==0||ms.player().isPlaying()&&end==false){
                try {
                    Thread.sleep(500);
                    if(ms.player()!=null){
                        seekBar.setMax(ms.player().getDuration());
                        seekBar.setProgress(ms.getcur());
                        time=ms.getcur();
                        oncreate=ms.getoncreate();
                    }
                } catch (Exception e) {
                    Log.e("ProgressUpdate",e.getMessage());
                }

            }
        }

    }
    class play extends Thread{
        public void run() {
            Intent intent2 = new Intent(MusicPlayer.this, MusicService.class);
            intent2.putExtra("id",list.get(position).getId());
            intent2.putExtra("size",list.size());
            intent2.putExtra("position",position);
            //intent2.putExtra("id+1",list.get(position+1).getId());
            intent2.putExtra("restart",0);

            startService(intent2);
            bindService(intent2, // intent 객체
                  conn, // 서비스와 연결에 대한 정의
                   Context.BIND_AUTO_CREATE);


        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
