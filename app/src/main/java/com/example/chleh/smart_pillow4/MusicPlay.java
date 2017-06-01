package com.example.chleh.smart_pillow4;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MusicPlay extends AppCompatActivity {
    private ListView listView2;
    public static ArrayList<MusicDto> list;
    public static boolean autoplay;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        name = getIntent().getStringExtra("folder");

        getMusicList(); // 디바이스 안에 있는 mp3 파일 리스트를 조회하여 LIst를 만듭니다.
        listView2 = (ListView)findViewById(R.id.listview);


        MyAdapter2 adapter2 = new MyAdapter2(this,list);
        listView2.setAdapter(adapter2);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MusicPlay.this,MusicPlayer.class);
                intent.putExtra("position",position);
                intent.putExtra("playlist",list);
                startActivity(intent);
            }
        });
    }
    public  void getMusicList(){
        list = new ArrayList<>();

        //가져오고 싶은 컬럼 명을 나열합니다. 음악의 아이디, 앰블럼 아이디, 제목, 아스티스트 정보를 가져옵니다.
        FileInputStream fis;
        BufferedReader bufferReader;
        // 파일 내용 읽어오기
        try {
            fis = openFileInput(String.format(name+".txt"));
            bufferReader = new BufferedReader(new InputStreamReader(fis));
            String content="", temp="";
            while( (temp = bufferReader.readLine()) != null ) {
                MusicDto musicDto=new MusicDto();
                StringTokenizer tokens= new StringTokenizer(temp);
                if(temp.equals(""))
                    continue;
                musicDto.setId(tokens.nextToken(","));
                musicDto.setAlbumId(tokens.nextToken(","));
                musicDto.setArtist(tokens.nextToken(","));
               musicDto.setTitle(tokens.nextToken(","));
                list.add(musicDto);

                // 추가
            }
            fis.close();
            bufferReader.close();
            Log.v(null,""+content);
        } catch (Exception e) {}




    }
}
