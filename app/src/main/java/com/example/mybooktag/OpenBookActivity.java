package com.example.mybooktag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;

public class OpenBookActivity extends AppCompatActivity {

    TextView tv, tvBookName;
    String sdcardBookTagPath; //사용자가 생성한 폴더의 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);

        tv = (TextView) findViewById(R.id.tv);
        tvBookName = (TextView) findViewById(R.id.tvBookName);

        sdcardBookTagPath = Environment.getExternalStorageDirectory().getPath(); //경로로

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("FILENAME"); //파일이름
       // String saveFile = intent.getStringExtra("SAVEFILE"); //폴더이름

        tvBookName.setText(fileName);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(tvBookName.getText().toString());


        try {
            FileInputStream fileInputStream = new FileInputStream(sdcardBookTagPath + "/" + "mybooktag" + "/" + fileName); //fileInputStream에 읽어올 파일 대입
            byte[] txt = new byte[fileInputStream.available()]; //해당파일의 내용 모두
            fileInputStream.read(txt);
            tv.setText(new String(txt));
            fileInputStream.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "파일 내용 못 읽어옴!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //안드로이드스튜디오에서 지원해주는 메서드를이용하여, 액션바 뒤로가기버튼 클릭시 홈으로
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
