package com.example.chleh.smart_pillow4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import static android.os.Build.VERSION.SDK_INT;

public class Alram extends AppCompatActivity {

    private ArrayList<String> alram_item;
    private boolean pattern[];
    private TimePicker A_time;
    private ArrayAdapter adapter;
    private ListView alram_list;//알람 리스트
    private ToggleButton mon;
    private ToggleButton tue;
    private ToggleButton wen;
    private ToggleButton thu;
    private ToggleButton fri;
    private ToggleButton sat;
    private ToggleButton sun;

    private Alram_Service alram_service = null;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            alram_service = ((Alram_Service.LocalBinder) service).getService();
            int i;
            List<Alram_Infor> temp = alram_service.getAlram_list();
            alram_item.clear();
            for(i = 0 ; i < temp.size() ; i++){
                alram_item.add(changeItem(temp.get(i)));
            }
            adapter.notifyDataSetChanged();
            // Automatically connects to the device upon successful start-up initialization.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            alram_service = null;
        }
    };

    BroadcastReceiver mBroadcastRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(Alram_Service.ALRAM_DONE)){
                List<Alram_Infor> temp = alram_service.getAlram_list();
                alram_item.clear();
                int i;
                for(i = 0 ; i < temp.size() ; i++){
                    alram_item.add(changeItem(temp.get(i)));
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    public String changeItem(Alram_Infor item)
    {
        String result;
        result=String.format("%d일 %d시 %d분",item.getDay(),item.getHour(),item.getMin());
        if(pattern[2]==true)
        {
            result+=" 월";
        }
        if(pattern[3]==true)
        {
            result+=" 화";
        }
        if(pattern[4]==true)
        {
            result+=" 수";
        }
        if(pattern[5]==true)
        {
            result+=" 목";
        }
        if(pattern[6]==true)
        {
            result+=" 금";
        }
        if(pattern[7]==true)
        {
            result+=" 토";
        }
        if(pattern[1]==true)
        {
            result+=" 일";
        }
        return result;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         pattern=new boolean[8];
         A_time=(TimePicker)findViewById(R.id.timePicker);
         alram_item =new ArrayList<String>();
         adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,alram_item);
         alram_list = (ListView)findViewById(R.id.List_alram);//알람 리스트
         mon=(ToggleButton)findViewById(R.id.Btn_mon);
         tue=(ToggleButton)findViewById(R.id.Btn_tue);
         wen=(ToggleButton)findViewById(R.id.Btn_wen);
         thu=(ToggleButton)findViewById(R.id.Btn_thu);
         fri=(ToggleButton)findViewById(R.id.Btn_fri);
         sat=(ToggleButton)findViewById(R.id.Btn_sat);
         sun=(ToggleButton)findViewById(R.id.Btn_sun);

        Intent service = new Intent(this, Alram_Service.class);
        bindService(service, mServiceConnection, 0);


        /*
        반복되는 요일정보 setting
        디폴트 false;
         */
        SetDay(pattern);

        alram_list.setAdapter(adapter);

        //시간 계산

        mon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(mon.isChecked())
                {
                    pattern[2]=true;
                }
                else
                {
                    pattern[2]=false;
                }

            }
        });
        tue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(tue.isChecked())
                {
                    pattern[3]=true;
                }
                else
                {
                    pattern[3]=false;
                }

            }
        });
        wen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(wen.isChecked())
                {
                    pattern[4]=true;
                }
                else
                {
                    pattern[4]=false;
                }

            }
        });
        thu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(thu.isChecked())
                {
                    pattern[5]=true;
                }
                else
                {
                    pattern[5]=false;
                }

            }
        });
        fri.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(fri.isChecked())
                {
                    pattern[6]=true;
                }
                else
                {
                    pattern[6]=false;
                }

            }
        });
        sat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(sat.isChecked())
                {
                    pattern[7]=true;
                }
                else
                {
                    pattern[7]=false;
                }

            }
        });
        sun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(sun.isChecked())
                {
                    pattern[1]=true;
                }
                else
                {
                    pattern[1]=false;
                }

            }
        });

        //삽입 버튼 만들기
        Button button1 = (Button)findViewById(R.id.Btn_insert);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(alram_service == null){
                    Toast.makeText(getApplicationContext(),"알람서비스가 없습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar calendar= Calendar.getInstance();
                int temp_year=calendar.get(Calendar.YEAR);
                int temp_month=calendar.get(Calendar.MONTH);
                int temp_day=calendar.get(Calendar.DAY_OF_MONTH);
                int temp_week=calendar.get(Calendar.DAY_OF_WEEK);
                Random random = new Random();
                int temp_hour=calendar.get(Calendar.HOUR_OF_DAY);
                int temp_min=calendar.get(Calendar.MINUTE);

                if(pattern[1]==false&&pattern[2]==false&&pattern[3]==false&&pattern[4]==false&&pattern[5]==false&&pattern[6]==false&&pattern[7]==false) {
                    if (A_time.getHour() < temp_hour) {
                        calendar.set(Calendar.DAY_OF_MONTH, temp_day + 1);
                        temp_day++;
                    } else if (A_time.getHour() == temp_hour && temp_min > A_time.getMinute()) {
                        calendar.set(Calendar.DAY_OF_MONTH, temp_day + 1);
                        temp_day++;
                    }
                }
                alram_service.add_alram(new Alram_Infor(temp_year,temp_month,temp_day,temp_week,A_time.getHour(),A_time.getMinute(),pattern,random.nextInt(10^5)));

                int i;
                List<Alram_Infor> temp = alram_service.getAlram_list();
                alram_item.clear();
                for(i = 0 ; i < temp.size() ; i++){
                    String s = changeItem(temp.get(i));
                    Log.i("정보 : ", s);
                    alram_item.add(s);
                }
                adapter.notifyDataSetChanged();

                SetDay(pattern);
                mon.setChecked(false);
                thu.setChecked(false);
                wen.setChecked(false);
                tue.setChecked(false);
                fri.setChecked(false);
                sat.setChecked(false);
                sun.setChecked(false);
            }
        });




        //삭제
        Button button2 = (Button)findViewById(R.id.Btn_delete);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(alram_service == null){
                    return;
                }
                List<Alram_Infor> temp= alram_service.getAlram_list();
                alram_service.remove_alram(temp.get(alram_list.getCheckedItemPosition()).getId());
                int i;
                temp = alram_service.getAlram_list();
                alram_item.clear();
                for(i = 0 ; i < temp.size() ; i++){
                    String s = changeItem(temp.get(i));
                    Log.i("정보 : ", s);
                    alram_item.add(s);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();

        IntentFilter filter = new IntentFilter(Alram_Service.ALRAM_DONE);
        registerReceiver(mBroadcastRecevier, filter);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mBroadcastRecevier);
    }
   void SetDay(boolean[] pattern2)
   {
       for(int i=1;i<8;i++)
       {
           pattern2[i]=false;
       }
   }

}
