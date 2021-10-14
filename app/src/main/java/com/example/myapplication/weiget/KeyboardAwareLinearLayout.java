/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

/**
 * Copyright (C) 2014 Open Whisper Systems
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.example.myapplication.weiget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.myapplication.R;
import com.example.myapplication.utils.ActivityManager;
import com.example.myapplication.utils.KeyBordUtils;


import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;


/**
 * LinearLayout that, when a view container, will report back when it thinks a soft keyboard
 * has been opened and what its height would be.
 */
public class KeyboardAwareLinearLayout extends LinearLayoutCompat implements KeyBordHeightProvider.KeyBordHeightListener {
    private static final String TAG = KeyboardAwareLinearLayout.class.getSimpleName();
    protected KeyBordUtils keyBordUtils = new KeyBordUtils();
    private final Rect rect = new Rect();
    private final Set<OnKeyboardHiddenListener> hiddenListeners = new HashSet<>();
    private final Set<OnKeyboardShownListener> shownListeners = new HashSet<>();
    private final int minKeyboardSize;
    private final int minCustomKeyboardSize;
    private final int defaultCustomKeyboardSize;
    private final int minCustomKeyboardTopMargin;
    private final int statusBarHeight;
    private boolean isBackGroundFixed = false;
    private int viewInset;
    private boolean keyboardOpen = false;
    private int rotation = -1;
    private View bottomEmptyView = null;
    private KeyBordHeightProvider keyBordHeightProvider = null;
    private boolean isFullscreen = false;//是否全屏模式

    public KeyboardAwareLinearLayout(Context context) {
        this(context, null);
    }

