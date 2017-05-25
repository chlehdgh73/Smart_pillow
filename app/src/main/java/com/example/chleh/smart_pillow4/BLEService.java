package com.example.chleh.smart_pillow4;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static java.lang.StrictMath.abs;

public class BLEService extends Service {

    public static final int STATE_INIT = 0;//초기상태
    public static final int STATE_LAIN = 1;//누운상태
    public static final int STATE_DEEP = 2;//완전수면상태
    public static final int STATE_SHALLOW = 3;//뒤척임상태
    public static final int STATE_TEMP_AWAKE = 4;//잠깐깬상태
    public static final int STATE_RE_LAIN = 5;//다시누운상태
    public static final int STATE_COMPLETE_AWAKE = 6;//기상상태

    public static final String BLE_NOTIFY = "notify_broadcast.BLEService";
    public static final String DEVICE_ADDRESS = "this_device_address";
    public static final String BLE_WARNING = "warning_broadcast.BLEService";
    public static final String STATE_CHANGE_NOTIFY = "notify_state_changed";
    public static final String NOTIFY_STATE = "this_state_value";

    private static final int REQUEST_ENABLE_BT = 1;//사용자에게 블루투스 활성화 여부를 직접 물어서 켤경우 사용
    private static final int BLE_SCAN_PERIOD = 6000;//스캔하는 시간
    private static final int DECISION_VALUE_LAIN = 200;
    private static final int DECISION_VALUE_DIFF = 50;

    private final static String TARGET_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private final static String TARGET_CHARA_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private final static String TARGET_NAME = "HMSoft";

    private final static String SAVED_STATE_FILE_NAME = "saved_state.txt";
    private final static String SAVED_ADDRESS_FILE_NAME = "saved_address.txt";
    private final static String SAVED_TEMP_LOG = "saved_log.txt";
    private final static String LOG_FILE_FOLDER = "log";

    private String TARGET_ADDRESS = "";//"A8:1B:6A:AE:4F:48";

    private Context service_context;

    private List<BluetoothDevice> device_list = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothLeScanner mBLEScanner = null;
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothGattCharacteristic mBleCharacteristic = null;

    private Handler mHandler = new Handler();

    private boolean is_ble_possible = true;
    private boolean is_ble_scanned = false;
    private boolean is_ble_connected = false;
    private boolean mScanning = false;

