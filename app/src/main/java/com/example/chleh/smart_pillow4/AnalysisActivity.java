package com.example.chleh.smart_pillow4;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class AnalysisActivity extends AppCompatActivity {
    SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
    static final long HOUR = 30*60;
    static final long HALF_HOUR  = 10*60;
//    final String saveAnalysisPath = getFilesDir().getAbsolutePath() + File.separator + "analysis";

    private ArrayAdapter fileNameAdapter;
    private ListView listview;
    private TextView totalTimeView;
    private TextView sleepTimeView;
    private TextView shallowTimeView;
    private TextView awakeTimeView;
    private TextView sleepEfficiencyView;

    private List<String> FileNameList;
    private File analysisFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        final String saveAnalysisPath = getFilesDir().getAbsolutePath() + File.separator + "analysis";

        File file = new File(getFilesDir().getAbsolutePath() + File.separator + "log");
        if(!file.exists()){
            file.mkdirs();
        }
        File[] fileList = file.listFiles();

        analysisFile = new File(saveAnalysisPath);
        if(!analysisFile.exists()) {
            analysisFile.mkdirs();
        }

        Log.d("test","loop no??");
        for(int i = 0; i < fileList.length; i++){
            Log.d("test","loop");
            filterLog(fileList[i]);                         //로그분석
        }


        for(int i = 0; i < fileList.length; i++){
            Log.d("test","loop2");
            fileList[i].delete();                         //로그삭제
        }


        analysisFile = new File(saveAnalysisPath);          // 분석 파일 가져오기


        File[] analysisFileList = analysisFile.listFiles();

        FileNameList = new ArrayList<String>();

        Log.d("test length 2",  Integer.toString(analysisFileList.length));//테스트용
        for(int i = 0; i < analysisFileList.length ; i++){
            FileNameList.add(analysisFileList[i].getName());
        }

        final Comparator myComparator= new Comparator() {                           //파일 리스트 정렬
            private final Collator collator = Collator.getInstance();
            @Override
            public int compare(Object object1, Object object2) {
                return collator.compare(object1, object2);
            }
        };

        Collections.sort(FileNameList, myComparator);
        Collections.reverse(FileNameList);

        fileNameAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1) ;
        for(int i = 0 ; i < FileNameList.size() ; i++) {
            fileNameAdapter.add(FileNameList.get(i));
        }

        listview = (ListView) findViewById(R.id.AnalysisListView);
        listview.setAdapter(fileNameAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View clickedView, int position, long id){
                String totalSleep = "";
                String totalShallow = "";
                String totalAwake = "";
                String totalTime = "";
                String sleepEfficiency = "";

                //               String strText = (String) parent.getItemAtPosition(position);
                String select = fileNameAdapter.getItem(position).toString();
                Log.d("test result : ", select);
                //               File openFile = new File(saveAnalysisPath + "/" + select);
                FileInputStream file_in = null;
                BufferedReader buffer = null;

                totalTimeView = (TextView) findViewById(R.id.totalTimeView);
                sleepTimeView = (TextView) findViewById(R.id.sleepTimeView);
                shallowTimeView = (TextView) findViewById(R.id.shallowTimeView);
                awakeTimeView = (TextView) findViewById(R.id.awakeTimeView);
                sleepEfficiencyView = (TextView) findViewById(R.id.sleepEfficiencyView);

                try {
                    file_in = new FileInputStream(saveAnalysisPath + File.separator + select);
                    buffer = new BufferedReader(new InputStreamReader(file_in));

                    totalTime = buffer.readLine();
                    totalSleep = buffer.readLine();
                    totalShallow = buffer.readLine();
                    totalAwake = buffer.readLine();
                    sleepEfficiency = buffer.readLine();

                    totalTimeView.setText(totalTime);
                    sleepTimeView.setText(totalSleep);
                    shallowTimeView.setText(totalShallow);
                    awakeTimeView.setText(totalAwake);
                    sleepEfficiencyView.setText(sleepEfficiency);

                } catch (IOException e){
                    e.printStackTrace();
                }

            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View clickedView, final int position, long id){

                AlertDialog.Builder builder = new AlertDialog.Builder(AnalysisActivity.this);
                builder
                        .setTitle("삭제 알림")
                        .setMessage("해당 파일을 삭제하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("확인",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int button){
                                String select = fileNameAdapter.getItem(position).toString();
                                File deleteFile = new File(saveAnalysisPath + File.separator + select);
                                deleteFile.delete();

                                FileNameList.remove(position);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileNameAdapter.notifyDataSetChanged();
                                    }
                                });

                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        final String saveAnalysisPath = getFilesDir().getAbsolutePath() + File.separator + "analysis";

        File file = new File(getFilesDir().getAbsolutePath() + File.separator + "log");
        if(!file.exists()){
            file.mkdirs();
        }
        File[] fileList = file.listFiles();

        Log.d("test","loop no??");
        for(int i = 0; i < fileList.length; i++){
            Log.d("test","loop");
            filterLog(fileList[i]);                         //로그분석
        }

        for(int i = 0; i < fileList.length; i++){
            Log.d("test","loop2");
            fileList[i].delete();                         //로그삭제
        }

        analysisFile = new File(saveAnalysisPath);
        if(!analysisFile.exists()) {
            analysisFile.mkdirs();
        }
        File[] analysisFileList = analysisFile.listFiles();

        FileNameList = new ArrayList<String>();
        Log.d("test length 2",  Integer.toString(analysisFileList.length));//테스트용
        for(int i = 0; i < analysisFileList.length ; i++){
            FileNameList.add(analysisFileList[i].getName());
        }

        final Comparator myComparator= new Comparator() {                           //파일 리스트 정렬
            private final Collator collator = Collator.getInstance();
            @Override
            public int compare(Object object1, Object object2) {
                return collator.compare(object1, object2);
            }
        };

        Collections.sort(FileNameList, myComparator);
        Collections.reverse(FileNameList);

        fileNameAdapter.clear();
        for(int i = 0 ; i < FileNameList.size() ; i++) {
            fileNameAdapter.add(FileNameList.get(i));
        }
        fileNameAdapter.notifyDataSetChanged();
    }

    private void filterLog(File file){
        long totalSleep = 0;
        long totalShallow = 0;
        long totalAwake = 0;
        long totalTime = 0;
        double sleepEfficiency = 0;
        String saveAnalysisPath = getFilesDir().getAbsolutePath() + File.separator + "analysis";
        Log.d("test","filter in");
        FileInputStream file_in = null;
        BufferedReader buffer = null;
        FileOutputStream file_out = null;
        PrintWriter writer = null;

        String temp;
        int currentState = 0;
        String save_path = "";
        long sleepStartTime;
        String preStateTime = "";
        String postStateTime = "";
        String tempToken;
        try{
            file_in = new FileInputStream(getFilesDir().getAbsolutePath() + File.separator + "log" + File.separator + file.getName());      //getFilesDir().getAbsolutePath() + "/log"
            buffer = new BufferedReader(new InputStreamReader(file_in));
            Log.d("test","1");
            while((temp = buffer.readLine()) != null) {
                Log.d("test token 1 :", temp);
                StringTokenizer tokenizer = new StringTokenizer(temp,":");
                if(tokenizer.nextToken().equals("2")){
                    currentState = 2;
                    preStateTime = tokenizer.nextToken();

//                    sleepStartTime = format.parse(preStateTime).getTime()-(60*60*1000);
                    sleepStartTime = Long.parseLong(preStateTime);
                    Date date = new Date(sleepStartTime-(HOUR*1000));
//                    save_path = "/analysis/" + format.format(new Date(sleepStartTime)) + ".txt";
//                    save_path = saveAnalysisPath + File.separator + format.format(new Date(sleepStartTime)) + ".txt";
                    //                   save_path = format.format(new Date(sleepStartTime).toString());
                    save_path = format.format(date);
                    break;
                }
            }
            totalSleep += HOUR;

            while((temp = buffer.readLine()) != null){
                StringTokenizer tokenizer = new StringTokenizer(temp,":");
                tempToken = tokenizer.nextToken();

                if (currentState == 2){
                    if(tempToken.equals("3")){
                        currentState = 3;
                        postStateTime = tokenizer.nextToken();
                        totalSleep += timeCalculator(preStateTime, postStateTime);
                        preStateTime = postStateTime;
                    }
                    if(tempToken.equals("4")){
                        currentState = 4;
                        postStateTime = tokenizer.nextToken();
                        totalSleep += timeCalculator(preStateTime,postStateTime);
                        preStateTime = postStateTime;
                    }
                }//완료
                else if(currentState == 3){
                    if(tempToken.equals("2")){
                        currentState = 2;
                        postStateTime = tokenizer.nextToken();
                        totalShallow += (timeCalculator(preStateTime, postStateTime)-HALF_HOUR);
                        preStateTime = postStateTime;
                        totalSleep += HALF_HOUR;
                    }
                    if(tempToken.equals("4")){
                        currentState = 4;
                        postStateTime = tokenizer.nextToken();
                        totalShallow += timeCalculator(preStateTime,postStateTime);
                        preStateTime = postStateTime;
                    }
                }
                else if(currentState == 4){
                    if(tempToken.equals("2")) {
                        currentState = 2;
                        postStateTime = tokenizer.nextToken();
                        totalAwake += (timeCalculator(preStateTime, postStateTime) - HALF_HOUR);
                        preStateTime = postStateTime;
                        totalSleep += HALF_HOUR;
                    }
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
            // 토스트 에러
        }
        finally{
            try {
                if(buffer != null) buffer.close();
                if(file_in != null) file_in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("test","2");
        totalTime = totalSleep + totalAwake + totalShallow;

        try {
            file_out = new FileOutputStream(saveAnalysisPath + File.separator + save_path);
            writer = new PrintWriter(file_out);

            String totalTimeString = convertDisplayFormat(totalTime);
            String totalSleepString = convertDisplayFormat(totalSleep);
            String totalShallowString = convertDisplayFormat(totalShallow);
            String totalAwakeString = convertDisplayFormat(totalAwake);

            writer.println(totalTimeString);
            writer.println(totalSleepString);
            writer.println(totalShallowString);
            writer.println(totalAwakeString);

            sleepEfficiency = ((double) totalSleep/(double) totalTime)*100;
            sleepEfficiency = Math.round(sleepEfficiency*100d) / 100d;  //소수점 둘째자리 반올림
            String sleepEfficiencyString = sleepEfficiency + "%";
            writer.println(sleepEfficiencyString);

            writer.flush();
            Log.d("test","3");

        } catch (IOException e){
            e.printStackTrace();
        }
        finally{
            try {
                if(writer != null) writer.close();
                if(file_out != null) file_out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private long timeCalculator(String pre, String post){
        long diff;
        long diffsec = 0;

        try {
//            Date start_time = format.parse(pre);
//            Date end_time = format.parse(post);
            long start_time = Long.parseLong(pre);
            long end_time = Long.parseLong(post);
//            diff = end_time.getTime() - start_time.getTime();
            diff = end_time - start_time;
            diffsec = diff / (1000);

        } catch (Exception e){
            e.printStackTrace();
        }

        return diffsec;
    }
    private String convertDisplayFormat(long time){
        String format= "";
        long min;
        long hour;
        long sec = time;

        min = sec / 60;
        hour = min / 60;
        sec -= min * 60;
        min -= hour * 60;


        format = hour + "시간 " + min + "분 " + sec + "초";

        return format;
    }
}
