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

        progressUpdate = new ProgressUpdate();
        progressUpdate.start();

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
                ms.mediaPlayer.seekTo(seekBar.getProgress());
                if(seekBar.getProgress()>0 && play.getVisibility()== View.GONE){
                   // ms.mediaPlayer.start();
                    Intent intent2 = new Intent(MusicPlayer.this, MusicService.class);
                    intent2.putExtra("restart",0);

                    bindService(intent2, // intent 객체
                            conn, // 서비스와 연결에 대한 정의
                            Context.BIND_AUTO_CREATE);

                }
            }
        });
      //  UiControl(list.get(position));
      //  ms.player().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
 /*      mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(position+1<list.size()) {
                    position++;
                    playMusic(list.get(position));
                }
            }
        });*/
    }

    public void playMusic(MusicDto musicDto) {

        try {
            seekBar.setProgress(0);
            title.setText(musicDto.getArtist() + " - " + musicDto.getTitle());
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicDto.getId());
           /* Intent intent2 = new Intent(this, MusicService.class);
            intent2.putExtra("id",musicDto.getId());
            bindService(intent2, // intent 객체
                    conn, // 서비스와 연결에 대한 정의
                    Context.BIND_AUTO_CREATE);*/

              play2 = new play();
              play2.start();
              play2.join();

             // mediaPlayer = ms.mediaPlayer;
            //startService(intent2);
            //mediaPlayer.reset();
            //mediaPlayer.setDataSource(this, musicURI);
            //mediaPlayer.prepare();
        /*
                // mediaPlayer.start();



            seekBar.setMax(ms.mediaPlayer.getDuration());
            if(ms.mediaPlayer.isPlaying()){
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }else{
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }
*/

         //   Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicDto.getAlbumId()),getApplication()));
        //    album.setImageBitmap(bitmap);

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
                Intent intent2 = new Intent(MusicPlayer.this, MusicService.class);
                intent2.putExtra("id",list.get(position).getId());
                intent2.putExtra("restart",time);
                bindService(intent2, // intent 객체
                        conn, // 서비스와 연결에 대한 정의
                        Context.BIND_AUTO_CREATE);
                //    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
               //     mediaPlayer.start();

                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                unbindService(conn); // 서비스 종료


                //mediaPlayer.pause();
                break;
            case R.id.pre:
                if(position-1>=0 ){
                    position--;
                   // playMusic(list.get(position));
                    unbindService(conn); // 서비스 종료

                    Intent intent3 = new Intent(MusicPlayer.this, MusicService.class);
                    intent3.putExtra("id",list.get(position).getId());
                    intent3.putExtra("restart",time);
                    bindService(intent3, // intent 객체
                            conn, // 서비스와 연결에 대한 정의
                            Context.BIND_AUTO_CREATE);
                    UiControl(list.get(position));
                    seekBar.setProgress(0);
                    oncreate=0;
                    progressUpdate.start();
                }
                break;
            case R.id.next:
                if(position+1<list.size()){
                    position++;
                    unbindService(conn); // 서비스 종료

                    Intent intent3 = new Intent(MusicPlayer.this, MusicService.class);
                    intent3.putExtra("id",list.get(position).getId());
                    intent3.putExtra("restart",time);
                    bindService(intent3, // intent 객체
                            conn, // 서비스와 연결에 대한 정의
                            Context.BIND_AUTO_CREATE);
                   // playMusic(list.get(position));
                    UiControl(list.get(position));
                    seekBar.setProgress(0);
                    oncreate=0;
                    progressUpdate.start();
                }

                break;
        }
    }


    class ProgressUpdate extends Thread {
        @Override
        public void run() {
            while(oncreate==0||ms.player().isPlaying()){
                try {
                    Thread.sleep(500);
                    if(ms.mediaPlayer!=null){
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
            intent2.putExtra("id+1",list.get(position+1).getId());
            intent2.putExtra("restart",0);

            bindService(intent2, // intent 객체
                   conn, // 서비스와 연결에 대한 정의
                    Context.BIND_AUTO_CREATE);


        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if(ms.mediaPlayer!=null){
            ms.mediaPlayer.release();
            ms.mediaPlayer = null;
        }
    }
}
