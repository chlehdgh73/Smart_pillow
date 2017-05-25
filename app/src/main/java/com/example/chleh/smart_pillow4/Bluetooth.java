package com.example.chleh.smart_pillow4;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Bluetooth extends AppCompatActivity {
    private Button button1, button2, button3;
    private ListView listview;
    private ArrayAdapter<String> device_adapter;
    private BLEService bluetooth_service = null;
    private Context context = this;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_service = ((BLEService.LocalBinder) service).getService();
            Log.i("정보 : ","service_connect 호출");
            // Automatically connects to the device upon successful start-up initialization.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_service = null;
            Log.i("정보 : ","service_disconnect 호출");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth2);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        listview = (ListView)findViewById(R.id.listView);

        device_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        listview.setAdapter(device_adapter);

        check();

        Intent service = new Intent(this, BLEService.class);
        startService(service);

        bindService(service, mServiceConnection, 0);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(bluetooth_service != null) {
                    bluetooth_service.select_device(device_adapter.getItem(position));

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setTitle("알림.")
                            .setMessage("선택되었습니다.")
                            .setCancelable(true)
                            .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int button){
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    re_scan_device();
                }
            }
        });

        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.button1:
                        if(!re_scan_device()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder
                                    .setTitle("검색에 실패했습니다.")
                                    .setMessage("잠시후에 다시시도해주세요.")
                                    .setCancelable(true)
                                    .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int button){
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        break;

                    case R.id.button2:
                        if(bluetooth_service != null){
                            boolean connection_state = bluetooth_service.query_connection();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder
                                    .setTitle("상태.");
                            if(connection_state) {
                                builder.setMessage("연결되어있습니다.");
                            }
                            else{
                                builder.setMessage("연결되어있지않습니다.");
                            }
                            builder
                                    .setCancelable(true)
                                    .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int button){
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                        break;

                    case R.id.button3:
                        if(bluetooth_service != null){
                            bluetooth_service.select_device("");
                            if(!bluetooth_service.query_connection()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder
                                        .setTitle("알림.")
                                        .setMessage("해제되었습니다.")
                                        .setCancelable(true)
                                        .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int button){
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }

                        break;
                }
            }
        };

        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);



    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter1 = new IntentFilter(BLEService.BLE_NOTIFY);
        IntentFilter filter2 = new IntentFilter(BLEService.BLE_WARNING);
        registerReceiver(mReceiver, filter1);
        registerReceiver(mReceiver, filter2);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public boolean re_scan_device(){
        device_adapter.clear();
        device_adapter.notifyDataSetChanged();
        if(bluetooth_service != null){
            return bluetooth_service.reScan_device();
        }
        return false;
    }

    public void check(){
        //gps기능(위치기능)켜져있는지 확인
        LocationManager locate = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(!locate.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alert_on_gps();
        }

        int permission_fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permission_fine != PackageManager.PERMISSION_GRANTED || permission_coarse != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    private void alert_on_gps(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
    public void onRequestPermissionsResult(int requestCode, String[] Permission, int[] grantResults){
        switch (requestCode){
            case 100:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setTitle("알림.")
                            .setMessage("블루투스4.0을 사용하기 위한 위치권한을 얻지 못했습니다.\n설정->앱 에서 위치권한을 켜주세요.")
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
                }
                break;
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BLEService.BLE_NOTIFY)){
                device_adapter.add(intent.getStringExtra(BLEService.DEVICE_ADDRESS));
                device_adapter.notifyDataSetChanged();
            }
            else if(action.equals(BLEService.BLE_WARNING)){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setTitle("경고")
                        .setMessage("블루투스 강제종료등의 이유로 블루투스 장비의 일시적인 문제가 생겼습니다.\n장비와 스마트폰의 블루투스기능을 끄고 몇분뒤에 다시 켜주세요")
                        .setCancelable(true)
                        .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int button){
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    };


}
