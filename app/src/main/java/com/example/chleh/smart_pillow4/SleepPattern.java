package com.example.chleh.smart_pillow4;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
public class SleepPattern extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep_pattern);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView total=(TextView)findViewById(R.id.total_sleep);
        final TextView  move=(TextView)findViewById(R.id.move_sleep);
        final TextView meta = (TextView)findViewById(R.id.meta_sleep);
        final TextView not = (TextView)findViewById(R.id.not_sleep);
        final TextView efficient=(TextView)findViewById(R.id.efficient_sleep);
        final Spinner date_list=(Spinner)findViewById(R.id.Date_list);
        final ArrayList<String> sleep_item =new ArrayList<String>();
        final ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_selectable_list_item,sleep_item);
        date_list.setAdapter(adapter1);
       adapter1.add("2017년 4월 15일");
       adapter1.add("2017년 3월 12일");
       adapter1.notifyDataSetChanged();
        date_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
               total.setText(parent.getSelectedItem().toString());

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });








    }

}
