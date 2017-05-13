package com.example.chleh.smart_pillow4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Bluetooth extends AppCompatActivity {
    private static final String TAG = "Bluetooth";
    private static final int REQUEST_CONNECT_DEVICE =1;
    private static final int REQUEST_ENABLE_BT =2;
    private static final boolean D=true;
    public static final int MESSAGE_STATE_CHANGE=1;
    private Button btn_Connect;
    private BluetoothService bluetoothService_obj = null;
private Handler mHandler = new Handler()
{
    public void handlerMessage(Message msg)
    {
        super.handleMessage(msg);
        switch (msg.what){

            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG,"MESSAGE_STATE_CHANGE"+msg.arg1);
            switch (msg.arg1)
            {
                case BluetoothService.STATE_CONNECTED :
                    Toast.makeText(getApplicationContext(),"블루투스 성공",Toast.LENGTH_SHORT).show();
                break;
                case BluetoothService.STATE_FAIL:
                    Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                break;
            }
        break;
        }

    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate");

        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        btn_Connect=(Button)findViewById(R.id.bluetooth_connect);
        btn_Connect.setOnClickListener(mClickListener);


        if(bluetoothService_obj==null)
        {
            bluetoothService_obj = new BluetoothService(this,mHandler);
        }

    }
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //분기.
            switch ( v.getId() ){

                case R.id.bluetooth_connect :  //모든 블루투스의 활성화는 블루투스 서비스 객체를 통해 접근한다.

                    if(bluetoothService_obj.getDeviceState()) // 블루투스 기기의 지원여부가 true 일때
                    {
                        bluetoothService_obj.enableBluetooth();  //블루투스 활성화 시작.
                    }
                    else
                    {
                        finish();
                    }
                    break ;

                default: break ;

            }//switch
        }
    };
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult" + resultCode);
        // TODO Auto-generated method stub

        switch(requestCode)
        {

           case REQUEST_ENABLE_BT:
                //When the request to enable Bluetooth returns
                if(resultCode != Activity.RESULT_OK)  //취소를 눌렀을 때
                {
                    bluetoothService_obj.scanDevice();
                }
              else
                {
                    Log.d(TAG,"bluetooth is not enable");

                }
            break;
            case REQUEST_CONNECT_DEVICE:
                if(requestCode == Activity.RESULT_OK)
                {
                        bluetoothService_obj.getDeviceInfo(data);
                }
                break;
        }

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
}
