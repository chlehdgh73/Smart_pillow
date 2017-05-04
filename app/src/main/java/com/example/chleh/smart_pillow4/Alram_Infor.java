package com.example.chleh.smart_pillow4;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by chleh on 2017-04-02.
 */

public class Alram_Infor {

    private int year;
    private int month;
    private int day;
    private int hour;//0~23
    private int min;//
    private int week_first_day;//그주의 첫번째 일을 구해서 그 차이를 이용하여 월화수목금 계산
    private int week_order_day;//월화수목금
    private boolean pattern[] =new boolean [7];
    private String clock;


        Alram_Infor(int year, int month, int day, int week_first_day, int hour, int min, boolean pattern [])
       {
           for(int i=0;i<7;i++)
           {
               this.pattern[i]=false;
           }
           getYear(year);
           getMonth(month);
           getDay(day);
           getDayOrder(week_first_day);
           getHour(hour);
           getMin(min);
           getPattern(pattern);
       }

    void getYear(int year)
    {
        this.year=year;
    }
    void getMonth(int month)
    {
        this.month=month;

    }
    void getDay(int day)
    {
        this.day=day;
    }
    void getHour(int hour)
    {
        this.hour=hour;
    }
    void getMin(int min)
    {
        this.min=min;
    }
    void getDayOrder(int week_first_day)
    {
        this.week_first_day=week_first_day;
        this.week_order_day=this.day-this.week_first_day;
    }//0=일요일 , 6은 토요일

    int putYear()
    {
        return this.year;
    }
    int putMonth()
    {
        return this.month;
    }
    int putDay()
    {
        return this.day;
    }
    int putMin()
    {
        return this.min;
    }
    int putHour()
    {
        return this.hour;
    }
    int putOrderDay()
    {
        return this.week_order_day;
    }
    void getPattern(boolean pattern[])
    {
        for(int i=0;i<7;i++)
        this.pattern[i]=pattern[i];

    }




}
