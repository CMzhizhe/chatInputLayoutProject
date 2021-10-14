package com.example.myapplication.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * @date 创建时间: 2021/10/7
 * @auther gaoxiaoxiong
 * @description Activity 管理工具
 **/
public class ActivityManager {
    private Stack<Activity> activityStack;
    private static ActivityManager instance;

    public static ActivityManager getInstance() {
        if (instance == null) {
            synchronized (ActivityManager.class) {
                instance = new ActivityManager();
            }
        }
        return instance;
    }

    private ActivityManager() {
    }

    /**
     * @date 创建时间 2019/7/31
     * @author gaoxiaoxiong
     * @desc 添加进Stack
     **/
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }
        activityStack.add(activity);
    }

    /**
     * @date: 创建时间:2019/9/19
     * @author: gaoxiaoxiong
     * @descripion:获取当前的Activity
     **/
    public WeakReference<Activity> currentActivity() {
        WeakReference<Activity> activityWeakReference = null;
        if (activityStack != null) {
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                if (!activityStack.get(i).isFinishing()){
                    activityWeakReference = new WeakReference<Activity>(activityStack.get(i));
                    break;
                }
            }
            return activityWeakReference;
        }
        return null;
    }

    public void removeActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            activityStack.remove(activity);
        }
    }
}
