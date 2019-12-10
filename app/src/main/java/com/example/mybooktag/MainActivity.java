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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    View dialogView;
    EditText edtUserInfo_DialogAct, edtUserName_DialogAct; // dialog꺼
    ImageView ivSpeakerIcon;
    ImageView imgDataIn, imgUserInfo, imgEnd, imgStart;
    MediaPlayer mediaPlayer;

    SQLiteDatabase sqlDB;
    UserDB userDB;
    String userName;
    String userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgStart = (ImageView) findViewById(R.id.imgStart);
        imgUserInfo = (ImageView) findViewById(R.id.imgUserInfo);
        imgDataIn = (ImageView) findViewById(R.id.imgDataIn);
        imgEnd = (ImageView) findViewById(R.id.imgEnd);
        ivSpeakerIcon = (ImageView) findViewById(R.id.ivSpeakerIcon);
        userDB = new UserDB(this);
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bolero);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE); //접근성 동의

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ivSpeakerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mediaPlayer.isPlaying())) { //만약, 미디어플레이가 실행중이 아니면 참
                    ivSpeakerIcon.setImageResource(R.drawable.unspeaker3); //
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bolero);
                    mediaPlayer.setLooping(true); //무한반복
                    mediaPlayer.start();
                } else {
                    ivSpeakerIcon.setImageResource(R.drawable.speaker3); //
                    mediaPlayer.stop();
                }
            }
        });

        imgDataIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataInActivity.class);
                startActivity(intent);
            }
        });

        imgUserInfo.setOnClickListener(new View.OnClickListener() {
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
                        Cursor cursor = sqlDB.rawQuery("SELECT * FROM UserTBL", null);


                        if (edtUserName_DialogAct.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "사용자이름을 적어주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            sqlDB = userDB.getReadableDatabase(); //userDB읽기
                            sqlDB.execSQL("INSERT OR REPLACE INTO UserTBL (UserName, UserInfo) VALUES ( '" + edtUserName_DialogAct.getText().toString() + "','" + edtUserInfo_DialogAct.getText().toString() + "');");
                            sqlDB.rawQuery("SELECT * FROM UserTBL", null);
                            Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                        }

                        sqlDB.close();
                        cursor.close();
                    }
                });
                dialogBuilder.show();
// TODO: 2019-12-02 moveToLast로 저장되어있는 값이 다이어로그의 edit문에 hint로 들어가 있어야 함
            }
        });

        imgStart.setOnClickListener(new View.OnClickListener() { // btnBooktag클릭시 ChoiceAct로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChoiceActivity.class);
                startActivity(intent);
            }
        });

        imgEnd.setOnClickListener(new View.OnClickListener() { //종료
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    } //onCreate END


    public static class UserDB extends SQLiteOpenHelper {
        public UserDB(@Nullable Context context) {
            super(context, "UserDB", null, 1);//다이어로그에 저장되는값 DB
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


