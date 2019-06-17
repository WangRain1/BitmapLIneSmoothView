/*
 * Copyright (c) 2019. Parrot Faurecia Automotive S.A.S. All rights reserved.
 */

package com.parrot.car.bitmaplinesmoothview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class SmoothLineView extends View {

    boolean isFromLeft;
    Drawable imgDown;
    Drawable imgTop;
    Bitmap imgDownBitmap;
    Bitmap imgTopBitmap;
    Paint mPaint;
    int width;
    int height;
    float xDiretion = 0;
    boolean isShow = true;

    PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    public SmoothLineView(Context context) {
        this(context, null);
    }

    public SmoothLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(
                attrs, R.styleable.SmoothLine, defStyleAttr, 0);

        isFromLeft = array.getBoolean(R.styleable.SmoothLine_onStartLeft, true);
        imgDown = array.getDrawable(R.styleable.SmoothLine_imgDown);
        imgTop = array.getDrawable(R.styleable.SmoothLine_imgTop);
        array.recycle();
        init();
    }

    private void init() {
//        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                startAnimator();
//            }
//        });
        imgDownBitmap = drawableToBitmap(imgDown);
        imgTopBitmap = drawableToBitmap(imgTop);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = imgTopBitmap.getWidth();
        height = imgTopBitmap.getHeight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(isShow ? imgDownBitmap : imgTopBitmap, 0, 0, mPaint);

        int layer = canvas.saveLayer(0, 0, width, height, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(isShow ? imgTopBitmap : imgDownBitmap, 0, 0, mPaint);
        mPaint.setXfermode(mXfermode);
        mPaint.setColor(Color.RED);
        RectF rectF = new RectF(isFromLeft ? xDiretion : width - xDiretion, 0, width, height);
        canvas.drawRect(rectF, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layer);
    }

    public void startAnimator(boolean isShow) {
        this.isShow = isShow;
        ValueAnimator animator = ValueAnimator.ofFloat(0, width);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xDiretion = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(1500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
