package com.indris.material;

/*
 * Copyright (C) 2013 Muthuramakrishnan <siriscac@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.*;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.indris.R;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

@SuppressWarnings("deprecation")
@SuppressLint("ClickableViewAccessibility")

public class RippleView extends Button {

    private float mDownX;
    private float mDownY;
    private float mAlphaFactor;

    private float mRadius;
    private float mMaxRadius;

    private int mRippleColor;
    private boolean mIsAnimating = false;

    private RadialGradient mRadialGradient;
    private Paint mPaint;
    private ObjectAnimator mRadiusAnimator;

    private float mDensity;

    private int dp(int dp) {
        return (int) (dp * mDensity + 0.5f);
    }

    public RippleView(Context context) {
        super(context);
        init();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RippleView);
        mRippleColor = a.getColor(R.styleable.RippleView_rippleColor,
                mRippleColor);
        mAlphaFactor = a.getFloat(R.styleable.RippleView_alphaFactor,
                mAlphaFactor);
        a.recycle();
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RippleView);
        mRippleColor = a.getColor(R.styleable.RippleView_rippleColor,
                mRippleColor);
        mAlphaFactor = a.getFloat(R.styleable.RippleView_alphaFactor,
                mAlphaFactor);
        a.recycle();
    }

    public void init() {
        mDensity = getContext().getResources().getDisplayMetrics().density;

        mPaint = new Paint();
        mPaint.setAlpha(100);
        setRippleColor(Color.BLACK, 0.2f);
        ShapeDrawable normal = new ShapeDrawable(new RectShape());
        normal.getPaint().setColor(Color.parseColor("#00FFFFFF"));
        StateListDrawable states = new StateListDrawable();

        states.addState(new int[] { android.R.attr.state_pressed,
                android.R.attr.state_enabled }, normal);
        states.addState(new int[] { android.R.attr.state_focused,
                android.R.attr.state_enabled }, normal);
        states.addState(new int[] { android.R.attr.state_enabled }, normal);
        states.addState(new int[] { -android.R.attr.state_enabled }, normal);
        setBackgroundDrawable(states);
    }

    public void setRippleColor(int rippleColor, float alphaFactor) {
        this.mRippleColor = rippleColor;
        this.mAlphaFactor = alphaFactor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxRadius = (float) Math.sqrt(w * w + h * h);
    }

    private RectF mRect;
    private boolean mAnimationIsCancel;

    @Override
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mDownX = event.getX();
            mDownY = event.getY();

            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", 0, dp(50)).setDuration(500);
            mRadiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mRadiusAnimator.start();

        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            mRect = new RectF(getLeft(), getTop(), getRight(), getBottom());
            mDownX = event.getX();
            mDownY = event.getY();
            
            // Cancel the ripple animation when moved outside 
            if (mAnimationIsCancel = !mRect.contains(mDownX, mDownY)) { 
                setRadius(0);
            } else {
                setRadius(dp(50));
            }
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP && !mIsAnimating && !mAnimationIsCancel) {
            mDownX = event.getX();
            mDownY = event.getY();

            final float tempRadius = (float) Math.sqrt(mDownX * mDownX + mDownY * mDownY);
            float targetRadius = Math.max(tempRadius, mMaxRadius);

            mRadiusAnimator = ObjectAnimator.ofFloat(this, "radius", dp(50),
                    targetRadius);
            mRadiusAnimator.setDuration(500);
            mRadiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mRadiusAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mIsAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setRadius(0);
                    setAlpha(1);
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            mRadiusAnimator.start();

        }
        return super.onTouchEvent(event);
    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public void setRadius(final float radius) {
        mRadius = radius;
        if (mRadius > 0) {
            mRadialGradient = new RadialGradient(mDownX, mDownY, mRadius,
                    adjustAlpha(mRippleColor, mAlphaFactor), mRippleColor,
                    Shader.TileMode.MIRROR);
            mPaint.setShader(mRadialGradient);
        }
        invalidate();
    }

    private Path mPath = new Path();

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            return;
        }

        canvas.save(Canvas.CLIP_SAVE_FLAG);

        mPath.reset();
        mPath.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW);

        canvas.clipPath(mPath);
        canvas.restore();
        
        canvas.drawCircle(mDownX, mDownY, mRadius, mPaint);
    }

}
