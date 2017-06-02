package com.example.chleh.smart_pillow4;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class Main extends AppCompatActivity {

    boolean is_permission_granted = false;
    private static final String TAG = "AppPermission";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        //알람

        if(is_permission_granted == false){
            Toast.makeText(this, "권한이 거절되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        Intent service = new Intent(this, BLEService.class);
        startService(service);

        service = new Intent(this, Alram_Service.class);
        startService(service);

        service = new Intent(this, CCService.class);
        startService(service);

        service = new Intent(this,MusicService.class);
        startService(service);

        Button button = (Button) findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), Alram.class);
                startActivity(i);
            }
        });

        //블루투스
        Button button2 = (Button) findViewById(R.id.button9);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent blue = new Intent(getApplicationContext(), Bluetooth.class);
                startActivity(blue);
            }
        });

        //음악
        Button button3 = (Button) findViewById(R.id.button5);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                Intent k = new Intent(getApplicationContext(), music_play.class);
                startActivity(k);
            }
        });

        Button button4 = (Button) findViewById(R.id.button7);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent j = new Intent(getApplicationContext(), AnalysisActivity.class);
                startActivity(j);
            }
        });

        Button button5 = (Button) findViewById(R.id.button4);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent l = new Intent(getApplicationContext(), Call_control.class);
                startActivity(l);
            }
        });

   /*     FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermission() {//권한
        Log.i(TAG, "CheckPermission : " + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));

        LocationManager locate = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(!locate.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alert_on_gps();
        }

        int permission_external_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission_external_write = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission_fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission_read_contacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permission_read_phone_state = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permission_call_phone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if(permission_external_read != PackageManager.PERMISSION_GRANTED || permission_external_write != PackageManager.PERMISSION_GRANTED
                || permission_fine != PackageManager.PERMISSION_GRANTED || permission_coarse != PackageManager.PERMISSION_GRANTED
                || permission_read_contacts != PackageManager.PERMISSION_GRANTED || permission_read_phone_state != PackageManager.PERMISSION_GRANTED
                || permission_call_phone != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_STORAGE);
        }
        else{
            is_permission_granted = true;
        }
    }

    private void alert_on_gps(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder
                .setTitle("알림.")
                .setMessage("블루투스4.0을 사용하기 위한 GPS기능이 켜져있지 않습니다.\nGPS기능을 켜주세요.")
                .setCancelable(false)
                .setPositiveButton("켜기",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int button){
                        Intent gps_intent= new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gps_intent);
                    }
                })
                .setNegativeButton("취소",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int button){
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                        || grantResults[1] != PackageManager.PERMISSION_GRANTED
                        || grantResults[2] != PackageManager.PERMISSION_GRANTED
                        || grantResults[3] != PackageManager.PERMISSION_GRANTED
                        || grantResults[4] != PackageManager.PERMISSION_GRANTED
                        || grantResults[5] != PackageManager.PERMISSION_GRANTED) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder
                            .setTitle("알림.")
                            .setMessage("어플리케이션에 필요한 권한을 얻지 못했습니다. \n설정->앱 에서 모든 권한을 켜주세요.")
                            .setCancelable(true)
                            .setPositiveButton("설정하기",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int button){
                                    Intent app_intent= new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                    startActivity(app_intent);
                                }
                            })
                            .setNegativeButton("취소",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int button){
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {

                    is_permission_granted = true;
                }
                break;
        }
    }
}