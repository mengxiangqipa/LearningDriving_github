package com.my.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.my.pulltorefresh.pullableview.Pullable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义的布局，用来管理三个子控件，其中一个是下拉头，一个是包含内容的pullableView（可以是实现Pullable接口的的任何View），
 * 还有一个上拉头，更多详解见博客http://blog.csdn.net/zhongkejingwang/article/details/38868463
 */
public class PullToRefreshLayout extends RelativeLayout {
    public static final String TAG = "PullToRefreshLayout";
    /**
     * last y
     */
    private int mLastMotionY;
    /**
     * last x
     */
    private int mLastMotionX;
    /**
     * 初始状态
     */
    public static final int INIT = 0;
    /**
     * 释放刷新
     */
    public static final int RELEASE_TO_REFRESH = 1;
    /**
     * 正在刷新
     */
    public static final int REFRESHING = 2;
    /**
     * 释放加载
     */
    public static final int RELEASE_TO_LOAD = 3;
    /**
     * 正在加载
     */
    public static final int LOADING = 4;
    /**
     * 操作完毕
     */
    public static final int DONE = 5;
    /**
     * 当前状态
     */
    private int state = INIT;
    /**
     * 刷新回调接口
     */
    private OnRefreshListener mListener;
    /**
     * 刷新成功
     */
    public static final int SUCCEED = 0;
    /**
     * 刷新失败
     */
    public static final int FAIL = 1;
    /**
     * downY按下Y坐标，lastY上一个事件点Y坐标
     */
    private float downY, lastY;

    /**
     * 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
     */
    public float pullDownY = 0;
    /**
     * 上拉的距离
     */
    private float pullUpY = 0;
    /**
     * 释放刷新的距离
     */
    float refreshDist = 150;
    /**
     * 释放加载的距离
     */
    float loadmoreDist = 150;

    private MyTimer timer;
    /**
     * 回滚速度
     */
    public float MOVE_SPEED = 8;
    /**
     * 第一次执行布局
     */
    private boolean isLayout = false;
    /**
     * 在刷新过程中滑动操作
     */
    private boolean isTouch = false;
    /**
     * 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
     */
    private float radio =1f;

    // 下拉箭头的转180°动画
    private Animation rotateAnimation;
    // 均匀旋转动画
    private Animation refreshingAnimation;

    /**
     * 下拉头
     */
    View refreshView;
    /**
     * 下拉的箭头
     */
    View pullDownView;
    /**
     * 正在刷新的图标
     */
    View refreshingView;
    /**
     * 刷新结果图标
     */
    View refreshStateImageView;
    /**
     * 刷新结果：成功或失败
     */
    TextView refreshStateTextView;
    /**
     * txt:让更新来的更猛烈些吧
     */
    TextView refreshContentTextView;
    /**
     * 上拉头
     */
    View loadmoreView;
    /**
     * 上拉的箭头
     */
    View pullUpView;
    /**
     * 正在加载的图标
     */
    View loadingView;
    /**
     * 加载结果图标
     */
    View loadStateImageView;
    /**
     * 加载结果：成功或失败
     */
    TextView loadStateTextView;
    /**
     * txt:让更新来的更猛烈些吧
     */
    TextView loadContentTextView;

    /**
     * 实现了Pullable接口的View
     */
    private View pullableView;
    /**
     * 过滤多点触碰
     */
    private int mEvents;
    /**
     * 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
     */
    private boolean canPullDown = true;
    /**
     * 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
     */
    private boolean canPullUp = true;

