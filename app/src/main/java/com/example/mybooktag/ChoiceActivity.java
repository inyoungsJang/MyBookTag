package com.example.mybooktag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChoiceActivity extends AppCompatActivity {
    Button btnBack_ChoiceAct;
    TextView tvUserName_ChoiceAct, tvUserInfo_ChoiceAct;
    GridView gridView;
    //  GridLayout gridLayout;
    Integer[] bookImgID = {R.drawable.bookimage, R.drawable.berry2, R.drawable.berry3, R.drawable.berry4, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage, R.drawable.bookimage,R.drawable.bookimage,R.drawable.bookimage,R.drawable.bookimage,R.drawable.bookimage,R.drawable.bookimage,R.drawable.bookimage,R.drawable.bookimage,R.drawable.bookimage,};
    int[] bookOpen;

    String userName, userInfo;
    MainActivity.UserDB userDB;
    SQLiteDatabase sqlDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        gridView = (GridView) findViewById(R.id.bookImg);
        tvUserInfo_ChoiceAct = (TextView) findViewById(R.id.tvUserInfo_ChoiceAct);
        tvUserName_ChoiceAct = (TextView) findViewById(R.id.tvUserName_ChoiceAct);
        userDB = new MainActivity.UserDB(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("갖고있는도서");

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

        BookImage bookImage = new BookImage(this);
        gridView.setAdapter(bookImage);
        sqlDB.close();
        cursor.close();


    } //onCreate END

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
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
}

