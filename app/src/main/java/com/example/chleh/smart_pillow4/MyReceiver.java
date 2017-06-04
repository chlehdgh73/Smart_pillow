package com.example.chleh.smart_pillow4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
        @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("alram!");
        int id = intent.getIntExtra("this_is_id", -1);

        if(action == null){
            return;
        }

        if(action.equals("start_alram")) {
            Intent call = new Intent(Alram_Service.ALRAM_START);
            call.putExtra("this_is_id", id);
            context.sendBroadcast(call);
        }
        else if(action.equals("cancle_alram")){
            Intent call = new Intent(Alram_Service.ALRAM_CANCLE);
            context.sendBroadcast(call);
        }
    }
}
