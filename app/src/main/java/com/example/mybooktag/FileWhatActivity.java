package com.example.mybooktag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class FileWhatActivity extends AppCompatActivity {
TextView tvFileWhat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_what);

        tvFileWhat=(TextView)findViewById(R.id.tvFileWhat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("궁금한게있으신가요 ?");


        InputStream inputStream = getResources().openRawResource(R.raw.file_what_raw); //
        try {
            byte[] txt = new byte[inputStream.available()];
            inputStream.read(txt);
           tvFileWhat.setText(new String(txt));// txt(byte) => txt(String)
            inputStream.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"오류",Toast.LENGTH_SHORT).show();
        }
    }//onCreate END


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
