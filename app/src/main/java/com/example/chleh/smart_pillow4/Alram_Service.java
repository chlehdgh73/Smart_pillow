package com.example.chleh.smart_pillow4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;


public class Alram_Service extends Service {
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private GregorianCalendar cal, first, second, result;

    public static final String ALRAM_START = "ALRAM_START";
    public static final String ALRAM_CANCLE = "ALRAM_CANCLE";
    public static final String ALRAM_DONE = "ALRAM_DONE";
    private final int cancle_timer = 55555555;
    private boolean complete_end = true;

    private final IBinder mBinder = new LocalBinder();

    public static ArrayList<Alram_Infor> alram_infor_list;
    private Thread alram_thread = null;
    private BLEService bluetooth_service = null;
    public static PendingIntent sender= null;
    private List<Alram_Infor> alram_list = new ArrayList<>();

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_service = ((BLEService.LocalBinder) service).getService();
            // Automatically connects to the device upon successful start-up initialization.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_service = null;
        }
    };

    public class LocalBinder extends Binder {
        Alram_Service getService() {
            return Alram_Service.this;
        }
    }

    BroadcastReceiver mBroadcastRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BLEService.STATE_CHANGE_NOTIFY)) {

                if(bluetooth_service == null){
                    if(mediaPlayer.isPlaying() == true){
                        mediaPlayer.pause();
                        complete_end = true;
                    }
                    return;
                }

                if(bluetooth_service.query_lain_state() == true){
                    if(complete_end == false) {
                        Intent alram = new Intent(Alram_Service.this, MyReceiver.class);
                        PendingIntent sender = PendingIntent.getBroadcast(Alram_Service.this, cancle_timer, alram, 0);
                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        am.cancel(sender);

                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                }
                else{
                    Intent alram = new Intent(Alram_Service.this,MyReceiver.class);
                    intent.putExtra("alram!","cancle_alram");
                    PendingIntent sender = PendingIntent.getBroadcast(Alram_Service.this,cancle_timer,alram,0);
                    long timer = System.currentTimeMillis() + (600*1000);
                    AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timer,sender);

                    mediaPlayer.pause();
                }


            }
            else if(action.equals(ALRAM_START)){
                int id = intent.getIntExtra("this_is_id", -1);
                int i;
                Alram_Infor temp = null;
                if(id == -1){
                    return;
                }
                else{
                    for(i = 0 ; i < alram_list.size() ; i++){
                        if(alram_list.get(i).getId() == id){
                            temp = alram_list.get(i);
                            break;
                        }
                    }
                }
                if(temp == null){
                    return;
                }

                if(temp.get_redo() == false){
                    alram_list.remove(temp);
                    Intent send = new Intent(ALRAM_DONE);
                    sendBroadcast(send);
                }
                else{
                    setAlram(temp);
                }
                if(mediaPlayer.isPlaying())
                {
                   return;

                }
                else{
                    if(bluetooth_service == null){
                        return;
                    }
                    if(bluetooth_service.query_state() != BLEService.STATE_INIT) {
                        complete_end = false;
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                }
            }
            else if(action.equals(ALRAM_CANCLE)){
                mediaPlayer.pause();
                complete_end = true;
            }
        }
    };

    @Override
    public void onCreate() {
        Log.i("정보 : ", "알람서비스가 생성되었음");
        read_infor();

        Intent service = new Intent(this, BLEService.class);
        bindService(service, mServiceConnection, 0);

        setReceiver();

        int size=alram_list.size();
        int temp_size;
        for(int i=0;i<size;i++)
        {
            setAlram(alram_list.get(i));//하다가 제거하면 인덱스 엿됨
            temp_size=alram_list.size();
            if(temp_size!=size)
            {
                size=temp_size;
                i--;//
            }
        }

        try {

            mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.wakeup);
            mediaPlayer.setLooping(true);
        }
        catch (Exception e){ }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBroadcastRecevier);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;

    }

    void setReceiver() {
        IntentFilter filter1 = new IntentFilter(BLEService.STATE_CHANGE_NOTIFY);
        IntentFilter filter2 = new IntentFilter(ALRAM_START);
        IntentFilter filter3 = new IntentFilter(ALRAM_CANCLE);
        registerReceiver(mBroadcastRecevier, filter1);
        registerReceiver(mBroadcastRecevier, filter2);
        registerReceiver(mBroadcastRecevier, filter3);
    }

    public void read_infor()
    {
        FileInputStream fis;
        BufferedReader bufferReader;
        // 파일 내용 읽어오기
        try {
            fis = openFileInput("myFile.txt");
            bufferReader = new BufferedReader(new InputStreamReader(fis));
            String content="", temp="";
            while( (temp = bufferReader.readLine()) != null ) {//파일 끝까지 모든 정보 읽어서 리스트에 저장
                StringTokenizer tokens= new StringTokenizer(temp);
                int temp_year=0;
                int temp_month=0;
                int temp_firstDay=0;
                int temp_day=0;
                int temp_hour=0;
                int temp_min=0;
                int temp_id=0;
                boolean[] temp_pa= new boolean[8];
                if(temp.equals(""))
                    continue;
                temp_year=Integer.parseInt(tokens.nextToken(","));
                temp_month=Integer.parseInt(tokens.nextToken(","));
                temp_firstDay=Integer.parseInt(tokens.nextToken(","));
                temp_day=Integer.parseInt(tokens.nextToken(","));
                temp_hour=Integer.parseInt(tokens.nextToken(","));
                temp_min=Integer.parseInt(tokens.nextToken(","));
                for(int i=1;i<=7;i++)
                {
                    temp_pa[i]=decide(Integer.parseInt(tokens.nextToken(",")));
                }
                temp_id=Integer.parseInt(tokens.nextToken(","));
                alram_list.add(new Alram_Infor(temp_year,temp_month,temp_day,temp_firstDay,temp_hour,temp_min,temp_pa,temp_id));
            }
            fis.close();
            bufferReader.close();
        } catch (Exception e) {}
    }

    public boolean decide(int count)
    {
        if(count==0)
        {
            return false;
        }

            return true;
    }

    private Alram_Infor check_same(Alram_Infor item)
    {
        int size=alram_list.size();

        for(int i=0; i<size;i++)
        {
            if(alram_list.get(i).getYear()!=item.getYear())//년
            {
                continue;
            }
             if(alram_list.get(i).getMonth()!=item.getMonth())//월
            {
                continue;
            }
             if(alram_list.get(i).getDay()!=item.getDay())//일
            {
                continue;
            }
             if(alram_list.get(i).getHour()!=item.getHour())//시간
            {
                continue;
            }
             if(alram_list.get(i).getMin()!=item.getMin())//분
            {
                continue;
            }
            return alram_list.get(i);
        }

        return null;
    }

    public int check_id(int id)
    {
        int size=alram_list.size();
        for(int i=0;i<size;i++)
        {
            if(alram_list.get(i).getId()==id)
            {
                return i;
            }
        }
        return -1;
    }
    public void modify(Alram_Infor item, Alram_Infor target)
    {

        for(int i=1;i<8;i++)
        {
            if (target.getPattern(i) == false && item.getPattern(i) == true) {
                target.setdaypattern(i);
            }
        }
        releaseAlram(target);
        setAlram(target);
        write_list(alram_list);
    }
    public void add_alram(Alram_Infor item)
    {
      Alram_Infor checked = check_same(item);
      if(checked == null)//같은게 없을경우 추가
      {
          while(true)//id  식별
          {
              if(check_id(item.getId())==-1)
              {
                  break;
              }
              else
              {
                  Random random = new Random();
                  item.setId(random.nextInt(10^5));
              }
          }
          alram_list.add(item);
          add_item(item);
          setAlram(item);
      }
      else
      {
          modify(item,checked);
      }
    }
    public void setAlram(Alram_Infor item)
    {
        Calendar today=Calendar.getInstance();
        long time_long;
        int today_temp=today.get(Calendar.DAY_OF_WEEK);
        int today_hour = today.get(Calendar.HOUR_OF_DAY);
        int today_min = today.get(Calendar.MINUTE);
        if(item.get_redo())
        {
            int i;
            int diff = 0;
            boolean is_same = false;
            for(i=today_temp; i < 8; i++){
                if(item.getPattern(i) == true){
                    is_same = true;
                    break;
                }
                else{
                    diff++;
                }
            }

            if(diff == 0){
                if(today_hour > item.getHour() || (today_hour == item.getHour() && today_min >= item.getMin())){
                    i++;
                    is_same = false;
                    for(; i < 8; i++){
                        if(item.getPattern(i) == true){
                            is_same = true;
                            break;
                        }
                        else{
                            diff++;
                        }
                    }
                }
            }

            if(is_same == false){
                for(i=1; i < 8; i++){
                    if(item.getPattern(i) == true){
                        is_same = true;
                        break;
                    }
                    else{
                        diff++;
                    }
                }
            }

            if(is_same == true){

                today.set(Calendar.DAY_OF_MONTH,today.get(Calendar.DAY_OF_MONTH)+diff);
                today.set(Calendar.HOUR_OF_DAY,item.getHour());
                today.set(Calendar.MINUTE,item.getMin());
                today.set(Calendar.SECOND,0);
            }
            time_long = today.getTimeInMillis();
            Log.i("정보 : ", today.toString());
        }
        else //반복요일 아닌 경우
        {
            Calendar time_to_day= Calendar.getInstance();
            time_to_day.set(Calendar.YEAR,item.getYear());
            time_to_day.set(Calendar.MONTH,item.getMonth());
            time_to_day.set(Calendar.DAY_OF_MONTH,item.getDay());
            time_to_day.set(Calendar.HOUR_OF_DAY,item.getHour());
            time_to_day.set(Calendar.MINUTE,item.getMin());
            time_to_day.set(Calendar.SECOND,0);

            if((today.getTimeInMillis() - time_to_day.getTimeInMillis()) > 0 ){
                //alram_list.remove(item);
                //write_list(alram_list);
                //return;
                time_to_day.set(Calendar.DAY_OF_MONTH,time_to_day.get(Calendar.DAY_OF_MONTH)+1);
                item.setYear(time_to_day.get(Calendar.YEAR));
                item.setMonth(time_to_day.get(Calendar.MONTH));
                item.setDay(time_to_day.get(Calendar.DAY_OF_MONTH));
                item.setDayOrder(time_to_day.get(Calendar.DAY_OF_WEEK));
                item.setMin(time_to_day.get(Calendar.MINUTE));
                item.setHour(time_to_day.get(Calendar.HOUR_OF_DAY));
            }
            time_long = time_to_day.getTimeInMillis();
            Log.i("정보 : ", time_to_day.toString());

        }

        Intent intent = new Intent(Alram_Service.this,MyReceiver.class);
        intent.putExtra("alram!","start_alram");
        intent.putExtra("this_is_id", item.getId());
        PendingIntent sender = PendingIntent.getBroadcast(Alram_Service.this,item.getId(),intent,0);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time_long,sender);

    }

    public void releaseAlram(Alram_Infor item)
    {
      Intent intent = new Intent(Alram_Service.this,MyReceiver.class);
      PendingIntent sender = PendingIntent.getBroadcast(Alram_Service.this,item.getId(),intent,0);
      AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);

    }

    public void write_list(List<Alram_Infor> list)
    {
        try {
            FileOutputStream fos;
            fos = openFileOutput("myFile.txt", MODE_PRIVATE);
            PrintWriter out = new PrintWriter(fos);
            for (int i = 0; i < list.size(); i++) {
                out.println("");
                //년
                out.print(list.get(i).getYear());
                out.print(",");
                //월
                out.print(list.get(i).getMonth());
                out.print(",");
                //요일
                out.print(list.get(i).getOrderDay());
                out.print(",");
                //일
                out.print(list.get(i).getDay());
                out.print(",");
                //시
                out.print(list.get(i).getHour());
                out.print(",");
                //분
                out.print(list.get(i).getMin());
                out.print(",");
                //
                if (!list.get(i).getPattern(1))
                    out.print(0);
                else
                    out.print(1);
                out.print(",");
                if (!list.get(i).getPattern(2))
                    out.print(0);
                else
                    out.print(1);
                out.print(",");
                if (!list.get(i).getPattern(3))
                    out.print(0);
                else
                    out.print(1);
                out.print(",");
                if (!list.get(i).getPattern(4))
                    out.print(0);
                else
                    out.print(1);
                out.print(",");
                if (!list.get(i).getPattern(5))
                    out.print(0);
                else
                    out.print(1);
                out.print(",");
                if (!list.get(i).getPattern(6))
                    out.print(0);
                else
                    out.print(1);
                out.write(",");
                if (!list.get(i).getPattern(7))
                    out.print(0);
                else
                    out.print(1);
                out.write(",");
                out.print(list.get(i).getId());
                out.write(",");
            }
            out.close();
            fos.close();
        }
        catch (Exception e){

        }

    }

    public void add_item(Alram_Infor item)
    {
      FileOutputStream fos;
      try {
          fos = openFileOutput("myFile.txt",MODE_APPEND);
          PrintWriter out= new PrintWriter(fos);
              out.println("");
          //년
              out.print(item.getYear());
              out.print(",");
              //월
              out.print(item.getMonth());
              out.print(",");
              //요일
              out.print(item.getOrderDay());
              out.print(",");
              //일
              out.print(item.getDay());
              out.print(",");
              //시
              out.print(item.getHour());
              out.print(",");
              //분
              out.print(item.getMin());
              out.print(",");
              //
              if (!item.getPattern(1))
                  out.print(0);
              else
                  out.print(1);
              out.print(",");
              if (!item.getPattern(2))
                  out.print(0);
              else
                  out.print(1);
              out.print(",");
              if (!item.getPattern(3))
                  out.print(0);
              else
                  out.print(1);
              out.print(",");
              if (!item.getPattern(4))
                  out.print(0);
              else
                  out.print(1);
              out.print(",");
              if (!item.getPattern(5))
                  out.print(0);
              else
                  out.print(1);
              out.print(",");
              if (!item.getPattern(6))
                  out.print(0);
              else
                  out.print(1);
              out.write(",");
              if (!item.getPattern(7))
                  out.print(0);
              else
                  out.print(1);
              out.write(",");
          out.print(item.getId());
          out.write(",");
          out.close();
          fos.close();
      }
      catch (Exception e)
      {

      }
    }

    public void remove_alram(int id)
    {
      int size=alram_list.size();
      for(int i=0;i<size;i++)
      {
          if(alram_list.get(i).getId()==id)
          {
              releaseAlram(alram_list.get(i));
              alram_list.remove(i);
              write_list(alram_list);
          }
      }
    }
    List<Alram_Infor> getAlram_list(){//읽기  전용
      return alram_list;
    }




}
