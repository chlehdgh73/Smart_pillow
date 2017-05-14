package com.example.chleh.smart_pillow4;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by chleh on 2017-04-30.
 */
public class Output {
    private Context m_context;

    public Output(Context parm_context) {
        m_context = parm_context;
    }

    public void ShowMessage(String parm_msg) {
        Toast.makeText(m_context, parm_msg, Toast.LENGTH_SHORT).show();
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }
    public static void w(String tag, String message) {
        Log.w(tag, message);
    }
    public static void d(String tag, String message) {
        Log.d(tag, message);
    }
    public static void e(String tag, String message) {
        Log.e(tag, message);
    }
}