    /**
     * 执行自动回滚的handler
     */
    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 回弹速度随下拉距离moveDeltaY增大而增大
            MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
                    / getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
            if (!isTouch) {
                // 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
                if (state == REFRESHING && pullDownY <= refreshDist) {
                    pullDownY = refreshDist;
                    timer.cancel();
                } else if (state == LOADING && -pullUpY <= loadmoreDist) {
                    pullUpY = -loadmoreDist;
                    timer.cancel();
                }

            }
            if (pullDownY > 0)
                pullDownY -= MOVE_SPEED;
            else if (pullUpY < 0)
                pullUpY += MOVE_SPEED;
            if (pullDownY < 0) {
                // 已完成回弹
                pullDownY = 0;
                pullDownView.clearAnimation();
                // 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                if (state != REFRESHING && state != LOADING)
                    changeState(INIT);
                timer.cancel();
            }
            if (pullUpY > 0) {
                // 已完成回弹
                pullUpY = 0;
                pullUpView.clearAnimation();
                // 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                if (state != REFRESHING && state != LOADING)
                    changeState(INIT);
                timer.cancel();
            }
            // 刷新布局,会自动调用onLayout
            requestLayout();
        }

    };

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public PullToRefreshLayout(Context context) {
        super(context);
        initView(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        timer = new MyTimer(updateHandler);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.reverse_anim);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.rotating);
        // 添加匀速转动动画
        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);
        refreshingAnimation.setInterpolator(lir);
    }

    private void hide() {
        timer.schedule(5);
    }

    /**
     * 完成刷新操作，显示刷新结果。注意：刷新完成后一定要调用这个方法
     */
    /**
     * @param refreshResult PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
     */
    public void refreshFinish(int refreshResult) {
        try {
            refreshingView.clearAnimation();
            refreshingView.setVisibility(View.GONE);
            switch (refreshResult) {
                case SUCCEED:
                    // 刷新成功
                    refreshStateImageView.setVisibility(View.VISIBLE);

                    if (null != PullDownStateText)
                        refreshStateTextView.setText(PullDownStateText);
                    else
                        refreshStateTextView.setText(R.string.refresh_succeed);
                    refreshStateImageView
                            .setBackgroundResource(R.drawable.allview_refresh_succeed);
                    break;
                case FAIL:
                default:
                    // 刷新失败
                    refreshStateImageView.setVisibility(View.VISIBLE);
                    refreshStateTextView.setText(R.string.refresh_fail);
                    refreshStateImageView
                            .setBackgroundResource(R.drawable.allview_refresh_failed);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 刷新结果停留500毫秒
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                changeState(DONE);
                hide();
            }
        }.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 加载完毕，显示加载结果。注意：加载完成后一定要调用这个方法
     *
     * @param refreshResult PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
     */
    public void loadmoreFinish(int refreshResult) {
        try {
            loadingView.clearAnimation();
            loadingView.setVisibility(View.GONE);
            switch (refreshResult) {
                case SUCCEED:
                    // 加载成功
                    loadStateImageView.setVisibility(View.VISIBLE);
                    loadStateTextView.setText(R.string.load_succeed);
                    loadStateImageView
                            .setBackgroundResource(R.drawable.allview_load_succeed);
                    break;
                case FAIL:
                default:
                    // 加载失败
                    loadStateImageView.setVisibility(View.VISIBLE);
                    loadStateTextView.setText(R.string.load_fail);
                    loadStateImageView
                            .setBackgroundResource(R.drawable.allview_load_failed);
                    break;
            }
            // 刷新结果停留500毫秒
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    changeState(DONE);
                    hide();
                }
            }.sendEmptyMessageDelayed(0, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeState(int to) {
        state = to;
        switch (state) {
            case INIT:
                // 下拉布局初始状态
                refreshStateImageView.setVisibility(View.GONE);
                refreshStateTextView.setText(R.string.pull_to_refresh);
                pullDownView.clearAnimation();
                pullDownView.setVisibility(View.VISIBLE);
                // 上拉布局初始状态
                loadStateImageView.setVisibility(View.GONE);
                loadStateTextView.setText(R.string.pullup_to_load);
                pullUpView.clearAnimation();
                pullUpView.setVisibility(View.VISIBLE);
                break;
            case RELEASE_TO_REFRESH:
                // 释放刷新状态
                refreshStateTextView.setText(R.string.release_to_refresh);
                pullDownView.startAnimation(rotateAnimation);
                break;
            case REFRESHING:
                // 正在刷新状态
                pullDownView.clearAnimation();
                refreshingView.setVisibility(View.VISIBLE);
                pullDownView.setVisibility(View.INVISIBLE);
                refreshingView.startAnimation(refreshingAnimation);
                refreshStateTextView.setText(R.string.refreshing);
                break;
            case RELEASE_TO_LOAD:
                // 释放加载状态
                loadStateTextView.setText(R.string.release_to_load);
                pullUpView.startAnimation(rotateAnimation);
                break;
            case LOADING:
                // 正在加载状态
                pullUpView.clearAnimation();
                loadingView.setVisibility(View.VISIBLE);
                pullUpView.setVisibility(View.INVISIBLE);
                loadingView.startAnimation(refreshingAnimation);
                loadStateTextView.setText(R.string.loading);
                break;
            case DONE:
                // 加载完成后设置为初始状态
                state = INIT;
                timer.schedule(50);
                mHandle.sendEmptyMessageDelayed(1000, 50);
                //
                break;
        }
    }

    /**
     * 不限制上拉或下拉
     */
    private void releasePull() {
        canPullDown = true;
        canPullUp = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        int x = (int) e.getRawX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动< 0是向上运动
                int deltaY = y - mLastMotionY;
                int deltaX = x - mLastMotionX;
                if (Math.abs(deltaX * 3) > Math.abs(deltaY)) {
                    return false;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    /*
     * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                lastY = downY;
                timer.cancel();
                mEvents = 0;
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 过滤多点触碰
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:

                if (mEvents == 0) {
                    if (((Pullable) pullableView).canPullDown() && canPullDown
                            && state != LOADING) {
                        // 可以下拉，正在加载时不能下拉
                        // 对实际滑动距离做缩小，造成用力拉的感觉
                        pullDownY = pullDownY + (ev.getY() - lastY) / radio;
                        if (pullDownY < 0) {
                            pullDownY = 0;
                            canPullDown = false;
                            canPullUp = true;
                        }
                        if (pullDownY > getMeasuredHeight())
                            pullDownY = getMeasuredHeight();
                        if (state == REFRESHING) {
                            // 正在刷新的时候触摸移动
                            isTouch = true;
                        }
                    } else if (((Pullable) pullableView).canPullUp() && canPullUp
                            && state != REFRESHING) {
                        // 可以上拉，正在刷新时不能上拉
                        pullUpY = pullUpY + (ev.getY() - lastY) / radio;
                        if (pullUpY > 0) {
                            pullUpY = 0;
                            canPullDown = true;
                            canPullUp = false;
                        }
                        if (pullUpY < -getMeasuredHeight())
                            pullUpY = -getMeasuredHeight();
                        if (state == LOADING) {
                            // 正在加载的时候触摸移动
                            isTouch = true;
                        }

                    } else
                        releasePull();

                } else {
                    mEvents = 0;
                }
                lastY = ev.getY();
                // 根据下拉距离改变比例
                radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                        * (pullDownY + Math.abs(pullUpY))));
                requestLayout();
                if (pullDownY <= refreshDist && state == RELEASE_TO_REFRESH) {
                    // 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                    changeState(INIT);
                }
                if (pullDownY >= refreshDist && state == INIT) {
                    // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                    changeState(RELEASE_TO_REFRESH);
                }
                // 下面是判断上拉加载的，同上，注意pullUpY是负值
                if (-pullUpY <= loadmoreDist && state == RELEASE_TO_LOAD) {
                    changeState(INIT);
                }
                if (-pullUpY >= loadmoreDist && state == INIT) {
                    changeState(RELEASE_TO_LOAD);
                }
                // 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
                // Math.abs(pullUpY))就可以不对当前状态作区分了
                if ((pullDownY + Math.abs(pullUpY)) > 8) {
                    // 防止下拉过程中误触发长按事件和点击事件
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
                    // 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
                    isTouch = false;
                if (state == RELEASE_TO_REFRESH) {
                    changeState(REFRESHING);
                    // 刷新操作
                    if (mListener != null)
                        mListener.onRefresh(this);
                } else if (state == RELEASE_TO_LOAD) {
                    changeState(LOADING);
                    // 加载操作
                    if (mListener != null)
                        mListener.onLoadMore(this);
                }
                hide();
            default:
                break;
        }
        // 事件分发交给父类
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void initView() {
        Log.i("yy", "initView");
        // 初始化下拉布局
        pullDownView = refreshView.findViewById(R.id.pulldown_icon);
        refreshStateTextView = (TextView) refreshView
                .findViewById(R.id.tv_state);
        refreshContentTextView = (TextView) refreshView
                .findViewById(R.id.tv_content);
        refreshingView = refreshView.findViewById(R.id.refreshing_icon);
        refreshStateImageView = refreshView.findViewById(R.id.iv_state);
        // 初始化上拉布局
        pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
        loadStateTextView = (TextView) loadmoreView
                .findViewById(R.id.tv_loadstate);
        loadContentTextView = (TextView) loadmoreView
                .findViewById(R.id.tv_content);
        loadingView = loadmoreView.findViewById(R.id.loading_icon);
        loadStateImageView = loadmoreView.findViewById(R.id.iv_loadstate);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isLayout) {
            // 这里是第一次进来的时候做一些初始化
            refreshView = getChildAt(0);
            pullableView = getChildAt(1);
            loadmoreView = getChildAt(2);
            isLayout = true;
            refreshDist = ((ViewGroup) refreshView).getChildAt(0)
                    .getMeasuredHeight();
            loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0)
                    .getMeasuredHeight();
            initView();
            initData();
        }
        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分
        refreshView.layout(0,
                (int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
                refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
        pullableView.layout(0, (int) (pullDownY + pullUpY),
                pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
                        + pullableView.getMeasuredHeight());
        loadmoreView.layout(0,
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
                loadmoreView.getMeasuredWidth(),
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
                        + loadmoreView.getMeasuredHeight());
    }

    Handler mHandle = new Handler() {
        public void handleMessage(Message msg) {
            try {
                // 下拉布局初始状态
                refreshStateImageView.setVisibility(View.GONE);
                refreshStateTextView.setText(R.string.pull_to_refresh);
                pullDownView.clearAnimation();
                pullDownView.setVisibility(View.VISIBLE);
                // 上拉布局初始状态
                loadStateImageView.setVisibility(View.GONE);
                loadStateTextView.setText(R.string.pullup_to_load);
                pullUpView.clearAnimation();
                pullUpView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ;
    };

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

    /**
     * 刷新加载回调接口
     */
    public interface OnRefreshListener {
        /**
         * 刷新操作
         */
        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

        /**
         * 加载操作
         */
        void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
    }

    @SuppressLint("NewApi")
    private void initData() {
        // TODO Auto-generated method stub
        // 初始化下拉布局
        pullDownView = refreshView.findViewById(R.id.pulldown_icon);
        refreshStateTextView = (TextView) refreshView
                .findViewById(R.id.tv_state);
        refreshContentTextView = (TextView) refreshView
                .findViewById(R.id.tv_content);
        refreshingView = refreshView.findViewById(R.id.refreshing_icon);
        refreshStateImageView = refreshView.findViewById(R.id.iv_state);
        // 初始化上拉布局
        pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
        loadStateTextView = (TextView) loadmoreView
                .findViewById(R.id.tv_loadstate);
        loadContentTextView = (TextView) loadmoreView
                .findViewById(R.id.tv_content);
        loadingView = loadmoreView.findViewById(R.id.loading_icon);
        loadStateImageView = loadmoreView.findViewById(R.id.iv_loadstate);
        if (null != PullDownContentText) {
            // 设这下拉content文字
            refreshContentTextView.setText(PullDownContentText);
        }
        if (null != PullDownStateText) {
            Log.i("yy", "null != PullDownStateText");
            // 设这下拉状态文字
            refreshStateTextView.setText(PullDownStateText);
        }
        if (null != PullUpContentText) {
            // 设这上拉content文字
            loadContentTextView.setText(PullUpContentText);
        }
        if (null != PullUpStateText) {
            // 设这上拉state文字
            loadStateTextView.setText(PullUpStateText);
        }
        if (PullDownContentTextSize > 0) {
            // 设这下拉content文字大小
            refreshContentTextView.setTextSize(PullDownContentTextSize);
        }
        if (PullDownStateTextSize > 0) {
            // 设这下拉state文字大小
            refreshStateTextView.setTextSize(PullDownStateTextSize);
        }
        if (PullUpContentTextSize > 0) {
            // 设这上拉content文字大小
            loadContentTextView.setTextSize(PullUpContentTextSize);
        }
        if (PullUpStateTextSize > 0) {
            // 设这上拉state文字大小
            loadStateTextView.setTextSize(PullUpStateTextSize);
        }
        if (PullDownContentTextColor > 0) {
            refreshContentTextView.setTextColor(PullDownContentTextColor);
        }
        if (PullDownStateTextColor > 0) {
            refreshStateTextView.setTextColor(PullDownStateTextColor);
        }
        if (PullUpContentTextColor > 0) {
            loadContentTextView.setTextColor(PullUpContentTextColor);
        }
        if (PullUpStateTextColor > 0) {
            loadStateTextView.setTextColor(PullUpStateTextColor);
        }
        if (HeadViewBackgroundColor != 0) {
            refreshView.setBackgroundColor(HeadViewBackgroundColor);
        }
        if (HeadViewBackgroundResource > 0) {
            refreshView.setBackgroundResource(HeadViewBackgroundResource);
        }
        if (LoadMoreViewBackgroundColor != 0) {
            loadmoreView.setBackgroundColor(LoadMoreViewBackgroundColor);
        }
        if (LoadMoreViewBackgroundResource > 0) {
            loadmoreView.setBackgroundResource(LoadMoreViewBackgroundResource);
        }
        if (null != HeadViewBackground) {
            refreshView.setBackground(HeadViewBackground);
        }
        if (null != HeadViewBackgroundDrawable) {
            refreshView.setBackgroundDrawable(HeadViewBackgroundDrawable);
        }
        if (null != LoadMoreViewBackground) {
            loadmoreView.setBackground(LoadMoreViewBackground);
        }
        if (null != LoadMoreViewBackgroundDrawable) {
            loadmoreView.setBackgroundDrawable(LoadMoreViewBackgroundDrawable);
        }
        if (mRefreshDist > 0) {
            refreshDist = mRefreshDist;
        }
        if (mLoadmoreDist > 0) {
            loadmoreDist = mLoadmoreDist;
        }
    }

    String PullDownContentText, PullDownStateText;
    String PullUpContentText, PullUpStateText;
    float PullDownContentTextSize, PullDownStateTextSize;
    float PullUpContentTextSize, PullUpStateTextSize;
    int PullDownContentTextColor, PullDownStateTextColor,
            HeadViewBackgroundColor, HeadViewBackgroundResource;
    int PullUpContentTextColor, PullUpStateTextColor,
            LoadMoreViewBackgroundColor, LoadMoreViewBackgroundResource;
    Drawable HeadViewBackground, HeadViewBackgroundDrawable,
            LoadMoreViewBackground, LoadMoreViewBackgroundDrawable;
    // 距离
    float mRefreshDist, mLoadmoreDist;

    public void setAnimation() {
    }

    /**
     * 设置下拉头提示文字/让更新来的更猛烈些吧
     *
     * @param txt
     */
    public void setPullDownContentText(String txt) {
        this.PullDownContentText = txt;
    }

    /**
     * 设置下拉头刷新状态文字
     *
     * @param txt
     */
    public void setPullDownStateText(String txt) {
        this.PullDownStateText = txt;
        Log.i("yy", "setPullDownStateText");
    }

    /**
     * 设置下拉头提示文字的字体大小/让更新来的更猛烈些吧
     *
     * @param size
     */
    public void setPullDownContentTextSize(float size) {
        this.PullDownContentTextSize = size;
    }

    /**
     * 设置下拉头刷新状态文字的字体大小
     *
     * @param size
     */
    public void setPullDownStateTextSize(float size) {
        this.PullDownStateTextSize = size;
    }

    /**
     * 设置下拉头提示文字的字体颜色/让更新来的更猛烈些吧
     *
     * @param color
     */
    public void setPullDownContentTextColor(int color) {
        this.PullDownContentTextColor = color;
    }

    /**
     * 设置下拉头刷新状态文字的字体颜色
     *
     * @param color
     */
    public void setPullDownStateTextColor(int color) {
        this.PullDownStateTextColor = color;
    }

    /**
     * 设置下拉布局的背景
     *
     * @param background
     */
    @SuppressLint("NewApi")
    public void setHeadViewBackground(Drawable background) {
        this.HeadViewBackground = background;
    }

    /**
     * 设置下拉布局的背景
     *
     * @param color
     */
    public void setHeadViewBackgroundColor(int color) {
        this.HeadViewBackgroundColor = color;
    }

    /**
     * 设置下拉布局的背景
     *
     * @param background
     */
    public void setHeadViewBackgroundDrawable(Drawable background) {
        this.HeadViewBackgroundDrawable = background;
    }

    /**
     * 设置下拉布局的背景
     *
     * @param resid
     */
    public void setHeadViewBackgroundResource(int resid) {
        this.HeadViewBackgroundResource = resid;
    }

    // //下拉开始

    /**
     * 设置上拉头提示文字/让更新来的更猛烈些吧
     *
     * @param txt
     */
    public void setPullUpContentText(String txt) {
        this.PullUpContentText = txt;
    }

    /**
     * 设置上拉头刷新状态文字
     *
     * @param txt
     */
    public void setPullUpStateText(String txt) {
        this.PullUpStateText = txt;
    }

    /**
     * 设置上拉头提示文字的字体大小/让更新来的更猛烈些吧
     *
     * @param size
     */
    public void setPullUpContentTextSize(float size) {
        this.PullUpContentTextSize = size;
    }

    /**
     * 设置上拉头刷新状态文字的字体大小
     *
     * @param size
     */
    public void setPullUpStateTextSize(float size) {
        this.PullUpStateTextSize = size;
    }

    /**
     * 设置上拉头提示文字的字体颜色/让更新来的更猛烈些吧
     *
     * @param color
     */
    public void setPullUpContentTextColor(int color) {
        this.PullUpContentTextColor = color;
    }

    /**
     * 设置上拉头刷新状态文字的字体颜色
     *
     * @param color
     */
    public void setPullUpStateTextColor(int color) {
        this.PullUpStateTextColor = color;
    }

    /**
     * 设置上拉布局的背景
     *
     * @param background
     */
    @SuppressLint("NewApi")
    public void setLoadMoreViewBackground(Drawable background) {
        this.LoadMoreViewBackground = background;
    }

    /**
     * 设置上拉布局的背景
     *
     * @param color
     */
    public void setLoadMoreViewBackgroundColor(int color) {
        this.LoadMoreViewBackgroundColor = color;
    }

    /**
     * 设置上拉布局的背景
     *
     * @param background
     */
    public void setLoadMoreViewBackgroundDrawable(Drawable background) {
        this.LoadMoreViewBackgroundDrawable = background;
    }

    /**
     * 设置上拉布局的背景
     *
     * @param resid
     */
    public void setLoadMoreViewBackgroundResource(int resid) {
        this.LoadMoreViewBackgroundResource = resid;
    }

    /**
     * 设置下拉刷新的距离
     *
     * @param refreshDist
     */
    private void setPullUpDist(float refreshDist) {
        this.mRefreshDist = refreshDist;
    }

    /**
     * 设置上拉刷新的距离
     *
     * @param loadmoreDist
     */
    private void setPullDownDist(float loadmoreDist) {
        this.mLoadmoreDist = loadmoreDist;
    }
}
