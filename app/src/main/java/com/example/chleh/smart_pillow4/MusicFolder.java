package com.example.chleh.smart_pillow4;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MusicFolder extends AppCompatActivity {

    private Button insert;
    private Button delete;
    private Button choice;
    private ListView folderList;
    public static ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_folder);
        insert=(Button)findViewById(R.id.btn_insert);
        delete=(Button)findViewById(R.id.btn_delete);
        choice=(Button)findViewById(R.id.btn_select);
        folderList=(ListView)findViewById(R.id.folderList);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_multiple_choice,list);
        folderList.setAdapter(adapter);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {







                                         }
                                  });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {







            }
        });
        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {







            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
