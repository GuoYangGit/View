package com.fuqin.android.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fuqin.android.view.arcview.ArcActivity;
import com.fuqin.android.view.chatview.ChatActivity;
import com.fuqin.android.view.neterrorview.NetErrorActicity;
import com.fuqin.android.view.progressbar.ProgressBarActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Chat(View view) {
        startActivity(new Intent(this, ChatActivity.class));
    }

    public void Arc(View view) {
        startActivity(new Intent(this, ArcActivity.class));
    }

    public void NetError(View view) {
        startActivity(new Intent(this, NetErrorActicity.class));
    }

    public void Screen(View view) {
        startActivity(new Intent(this,ScreenActivity.class));
    }

    public void Progressbar(View view) {
        startActivity(new Intent(this,ProgressBarActivity.class));
    }
}