    private boolean is_lain = false;
    private int now_state;
    private int[] sensor_value = new int[3];
    private long state_start_time;
    private boolean restart_flag = false;
    private boolean reset_flag = true;
    //이값을 Date클래스의 생성자에 넣으면 시간을 구할수 있음, 그리고 state가 INIT일때는 의미없는 값
    //근데 영국 기준시간인데 Date 내부에서 한국시간으로 처리를 하나?

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i("정보 : ","onCreate 호출");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            is_ble_possible = false;
            return;
        }

        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            is_ble_possible = false;
            return;
        }

        service_context = this;

        mScanning = false;

        restart_flag = false;
        reset_flag = true;

        String folder_path = getFilesDir().getAbsolutePath() + File.separator + LOG_FILE_FOLDER;
        File folder = new File(folder_path);
        if(!folder.exists()){
            folder.mkdirs();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid){
        Log.i("정보 : ","onStartCommand 호출");

        if(is_ble_possible == false){
            //블루투스 지원안됨, 바로 리턴
            return super.onStartCommand(intent,flags,startid);
        }

        if(intent != null){
            Log.i("정보 : ","그냥시작됨");
            if(reset_flag == true){
                restart_flag = false;
                reset_flag = false;
                //음 여긴 뭐 딱히 할게 없다.
                now_state = STATE_INIT;
                for(int i = 0 ; i < 3 ; i++) {
                    sensor_value[i] = 0;
                }
                state_start_time = System.currentTimeMillis();
                is_lain = false;

                start_connect();
            }
        }
        else{
            Log.i("정보 : ","재시작됨");

            restart_flag = true;
            reset_flag = false;
            //파일에서 마지막으로 저장되어있던 상태를 복구함
            FileInputStream file_in = null;
            BufferedReader buffer = null;
            //여기서 파일을 읽어서 저장된 설정(선택된 베개의 블루투스 주소)를 읽어온다.
            try {
                file_in = openFileInput(SAVED_STATE_FILE_NAME);
                buffer = new BufferedReader(new InputStreamReader(file_in));
                now_state = Integer.parseInt(buffer.readLine());
                state_start_time = Long.parseLong(buffer.readLine());
                if(now_state == 0 || now_state == 4 || now_state == 6){
                    is_lain = false;
                }
                else{
                    is_lain = true;
                }
            } catch (FileNotFoundException e) {
                //파일이 없다 -> 저장된(셋팅된) 장비가 없음 -> 기본값
                now_state = STATE_INIT;
                state_start_time = System.currentTimeMillis();
                is_lain = false;
            }
            catch (IOException e1){
                //왜 발생할지 감도 안잡힙니다.
                now_state = STATE_INIT;
                state_start_time = System.currentTimeMillis();
                is_lain = false;
            }
            finally {
                try {
                    if(buffer != null) buffer.close();
                    if(file_in != null) file_in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            start_connect();
        }

        //return START_STICKY;
        return super.onStartCommand(intent,flags,startid);
    }

    public class LocalBinder extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.i("정보 : ","onBind 호출");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent){
        Log.i("정보 : ","onRebind 호출");
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.i("정보 : ","onUnbind 호출");
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("정보 : ","onDestroy 호출");

        disconnect_gatt();
    }

    private void start_connect(){
        Log.i("정보 : ","start_connect 호출");
        if(mScanning == true){
            return;
        }
        scanLeDevice(true);

        FileInputStream file_in = null;
        BufferedReader buffer = null;
        //여기서 파일을 읽어서 저장된 설정(선택된 베개의 블루투스 주소)를 읽어온다.
        try {
            file_in = openFileInput(SAVED_ADDRESS_FILE_NAME);
            buffer = new BufferedReader(new InputStreamReader(file_in));
            TARGET_ADDRESS = buffer.readLine();
        } catch (FileNotFoundException e) {
            //파일이 없다 -> 저장된(셋팅된) 장비가 없음 -> 기본값
            TARGET_ADDRESS = "null";
        }
        catch (IOException e1){
            //왜 발생할지 감도 안잡힙니다.
            TARGET_ADDRESS = "null";
        }
        finally {
            try {
                if(buffer != null) buffer.close();
                if(file_in != null) file_in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connect_gatt(TARGET_ADDRESS);
            }
        }, BLE_SCAN_PERIOD);

    }
    private void scanLeDevice(final boolean enable) {
        Log.i("정보 : ","scanLeDevice 호출");
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
            //블루투스 비활성화 되있으면 무조건 강제로 켭니다
            //만약 안켜져 있으면 블루투스 연결하지말고 경고를 포그라운드 서비스 형태로 알리는것도 괜찮을꺼 같습니다.
        }

        mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if(mBLEScanner == null){
            is_ble_scanned = false;
            return;
        }

        if (enable) {
            mScanning = true;
            is_ble_scanned = false;

            device_list.clear();

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    try {
                        mBLEScanner.stopScan(ScanCallBack);
                    }
                    catch(Exception e){
                        //6초안에 블루투스를 강제로 종료해버리면 가능한 경우
                    }
                }
            }, BLE_SCAN_PERIOD);

            mBLEScanner.startScan(ScanCallBack);

        } else {
            mScanning = false;
            mBLEScanner.stopScan(ScanCallBack);
        }

    }

    private void connect_gatt(String address){
        Log.i("정보 : ","connect_gatt 호출");
        if(mBluetoothGatt != null){
            mBluetoothGatt.close();
            is_ble_connected = false;
            mBluetoothGatt = null;
        }
        synchronized (device_list) {
            for (BluetoothDevice device : device_list) {
                if (0 == device.getAddress().compareTo(address)) {
                    mBluetoothGatt = device.connectGatt(service_context, false, mGattCallback);
                    break;
                }
            }
        }
    }

    private void disconnect_gatt(){
        Log.i("정보 : ","disconnect_gatt 호출");
        if(mBluetoothGatt != null){
            mBluetoothGatt.close();
            is_ble_connected = false;
            mBluetoothGatt = null;
        }
    }

    private ScanCallback ScanCallBack = new ScanCallback(){
        @Override
        public void onScanResult(int callbackType, ScanResult result){
            processResult(result);
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results){
            for(ScanResult result : results) {
                processResult(result);
            }
        }
        @Override
        public void onScanFailed(int errorCode){
            is_ble_scanned = false;
        }

        private void processResult(final ScanResult result){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    BluetoothDevice a = result.getDevice();

                    if (a != null && a.getName() != null && 0 == a.getName().compareTo(TARGET_NAME)) {
                        synchronized (device_list) {
                            if (!device_list.contains(a)) {

                                Intent intent = new Intent(BLE_NOTIFY);
                                intent.putExtra(DEVICE_ADDRESS, a.getAddress());
                                sendBroadcast(intent);
                                device_list.add(a);
                                is_ble_scanned = true;
                            }
                        }
                    }

                }
            });
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("정보 : ","연결됨");
                is_ble_connected = true;
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("정보 : ","끊김");
                is_ble_connected = false;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("정보 : ","서비스찾음");
                mBleCharacteristic = null;
                find_target(mBluetoothGatt.getServices());
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //만약 notify형태가아닌 read형태로 바꾸면 필요할지도?
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //여기서 데이터를 읽어들이는 작업을 해야한다.
            Log.i("정보 : ","캐릭터변경");
            data_process(characteristic);
        }

        private void find_target(List<BluetoothGattService> service_list){
            for (BluetoothGattService gattService : service_list){
                if(0 != gattService.getUuid().toString().compareTo(TARGET_SERVICE_UUID)){
                    continue;
                }
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics){
                    if(0 == gattCharacteristic.getUuid().toString().compareTo(TARGET_CHARA_UUID)){
                        Log.i("정보 : ","uuid밝혀냄");
                        mBleCharacteristic = gattCharacteristic;
                        mBluetoothGatt.setCharacteristicNotification(mBleCharacteristic, true);
                        break;
                    }
                }
            }
            if(mBleCharacteristic == null){
                Intent intent = new Intent(BLE_WARNING);
                sendBroadcast(intent);
            }
        }

        private void data_process(BluetoothGattCharacteristic characteristic){
            final byte[] data = characteristic.getValue();
            String input_data = new String(data);

            input_data = input_data.substring(0,input_data.length() - 2);

            StringTokenizer tokenizer = new StringTokenizer(input_data,",");
            int[] value = new int[3];
            int i;

            if(tokenizer.countTokens() != 3){
                //이럴리가?
                return;
            }

            i = 0;
            while(tokenizer.hasMoreTokens()){
                value[i] = Integer.parseInt(tokenizer.nextToken());
                i++;
            }

            if(restart_flag == true){
                restart_flag = false;
                i = 0;
                while(i < 3){
                    sensor_value[i] = value[i];
                    i++;
                }
                //서비스가 여러 요소로인해 서비스중에 강제종료되었다 재개되면 첫번째 센서값을 기준값으로 삼는다.
                return;
            }

            int []diff = new int[3];
            i = 0;
            while(i < 3){
                diff[i] =  abs(value[i] - sensor_value[i]);
                i++;
            }
            long time = System.currentTimeMillis();
            Log.i("정보 : ", "" + value[0] + "," + value[1] + "," + value[2]);

            Intent intent = new Intent(STATE_CHANGE_NOTIFY);

            switch(now_state){
                case STATE_INIT://현재상태가 초기상태
                    //누움을 판단하는 기준값을 넘었는지 판단하고
                    if(value[0] > DECISION_VALUE_LAIN || value[1] > DECISION_VALUE_LAIN || value[2] > DECISION_VALUE_LAIN){
                        //넘었으면 누움상태로 만들고 스테이트를 파일로 적어 저장하고
                        save_state(STATE_LAIN, time);
                        //최종 로그파일을 형성할때 사용할 임시 파일 만들고
                        //임시파일에 시작된시간, 넘어간 상태 적고
                        save_change_log(STATE_LAIN, time);
                        //상태를 바꾼다.
                        is_lain = true;
                        now_state = STATE_LAIN;
                        state_start_time = time;

                        intent.putExtra(NOTIFY_STATE, now_state);
                        sendBroadcast(intent);
                    }

                    break;
                case STATE_LAIN://현재상태가 누움상태
                    //누움을 판단하는 기준값을 넘었는지 판단하고->안넘으면 초기상태로 복귀(임시파일 삭제,스테이트파일 저장)
                    if(value[0] > DECISION_VALUE_LAIN || value[1] > DECISION_VALUE_LAIN || value[2] > DECISION_VALUE_LAIN){
                        //누움을 넘은상태라면 값이 심각하게 변화한다면 임시파일에 해당 시간과 상태를 기록하고 상태 갱신
                        if(diff[0] > DECISION_VALUE_DIFF || diff[1] > DECISION_VALUE_DIFF || diff[2] > DECISION_VALUE_DIFF){
                            save_state(STATE_LAIN, time);
                            save_change_log(STATE_LAIN, time);
                            state_start_time = time;//상태자체는 바꿀 필요 없고 기준 시간값만 바꾸면됨
                        }
                        else{
                            //값이 거의 변화하지 않았다면 완전수면시간으로 넘어갈 시간(3600초 = 1시간)인지 판단
                            if((time - state_start_time) > 1800000){
                                save_state(STATE_DEEP, time);
                                save_change_log(STATE_DEEP, time);
                                now_state = STATE_DEEP;
                                state_start_time = time;

                                intent.putExtra(NOTIFY_STATE, now_state);
                                sendBroadcast(intent);
                            }
                        }
                    }
                    else{
                        save_state(STATE_INIT, time);
                        clear_log_file();
                        is_lain = false;
                        now_state = STATE_INIT;
                        state_start_time = time;

                        intent.putExtra(NOTIFY_STATE, now_state);
                        sendBroadcast(intent);
                    }
                    break;
                case STATE_DEEP:
                    //여기부턴 위와 거의 동일한 논리로 동작하므로 자세한 주석은 생략
                    if(value[0] > DECISION_VALUE_LAIN || value[1] > DECISION_VALUE_LAIN || value[2] > DECISION_VALUE_LAIN){
                        if(diff[0] > DECISION_VALUE_DIFF || diff[1] > DECISION_VALUE_DIFF || diff[2] > DECISION_VALUE_DIFF){
                            save_state(STATE_SHALLOW, time);
                            save_change_log(STATE_SHALLOW, time);
                            now_state = STATE_SHALLOW;
                            state_start_time = time;

                            intent.putExtra(NOTIFY_STATE, now_state);
                            sendBroadcast(intent);
                        }
                    }
                    else{
                        save_state(STATE_TEMP_AWAKE, time);
                        save_change_log(STATE_TEMP_AWAKE, time);
                        is_lain = false;
                        now_state = STATE_TEMP_AWAKE;
                        state_start_time = time;

                        intent.putExtra(NOTIFY_STATE, now_state);
                        sendBroadcast(intent);
                    }
                    break;
                case STATE_SHALLOW:
                    if(value[0] > DECISION_VALUE_LAIN || value[1] > DECISION_VALUE_LAIN || value[2] > DECISION_VALUE_LAIN){
                        if(diff[0] > DECISION_VALUE_DIFF || diff[1] > DECISION_VALUE_DIFF || diff[2] > DECISION_VALUE_DIFF){
                            save_state(STATE_SHALLOW, time);
                            save_change_log(STATE_SHALLOW, time);
                            state_start_time = time;
                        }
                        else{
                            if((time - state_start_time) > 600000){
                                save_state(STATE_DEEP, time);
                                save_change_log(STATE_DEEP, time);
                                now_state = STATE_DEEP;
                                state_start_time = time;

                                intent.putExtra(NOTIFY_STATE, now_state);
                                sendBroadcast(intent);
                            }
                        }
                    }
                    else{
                        save_state(STATE_TEMP_AWAKE, time);
                        save_change_log(STATE_TEMP_AWAKE, time);
                        is_lain = false;
                        now_state = STATE_TEMP_AWAKE;
                        state_start_time = time;

                        intent.putExtra(NOTIFY_STATE, now_state);
                        sendBroadcast(intent);
                    }
                    break;
                case STATE_TEMP_AWAKE:
                    if(value[0] > DECISION_VALUE_LAIN || value[1] > DECISION_VALUE_LAIN || value[2] > DECISION_VALUE_LAIN){
                        save_state(STATE_RE_LAIN, time);
                        save_change_log(STATE_RE_LAIN, time);
                        is_lain = true;
                        now_state = STATE_RE_LAIN;
                        state_start_time = time;

                        intent.putExtra(NOTIFY_STATE, now_state);
                        sendBroadcast(intent);
                    }
                    else{
                        if((time - state_start_time) > 1800000){
                            save_state(STATE_COMPLETE_AWAKE, time);
                            save_change_log(STATE_COMPLETE_AWAKE, time);
                            now_state = STATE_COMPLETE_AWAKE;
                            state_start_time = time;

                            intent.putExtra(NOTIFY_STATE, now_state);
                            sendBroadcast(intent);
                        }
                    }
                    break;
                case STATE_RE_LAIN:
                    if(value[0] > DECISION_VALUE_LAIN || value[1] > DECISION_VALUE_LAIN || value[2] > DECISION_VALUE_LAIN){
                        if(diff[0] > DECISION_VALUE_DIFF || diff[1] > DECISION_VALUE_DIFF || diff[2] > DECISION_VALUE_DIFF){
                            save_state(STATE_RE_LAIN, time);
                            save_change_log(STATE_RE_LAIN, time);
                            state_start_time = time;
                        }
                        else{
                            if((time - state_start_time) > 600000){
                                save_state(STATE_DEEP, time);
                                save_change_log(STATE_DEEP, time);
                                now_state = STATE_DEEP;
                                state_start_time = time;

                                intent.putExtra(NOTIFY_STATE, now_state);
                                sendBroadcast(intent);
                            }
                        }
                    }
                    else{
                        save_state(STATE_TEMP_AWAKE, time);
                        save_change_log(STATE_TEMP_AWAKE, time);
                        is_lain = false;
                        now_state = STATE_TEMP_AWAKE;
                        state_start_time = time;

                        intent.putExtra(NOTIFY_STATE, now_state);
                        sendBroadcast(intent);

                    }
                    break;
                case STATE_COMPLETE_AWAKE:
                    complete_log();
                    clear_log_file();
                    save_state(STATE_INIT, time);
                    now_state = STATE_INIT;
                    state_start_time = time;

                    intent.putExtra(NOTIFY_STATE, now_state);
                    sendBroadcast(intent);

                    break;
            }
            i = 0;
            while(i < 3){
                sensor_value[i] = value[i];
                i++;
            }


        }

        private boolean save_state(int state, long time){
            FileOutputStream file_out = null;
            PrintWriter writer = null;

            //여기서 파일을 읽어서 저장된 설정(선택된 베개의 블루투스 주소)를 읽어온다.
            try {
                file_out = openFileOutput(SAVED_STATE_FILE_NAME,Context.MODE_PRIVATE);
                writer = new PrintWriter(file_out);

                writer.println(state);
                writer.println(time);

            } catch (FileNotFoundException e) {
                //파일이 없고 생성도 실패
                return false;
            }
            catch (IOException e1){
                //왜 발생할지 감도 안잡힙니다.
                return false;
            }
            finally{
                try {
                    if(writer != null) writer.close();
                    if(file_out != null) file_out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }

        private boolean save_change_log(int state, long time){
            FileOutputStream file_out = null;
            PrintWriter writer = null;
            long now_time;
            //여기서 파일을 읽어서 저장된 설정(선택된 베개의 블루투스 주소)를 읽어온다.
            try {
                file_out = openFileOutput(SAVED_TEMP_LOG,Context.MODE_APPEND);
                writer = new PrintWriter(file_out);

                writer.println(state + ":" + time);

            } catch (FileNotFoundException e) {
                //파일이 없고 생성도 실패
                return false;
            }
            catch (IOException e1){
                //왜 발생할지 감도 안잡힙니다.
                return false;
            }
            finally{
                try {
                    if(writer != null) writer.close();
                    if(file_out != null) file_out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }

        private boolean clear_log_file(){
            String path = getFilesDir().getAbsolutePath() + File.separator + SAVED_TEMP_LOG;

            File file = new File(path);
            if(file != null){
                if(file.exists()){
                    return file.delete();
                }
            }
            return false;
        }

        //"2017:05:05:23:44:33~2017:05:06:07:21:43.txt"와 같은 형태로 파일이 저장된다.
        private boolean complete_log(){
            FileOutputStream file_out = null;
            PrintWriter writer = null;
            FileInputStream file_in = null;
            BufferedReader buffer = null;

            long first_time;
            String save_path = "Unknown.txt";
            String temp;

            Date start_time, end_time;
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");

            //여기서 파일을 읽어서 저장된 설정(선택된 베개의 블루투스 주소)를 읽어온다.
            try {
                file_in = openFileInput(SAVED_TEMP_LOG);
                buffer = new BufferedReader(new InputStreamReader(file_in));

                //로그파일의 제일 처음 기록은 제일 처음 누운시각이 기록되어 있다.
                if((temp = buffer.readLine()) != null){
                    StringTokenizer tokenizer = new StringTokenizer(temp,":");
                    //처음 토큰(state)는 버리고
                    tokenizer.nextToken();
                    first_time = Long.parseLong(tokenizer.nextToken());
                    start_time = new Date(first_time);
                    end_time = new Date(state_start_time);//임시 로그파일의 완전출력을 요구하는건 완전히 깨어났을때 이므로 이시간을 쓰면됨
                    save_path = getFilesDir().getAbsolutePath() + File.separator + LOG_FILE_FOLDER;
                    save_path += File.separator + format.format(first_time) + "~" + format.format(end_time) + ".txt";
                }

                file_out = new FileOutputStream(save_path);
                writer = new PrintWriter(file_out);

                if(temp != null){
                    writer.println(temp);
                }
                while((temp = buffer.readLine()) != null){
                    writer.println(temp);
                }

            } catch (FileNotFoundException e) {
                //파일이 없고 생성도 실패
                return false;
            }
            catch (IOException e1){
                //왜 발생할지 감도 안잡힙니다.
                return false;
            }
            finally{
                try {
                    if(writer != null) writer.close();
                    if(file_out != null) file_out.close();
                    if(buffer != null) buffer.close();
                    if(file_in != null) file_in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }
    };

    //return값이 비었으면 둘중하나 : 정말로 디바이스가 없던가 or 아직 스캔중이여서 전부 못찾았거나
    public List<String> query_device() {
        Log.i("정보 : ", "query_device 호출");
        List<String> result = new ArrayList<String>();

        synchronized (device_list) {
            for (BluetoothDevice device : device_list) {
                result.add(device.getAddress());
            }
        }
        return result;
    }

    public int query_state(){
        Log.i("정보 : ","query_state 호출");
        return now_state;
    }

    public boolean query_lain_state(){
        Log.i("정보 : ","query_lain_state 호출");
        return is_lain;
    }

    public boolean query_connection(){
        Log.i("정보 : ","query_connection 호출");
        return is_ble_connected;
    }

    public boolean select_device(String address){
        Log.i("정보 : ","select_device 호출");
        //파일I/O로 파일에 셋팅내용을 쓴다
        FileOutputStream file_out = null;
        PrintWriter writer = null;
        //여기서 파일을 읽어서 저장된 설정(선택된 베개의 블루투스 주소)를 읽어온다.
        try {
            file_out = openFileOutput(SAVED_ADDRESS_FILE_NAME,Context.MODE_PRIVATE);
            writer = new PrintWriter(file_out);
            writer.println(address);
        } catch (FileNotFoundException e) {
            //파일이 없고 생성도 실패
            return false;
        }
        catch (IOException e1){
            //왜 발생할지 감도 안잡힙니다.
            return false;
        }
        finally{
            try {
                if(writer != null) writer.close();
                if(file_out != null) file_out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TARGET_ADDRESS = address;

        connect_gatt(address);

        return true;
    }

    public boolean reScan_device(){
        Log.i("정보 : ","reScan_device 호출");
        if(mScanning){
            //true이면 이미 스캔중
            return false;
        }
        else{
            scanLeDevice(true);
            return true;
        }
    }
}
