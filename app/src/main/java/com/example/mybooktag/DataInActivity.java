package com.example.mybooktag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DataInActivity extends AppCompatActivity {
    TextView tvShowData; //list클릭시 파일내용 보여줄 TextView
    TextView tvFileNum;
    ListView listviewData;
    int fileNum=0;

    String sdcardBookTagPath = Environment.getExternalStorageDirectory().getPath() + "/BookTag/"; //sd카드의 파일 저장경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_in);

        tvShowData = (TextView) findViewById(R.id.tvShowData);
        tvFileNum = (TextView) findViewById(R.id.tvFileNum);
        listviewData = (ListView) findViewById(R.id.listviewData);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("문서 관리");

        SdcardBookList();




    } //onCreate END


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //?
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.datainmenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //안드로이드스튜디오에서 지원해주는 메서드를이용하여, 액션바 뒤로가기버튼 클릭시 홈으로
                finish();
                break;
            case R.id.item_Upgrade:
                Intent intentUpgrade = new Intent(getApplicationContext(), FileUpgradeActivity.class);
                startActivity(intentUpgrade);
                break;
            case R.id.item_Delete:
                Intent intentDelete = new Intent(getApplicationContext(), FileDeleteActivity.class);
                startActivity(intentDelete);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

   /* public static class Books extends SQLiteOpenHelper {
        public Books(@Nullable Context context) {
            super(context, "UserDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE UserTBL (UserName TEXT PRIMARY KEY, UserInfo TEXT);"); //UserTBL Table생성
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }*/

    void SdcardBookList() {

        final ArrayList<String> bookName = new ArrayList<String>();// text파일 이름
        File[] listFile = new File(sdcardBookTagPath).listFiles(); //sdcardBookTagPath의 경로에있는 파일을 불러옴
        String fileName, extName; //

        for (File file : listFile) {
            fileName = file.getName(); //파일이름
            extName = fileName.substring(fileName.length() - 3); //확장자만 걸러옴
            if (extName.equals("txt")) { //걸러온 확장자가 'txt' 단어이면 참
                bookName.add(fileName);
                fileNum++;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, bookName);

        listviewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strText = (String) parent.getItemAtPosition(position); //리스트이름
                try {
                    FileInputStream fileInputStream = new FileInputStream(sdcardBookTagPath + strText); //fileInputStream에 읽어올 파일 대입
                    byte[] txt = new byte[fileInputStream.available()]; //해당파일의 내용 모두
                    fileInputStream.read(txt);
                    tvShowData.setText(new String(txt));
                    fileInputStream.close();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "오류!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvFileNum.setText(fileNum); //????????????오류
        adapter.notifyDataSetChanged();
        listviewData.setAdapter(adapter);
    }
}
