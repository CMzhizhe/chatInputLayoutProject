package com.example.myapplication.weiget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;


/**
 * @date 创建时间:2021/10/13 0013
 * @auther gaoxiaoxiong
 * @Descriptiion 软键盘高度提供
 **/
public class KeyBordHeightProvider extends PopupWindow implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "KeyBordHeightProvider";
    private Activity mActivity;
    private View rootView;
    private KeyBordHeightListener listener;
    private int heightMax; // 记录popup内容区的最大高度
    public KeyBordHeightProvider(Activity activity) {
        super(activity);
        this.mActivity = activity;

        // 基础配置
        rootView = new View(activity);
        setContentView(rootView);

        // 监听全局Layout变化
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        setBackgroundDrawable(new ColorDrawable(0));
        //setBackgroundDrawable(ResourcesUtils.getResourceDrawable(rootView.getContext(), R.drawable.dra_gray_6600000));

        // 设置宽度为0，高度为全屏
        setWidth(0);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        // 设置键盘弹出方式
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

    }

    /**
     * @date 创建时间:2021/10/13 0013
     * @auther gaoxiaoxiong
     * @Descriptiion 显示popuwindow
     **/
    public void init(){
        if (!isShowing()) {
            final View view = mActivity.getWindow().getDecorView();
            // 延迟加载popupwindow，如果不加延迟就会报错
            view.post(new Runnable() {
                @Override
                public void run() {
                    showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
                }
            });
        }
    }

    public KeyBordHeightProvider setHeightListener(KeyBordHeightListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        if (rect.bottom > heightMax) {
            heightMax = rect.bottom;
        }

        // 两者的差值就是键盘的高度
        int keyboardHeight = heightMax - rect.bottom;
        if (keyboardHeight > getPhoneHeight(mActivity) / 2){
            return;
        }

        if (listener != null) {
            listener.onkeyBordHeightChanged(keyboardHeight);
        }
    }

    public interface KeyBordHeightListener {
        void onkeyBordHeightChanged(int height);
    }

    /**
     * @date: 2019/3/14 0014
     * @author: gaoxiaoxiong
     * @description:获取屏幕的高度，包含状态栏的高度，此高度是不包含导航键的高度的
     **/
    public int getPhoneHeight(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return  outMetrics.heightPixels;
    }

}

