package com.example.mybooktag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileDeleteActivity extends AppCompatActivity {

    Button btnFileDelete;
    ListView listviewData;
    TextView tvDeleteFileName;
    String sdcardBookTagPath; //sd카드의 파일 저장경로
    File[] listFile; //sdcardBookTagPath의 경로에있는 파일을 불러옴
    String strText; //리스트 파일이름 담을 변수
    String saveFile = "";
    String fileName;
    File file;

    SQLiteDatabase sqlDB;
   // DataInActivity.UserFileDB userFileDB;

    ArrayList<String> bookName;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_delete);

        btnFileDelete = (Button) findViewById(R.id.btnFileDelete);
        listviewData = (ListView) findViewById(R.id.listviewData);
        tvDeleteFileName = (TextView) findViewById(R.id.tvDeleteFileName);

        sdcardBookTagPath = Environment.getExternalStorageDirectory().getPath(); //파일 경로
//        userFileDB = new DataInActivity.UserFileDB(getApplicationContext());
//        sqlDB = userFileDB.getReadableDatabase(); //DB읽기

//        Cursor cursor = sqlDB.rawQuery("SELECT * FROM UserFileTBL;", null); //UserFileTBL에 저장된 파일명가져오기위해
//        cursor.moveToFirst(); //최초값
//        saveFile = cursor.getString(0); //saveFile에 0번째인덱스값을 저장

        listFile = new File(sdcardBookTagPath + "/" + "mybooktag").listFiles(); //sdcardBookTagPath의 경로에있는 파일을 불러옴
        file = new File(sdcardBookTagPath + "/" + "mybooktag");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("삭제할 도서 선택");

        SdcardBookList();

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
        }

        btnFileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
               //     String strText = (String) parent.getItemAtPosition(position); //리스트이름
                    File file = new File(sdcardBookTagPath + "/" + "mybooktag" + "/" + strText); //항목
                    file.delete();
                    bookName.remove(strText); // 삭제
                    adapter.notifyDataSetChanged(); //갱신
                    Toast.makeText(FileDeleteActivity.this, strText + "삭제됌", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }//onCreate END

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void SdcardBookList() {
        bookName = new ArrayList<String>();// text파일 이름
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, bookName);
        //sdcardBookTagPath의 경로에있는 파일을 불러옴
        String fileName, extName; //

        for (File file : listFile) {
            fileName = file.getName(); //파일이름
            extName = fileName.substring(fileName.length() - 3); //확장자만 걸러옴
            if (extName.equals("txt") || extName.equals("tml")) { //걸러온 확장자가 'txt' 단어이면 참
                bookName.add(fileName);
            }
        }

        listviewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strText = (String) parent.getItemAtPosition(position); //리스트이름
                try {
                    FileInputStream fileInputStream = new FileInputStream(sdcardBookTagPath + "/" + "mybooktag" + "/" + strText); //fileInputStream에 읽어올 파일 대입
                    // TODO: 2019-12-17 삭제추가해야함
                    tvDeleteFileName.setText(strText);
                    fileInputStream.close();
                    adapter.notifyDataSetChanged(); //갱신
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "오류!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listviewData.setAdapter(adapter); //adapter Set
    }
}
