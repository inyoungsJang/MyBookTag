package com.example.mybooktag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DataInActivity extends AppCompatActivity {
    TextView tvShowData; //list클릭시 파일내용 보여줄 TextView
    EditText edtCreateFile_CreatefileAct;
    ListView listviewData;
    View dialogView;

    SQLiteDatabase sqlDB;
    UserFileDB userFileDB;
    File file; //폴더
    String fileNameMake; //사용자가 생성할 폴더의 이름
    String sdcardBookTagPath; //사용자가 생성한 폴더의 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_in);
        sdcardBookTagPath = Environment.getExternalStorageDirectory().getPath();
        tvShowData = (TextView) findViewById(R.id.tvShowData);
        listviewData = (ListView) findViewById(R.id.listviewData);

        userFileDB = new UserFileDB(getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("문서 관리");

        SdcardBookList();

    } //onCreate END

    @Override
    protected void onResume() {
        super.onResume();
        SdcardBookList();
    }

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
            case R.id.item_What:
                Intent intentWhat = new Intent(getApplicationContext(), FileWhatActivity.class);
                startActivity(intentWhat);
                break;
            case R.id.item_Delete:
                Intent intentDelete = new Intent(getApplicationContext(), FileDeleteActivity.class);
                startActivity(intentDelete);
                break;
            case R.id.item_CreateFile:
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DataInActivity.this);
                dialogView = (View) View.inflate(DataInActivity.this, R.layout.createfile, null); //dialog_userinfo(커스텀 다이어로그)inflate
                // TODO: 2019-12-23 item_CreateFile 클릭시 enabled 적용 또는 file이 존재하면 enabled 적용
                edtCreateFile_CreatefileAct = (EditText) dialogView.findViewById(R.id.edtCreateFile_CreatefileAct);

                dialogBuilder.setView(dialogView); //세팅
                dialogBuilder.setNegativeButton("취소", null);
                dialogBuilder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sqlDB = userFileDB.getWritableDatabase(); //쓰다
                        //sd카드의 파일 저장경로
                        fileNameMake = edtCreateFile_CreatefileAct.getText().toString(); //editText의 입력값을 fileNameMake에 저장하여 파일생성에 쓰일것임
                        file = new File(sdcardBookTagPath + "/" + fileNameMake);

                        if (!file.exists()) {
                            sqlDB.execSQL("INSERT OR REPLACE INTO UserFileTBL VALUES ( '" + fileNameMake + "');"); //SQLite를 이용하여 fileNameMake의 값을 저장!!!!!!!!
                            file.mkdir(); //폴더생성
                            Toast.makeText(getApplicationContext(), "폴더가 생성되었습니다", Toast.LENGTH_SHORT).show();
                        }
                        sqlDB.close();
                    }
                });
                dialogBuilder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    void SdcardBookList() {
        String fileName, extName; //
        String saveFile = "";

        sqlDB = userFileDB.getWritableDatabase(); //오픈
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM UserFileTBL;", null); //파일 이름가져옴
        try {
            cursor.moveToFirst(); //마지막값(최신값)을
            saveFile = cursor.getString(0); //saveFile에 0번째인덱스값을 저장
            fileNameMake = saveFile;
            file = new File(sdcardBookTagPath + "/" + saveFile); //값이 계속 남아있어야하기때문에 데이터베이스에 사용자가 생성한 파일명을 저장하여 새로이 file이 탄생쓰
            // Toast.makeText(getApplicationContext(),"DB읽음"+file,Toast.LENGTH_SHORT).show();
        } catch (CursorIndexOutOfBoundsException e) {
            file = new File(sdcardBookTagPath + "/" + saveFile);
            // Toast.makeText(getApplicationContext(),"예외!!",Toast.LENGTH_SHORT).show();
        }

        //   if (cursor != null && cursor.moveToFirst()) { //cursor에 값이 있는경우
        if (file.exists()) { //폴더가 존재하면
            final ArrayList<String> bookName = new ArrayList<String>();// text파일 이름
            File[] listFile = new File(sdcardBookTagPath + "/" + saveFile).listFiles(); //sdcardBookTagPath의 경로에있는 파일을 불러옴
            for (File file : listFile) {
                fileName = file.getName(); //파일이름
                extName = fileName.substring(fileName.length() - 3); //확장자만 걸러옴
                if (extName.equals("txt")|| extName.equals("tml")) { //걸러온 확장자가 'txt' 단어이면 참
                    bookName.add(fileName);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, bookName);

            final String finalSaveFile = saveFile;
            listviewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String strText = (String) parent.getItemAtPosition(position); //리스트이름
                    try {
                        FileInputStream fileInputStream = new FileInputStream(sdcardBookTagPath + "/" + finalSaveFile + "/" + strText); //fileInputStream에 읽어올 파일 대입
                        byte[] txt = new byte[fileInputStream.available()]; //해당파일의 내용 모두
                        fileInputStream.read(txt);
                        tvShowData.setText(new String(txt));
                        fileInputStream.close();

                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "오류!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            sqlDB.close();
            cursor.close();
            adapter.notifyDataSetChanged(); //갱신
            listviewData.setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "먼저 폴더를 생성해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public static class UserFileDB extends SQLiteOpenHelper {
        public UserFileDB(@Nullable Context context) {
            super(context, "UserFileDB", null, 1);//다이어로그에 저장되는값 DB
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE UserFileTBL (UserFileName TEXT PRIMARY KEY);"); //UserFile TBL Table생성
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {     }
        @Override
        public SQLiteDatabase getWritableDatabase() {
            return super.getWritableDatabase();
        }
        @Override
        public SQLiteDatabase getReadableDatabase() {
            return super.getReadableDatabase();
        }
    }
}

