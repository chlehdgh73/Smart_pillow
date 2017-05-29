package com.example.chleh.smart_pillow4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MyReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
            Intent intent1 =new Intent(context,Alram_Service.class);
        intent1.putExtra("YEAR",intent.getIntExtra("YEAR",1));
        intent1.putExtra("MONTH",intent.getIntExtra("MONTH",1));
        intent1.putExtra("DAY",intent.getIntExtra("DAY",1));
        intent1.putExtra("MIN",intent.getIntExtra("MIN",1));
        intent1.putExtra("HOUR",intent.getIntExtra("HOUR",1));
            context.startService(intent1);


    }
}
