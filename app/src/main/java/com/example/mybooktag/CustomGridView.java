package com.example.mybooktag;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class CustomGridView extends AppCompatActivity {

    TextView tvBookName_customGridAct;
    //   DataInActivity.UserFileDB userFileDB;
  //  SQLiteDatabase sqlDB;
    String fileName = "";
    String sdcardBookTagPath;
    File file;
    ArrayList<String> bookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_grid_view);

        tvBookName_customGridAct = findViewById(R.id.tvBookName_customGridAct);
        //  userFileDB = new DataInActivity.UserFileDB(getApplicationContext());

        //  sqlDB=userFileDB.getReadableDatabase(); //읽다
        // Cursor cursor = sqlDB.rawQuery("SELECT * FROM UserFileTBL;", null); //파일 이름가져옴
        //   fileName=cursor.getString(0);

        //    if (cursor != null && cursor.moveToFirst()) {
        //        cursor.moveToFirst();
        //     fileName=cursor.getString(0);
        // }else {
        //    Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
        // }
        sdcardBookTagPath = Environment.getExternalStorageDirectory().getPath();
        file = new File(sdcardBookTagPath + "/" + "mybooktag");

        if (file.exists()) {
            bookName = new ArrayList<String>();// text파일 이름
            File[] listFile = new File(sdcardBookTagPath + "/" + "mybooktag").listFiles(); //sdcardBookTagPath의 경로에있는 파일을 불러옴
            for (File file : listFile) {
                fileName = file.getName(); //파일이름
//                extName = fileName.substring(fileName.length() - 3); //확장자만 걸러옴
//                if (extName.equals("txt") || extName.equals("tml")) { //걸러온 확장자가 'txt' 단어이면 참
                bookName.add(fileName);
//                tvBookName_customGridAct.setText(fileName);
                //  }

            }
            tvBookName_customGridAct.setText(fileName);
        }

        //    cursor.close();
        //   sqlDB.close();

    } //onCreate END
}
