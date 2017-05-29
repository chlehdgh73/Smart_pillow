package com.example.chleh.smart_pillow4;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
        list =new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,list);
        folderList.setAdapter(adapter);
        list.add("All");

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count;
                count = adapter.getCount();
                list.add("all"+Integer.toString(count + 1));
                adapter.notifyDataSetChanged();
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
                Toast.makeText(getApplicationContext(),list.get(checked),Toast.LENGTH_SHORT).show();

                if(list.get(checked).equals("All"))
                {
                    Intent k = new Intent(getApplicationContext(), Music.class);
                    startActivity(k);
                }



            }
        });




    }

}
