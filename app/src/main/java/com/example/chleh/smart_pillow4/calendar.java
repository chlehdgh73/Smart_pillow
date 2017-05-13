package com.example.chleh.smart_pillow4;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by chleh on 2017-04-01.
 */

public class calendar {

    private String str_date;

    public String getDateString()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String str_date = df.format(new Date());


        return str_date;
    }

}