    public KeyboardAwareLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardAwareLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardAwareLinearLayout);
        isBackGroundFixed = typedArray.getBoolean(R.styleable.KeyboardAwareLinearLayout_keybord_aware_backfixed, false);
        final int statusBarRes = getResources().getIdentifier("status_bar_height", "dimen", "android");
        minKeyboardSize = getResources().getDimensionPixelSize(R.dimen.min_keyboard_size);
        minCustomKeyboardSize = getResources().getDimensionPixelSize(R.dimen.min_custom_keyboard_size);
        defaultCustomKeyboardSize = getResources().getDimensionPixelSize(R.dimen.default_custom_keyboard_size);
        minCustomKeyboardTopMargin = getResources().getDimensionPixelSize(R.dimen.min_custom_keyboard_top_margin);
        statusBarHeight = statusBarRes > 0 ? getResources().getDimensionPixelSize(statusBarRes) : 0;
        viewInset = getViewInset();
        if (isBackGroundFixed) {//需要将 android:windowSoftInputMode="adjustNothing"
            WeakReference<Activity> activityWeakReference = ActivityManager.getInstance().currentActivity();
            if (activityWeakReference != null && activityWeakReference.get() != null) {
                keyBordHeightProvider = new KeyBordHeightProvider(activityWeakReference.get());
                keyBordHeightProvider.setHeightListener(this);
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        keyBordHeightProvider.init();
                    }
                });
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updateRotation();
        updateKeyboardState();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateRotation() {
        int oldRotation = rotation;
        rotation = getDeviceRotation();
        if (oldRotation != rotation) {
            Log.i(TAG, "rotation changed");
            onKeyboardClose();
        }
    }

    private void updateKeyboardState() {
        if (isLandscape()) {
            if (keyboardOpen) onKeyboardClose();
            return;
        }
        if (isBackGroundFixed && bottomEmptyView != null && bottomEmptyView.getTag() != null) {
            int keyboardHeight = (int) bottomEmptyView.getTag();
            if (keyboardHeight > minKeyboardSize) {
                if (getKeyboardHeight() != keyboardHeight) {
                    setKeyboardPortraitHeight(keyboardHeight);
                }
                if (bottomEmptyView.getVisibility() == VISIBLE) {
                    onKeyboardOpen(keyboardHeight);
                } else {
                    onKeyboardClose();
                }
            } else if (keyboardOpen) {
                onKeyboardClose();
            }
        } else {
            if (viewInset == 0 && Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
                viewInset = getViewInset();

            getWindowVisibleDisplayFrame(rect);

            final int availableHeight = getAvailableHeight();
            final int keyboardHeight = availableHeight - (rect.bottom - rect.top);

            if (keyboardHeight > minKeyboardSize) {
                if (getKeyboardHeight() != keyboardHeight) {
                    if (isLandscape()) {
                        setKeyboardLandscapeHeight(keyboardHeight);
                    } else {
                        setKeyboardPortraitHeight(keyboardHeight);
                    }
                }
                if (!keyboardOpen) {
                    onKeyboardOpen(keyboardHeight);
                }
            } else if (keyboardOpen) {
                onKeyboardClose();
            }
        }
    }

    private int getAvailableHeight() {
        final int availableHeight = this.getRootView().getHeight() - viewInset - (!isFullscreen ? statusBarHeight : 0);
        final int availableWidth = this.getRootView().getWidth() - (!isFullscreen ? statusBarHeight : 0);

        if (isLandscape() && availableHeight > availableWidth) {
            //noinspection SuspiciousNameCombination
            return availableWidth;
        }

        return availableHeight;
    }

    //底部虚拟按键高度
    @TargetApi(VERSION_CODES.LOLLIPOP)
    private int getViewInset() {
        // In Android 10 we can't use mAttatchInfo anymore, instead use the SDK interface:
        if (Build.VERSION.SDK_INT >= VERSION_CODES.Q) {
            WindowInsets insets = getRootWindowInsets();
            if (insets == null) return 0;
            return insets.getStableInsetBottom();
        } else {
            // Some older Android versions don't know `getRootViewInsets()` yet, so we still need the old way:
            try {
                Field attachInfoField = View.class.getDeclaredField("mAttachInfo");
                attachInfoField.setAccessible(true);
                Object attachInfo = attachInfoField.get(this);
                if (attachInfo != null) {
                    Field stableInsetsField = attachInfo.getClass().getDeclaredField("mStableInsets");
                    stableInsetsField.setAccessible(true);
                    Rect insets = (Rect) stableInsetsField.get(attachInfo);
                    return insets.bottom;
                }
            } catch (NoSuchFieldException nsfe) {

            } catch (IllegalAccessException iae) {

            }
        }
        return 0;
    }

    protected void onKeyboardOpen(int keyboardHeight) {
        Log.i(TAG, "onKeyboardOpen(" + keyboardHeight + ")");
        keyboardOpen = true;

        notifyShownListeners();
    }

    protected void onKeyboardClose() {
        Log.i(TAG, "onKeyboardClose()");
        keyboardOpen = false;
        notifyHiddenListeners();
    }

    public boolean isKeyboardOpen() {
        return keyboardOpen;
    }

    public int getKeyboardHeight() {
        return isLandscape() ? getKeyboardLandscapeHeight() : getKeyboardPortraitHeight();
    }

    public boolean isLandscape() {
        int rotation = getDeviceRotation();
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270;
    }

    private int getDeviceRotation() {
        return ((WindowManager) getContext().getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    }

    private int getKeyboardLandscapeHeight() {
        int keyboardHeight = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getInt("keyboard_height_landscape", defaultCustomKeyboardSize);
        return Math.min(Math.max(keyboardHeight, minCustomKeyboardSize), getRootView().getHeight() - minCustomKeyboardTopMargin);
    }

    private int getKeyboardPortraitHeight() {
        int keyboardHeight = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getInt("keyboard_height_portrait", defaultCustomKeyboardSize);
        return Math.min(Math.max(keyboardHeight, minCustomKeyboardSize), getRootView().getHeight() - minCustomKeyboardTopMargin);
    }

    private void setKeyboardPortraitHeight(int height) {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit().putInt("keyboard_height_portrait", height).apply();
    }

    private void setKeyboardLandscapeHeight(int height) {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit().putInt("keyboard_height_landscape", height).apply();
    }

    public void postOnKeyboardClose(final Runnable runnable) {
        if (keyboardOpen) {
            addOnKeyboardHiddenListener(new OnKeyboardHiddenListener() {
                @Override
                public void onKeyboardHidden() {
                    removeOnKeyboardHiddenListener(this);
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    public void postOnKeyboardOpen(final Runnable runnable) {
        if (!keyboardOpen) {
            addOnKeyboardShownListener(new OnKeyboardShownListener() {
                @Override
                public void onKeyboardShown() {
                    removeOnKeyboardShownListener(this);
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    public void addOnKeyboardHiddenListener(OnKeyboardHiddenListener listener) {
        hiddenListeners.add(listener);
    }

    public void removeOnKeyboardHiddenListener(OnKeyboardHiddenListener listener) {
        hiddenListeners.remove(listener);
    }

    public void addOnKeyboardShownListener(OnKeyboardShownListener listener) {
        shownListeners.add(listener);
    }

    public void removeOnKeyboardShownListener(OnKeyboardShownListener listener) {
        shownListeners.remove(listener);
    }

    private void notifyHiddenListeners() {
        for (OnKeyboardHiddenListener listener : hiddenListeners) {
            listener.onKeyboardHidden();
        }
    }

    private void notifyShownListeners() {
        for (OnKeyboardShownListener listener : shownListeners) {
            listener.onKeyboardShown();
        }
    }

    @Override
    public void onkeyBordHeightChanged(int height) {
        if (height > 0 && height > minKeyboardSize) {
            if (bottomEmptyView == null || bottomEmptyView.getTag() == null || (int) bottomEmptyView.getTag() <= 0) {
                bottomEmptyView = new View(this.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.height = height;
                bottomEmptyView.setLayoutParams(layoutParams);
                bottomEmptyView.setTag(height);
                if (bottomEmptyView.getParent() == null) {
                    this.addView(bottomEmptyView);
                }
            } else if (bottomEmptyView != null) {
                bottomEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if (bottomEmptyView != null && bottomEmptyView.getParent() != null) {
                bottomEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (keyBordHeightProvider != null) {
            keyBordHeightProvider.dismiss();
        }
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
    }

    public interface OnKeyboardHiddenListener {
        void onKeyboardHidden();
    }

    public interface OnKeyboardShownListener {
        void onKeyboardShown();
    }
}
