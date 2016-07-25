package com.example.yangdianwen.listviewdivider;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangdianwen on 16-7-24.
 */
public class AutoListView extends ListView implements AbsListView.OnScrollListener {
   //区分当前是刷新还是加载
    public static final int REFRESH=0;
    public static final int LOAD=1;
    //区分PULL和RELEASE的距离大小
    private static final int SPACE=20;
    //定义headerview的四种状态
    //正常状态
    private static final int NONE=0;
    //下拉状态
    private static final int PULL=1;
    //释放状态
    private static final int RELEASE=2;
    //正在刷新
    private static final int REFRESHING=3;
    private boolean isRemark;//标记listview是否为最顶端
    private int startY;//按下 开始时的Y值
    private int state;//
    private int firstVisibleItem;//
    private int scrollState;//当前滚动状态
    private MyReflashListener myReflashListener;//刷新接口
    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;
    private LayoutInflater inflater;
    private View headerView;
    private ImageView arrow;
    private int headerHeight;
    private static final String TAG = "AutoListView";
    private TextView tip;
    private ProgressBar progress;
    private TextView lastUpDateTime;


    public AutoListView(Context context) {
        super(context);
        initView(context);
    }
    public AutoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AutoListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    // 初始化组件
    private void initView(Context context) {
        // 设置箭头特效
        animation = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        reverseAnimation = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setDuration(500);
        reverseAnimation.setFillAfter(true);
        //listview的头布局
        inflater = LayoutInflater.from(context);
        headerView = inflater.inflate(R.layout.headerview, null);
        measureView(headerView);
        //添加头部布局
        this.addHeaderView(headerView);
        //设置滑动监听
        this.setOnScrollListener(this);
        //顶部布局文件的高度
        headerHeight = headerView.getMeasuredHeight();
        Log.d(TAG, "initView: 。。。。。。。。。。。 "+headerHeight);
        //取headerHeight负值来隐藏headerView
        topPadding(-headerHeight);

    }
    //设置headerView的默认高度
    public void topPadding(int topPadding){
        headerView.setPadding(headerView.getPaddingLeft(),topPadding,
                headerView.getPaddingRight(),headerView.getPaddingBottom());
        headerView.invalidate();
    }
    //通知父布局headerView所占的高度
    private void measureView(View view){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //getChildMeasureSpec()方法：三个参数 1headerView的左右边距，2内边距，子布局宽度
            int width = ViewGroup.getChildMeasureSpec(0, 0, layoutParams.width);
            int height;
            int temHeight=layoutParams.height;
            //headerView的高度不为空，则填充布局，否则高度为0,则不填充
            if (temHeight>0){
            height=MeasureSpec.makeMeasureSpec(temHeight,MeasureSpec.EXACTLY);
            }else {
                height=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
            }
            view.measure(width,height);
        }
    }
    //重写的滑动监听
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem=firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState=scrollState;
    }
     //监听手势事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            //按下行为
            case MotionEvent.ACTION_DOWN:
                //判断listview的item是否是最顶端
                if (firstVisibleItem==0){
                     isRemark=true;
                    startY= (int) ev.getY();
                }
            break;
            //移动行为
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
            break;
            //抬起行为
            case MotionEvent.ACTION_UP:
                //如果当前状态为释放状态
                if (state==RELEASE){
                    state=REFRESHING;
                    refreshByState();
                    //加载最新数据
                    myReflashListener.onReflash();
                }else if (state==PULL){
                    state=RELEASE;
                    isRemark=false;
                    refreshByState();
                }
            break;
        }
        return super.onTouchEvent(ev);
    }
    //移动过程中的操作
    private void onMove(MotionEvent ev) {
       if (!isRemark){
           return;
       }
        //当前listview的位置
        int tempY = (int) ev.getY();
        /***onReflash
         * listview移动的距离,如果space大于0,则状态更改为下拉状态,
         * 如果space大于一定高度，并且当前为滚动状态则显示释放刷新
         * 如果space小于一定高度，则状态改为下拉状态，
         * 如果space小于0,则状态改为正常状态
         */
        int space = tempY - startY;
        int topPadding = space - headerHeight;
        switch (state){
            case NONE:
                if (space>0){
                    state=PULL;
                    refreshByState();
                }
            break;
            case PULL:
                topPadding(topPadding);
                if (space>headerHeight+30
                        &&scrollState==SCROLL_STATE_TOUCH_SCROLL){
                    state=RELEASE;
                    refreshByState();
                }
            break;
            case RELEASE:
                topPadding(topPadding);
                //如果space小于一定高度，则状态改为下拉状态，
                if (space<headerHeight+30){
                    state=PULL;
                    refreshByState();
                }else if (space<=0){
                    state=NONE;
                    isRemark=false;
                    refreshByState();
                }
            break;
        }
    }
    //根据当前状态，更新界面显示
    private void refreshByState(){
        tip = (TextView) headerView.findViewById(R.id.tv_tip);
        arrow = (ImageView) headerView.findViewById(R.id.arrow);
        progress = (ProgressBar) headerView.findViewById(R.id.progress);
        switch (state){
            //正常状态不显示headerView
            case NONE:
             topPadding(-headerHeight);
            break;
            //下拉状态tip显示，arrow显示，progress不显示
            case PULL:
                arrow.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                tip.setText(R.string.pullToRefresh);
                arrow.clearAnimation();
                arrow.setAnimation(reverseAnimation);
            break;
            case RELEASE:
                arrow.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                tip.setText(R.string.ReleseToRefresh);
                arrow.clearAnimation();
                arrow.setAnimation(animation);
            break;
            case REFRESHING:
                topPadding(50);
                arrow.setVisibility(GONE);
                progress.setVisibility(VISIBLE);
                tip.setText(R.string.Refreshing);
                arrow.clearAnimation();
            break;
        }
    }
    //刷新完成，状态为正常状态
    public void refreshComplete(){
        state=NONE;
        isRemark=false;
        //刷新界面
        refreshByState();
        lastUpDateTime = (TextView) headerView.findViewById(R.id.tv_time);
        //设置上次刷新的时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastUpDateTime.setText(time);
    }
    //设置刷新监听的方法（类似setOnclickListener）
    public void setInterface(MyReflashListener myReflashListener){
        this.myReflashListener=myReflashListener;
    }
    //刷新数据接口
    public interface MyReflashListener{
        void onReflash();
    }
}
