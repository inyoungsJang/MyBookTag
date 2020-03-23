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
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
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
    ImageView icon;
    ImageView ivGalleryGo;

    View dialogView;
    EditText edtUserInfo_DialogAct, edtUserName_DialogAct; // dialog꺼
    ImageView ivSpeakerIcon;
    TextView tvDataIn, tvUserInfo, tvEnd;
    MediaPlayer mediaPlayer;
    File file; //폴더

    SQLiteDatabase sqlDB;
    UserDB userDB;
    String userName;
    String userInfo;

    // DataInActivity.UserFileDB userFileDB;
    // SQLiteDatabase sqlDB2;
    String saveFile = ""; //사용자가 저장한 폴더명
    String sdcardBookTagPath; //사용자가 생성한 폴더의 경로
    TextView tvBookName_customGridAct;

    GalleryActivity galleryActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  galleryActivity.tedPermission();//갤러리 권한

        tvDataIn = (TextView) findViewById(R.id.tvDataIn);
        tvUserInfo = (TextView) findViewById(R.id.tvUserInfo);
        tvEnd = (TextView) findViewById(R.id.tvEnd);
        icon = (ImageView) findViewById(R.id.icon);
        ivGalleryGo=(ImageView)findViewById(R.id.ivGalleryGo);

        gridView = (GridView) findViewById(R.id.bookImg);
        tvUserInfo_ChoiceAct = (TextView) findViewById(R.id.tvUserInfo_ChoiceAct);
        tvUserName_ChoiceAct = (TextView) findViewById(R.id.tvUserName_ChoiceAct);
        tvGoogle = (TextView) findViewById(R.id.tvGoogle);
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        tvVoice = (TextView) findViewById(R.id.tvVoice);

        ivSpeakerIcon = (ImageView) findViewById(R.id.ivSpeakerIcon);
        userDB = new UserDB(this);
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.bolero);

        sdcardBookTagPath = Environment.getExternalStorageDirectory().getPath(); //경로로

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE); //접근성 동의

        Intent intent = new Intent(getApplicationContext(), SplashActivity.class); //로딩화면
        startActivity(intent);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "얏홍", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        ivGalleryGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryActivity.gotoGallery();
                ivGalleryGo.setImageBitmap(galleryActivity.setImage());
//                Intent intent1 = new Intent(getApplicationContext(),GalleryActivity.class);
//                startActivity(intent1);
            }
        });*/

        File[] listFile = new File(sdcardBookTagPath + "/" + "mybooktag").listFiles(); //sdcardBookTagPath의 경로에있는 파일명을 불러옴
        ArrayList<String> strings = new ArrayList<String>();
        file = new File(sdcardBookTagPath + "/" + "mybooktag");
        if (file.exists()) {
            for (int i = 0; i < listFile.length; i++) {
                strings.add(listFile[i].getName());
            }
        } else {
            file.mkdir(); //폴더생성
        }


        BookImage bookImage = new BookImage(this, strings);
        gridView.setAdapter(bookImage);

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


    } //onCreate END

    void load() {
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
        LayoutInflater layoutInflater;
        ArrayList<String> strings;

        public BookImage(Context context, ArrayList<String> strings) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.strings = strings;
        }

        @Override
        public int getCount() {
            return strings.size();
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
            View myView = layoutInflater.inflate(R.layout.activity_custom_grid_view, null, false);

            tvBookName_customGridAct = (TextView) myView.findViewById(R.id.tvBookName_customGridAct);
            tvBookName_customGridAct.setText(strings.get(position));

            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay(); // 기기의 화면 사이즈 정보
            int x = display.getWidth() / 3; //기기의 화면사이즈 정보의 가로길이 나누기 3

            myView.setLayoutParams(new GridView.LayoutParams(x, x));

            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(getApplicationContext(), OpenBookActivity.class);
                    intent.putExtra("FILENAME", strings.get(position));
             //       intent.putExtra("SAVEFILE", saveFile);
                    startActivity(intent);
                    Log.i("putIntent", "인텐트 무사히 실행 ok");
                }
            });
            return myView;
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


