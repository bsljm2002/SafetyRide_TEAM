
package com.example.safetysplash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BlueActivity extends AppCompatActivity implements rr {

    public BluetoothSPP bt;
    private String TAG = "test";
    Button btnSave;
    EditText textPhoneNo;
    EditText textSMS;
    private SQLiteDatabase sqLiteDatabase;
    private final int MY_PERMISSION_REQUEST_SMS = 1001;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blue);
        btnSave = (Button) findViewById(R.id.button);
        textPhoneNo = (EditText) findViewById(R.id.phoneNumber);
        textSMS = (EditText) findViewById(R.id.message);

        bt = new BluetoothSPP(this);





        try {

            sqLiteDatabase = openOrCreateDatabase("phoneBook", MODE_PRIVATE, null);

        }catch (Exception e){
            Log.i(TAG, e.toString());
        }



        //SMS 권한 얻기
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("info");
                builder.setMessage("This app won't work properly unless you grant SMS permission");

                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(BlueActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SMS);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SMS);
            }
        }
        //블루투스 사용 불가할 경우
        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        //낙상 데이터 수신
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            TextView receivedata=findViewById(R.id.receivedata);
            TextView zerone=findViewById(R.id.zerone);

            public void onDataReceived(byte[] data, String message) {
                String[] array=message.split(",");

                receivedata.setText(array[0]);
                zerone.setText("");
                String blueData=array[0];
                int iblueData=Integer.parseInt(blueData);
                //낙상 데이터가 1일 경우 문자 발송
                if(iblueData==1) {

                    String phoneNumber = textPhoneNo.getText().toString();
                    String tmessage = textSMS.getText().toString();

                    if ((phoneNumber.length() > 0) && (tmessage.length() > 0)) {
                        sendSMS(phoneNumber, tmessage);
                        Toast.makeText(getBaseContext(), phoneNumber + "\n" + tmessage, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getBaseContext(), "전화번호와 메세지를 입력하시오", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            // 블루투스가 연결되었을때
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }
            //연결해제
            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();

            }
            //연결 실패
            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
        //연결버튼 누르면 블루투스와 연결 시도
        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

    }

    //SMS권한 결과값에 따른 허용/불허용
    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult){
        switch (requestCode){
            case MY_PERMISSION_REQUEST_SMS:{
                if ((grantResult.length > 0) && (grantResult[0] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onResume(){
        getTasks();

        super.onResume();
    }

    private void getTasks(){

        try{
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT tel FROM phoneBook", null);

            while(cursor.moveToNext()){
                textPhoneNo.append(cursor.getString(0));
            }
            cursor.close();
            sqLiteDatabase.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //SMS보내는 함수
    public void sendSMS(String phoneNumber,String ttmessage){

        SmsManager sms=SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber,null,ttmessage,null,null);
    }


    //연결중단
    public void onDestroy(){
        super.onDestroy();
        bt.stopService();
    }

    public void onStart(){
        super.onStart();
        if(!bt.isBluetoothEnabled()){
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        }else{
            if(!bt.isServiceAvailable()){
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);//아두이노와 안드로이드를 연결

            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);

            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}



