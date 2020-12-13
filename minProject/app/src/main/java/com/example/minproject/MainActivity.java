package com.example.minproject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView resultView;

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
        resultView = findViewById(R.id.result);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP
                        && editText.getText() != null) {
                    String word = editText.getText().toString();
                    word.replace("\n\r", "");
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    BaiduTranslateUtils.postToBaidu(word, resultView);

                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("word", word);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
                return false;
            }
        });

        Button sentence = findViewById(R.id.sentence);
        Button listen = findViewById(R.id.listen);
        Button audio = findViewById(R.id.audio);

/*
        ImageView dic = findViewById(R.id.dic);
        ImageView tran = findViewById(R.id.tran);
        ImageView account = findViewById(R.id.account);
*/

        sentence.setOnClickListener(onClickListener);
        listen.setOnClickListener(onClickListener);
        audio.setOnClickListener(onClickListener);
/*
        dic.setOnClickListener(onClickListener);
        tran.setOnClickListener(onClickListener);
        account.setOnClickListener(onClickListener);
*/
        ImageButton takePhoto = findViewById(R.id.imageButton);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                startActivity(intent);
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
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
/*
                case R.id.dic:
                    fragment = new MainFragment();
                    break;
                case R.id.tran:
                    fragment = new TransactionFragment();
                    break;
                case R.id.account:
                    fragment = new AccountFragment();
                    break;
*/
                default:
                    break;
            }
            /*if (v.getId() == R.id.dic || v.getId() == R.id.tran || v.getId() == R.id.account) {
                fragmentTransaction.replace(R.id.fragmentContainerMain, fragment);
            } else {

            }*/
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.commit();
        }
    };
}