package com.example.chleh.smart_pillow4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.StringTokenizer;

import static android.os.Build.VERSION.SDK_INT;

public class Alram extends AppCompatActivity {
    final boolean pattern[]=new boolean[7];
     DatePicker A_date;
     TimePicker A_time;
     ArrayList<String> alram_item ;
     ArrayAdapter adapter;
     ToggleButton mon;
     ToggleButton tue;
     ToggleButton wen;
     ToggleButton thu;
     ToggleButton fri;
     ToggleButton sat;
     ToggleButton sun;
     ArrayList<Alram_Infor> alram_infor_list;

     Calendar m;
    String dirPath ;
     String dirfile;
     File savefile ;
    File file ;
   public static PendingIntent sender= null;
    public static AlarmManager am = null;
    public static int size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final boolean pattern[]=new boolean[7];
          AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
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

        final Calendar m=Calendar.getInstance();
        String dirPath = getFilesDir().getAbsolutePath();
        final String dirfile=String.format(dirPath+"/Alram3.txt");
        final File savefile = new File(dirPath+"/Alram3.txt");
        File file =new File("myFile.txt");


        /*
        반복되는 요일정보 setting
        디폴트 false;
         */
        SetDay(pattern);
        //파일 읽기
        readAlramInfor(savefile,alram_infor_list,alram_item);
        adapter.notifyDataSetChanged();
        //파일 쓰기







        final ListView alram_list = (ListView)findViewById(R.id.List_alram);//알람 리스트
        alram_list.setAdapter(adapter);


