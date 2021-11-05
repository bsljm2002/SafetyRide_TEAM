package com.example.safetysplash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Contact_main extends AppCompatActivity {
    public String TAG = "test";
    public ListView listView;
    public SQLiteDatabase sqLiteDatabase;
    public ArrayAdapter<String> taskAdapter;
    public ArrayList<String> listNames;
    public ArrayList<String> listTels;



    Button add_btn;
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_main);
        tv = (TextView) findViewById(R.id.tv);
        setTitle("전화번호부");
        listView = findViewById(R.id.listview);
        add_btn = findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Contact_main.this, Contact_plus.class);
                startActivity(intent);
            }
        });
        try {


            sqLiteDatabase = openOrCreateDatabase("phoneBook", MODE_PRIVATE, null);


            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS phoneBook" +
                    "(name VARCHAR ," +
                    "tel VARCHAR)");


        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    @Override
    public void onResume() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Contact_main.this, Contact_info.class);
                intent.putExtra("name", listNames.get(position));
                intent.putExtra("tel", listTels.get(position));
                startActivity(intent);
            }
        });
        getTasks();

        super.onResume();
    }

    public void getTasks() {


        try {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM phoneBook ORDER BY name DESC", null);

            int indexNameColumn = cursor.getColumnIndex("name");
            int indexTelColumn = cursor.getColumnIndex("tel");



            listTels = new ArrayList<String>();
            listNames = new ArrayList<String>();

            taskAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listNames);

            listView.setAdapter(taskAdapter);

            cursor.moveToFirst();


            while (cursor != null) {
                Log.i(TAG, String.format("%s, %s", cursor.getString(indexNameColumn), cursor.getString(indexTelColumn)));
                listTels.add(cursor.getString(indexTelColumn));
                listNames.add(cursor.getString(indexNameColumn));
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}

