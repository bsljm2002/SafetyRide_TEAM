package com.example.safetysplash;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Contact_plus extends AppCompatActivity {
    private String TAG = "test";
    EditText etname,brand_phone;
    Button btn_cancel , btn_add;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_plus);
        etname = (EditText)findViewById(R.id.etname);
        brand_phone = (EditText)findViewById(R.id.ettel);
        brand_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        setTitle("새 전화번호");

        btn_cancel = findViewById(R.id.btnCancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {

            //creating database
            sqLiteDatabase = openOrCreateDatabase("phoneBook", MODE_PRIVATE, null);


            btn_add = findViewById(R.id.btnAdd);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etname.getText().toString();
                    String tel = brand_phone.getText().toString();
                    saveTask(name,tel);
                }
            });

        }catch (Exception e){
            Log.i(TAG, e.toString());
        }
    }
    private void saveTask(String name, String tel){
        try{
            if(!name.isEmpty() && !tel.isEmpty() ){
                sqLiteDatabase.execSQL("INSERT INTO phoneBook (name,tel) VALUES ('" + name + "' , '" + tel + "') ");
                Toast.makeText(this, "전화번호가 저장되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(this, "전화번호 저장 실패", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

