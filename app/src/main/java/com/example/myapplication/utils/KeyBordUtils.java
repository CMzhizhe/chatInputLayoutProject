package com.example.myapplication.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author : gaoxiaoxiong
 * @date :2019/8/26 0026
 * @description:软件盘工具类
 **/
public class KeyBordUtils {

    private static KeyBordUtils keyBordUtils;

    public static KeyBordUtils getInstance(){
        if (keyBordUtils == null){
            synchronized (KeyBordUtils.class){
                if (keyBordUtils == null){
                    keyBordUtils = new KeyBordUtils();
                }
            }
        }
        return keyBordUtils;
    }

    /**
     * @date 创建时间:2021/6/13
     * @auther gaoxiaoxiong
     * @description 点击空白地方，隐藏输入框
     */
    public void clickEmptyHideEditTextKeyboard(MotionEvent event, View view) {
        if (view != null && view instanceof EditText) {
            int[] location = {0, 0};
            view.getLocationInWindow(location);
            int left = location[0], top = location[1], right = left
                    + view.getWidth(), bootom = top + view.getHeight();
            if (event.getRawX() < left || event.getRawX() > right
                    || event.getY() < top || event.getRawY() > bootom) {
                hideSoftKeyboard(view);
            }
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public boolean hideSoftKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * @date 创建时间:2021/2/15
     * @auther gaoxiaoxiong
     * @description 获取软键盘的service
     */
    public  InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


}
