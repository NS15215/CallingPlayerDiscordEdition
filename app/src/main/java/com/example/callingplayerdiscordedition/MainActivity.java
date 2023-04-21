// activity_mainを制御するクラス

//役割
//  擬似通話画面にて表示する名前を次のアクティビティに送信する

package com.example.callingplayerdiscordedition;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_AUDIO = 1;

    private String name;//擬似通話相手の名前を入力

    protected void setName(String name){
        this.name = name;
    }
    protected String getName(){
        return this.name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setName("");
        Button startButton = (Button) findViewById(R.id.start);

        startButton.setOnClickListener(new View.OnClickListener() {
            //動作
            //  name変数内の文字数が1文字以上であれば次のアクティビティへ遷移する。
            @Override
            public void onClick(View view) {
                EditText et = findViewById(R.id.callName);
                setName(et.getText().toString());
                if (getName().length() > 0) {
                    movingScreen();//次のアクティビティへ遷移する関数
                }
            }
        });
    }

    //name変数を次のアクティビティに渡し、アクティビティを遷移する処理
    private void movingScreen(){
        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        intent.putExtra("EXTRA_NAME", getName());

        startActivity(intent);
    }
}


