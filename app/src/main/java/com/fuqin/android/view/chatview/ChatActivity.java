package com.fuqin.android.view.chatview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fuqin.android.view.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private LineChart mPPChart;
    private String[] str = {"2.00%", "4.00%", "6.00%", "8.00%", "10.00%"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mPPChart = findViewById(R.id.ppchat);
        mPPChart.setData(getData(), str);
    }

    public List<ChatData> getData() {
        List list = new ArrayList();
        ChatData dataObject1 = new ChatData("30天", 50);
        list.add(dataObject1);
        ChatData dataObject2 = new ChatData("60天", 53);
        list.add(dataObject2);
        ChatData dataObject3 = new ChatData("90天", 55);
        list.add(dataObject3);
        ChatData dataObject4 = new ChatData("120天", 60);
        list.add(dataObject4);
        ChatData dataObject5 = new ChatData("150天", 62);
        list.add(dataObject5);
        ChatData dataObject6 = new ChatData("180天", 64);
        list.add(dataObject6);
        ChatData dataObject7 = new ChatData("240天", 70);
        list.add(dataObject7);
        ChatData dataObject8 = new ChatData("300天", 80);
        list.add(dataObject8);
        ChatData dataObject9 = new ChatData("360天", 90);
        list.add(dataObject9);
        return list;
    }

    public void onclick(View view) {
        mPPChart.startAnimation();
    }
}
