package com.fuqin.android.view.neterrorview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fuqin.android.view.R;

public class NetErrorActicity extends AppCompatActivity {
    private NetErrorBottom mBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_error_acticity);
        mBottom = findViewById(R.id.buttom);
        mBottom.setOnClickListener(v -> {
            mBottom.startAnimation();
        });
    }
}
