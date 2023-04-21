package com.example.callingplayerdiscordedition;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class SubActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_AUDIO = 1;

    private MediaPlayer mediaPlayer;//音声ファイル再生用の変数
    private boolean playFlag;//mp3ファイルが停止中か、再生中かの状態を保存する変数



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //指定されたファイルが音声ファイルかつ、データの取得が問題なく行えている場合に処理
        if (requestCode == REQUEST_CODE_SELECT_AUDIO && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();//指定した音声ファイルのパスを格納
            try{
                mediaPlayer = new MediaPlayer();
                setVolumeControlStream(AudioManager.STREAM_MUSIC);//音声出力先を設定
                mediaPlayer.setDataSource(getApplicationContext(), uri);//パスを指定してファイルを読み込む
                mediaPlayer.prepare();//指定したファイルを読み込む

            }catch(Exception e){}//強制終了防止
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        ImageView menu = findViewById(R.id.menu);
        Button skip = findViewById(R.id.skip);
        Button reverse = findViewById(R.id.reverse);
        Button stop = findViewById(R.id.stop);
        Button start = findViewById(R.id.start);
        Button end = findViewById(R.id.end);
        Button menu_visit = findViewById(R.id.menu_bar_visit);
        Button menu_hide = findViewById(R.id.menu_bar_hide);

        Intent intent = getIntent();
        String callName = intent.getStringExtra("EXTRA_NAME");

        TextView name_bottom = (TextView) findViewById(R.id.name_bottom);
        TextView name_middle = (TextView) findViewById(R.id.name_middle);
        TextView name_top = (TextView) findViewById(R.id.name_top);
        name_bottom.setText(callName);
        name_middle.setText(callName);
        name_top.setText(callName);

        playFlag = false;
        Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        mIntent.setType("audio/*");
        startActivityForResult(mIntent, REQUEST_CODE_SELECT_AUDIO);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            TextView time = findViewById(R.id.time_table);
            int seconds = 0;
            int minutes = 0;
            int hours = 0;
            @Override
            public void run() {
                // 処理を記述
                if(playFlag == true) {
                    if (seconds < 59) {
                        seconds += 1;
                    } else if (minutes < 59) {
                        minutes += 1;
                        seconds = 0;
                    } else {
                        hours += 1;
                        minutes = 0;
                    }

                    if (hours > 0) {
                        time.setText(String.format("%02:%02d:%02d", hours, minutes, seconds));
                    } else {
                        time.setText(String.format("%02d:%02d", minutes, seconds));
                    }
                }else {
                    if (hours > 0) {
                        time.setText(String.format("%02:%02d:%02d", hours, minutes, seconds));
                    } else {
                        time.setText(String.format("%02d:%02d", minutes, seconds));
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        menu_visit.setOnClickListener(new View.OnClickListener() {//メニューを隠す処理
            @Override
            public void onClick(View view) {
                start.setVisibility(View.GONE);//非表示に切り替え
                stop.setVisibility(View.GONE);//非表示に切り替え
                skip.setVisibility(View.GONE);//非表示に切り替え
                reverse.setVisibility(View.GONE);//非表示に切り替え
                end.setVisibility(View.GONE);//非表示に切り替え
                menu.setVisibility(View.GONE);//非表示に切り替え
                menu_visit.setVisibility(View.GONE);//非表示に切り替え

                menu_hide.setVisibility(View.VISIBLE);//表示切り替え
            }
        });

        menu_hide.setOnClickListener(new View.OnClickListener() {//メニューを表示する処理
            @Override
            public void onClick(View view) {
                menu_hide.setVisibility(View.GONE);//表示切り替え

                menu_visit.setVisibility(View.VISIBLE);//非表示に切り替え
                menu.setVisibility(View.VISIBLE);//非表示に切り替え
                skip.setVisibility(View.VISIBLE);//非表示に切り替え
                reverse.setVisibility(View.VISIBLE);//非表示に切り替え
                end.setVisibility(View.VISIBLE);//非表示に切り替え

                //再生中かどうか判断して表示させるボタンを変更する
                if(playFlag == true){stop.setVisibility(View.VISIBLE);}else{start.setVisibility(View.VISIBLE);}
            }
        });

        start.setOnClickListener(new View.OnClickListener() {//mp3を再生
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                playFlag = true;
                start.setVisibility(View.GONE);//非表示に切り替え
                stop.setVisibility(View.VISIBLE);//表示切り替え
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {//mp3を停止
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                playFlag = false;
                stop.setVisibility(View.GONE);//非表示に切り替え
                start.setVisibility(View.VISIBLE);//表示切り替え
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {//5秒早送り
            @Override
            public void onClick(View view) {

                int seek = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(seek + 5000);
            }
        });

        reverse.setOnClickListener(new View.OnClickListener() {//5秒早戻し
            @Override
            public void onClick(View view) {

                int seek = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(seek - 5000);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                mediaPlayer.release();
                finish();
            }
        });
    }
}