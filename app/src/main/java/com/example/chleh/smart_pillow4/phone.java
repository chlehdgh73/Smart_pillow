package com.example.chleh.smart_pillow4;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class phone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button button5 =(Button)findViewById(R.id.alram);//음악 리스트 만들어서 다시...
        button5.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent l = new Intent(getApplicationContext(),Alram_Music.class);
                startActivity(l);
            }
        });
        Button button6=(Button)findViewById(R.id.button3);//시험용.
        button6.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FileInputStream fis;
                BufferedReader bufferReader;
                try {
                    fis = openFileInput("Alram_infor.txt");
                    int Id;
                    bufferReader = new BufferedReader(new InputStreamReader(fis));
                    String content="", temp="";
                    temp = bufferReader.readLine();
                    StringTokenizer tokens= new StringTokenizer(temp);
                    Id=Integer.parseInt(tokens.nextToken(","));
                    fis.close();
                    bufferReader.close();
                    Toast.makeText(getApplicationContext(),String.format("%d",Id),Toast.LENGTH_SHORT).show();

                }
                catch (Exception e)
                {
            }
            }
        });


    }

}
