package com.example.minproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //todo
        /*
        * 1、基本翻译 中——英 （是否可以选择语言
        * 2、翻译页面展示 （跳转到另外一个界面
        * 3、翻译界面展示的内容
        * 4、是否能在主页面的底端添加固定按钮
        * 5、是否有开启动画
        *  */
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP
                        && !editText.getText().toString().equals(""))
                {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        Button sentence = findViewById(R.id.sentence);
        Button listen = findViewById(R.id.listen);
        Button audio = findViewById(R.id.audio);

        sentence.setOnClickListener(onClickListener);
        listen.setOnClickListener(onClickListener);
        audio.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = null;

            switch (v.getId()) {
                case R.id.sentence:
                    fragment = new SentenceFragment();
                    break;
                case R.id.listen:
                    fragment = new ListenFragment();
                    break;
                case R.id.audio:
                    fragment = new AudioFragment();
                    break;
                default:
                    break;
            }

            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.commit();
        }
    };
}