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
import java.io.InputStream;
import java.util.ArrayList;

public class DataInActivity extends AppCompatActivity {
    TextView tvShowData;
    ListView listviewData;

    String sdcardBookTagPath = Environment.getExternalStorageDirectory().getPath() + "/BookTag/"; //저장경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_in);

        tvShowData = (TextView) findViewById(R.id.tvShowData);
        listviewData = (ListView) findViewById(R.id.listviewData);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("문서 관리");

        SdcardBookList();


    } //onCreate END


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.datainmenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String sdcardPath = Environment.getRootDirectory().getAbsolutePath() + "/BookTag";
        File[] files = (new File(sdcardPath).listFiles());
        String strFileName;

        switch (item.getItemId()) {
            case android.R.id.home:
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

    public static class Books extends SQLiteOpenHelper {

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
    }

    void SdcardBookList() {
        final ArrayList<String> bookName = new ArrayList<String>();// text파일 이름
        File[] listFile = new File(sdcardBookTagPath).listFiles(); //sdcardBookTagPath의 경로에있는 파일을 불러옴
        String fileName, extName; //

        for (File file : listFile) {
            fileName = file.getName(); //파일이름
            extName = fileName.substring(fileName.length() - 3); //확장자만 걸러옴
            if (extName.equals("txt")) { //걸러온 확장자가 'txt' 단어이면 참
                bookName.add(fileName);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, bookName);
        listviewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// TODO: 2019-12-02 아이템 클릭시 클릭된 항목 배경색 변경

            }
        });
        listviewData.setAdapter(adapter);


    }


}
