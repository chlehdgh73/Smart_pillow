package com.example.chleh.smart_pillow4;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Alram_Music extends AppCompatActivity {


    private ListView listView;
    public static ArrayList<MusicDto> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram__music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getMusicList(); // 디바이스 안에 있는 mp3 파일 리스트를 조회하여 LIst를 만듭니다.
        listView = (ListView)findViewById(R.id.listview);
        MyAdapter2 adapter = new MyAdapter2(this,list);
        listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BufferedWriter file_write;
                        FileOutputStream fos;
                        try
                {
                    fos = openFileOutput("Alram_infor.txt", Context.MODE_PRIVATE);
                    PrintWriter out= new PrintWriter(fos);
                    out.print(list.get(position).getId());
                    out.print(",");
                    out.close();
                    Toast.makeText(getApplicationContext(),"저장완료!!!",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {

                }
                /*Intent intent = new Intent(Alram_Music.this,MusicPlayer.class);
                intent.putExtra("position",position);
                intent.putExtra("playlist",list);
                startActivity(intent);*/


            }
        });

    }
    public  void getMusicList(){
        list = new ArrayList<>();
        //가져오고 싶은 컬럼 명을 나열합니다. 음악의 아이디, 앰블럼 아이디, 제목, 아스티스트 정보를 가져옵니다.
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor =this.getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);

        while(cursor.moveToNext()){
            MusicDto musicDto = new MusicDto();
            musicDto.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musicDto.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musicDto.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicDto.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            list.add(musicDto);
        }
        cursor.close();
    }


}
