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
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView tvUserName_ChoiceAct, tvUserInfo_ChoiceAct;
    TextView tvVoice, tvSearch, tvGoogle;
    GridView gridView;
    Integer[] bookImgID = {R.drawable.bookimage, R.drawable.berry2, R.drawable.berry3, R.drawable.berry4, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage,};


    View dialogView;
    EditText edtUserInfo_DialogAct, edtUserName_DialogAct; // dialog꺼
    ImageView ivSpeakerIcon;
  //  ImageView imgDataIn, imgUserInfo, imgEnd, imgStart; //초기화면의 버튼이미지
    TextView tvDataIn,tvUserInfo,tvEnd;
    MediaPlayer mediaPlayer;

    SQLiteDatabase sqlDB, sqlDB2;
    UserDB userDB;
    String userName;
    String userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        imgStart = (ImageView) findViewById(R.id.imgStart);
//        imgUserInfo = (ImageView) findViewById(R.id.imgUserInfo);
//        imgDataIn = (ImageView) findViewById(R.id.imgDataIn);
//        imgEnd = (ImageView) findViewById(R.id.imgEnd);
        tvDataIn=(TextView)findViewById(R.id.tvDataIn);
        tvUserInfo=(TextView)findViewById(R.id.tvUserInfo);
        tvEnd=(TextView)findViewById(R.id.tvEnd);

        gridView = (GridView) findViewById(R.id.bookImg);
        tvUserInfo_ChoiceAct = (TextView) findViewById(R.id.tvUserInfo_ChoiceAct);
        tvUserName_ChoiceAct = (TextView) findViewById(R.id.tvUserName_ChoiceAct);
        tvGoogle = (TextView) findViewById(R.id.tvGoogle);
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        tvVoice = (TextView) findViewById(R.id.tvVoice);


        ivSpeakerIcon = (ImageView) findViewById(R.id.ivSpeakerIcon);
        userDB = new UserDB(this);
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bolero);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE); //접근성 동의

        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(intent);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ///      ((DataInActivity)DataInActivity.context).SdcardBookList();

//    File file = new File()

        ivSpeakerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mediaPlayer.isPlaying())) { //만약, 미디어플레이가 실행중이 아니면 참
                    ivSpeakerIcon.setImageResource(R.drawable.unspeaker); //
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bolero);
                    mediaPlayer.setLooping(true); //무한반복
                    mediaPlayer.start();
                } else {
                    ivSpeakerIcon.setImageResource(R.drawable.speaker); //
                    mediaPlayer.stop();
                }
            }
        });

        tvDataIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataInActivity.class);
                startActivity(intent);
            }
        });

        tvUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                //dialogBuilder.setTitle("정보 입력");
                dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_userinfo, null); //dialog_userinfo(커스텀 다이어로그)inflate
                edtUserInfo_DialogAct = (EditText) dialogView.findViewById(R.id.edtUserInfo_DialogAct);
                edtUserName_DialogAct = (EditText) dialogView.findViewById(R.id.edtUserName_DialogAct);

                dialogBuilder.setView(dialogView); //세트
                dialogBuilder.setNegativeButton("취소", null);
                dialogBuilder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = userDB.getWritableDatabase(); //쓰다
                        //  Cursor cursor = sqlDB.rawQuery("SELECT * FROM UserTBL", null);


                        if (edtUserName_DialogAct.getText().toString().equals("")) { //EditText에 입력값이 없을시 참
                            Toast.makeText(getApplicationContext(), "사용자이름을 적어주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            sqlDB = userDB.getReadableDatabase(); //userDB읽기
                            //SQLite에 insert (data추가)
                            sqlDB.execSQL("INSERT OR REPLACE INTO UserTBL (UserName, UserInfo) VALUES ( '" + edtUserName_DialogAct.getText().toString() + "','" + edtUserInfo_DialogAct.getText().toString() + "');");
                            //    sqlDB.rawQuery("SELECT * FROM UserTBL", null);
                            Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                        }
                        load();
                        sqlDB.close();
                        //   cursor.close();
                    }
                });
                dialogBuilder.show();
                // TODO: 2019-12-02 moveToLast로 저장되어있는 값이 다이어로그의 edit문에 hint로 들어가 있어야 함
            }
        });

        load();



        tvEnd.setOnClickListener(new View.OnClickListener() { //종료
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //암시적인텐트 호출

                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "마이크");

                startActivityForResult(intent, 50);
            }
        });
        tvGoogle.setOnClickListener(new View.OnClickListener() { //이벤트발생
            @Override
            public void onClick(View v) { //onClick은 메소드
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://translate.google.co.kr/?hl=ko#view=home&op=translate&sl=en&tl=ko&text=")); //+ result
                startActivity(intent); //실행
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() { //이벤트발생
            @Override
            public void onClick(View v) { //onClick은 메소드
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
                startActivity(intent); //실행
            }
        });


        BookImage bookImage = new BookImage(this);
        gridView.setAdapter(bookImage);
    } //onCreate END

    void load(){
        sqlDB = userDB.getReadableDatabase(); //userDB읽기
        sqlDB.rawQuery("SELECT * FROM UserTBL", null);
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM UserTBL", null);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToLast();
            userName = cursor.getString(0);
            userInfo = cursor.getString(1);
        }
        tvUserName_ChoiceAct.setText(userName);
        tvUserInfo_ChoiceAct.setText(userInfo);

        sqlDB.close();
        cursor.close();
      //  adapter.notifyDataSetChanged();
    }

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



    public class BookImage extends BaseAdapter {
        Context context;

        public BookImage(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return bookImgID.length;
        } //bookImgID의 배열길이 만큼 Count

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay(); // 기기의 화면 사이즈 정보
            int x = display.getWidth() / 3; //기기의 화면사이즈 정보의 가로길이 나누기 3

            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(x, x));

            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(bookImgID[position]);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(getApplicationContext(), BookStoryActivity.class);
                    startActivity(intent);
                }
            });
            return imageView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 50) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result = results.get(0); //ArrayList 는 length 가 아닌 get 으로.
            //tvResult.setText(result);

            //Uri uri= Uri.parse("https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query="+result); //네이버
            Uri uri = Uri.parse("https://www.google.com/search?q=" + result); //크롬
            //Uri uri = Uri.parse("https://translate.google.co.kr/?hl=ko#view=home&op=translate&sl=en&tl=ko&text="+ result); //크롬 번역기 Korea -> English
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}


