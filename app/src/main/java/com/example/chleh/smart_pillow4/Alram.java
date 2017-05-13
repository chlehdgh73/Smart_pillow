package com.example.chleh.smart_pillow4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class Alram extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DatePicker A_date=(DatePicker)findViewById(R.id.datePicker);
        final TimePicker A_time=(TimePicker)findViewById(R.id.timePicker);
        final ArrayList<String> alram_item =new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,alram_item);
        final ToggleButton mon=(ToggleButton)findViewById(R.id.Btn_mon);
        final ToggleButton tue=(ToggleButton)findViewById(R.id.Btn_tue);
        final ToggleButton wen=(ToggleButton)findViewById(R.id.Btn_wen);
        final ToggleButton thu=(ToggleButton)findViewById(R.id.Btn_thu);
        final ToggleButton fri=(ToggleButton)findViewById(R.id.Btn_fri);
        final ToggleButton sat=(ToggleButton)findViewById(R.id.Btn_sat);
        final ToggleButton sun=(ToggleButton)findViewById(R.id.Btn_sun);
        final ArrayList<Alram_Infor> alram_infor_list= new ArrayList<Alram_Infor>();
        final boolean pattern[]=new boolean[7];
        final AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        final Calendar m=Calendar.getInstance();


        /*
        알람 정보를 파일에 쓰고 그것을 읽
        * */
        String dirPath = getFilesDir().getAbsolutePath();
        File file = new File(dirPath);
        if( !file.exists() ) {
            file.mkdirs();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        // txt 파일 생성
        String testStr = "ABCDEFGHIJK...";
        File savefile = new File(dirPath+"/Alram.txt");
        try{
               FileOutputStream fos = new FileOutputStream(savefile);
               fos.write(testStr.getBytes());
             fos.close();
               Toast.makeText(this, "Save Success"+ dirPath, Toast.LENGTH_SHORT).show();
            } catch(IOException e){}

     /*   // 파일이 1개 이상이면 파일 이름 출력
        if ( file.listFiles().length > 0 )
               for ( File f : file.listFiles() ) {
                   String str = f.getName();
                  Log.v(null,"fileName : "+str);
                  // 파일 내용 읽어오기
                 String loadPath = dirPath+"/"+str;
                 try {
                          FileInputStream fis = new FileInputStream(loadPath);
                          BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));
                           String content="", temp="";
                        while( (temp = bufferReader.readLine()) != null ) {
                                   content += temp;
                               }
                        Log.v(null,""+content);
            } catch (Exception e) {}
              }*/










        final ListView alram_list = (ListView)findViewById(R.id.List_alram);//알람 리스트
        alram_list.setAdapter(adapter);


        //날짜 계산
        A_date.init(A_date.getYear(),A_date.getMonth(),A_date.getDayOfMonth(),
                new DatePicker.OnDateChangedListener(){
                    //값이 바뀔때 마다 바뀐 날짜의 값을 바꾸어 준다.
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //Toast.makeText(getApplicationContext(), String.format("%d%d%d",year,monthOfYear,dayOfMonth), Toast.LENGTH_SHORT).show();

                    }
                }
        );
        //시간 계산

        mon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(mon.isChecked())
                {
                    pattern[1]=true;
                }
                else
                {
                    pattern[1]=false;
                }

            }
        });
        tue.setOnClickListener(new View.OnClickListener() {

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
        wen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(mon.isChecked())
                {
                    pattern[3]=true;
                }
                else
                {
                    pattern[3]=false;
                }

            }
        });
        thu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(mon.isChecked())
                {
                    pattern[4]=true;
                }
                else
                {
                    pattern[4]=false;
                }

            }
        });
        fri.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(mon.isChecked())
                {
                    pattern[5]=true;
                }
                else
                {
                    pattern[5]=false;
                }

            }
        });
        sat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(mon.isChecked())
                {
                    pattern[6]=true;
                }
                else
                {
                    pattern[6]=false;
                }

            }
        });
        sun.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if(mon.isChecked())
                {
                    pattern[0]=true;
                }
                else
                {
                    pattern[0]=false;
                }

            }
        });




        //삽입 버튼 만들기
        Button button1 = (Button)findViewById(R.id.Btn_insert);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//API 버전 23이상 쩝....
                   // Toast.makeText(getApplicationContext(), String.format("%d %d %d %d %d",A_date.getYear(),A_date.getMonth()+1,A_date.getDayOfMonth(),A_time.getHour(),A_time.getMinute()), Toast.LENGTH_LONG).show();
                    //주마다 하는거나 매일 하는거 함수 만들기!
                    alram_item.add(String.format("%d년 %d월 %d일 %d시 %d분",A_date.getYear(),(A_date.getMonth()+1),A_date.getDayOfMonth(),A_time.getHour(),A_time.getMinute()));
                    adapter.notifyDataSetChanged();
                    alram_infor_list.add(new Alram_Infor(A_date.getYear(),(A_date.getMonth()+1),A_date.getFirstDayOfWeek(),A_date.getDayOfMonth(),A_time.getHour(),A_time.getMinute(),pattern));
                    m.set(Calendar.HOUR_OF_DAY,A_time.getHour());
                    m.set(Calendar.MINUTE,A_time.getMinute());
                    m.set(Calendar.SECOND,0);//현재 저장한 시간을 객체에 저장

                    Intent intent = new Intent(Alram.this,AlramService.class);
                    PendingIntent mAlramSender = PendingIntent.getService(Alram.this,0,intent,0);
                    am.set(am.RTC_WAKEUP,m.getTimeInMillis(),mAlramSender);

                }




            }
        });




        //삭제
        Button button2 = (Button)findViewById(R.id.Btn_delete);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //시험용
            int count, checked;
            count = adapter.getCount();
                if(count>0){
                    checked =alram_list.getCheckedItemPosition();

                    if(checked > -1 &&checked < count)
                    {
                        alram_item.remove(checked);
                        alram_list.clearChoices();
                        adapter.notifyDataSetChanged();;
                        alram_infor_list.remove(checked);
                    }
                }



            }
        });



    }

}
