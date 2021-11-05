package com.example.safetysplash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Contact_info extends AppCompatActivity {
    EditText tvname,tvtel;
    Button edit, delete ;
    private String TAG = "test";
    private SQLiteDatabase sqLiteDatabase;
    String name,tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        setTitle("정보 상세보기");
        tvname = (EditText) findViewById(R.id.txtname);
        tvtel = (EditText) findViewById(R.id.tvTel);
        edit = (Button) findViewById(R.id.btn_edit);
        delete = findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        tel = intent.getStringExtra("tel");
        tvname.setText(name);
        tvtel.setText(tel);

        try {
            //creating database
            sqLiteDatabase = openOrCreateDatabase("phoneBook", MODE_PRIVATE, null);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Contact_info.this);
                    dlg.setTitle("수정")
                            .setMessage("선택한 전화번호를 정말 수정하시겠습니까?")
                            .setNegativeButton("취소",null)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String nametv = tvname.getText().toString();
                                    String teltv = tvtel.getText().toString();
                                    updateTask(nametv,teltv);
                                }
                            })
                            .show();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Contact_info.this);
                    dlg.setTitle("삭제")
                            .setMessage("선택한 전화번호를 정말 삭제하시겠습니까?")
                            .setNegativeButton("취소",null)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String name = tvname.getText().toString();
                                    String tel = tvtel.getText().toString();
                                    removeTask(name,tel);
                                }
                            })
                            .show();
                }
            });
        }catch (Exception e){
            Log.i(TAG, e.toString());
        }
    }
    private void updateTask(String nametv, String teltv){
        try{
            if(!nametv.isEmpty() && !teltv.isEmpty()){
                sqLiteDatabase.execSQL("UPDATE phoneBook SET name = '" + nametv + "' , tel = '" + teltv +"' WHERE name = '" + name + "' AND tel = '" + tel +"'");
                Toast.makeText(this, "전화번호가 수정되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(this, "전화번호 수정 실패", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void removeTask(String name,String tel){
        try{
            sqLiteDatabase.execSQL("DELETE FROM phoneBook WHERE name ='" + name + "' AND tel = '" + tel +"'");
            Toast.makeText(this, "전화번호가 삭제되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

