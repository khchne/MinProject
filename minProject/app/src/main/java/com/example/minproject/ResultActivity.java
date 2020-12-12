package com.example.minproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    private ImageView back;
    String word;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        word = bundle.getString("word");
        TextView tv_word = findViewById(R.id.word);
        tv_word.setText(word);
        result = findViewById(R.id.result);
        initView();
    }

    private void initView() {
        BaiDuTranslation.postToBaiDu(word, result);
    }
}