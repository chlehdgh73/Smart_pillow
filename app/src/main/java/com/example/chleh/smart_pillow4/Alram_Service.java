package com.example.chleh.smart_pillow4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class Alram_Service extends Service {
    private MediaPlayer mediaPlayer;
    private  int Id;
    private boolean stop;
    private BroadcastReceiver setting;
    private int year;
    private  int month;
    private  int day;
    private int hour;
    private  int min;
    private  long date;
    private GregorianCalendar cal, first,second,result;
    boolean fisrtget =false, secondget=false;

    public static final int STATE_INIT = 0;//초기상태
    public static final int STATE_LAIN = 1;//누운상태
    public static final int STATE_DEEP = 2;//완전수면상태
    public static final int STATE_SHALLOW = 3;//뒤척임상태
    public static final int STATE_TEMP_AWAKE = 4;//잠깐깬상태
    public static final int STATE_RE_LAIN = 5;//다시누운상태
    public static final int STATE_COMPLETE_AWAKE = 6;//기상상태
    ArrayList<Alram_Infor> alram_infor_list;
    Ring ring;
    public Alram_Service() {


    }
    @Override
    public void onCreate()
    {
        mediaPlayer=new MediaPlayer();

        ring=new Ring();
        stop=false;
        alram_infor_list= new ArrayList<Alram_Infor>();
        setting =new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();

                if(action.equals("STATE_CHANGE_NOTIFY"))
                {
                    int state=intent.getIntExtra("NOTIFY_STATE",1);
                    switch(state)
                    {
                        case STATE_INIT:
                            break;
                        case STATE_LAIN:
                            break;
                        case STATE_DEEP:
                            break;
                        case STATE_SHALLOW:
                            break;
                        case STATE_TEMP_AWAKE:
                            break;
                        case STATE_RE_LAIN:
                            if(fisrtget)//일어났는데 다시 누우면
                            {
                              first= new GregorianCalendar();
                            }

                            break;
                        case STATE_COMPLETE_AWAKE:
                            if(!fisrtget) {
                                first = new GregorianCalendar();
                                fisrtget=true;
                            }
                            else if(!secondget)
                            {
                            /*    second = new GregorianCalendar();
                                if((second.get(Calendar.MINUTE)-first.get(Calendar.MINUTE)>=10)&&(second.get(Calendar.SECOND)-first.get(Calendar.SECOND)>=0))//10분차
                                {
                                 stop=true;
                                }*/
                            }

                            break;
                    }
                }
            }
        };

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    first=new GregorianCalendar();
        cal= new GregorianCalendar();
         year = cal.get(Calendar.YEAR);          // 연도를 리턴
         month = cal.get(Calendar.MONTH)+1;    // 월을 리턴
         day = cal.get(Calendar.DAY_OF_MONTH);          // 일을 리턴
         hour = cal.get(Calendar.HOUR);         // 시를 리턴
         min = cal.get(Calendar.MINUTE);       // 분을 리턴
        int line=0;
        boolean correct=false;


        FileInputStream fis;
        BufferedReader bufferReader;
        try {
            fis = openFileInput("myFile.txt");
            bufferReader = new BufferedReader(new InputStreamReader(fis));
            String content="", temp="";
            while( (temp = bufferReader.readLine()) != null ) {
                StringTokenizer tokens = new StringTokenizer(temp);
             //
                //   Id = Integer.parseInt(tokens.nextToken(","));
                int temp_year=0;
                int temp_month=0;
                int temp_firstDay=0;
                int temp_day=0;
                int temp_hour=0;
                int temp_min=0;
                boolean[] temp_pa= new boolean[7];
                if(temp.equals(""))
                    continue;
                if(!correct)
                 line++;//어우 줄세기???
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
                alram_infor_list.add(new Alram_Infor(temp_year,temp_month,temp_day,temp_firstDay,temp_hour,temp_min,temp_pa));
                if(temp_year==year)
                {
                    if(temp_month==month)
                    {
                        if(temp_day==day)
                        {
                            if(temp_hour==hour)
                            {
                                if(temp_min==min)
                                {
                                    correct=true;
                                    break; //같은거
                                }
                            }
                        }
                    }
                }
            }//end while
            fis.close();
            bufferReader.close();
        }
        catch (Exception e)
        {
        }//읽기 끝


        try {
            fis = openFileInput("Alram_infor.txt");
            bufferReader = new BufferedReader(new InputStreamReader(fis));
            String content="", temp="";
            while( (temp = bufferReader.readLine()) != null ) {
                StringTokenizer tokens = new StringTokenizer(temp);
                //
                   Id = Integer.parseInt(tokens.nextToken(","));
            }//end while
            fis.close();
            bufferReader.close();
        }
        catch (Exception e)
        {
        }//읽기 끝



        //덮어 쓰면 되겠따
        int today=alram_infor_list.get(line-1).putOrderDay();
        for(int i=0;i<=6;i++)
        {
            int temp_day=today+i+1;
            if(temp_day>7)
                temp_day=temp_day-7;
            if(alram_infor_list.get(line-1).putPattern(temp_day-1))
            {
                int plus = (temp_day-alram_infor_list.get(line-1).putOrderDay());
                if(plus<=0)
                {
                    plus=7-plus;
                }
                result= new GregorianCalendar();
                alram_infor_list.get(line-1).getDay(alram_infor_list.get(line-1).putDay()+plus);
                break;

               //반복요일 정해서 결정해서 만약 없으면 삭제하고 있으면 수정해서 다시 써야함 퍽...
            }
        }
        boolean only=true;
        for(int i=0;i<7;i++)
        {
            if(alram_infor_list.get(line-1).putPattern(i))
            {
                only=false;
            }
        }
        if(only)
        {
            alram_infor_list.remove(line-1);
        }
        //파일 쓰기


        FileOutputStream fos;
        try {
            fos = openFileOutput("myFile.txt",MODE_PRIVATE);
            PrintWriter out= new PrintWriter(fos);
           int size =alram_infor_list.size();

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
//파일에 덮어쓰기 끝
        //새로운거 다시
        Intent intent2 = new Intent(Alram_Service.this,MyReceiver.class);
        //엑스트라 넣어서
        int start_time=alram_infor_list.get(line-1).putMin()+alram_infor_list.get(line-1).putHour()*100+alram_infor_list.get(line-1).putDay()*10000+alram_infor_list.get(line-1).putMonth()*1000000;
        Calendar newStart= Calendar.getInstance();
        newStart.set(Calendar.YEAR,alram_infor_list.get(line-1).putYear());
        newStart.set(Calendar.MONTH,alram_infor_list.get(line-1).putMonth());
        newStart.set(Calendar.DAY_OF_MONTH,alram_infor_list.get(line-1).putDay());
        newStart.set(Calendar.HOUR_OF_DAY,alram_infor_list.get(line-1).putHour());
        newStart.set(Calendar.MINUTE,alram_infor_list.get(line-1).putMin());
        PendingIntent sender = PendingIntent.getBroadcast(Alram_Service.this,start_time,intent,0);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,newStart.getTimeInMillis(),sender);//










        //
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        try {
            Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" +Id );
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            ring.start();
        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
        // 무한 반복재생
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               mediaPlayer.start();
            }
        });
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    void setReceiver()
    {
        IntentFilter filter =new IntentFilter();
        filter.addAction("STATE_CHANGE_NOTIFY");
        registerReceiver(setting,filter);
    }
  class Ring extends Thread
  {
      @Override
      public void run() {
          setReceiver();
          while(true){
              try {
                    if(stop)
                    {
                        mediaPlayer.pause();
                        break;
                    }
                               second = new GregorianCalendar();
                                if((second.get(Calendar.MINUTE)-first.get(Calendar.MINUTE)>=10)&&(second.get(Calendar.SECOND)-first.get(Calendar.SECOND)>=0))//10분차
                                {
                                 stop=true;
                                }

                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          } // end while





      } // end run()





  }





}
