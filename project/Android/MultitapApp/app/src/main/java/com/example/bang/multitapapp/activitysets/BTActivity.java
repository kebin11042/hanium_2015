package com.example.bang.multitapapp.activitysets;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bang.multitapapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BTActivity extends Activity implements View.OnClickListener{

    private final int REQUEST_BT_ENABLE = 10;
    private static final int REQUEST_CONNECT_DEVICE = 11;

    private static final String mStrDelimiter = "\n";


    //블루투스
    private boolean isBluetoothOk;
    private boolean isBluetoothConnect;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private OutputStream outputStream;
    private InputStream inputStream;

    private String address;

    private int readBufferPosition;
    private byte[] readBuffer;

    //위젯
    private ImageButton ibtnBack;

    private TextView txtvBluetoothPlay;
    private TextView txtvBTAddress;
    private Button btn1, btn2, btn3, btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        Init();

        isBluetoothConnect = false;

        isBluetoothOk = checkBluetooth();
        if(isBluetoothOk){
            txtvBluetoothPlay.setEnabled(true);
        }
        else{
            txtvBluetoothPlay.setEnabled(false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bluetoothAdapter.disable();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.left_to_origin, R.anim.left_to_right_out);
    }

    public void Init(){
        ibtnBack = (ImageButton) findViewById(R.id.ibtnBack);

        txtvBluetoothPlay = (TextView) findViewById(R.id.txtvBluetoothPlay);
        txtvBTAddress = (TextView) findViewById(R.id.txtvMainAddress);

        btn1 = (Button) findViewById(R.id.btnMainSocket1);
        btn2 = (Button) findViewById(R.id.btnMainSocket2);
        btn3 = (Button) findViewById(R.id.btnMainSocket3);
        btn4 = (Button) findViewById(R.id.btnMainSocket4);

        ibtnBack.setOnClickListener(this);
        txtvBluetoothPlay.setOnClickListener(this);
        btn1.setOnClickListener(BTActivity.this);
        btn2.setOnClickListener(BTActivity.this);
        btn3.setOnClickListener(BTActivity.this);
        btn4.setOnClickListener(BTActivity.this);
    }

    public boolean checkBluetooth (){

        boolean check = false;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Toast.makeText(BTActivity.this, "기기가 블루투스 기능을 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            check = true;
        }

        return check;
    }

    public void getDeviceInfo(Intent data) {
        address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

        connectToSelectedDevice(bluetoothDevice);
    }

    public void connectToSelectedDevice(BluetoothDevice bluetoothDevice){
        //탐색 중지
        bluetoothAdapter.cancelDiscovery();
        try {
            ParcelUuid[] uuids = bluetoothDevice.getUuids();
            //소켓 생성
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuids[0].getUuid());
            bluetoothSocket.connect();
            if(bluetoothSocket.isConnected()){
                Toast.makeText(BTActivity.this, "블루투스 연결 완료", Toast.LENGTH_SHORT).show();
                txtvBTAddress.setText("블루투스 기기 주소 : " + address);
            }
            //in, out 스트림 연결
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            isBluetoothConnect = true;

            //beginListenForData();

        } catch (IOException e) {
            Toast.makeText(BTActivity.this, "블루투스 기기 연결 에러입니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendData(String msg){
        msg += mStrDelimiter;

        try {
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beginListenForData(){
        Log.w("beginListenForData", "beginListenForData Start");
        //버퍼 초기화
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        //쓰레드 생성
        //mWorkerThread = new WorkThread();
        //쓰레드 시작
        //mWorkerThread.start();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == txtvBluetoothPlay.getId()){

            if(isBluetoothOk){
                if(!bluetoothAdapter.isEnabled()){
                    Toast.makeText(BTActivity.this, "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_BT_ENABLE);
                }
                else{
                    Intent intent = new Intent(BTActivity.this, DeviceListActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                }
            }
        }
        else if(v.getId() == btn1.getId()){
            if(isBluetoothConnect){
                sendData("1");
            }
        }
        else if(v.getId() == btn2.getId()){
            if(isBluetoothConnect){
                sendData("2");
            }
        }
        else if(v.getId() == btn3.getId()){
            if(isBluetoothConnect){
                sendData("3");
            }
        }
        else if(v.getId() == btn4.getId()){
            if(isBluetoothConnect){
                sendData("4");
            }
        }
        else if(v.getId() == ibtnBack.getId()){
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_BT_ENABLE){
            if(resultCode == RESULT_OK){
                //Toast.makeText(BTActivity.this, "오케이", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(BTActivity.this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
            }
            else if(resultCode == RESULT_CANCELED){
                //Toast.makeText(BTActivity.this, "취소 데쓰", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQUEST_CONNECT_DEVICE){
            if(resultCode == RESULT_OK){
                getDeviceInfo(data);
            }
            else{
                //Toast.makeText(BTActivity.this, "취소 데쓰", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }





    public class BlueToothTask extends AsyncTask<String, Integer, String> {

        public BlueToothTask() {

        }

        @Override
        protected String doInBackground(String... params) {
            boolean task = true;

            while(task){
                try {
                    int bytesAvailable = inputStream.available();
                    if(bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        inputStream.read(packetBytes);
                    }
                } catch (IOException e) {
                    task = false;
                }
            }

            return null;
        }
    }
}
