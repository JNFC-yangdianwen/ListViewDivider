package com.example.yangdianwen.listviewdivider;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements AutoListView.MyReflashListener,AutoListView.OnLoadMoreListener {
    private AutoListView listview;
    private DataAdapter adapter;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (AutoListView) findViewById(R.id.local_groups_list);
        //设置接口
        listview.setInterface(this,this);
        listview.setDividerHeight(20);
        adapter = new DataAdapter();
        listview.setAdapter(adapter);
    }
   //刷新数据接口回调
    @Override
    public void onReflash() {
        adapter.clearDatas();
        Handler mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <20 ; i++) {
                    adapter.addDatas("下拉刷新的数据"+i);
                }
                listview.refreshComplete();
            }
        },2000);
    }

    @Override
    public void loadMore() {
        Handler mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <10 ; i++) {
                    adapter.addDatas("上拉加载更多的数据！");
                }
                listview.loadComplete();
            }
        },4000);
    }
}
