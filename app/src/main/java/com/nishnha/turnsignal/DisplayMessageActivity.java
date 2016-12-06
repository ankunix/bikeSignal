package com.nishnha.turnsignal;

import android.content.Intent;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        layout.addView(textView);
    }
}
