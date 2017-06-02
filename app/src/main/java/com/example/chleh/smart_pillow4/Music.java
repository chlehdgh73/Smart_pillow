package com.example.chleh.smart_pillow4;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import static java.security.AccessController.getContext;


public class Music extends AppCompatActivity {

    private ListView listView;
    public static ArrayList<MusicDto> list;
    public static boolean autoplay;
    private String folder_name;
    private Button complete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getMusicList(); // 디바이스 안에 있는 mp3 파일 리스트를 조회하여 LIst를 만듭니다.
        listView = (ListView)findViewById(R.id.listview);
        //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final MyAdapter adapter = new MyAdapter(this,list);
        complete= (Button)findViewById(R.id.Btn_com);

        listView.setAdapter(adapter);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    SparseBooleanArray array=listView.getCheckedItemPositions();

                int i;
                FileOutputStream fos;
                try {
                    fos = openFileOutput(String.format(folder_name+".txt"), Context.MODE_APPEND);
                    PrintWriter out= new PrintWriter(fos);
                    for (i = 0; i < adapter.getCount(); i++) {
                        if (array.get(i)) {
                            out.println();
                            out.print(list.get(i).getId());//아이디
                            out.print(",");
                            out.print(list.get(i).getAlbumId());
                            out.print(",");
                            out.print(list.get(i).getArtist());
                            out.print(",");
                            out.print(list.get(i).getTitle());
                            out.print(",");
                        }
                    }
                    out.close();

                }catch (Exception e){}
                finish();
            }

        });


        autoplay=true;
        AlertDialog.Builder Name = new AlertDialog.Builder(this);
       Name.setTitle("재생목록의 이름을 입력하세요");
        Name.setMessage("만드려는 재생목록의 이름 입력하세요");
        final EditText name = new EditText(this);
        Name.setView(name);
        Name.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                folder_name = name.getText().toString();

                FileOutputStream fos;
                try {

                    fos = openFileOutput("MusicFolder.txt", Context.MODE_APPEND);
                    PrintWriter out = new PrintWriter(fos);
                    out.println("");
                    out.print(folder_name);
                    out.close();
                    fos.close();

                }catch (Exception e){}
            }
        });
        Name.show();


     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Music.this,MusicPlayer.class);
                intent.putExtra("position",position);
                intent.putExtra("playlist",list);
                startActivity(intent);
            }
        });*/
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
