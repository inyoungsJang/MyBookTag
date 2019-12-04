package com.example.mybooktag;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnBooktag, btnEnd, btnUserInfo, btnDataIn; //
    View dialogView;
    EditText edtUserInfo_DialogAct, edtUserName_DialogAct; // dialog꺼

    SQLiteDatabase sqlDB;
    UserDB userDB;
    String userName;
    String userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBooktag = (Button) findViewById(R.id.btnBooktag);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnUserInfo = (Button) findViewById(R.id.btnUserInfo);
        btnDataIn = (Button) findViewById(R.id.btnDataIn);
        userDB = new UserDB(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE); //접근성 동의

        btnDataIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataInActivity.class);
                startActivity(intent);
            }
        });

        btnUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setTitle("정보 입력");
                dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_userinfo, null); //dialog_userinfo(커스텀 다이어로그)inflate
                edtUserInfo_DialogAct = (EditText) dialogView.findViewById(R.id.edtUserInfo_DialogAct);
                edtUserName_DialogAct = (EditText) dialogView.findViewById(R.id.edtUserName_DialogAct);

                dialogBuilder.setView(dialogView); //세트
                dialogBuilder.setNegativeButton("취소", null);
                dialogBuilder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = userDB.getWritableDatabase();

                        sqlDB.execSQL("INSERT OR REPLACE INTO UserTBL (UserName, UserInfo) VALUES ( '" + edtUserName_DialogAct.getText().toString() + "','" + edtUserInfo_DialogAct.getText().toString() + "');");
                        Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                        sqlDB = userDB.getReadableDatabase(); //userDB읽기

                        sqlDB.rawQuery("SELECT * FROM UserTBL", null);
                        Cursor cursor = sqlDB.rawQuery("SELECT * FROM UserTBL", null);

                        if (cursor != null && cursor.moveToFirst()) {
                            cursor.moveToLast(); //마지막 데이타를 읽엉어옴
                            userName = cursor.getString(0);
                            userInfo = cursor.getString(1);
                        }
                        edtUserName_DialogAct.setHint(userName);
                        edtUserInfo_DialogAct.setHint(userInfo);
                        sqlDB.close();
                        cursor.close();


                        // 저장 버튼 누를시 동작되는
                        //  edtUserInfo_DialogAct,edtUserName_DialogAct 의 입력값을 ChoiceAct의 tvUserName_ChoiceAct,tvUserInfo_ChoiceAct에 넘겨줌
                        //1120 DB // 1125 // 1126// 1127 //1202!!!!
                    }
                });
                dialogBuilder.show();
// TODO: 2019-12-02 moveToLast로 저장되어있는 값이 다이어로그의 edit문에 hint로 들어가 있어야 함
            }
        });

        btnBooktag.setOnClickListener(new View.OnClickListener() { // btnBooktag클릭시 ChoiceAct로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChoiceActivity.class);
                startActivity(intent);
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() { //종료
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    } //onCreate END


    public static class UserDB extends SQLiteOpenHelper {
        public UserDB(@Nullable Context context) {
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
}