        //날짜 계산
        A_date.init(A_date.getYear(),A_date.getMonth(),A_date.getDayOfMonth(),
                new DatePicker.OnDateChangedListener(){
                    //값이 바뀔때 마다 바뀐 날짜의 값을 바꾸어 준다.
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                       // Toast.makeText(getApplicationContext(), String.format("%d",A_date.getDayofWeek()), Toast.LENGTH_SHORT).show();


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
                if (SDK_INT >= Build.VERSION_CODES.M) {//API 버전 23이상 쩝....
                   // Toast.makeText(getApplicationContext(), String.format("%d %d %d %d %d",A_date.getYear(),A_date.getMonth()+1,A_date.getDayOfMonth(),A_time.getHour(),A_time.getMinute()), Toast.LENGTH_LONG).show();
                    //주마다 하는거나 매일 하는거 함수 만들기!
                    alram_item.add(String.format("%d년 %d월 %d일 %d시 %d분",A_date.getYear(),(A_date.getMonth()+1),A_date.getDayOfMonth(),A_time.getHour(),A_time.getMinute()));

                    adapter.notifyDataSetChanged();
                    Calendar cal= Calendar.getInstance();

                     cal.set(Calendar.YEAR,A_date.getYear());
                    cal.set(Calendar.MONTH,A_date.getMonth());
                    cal.set(Calendar.DAY_OF_MONTH,A_date.getDayOfMonth());
                    cal.set(Calendar.HOUR_OF_DAY,A_time.getHour());
                    cal.set(Calendar.MINUTE,A_time.getMinute());
                    cal.set(Calendar.SECOND,0);
                   int dayofweek= cal.get(Calendar.DAY_OF_WEEK);
                    alram_infor_list.add(new Alram_Infor(A_date.getYear(),(A_date.getMonth()+1),A_date.getDayOfMonth(),dayofweek,A_time.getHour(),A_time.getMinute(),pattern));
                    //




                    BufferedWriter file_write;
                    FileOutputStream fos;
                    try {

                        fos = openFileOutput("myFile.txt", Context.MODE_APPEND);
                        PrintWriter out= new PrintWriter(fos);
                  /*      FileWriter fileWriter = new FileWriter(savefile , true);
                        file_write = new BufferedWriter(fileWriter);
                        file_write.newLine();//한줄씩 쓰게 하기*/
                       //년
                        out.println("");
                        out.print(A_date.getYear());
                        out.print(",");

                       //월
                        out.print((A_date.getMonth()+1));
                        out.print(",");
                        //주의 첫번째 일
                        out.print(A_date.getFirstDayOfWeek());
                        out.print(",");
                        //일
                        out.print((A_date.getDayOfMonth()));
                        out.print(",");
                        //시
                        out.print(A_time.getHour());
                        out.print(",");
                        //분
                        out.print(A_time.getMinute());
                        out.print(",");
                        //
                        if(pattern[0]==false)
                            out.print(0);
                        else
                            out.print(1);
                        out.print(",");
                        if(pattern[1]==false)
                            out.print(0);
                        else
                            out.print(1);
                        out.print(",");
                        if(pattern[2]==false)
                            out.print(0);
                        else
                            out.print(1);
                        out.print(",");
                        if(pattern[3]==false)
                            out.print(0);
                        else
                            out.print(1);
                        out.print(",");
                        if(pattern[4]==false)
                            out.print(0);
                        else
                            out.print(1);
                        out.print(",");
                        if(pattern[5]==false)
                            out.print(0);
                        else
                            out.print(1);
                        out.print(",");
                        if(pattern[6]==false)
                            out.print(0);
                        else
                            out.print(1);
                        out.print(",");

                        out.close();
                       // fileWriter.close();

                    }
                    catch (Exception e)
                    {

                    }
                    setAlram(cal);//알람 설정
                }
                SetDay(pattern);
                 size=alram_infor_list.size();
                mon.setChecked(false);
                thu.setChecked(false);
                wen.setChecked(false);
                tue.setChecked(false);
                fri.setChecked(false);
                sat.setChecked(false);
                sun.setChecked(false);
                adapter.notifyDataSetChanged();;
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
                   int temp= alram_infor_list.get(checked).putMin()+alram_infor_list.get(checked).putHour()*100+alram_infor_list.get(checked).putDay()*10000+alram_infor_list.get(checked).putMonth()*1000000;
                    Intent intent = new Intent(Alram.this,MyReceiver.class);
                    sender = PendingIntent.getBroadcast(Alram.this,temp,intent,0);
                    AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    am.cancel(sender);
                    if(checked > -1 &&checked < count)
                    {
                        alram_item.remove(checked);
                        alram_list.clearChoices();
                        adapter.notifyDataSetChanged();;
                        alram_infor_list.remove(checked);

                        FileOutputStream fos;
                    try {
                        fos = openFileOutput("myFile.txt",MODE_PRIVATE);
                        PrintWriter out= new PrintWriter(fos);
                         size =alram_infor_list.size();

                        for(int i=0;i<size;i++) {
                            out.println("");
                            out.print(alram_infor_list.get(i).putYear());
                            out.print(",");
                            //월
                            out.print(alram_infor_list.get(i).putMonth());
                            out.print(",");
                            //주의 첫번째 일
                            out.print(alram_infor_list.get(i).putdayoffirst());
                            out.print(",");
                            //일
                            out.print(alram_infor_list.get(i).putDay());
                            out.print(",");
                            //시
                            out.print(alram_infor_list.get(i).putHour());
                            out.print(",");
                            //분
                            out.print(alram_infor_list.get(i).putMin());
                            out.print(",");
                            //
                            if (alram_infor_list.get(i).putPattern(0) == false)
                                out.print(0);
                            else
                                out.print(1);
                            out.print(",");
                            if (alram_infor_list.get(i).putPattern(1) == false)
                                out.print(0);
                            else
                                out.print(1);
                            out.print(",");
                            if (alram_infor_list.get(i).putPattern(2) == false)
                                out.print(0);
                            else
                                out.print(1);
                            out.print(",");
                            if (alram_infor_list.get(i).putPattern(3) == false)
                                out.print(0);
                            else
                                out.print(1);
                            out.print(",");
                            if (alram_infor_list.get(i).putPattern(4) == false)
                                out.print(0);
                            else
                                out.print(1);
                            out.print(",");
                            if (alram_infor_list.get(i).putPattern(5) == false)
                                out.print(0);
                            else
                                out.print(1);
                            out.write(",");
                            if (alram_infor_list.get(i).putPattern(6) == false)
                                out.print(0);
                            else
                                out.print(1);
                            out.write(",");
                            out.close();
                            // fileWriter.close();*/
                        }
                    }
                    catch (Exception e)
                    {

                    }








                    }
                }



            }
        });



    }
    //onCreate 끝
    /*
    요일을 판별해주는 것을 함수로써 만들기
     */
   void SetDay(boolean[] pattern2)
   {
       for(int i=0;i<7;i++)
       {
           pattern2[i]=false;
       }
   }

    void readAlramInfor(File temp_file,ArrayList<Alram_Infor> temp_alram_infor_list,ArrayList<String> temp_alram_item )
    {
        FileInputStream fis;
        BufferedReader bufferReader;
                // 파일 내용 읽어오기
                try {
                     fis = openFileInput("myFile.txt");
                    bufferReader = new BufferedReader(new InputStreamReader(fis));
                    String content="", temp="";
                    while( (temp = bufferReader.readLine()) != null ) {
                    //    content += temp;
                        /*
                        ,단위로 string 끊어치기기
                         */
                        StringTokenizer tokens= new StringTokenizer(temp);

                        /**
                         *생각해보는 반복요일 숫자인데 그거 지정해야하네....
                         */
                        int temp_year=0;
                        int temp_month=0;
                        int temp_firstDay=0;
                        int temp_day=0;
                        int temp_hour=0;
                        int temp_min=0;
                        boolean[] temp_pa= new boolean[7];
                        if(temp.equals(""))
                           continue;
                        temp_year=Integer.parseInt(tokens.nextToken(","));
                        temp_month=Integer.parseInt(tokens.nextToken(","));
                        temp_firstDay=Integer.parseInt(tokens.nextToken(","));
                        temp_day=Integer.parseInt(tokens.nextToken(","));
                        temp_hour=Integer.parseInt(tokens.nextToken(","));
                        temp_min=Integer.parseInt(tokens.nextToken(","));
                        for(int i=0;i<=6;i++)
                        {
                            temp_pa[i]=decide(Integer.parseInt(tokens.nextToken(",")));
                        }
                        temp_alram_item.add(String.format("%d년 %d월 %d일 %d시 %d분",temp_year,temp_month,temp_day,temp_hour,temp_min));
                        temp_alram_infor_list.add(new Alram_Infor(temp_year,temp_month,temp_day,temp_firstDay,temp_hour,temp_min,temp_pa));
                        // 추가
                    }
                    fis.close();
                    bufferReader.close();
                    Log.v(null,""+content);
                } catch (Exception e) {}




    }
    /*반복 요일 설정용

     */
    boolean decide(int what)
    {
        if(what==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

void setReceiver()
{
    IntentFilter filter = new IntentFilter();
    filter.addAction("STATE_CHANGE_NOTIFY");

  //  registerReceiver(MyReceiver,filter);


}



void setAlram(Calendar cal)
{

    Intent intent = new Intent(Alram.this,MyReceiver.class);
   //엑스트라 넣어서
    GregorianCalendar temp= new GregorianCalendar();
    int start_time=cal.get(Calendar.MINUTE)+cal.get(Calendar.HOUR_OF_DAY)*100+cal.get(Calendar.DAY_OF_MONTH)*10000+(cal.get(Calendar.MONTH)+1)*1000000;
    PendingIntent sender = PendingIntent.getBroadcast(Alram.this,start_time,intent,0);
    AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),sender);//
}
}
