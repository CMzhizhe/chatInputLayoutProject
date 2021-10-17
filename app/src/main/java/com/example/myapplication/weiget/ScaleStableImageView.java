package com.example.myapplication.weiget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * An image view that maintains the size of the drawable provided.
 * That means: when you set an image drawable it will be scaled to fit the screen once.
 * If you rotate the screen it will be rescaled to fit.
 * If you crop the screen (e.g. because the soft keyboard is displayed) the image is cropped instead.
 *
 * @author Angelo Fuchs
 */
public class ScaleStableImageView
        extends AppCompatImageView
        implements KeyboardAwareLinearLayout.OnKeyboardShownListener, KeyboardAwareLinearLayout.OnKeyboardHiddenListener {

    private static final String TAG = "ScaleStableImageView";

    private Drawable defaultDrawable;
    private Drawable currentDrawable;
    private Map<String, Drawable> storedSizes = new HashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    private int landscapeWidth = 0;
    private int landscapeHeight = 0;
    private int portraitWidth = 0;
    private int portraitHeight = 0;
    private boolean keyboardShown = false;

    public ScaleStableImageView(Context context) {
        this(context, null);
    }

    public ScaleStableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleStableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        defaultDrawable = drawable;
        if (storedSizes!=null){
            storedSizes.clear();
        }
        overrideDrawable(defaultDrawable);
    }

    private void overrideDrawable(Drawable newDrawable) {
        if (currentDrawable == newDrawable) return;
        currentDrawable = newDrawable;
        super.setImageDrawable(newDrawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(final int width, final int height, int oldWidth, int oldHeight) {
        if (width == 0 || height == 0) return;

        if (width > oldWidth && height > oldHeight && oldWidth==0 && oldHeight ==0){
            keyboardShown = false;
        }else {
            keyboardShown = true;
        }

        final String newKey = width + "x" + height;
        int orientation = getResources().getConfiguration().orientation;
        boolean portrait;
        if (orientation == ORIENTATION_PORTRAIT) {
            portrait = true;
        } else if (orientation == ORIENTATION_LANDSCAPE) {
            portrait = false;
        } else {
            Log.i(TAG, "orientation was: " + orientation);
            return; // something fishy happened.
        }

        if (!(defaultDrawable instanceof BitmapDrawable)) {
            return; // need Bitmap for scaling and cropping.
        }

        measureViewSize(width, height, oldWidth, oldHeight, portrait);
        // if the image is already fit for the screen, just show it.
        if (defaultDrawable.getIntrinsicWidth() == width &&
                defaultDrawable.getIntrinsicHeight() == height) {
            overrideDrawable(defaultDrawable);
        }

        // check if we have the new one already
        if (storedSizes.containsKey(newKey)) {
            super.setImageDrawable(storedSizes.get(newKey));
            return;
        }

        if (keyboardShown) {
            // don't scale; Crop.
            Drawable large;
            if (portrait)
                large = storedSizes.get(portraitWidth + "x" + portraitHeight);
            else
                large = storedSizes.get(landscapeWidth + "x" + landscapeHeight);
            if (large == null) return; // no baseline. can't work.
            Bitmap original = ((BitmapDrawable) large).getBitmap();
            if (height <= original.getHeight() && width <= original.getWidth()) {
                Bitmap cropped = Bitmap.createBitmap(original, 0, 0, width, height);
                Drawable croppedDrawable = new BitmapDrawable(getResources(), cropped);
                overrideDrawable(croppedDrawable);
            }
        } else {
            runOnBackground(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = ((BitmapDrawable) defaultDrawable).getBitmap();
                    Context context = getContext();
                    try {
                        Bitmap scaledBitmap = Glide.with(context)
                                .asBitmap()
                                .load(bitmap)
                                .centerCrop()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .submit(width, height)
                                .get();
                        final Drawable rescaled = new BitmapDrawable(getResources(), scaledBitmap);
                        storedSizes.put(newKey, rescaled);
                        runOnMain(new Runnable() {
                            @Override
                            public void run() {
                                overrideDrawable(rescaled);
                            }
                        });
                    } catch (ExecutionException | InterruptedException ex) {
                        Log.e(TAG, "could not rescale background", ex);
                    }
                }
            });

        }
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    public void runOnBackground(final @NonNull Runnable runnable) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public void runOnMain(final @NonNull Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) runnable.run();
        else handler.post(runnable);
    }

    private void measureViewSize(int width, int height, int oldWidth, int oldHeight, boolean portrait) {
        if (portraitWidth != 0 && portraitHeight != 0 && landscapeWidth != 0 && landscapeHeight != 0)
            return;

        if (oldWidth == 0 && oldHeight == 0) { // screen just opened from inside the app
            if (portrait) { // portrait
                portraitHeight = height;
                portraitWidth = width;
            } else { // landscape
                landscapeHeight = height;
                landscapeWidth = width;
            }
        } else {
            if (oldWidth == portraitWidth) { // was in portrait
                if (!portrait) { // rotate to landscape
                    landscapeHeight = height;
                    landscapeWidth = width;
                }
            } else if (oldHeight == landscapeHeight) {
                if (portrait) {
                    portraitHeight = height;
                    portraitWidth = width;
                }
            }
        }
    }

    @Override
    public void onKeyboardHidden() {
        keyboardShown = false;
    }

    @Override
    public void onKeyboardShown() {
        keyboardShown = true;
    }


    /**
     * @date 创建时间:2021/10/14 0014
     * @auther gaoxiaoxiong
     * @Descriptiion 计算宽高
     **/
    private void calculationWidthHeight(){

    }

}
