package com.example.yangdianwen.listviewdivider;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoListView.MyReflashListener {

    private AutoListView listview;
  List<String>data;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data=new ArrayList<>();
        listview = (AutoListView) findViewById(R.id.local_groups_list);
        //设置接口
        listview.setInterface(this);
        for (int i = 0; i <20 ; i++) {
            data.add(""+i);
        }
        listview.setDividerHeight(20);
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,data);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i <data.size() ; i++) {
                    if (position== i){
                        Toast.makeText(MainActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
   //刷新数据接口回调
    @Override
    public void onReflash() {
        Handler mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listview.refreshComplete();
            }
        },2000);

    }
}
