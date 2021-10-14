package com.example.myapplication.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

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
     * @date 创建时间:2021/2/15
     * @auther gaoxiaoxiong
     * @description 获取软键盘的service
     */
    public  InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


}
