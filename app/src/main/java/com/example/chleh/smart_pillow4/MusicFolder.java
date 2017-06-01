package com.example.chleh.smart_pillow4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MusicFolder extends AppCompatActivity {

    private Button insert;
    private Button delete;
    private Button choice;
    private ListView folderList;
    public static ArrayList<String> list;
    private Switch swit;
    private int Auto;
    private  FileOutputStream fos;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_music_folder);
            insert=(Button)findViewById(R.id.btn_insert);
            delete=(Button)findViewById(R.id.btn_delete);
            choice=(Button)findViewById(R.id.btn_select);
            folderList=(ListView)findViewById(R.id.folderList);
            list =new ArrayList<String>();
            swit =(Switch)findViewById(R.id.SW);


        swit.setChecked(false);
        Auto=0;

        try {

            fos = openFileOutput("Auto.txt", Context.MODE_PRIVATE);
            PrintWriter out = new PrintWriter(fos);
            out.print(Auto);
            out.close();
            fos.close();
        }catch (Exception e){}


        swit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Auto=1;
                    try {

                        fos = openFileOutput("Auto.txt", Context.MODE_PRIVATE);
                        PrintWriter out = new PrintWriter(fos);
                        out.print(Auto);
                        out.close();
                        fos.close();
                    }catch (Exception e){}
                }
                else
                {
                    Auto=0;
                    try {

                        fos = openFileOutput("Auto.txt", Context.MODE_PRIVATE);
                        PrintWriter out = new PrintWriter(fos);
                        out.print(Auto);
                        out.close();
                        fos.close();
                    }catch (Exception e){}
                }

            }
        });






        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,list);
        folderList.setAdapter(adapter);
        readFoleder();
        adapter.notifyDataSetChanged();


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent k = new Intent(MusicFolder.this, Music.class);
                startActivity(k);

                                         }
                                  });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int checked=folderList.getCheckedItemPosition();

                list.remove(checked);
                folderList.clearChoices();
                adapter.notifyDataSetChanged();;

                // 모든 선택 상태 초기화.
                folderList.clearChoices() ;
                adapter.notifyDataSetChanged();
            }
        });
        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int checked=folderList.getCheckedItemPosition();
               // Toast.makeText(getApplicationContext(),list.get(checked),Toast.LENGTH_SHORT).show();
                int count=list.size();
                    Intent k = new Intent(MusicFolder.this, MusicPlay.class);
                    k.putExtra("folder",list.get(folderList.getCheckedItemPosition()));
                    startActivity(k);

            }
        });

    }
    public void readFoleder()
    {

        FileInputStream fis;
        BufferedReader bufferReader;
        // 파일 내용 읽어오기
        try {
            fis = openFileInput("MusicFolder.txt");
            bufferReader = new BufferedReader(new InputStreamReader(fis));
            String content="", temp="";
            while( (temp = bufferReader.readLine()) != null ) {
              if(temp.equals(""))
                  continue;
                list.add(temp);
                // 추가
            }
            fis.close();
            bufferReader.close();
            Log.v(null,""+content);

        } catch (Exception e) {}
    }




}
