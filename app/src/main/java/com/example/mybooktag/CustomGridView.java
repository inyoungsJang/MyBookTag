package com.example.mybooktag;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CustomGridView extends AppCompatActivity {

    TextView tvBookName_customGridAct;
    DataInActivity.UserFileDB userFileDB;
    SQLiteDatabase sqlDB;
    String fileName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_grid_view);

        tvBookName_customGridAct=findViewById(R.id.tvBookName_customGridAct);
        userFileDB = new DataInActivity.UserFileDB(getApplicationContext());

        sqlDB=userFileDB.getReadableDatabase(); //읽다
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM UserFileTBL;", null); //파일 이름가져옴
     //   fileName=cursor.getString(0);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            fileName=cursor.getString(0);
        }else {
            Toast.makeText(getApplicationContext(),"오류",Toast.LENGTH_SHORT).show();
        }

        tvBookName_customGridAct.setText(fileName);
        cursor.close();
        sqlDB.close();

    } //onCreate END
}
