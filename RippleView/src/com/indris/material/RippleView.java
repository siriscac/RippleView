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

import com.indris.R;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
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

    private int mRippleColor;
    private boolean isAnimating = false;


    private RadialGradient mRadialGradient;
    private Paint mPaint;


    public RippleView(Context context) {
        super(context);
        init();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        mRippleColor = a.getColor(R.styleable.RippleView_rippleColor,mRippleColor);
        mAlphaFactor = a.getFloat(R.styleable.RippleView_alphaFactor, mAlphaFactor);
        a.recycle();
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        mRippleColor = a.getColor(R.styleable.RippleView_rippleColor,mRippleColor);
        mAlphaFactor = a.getFloat(R.styleable.RippleView_alphaFactor, mAlphaFactor);
        a.recycle();
    }

	public void init() {
        mPaint = new Paint();
        mPaint.setAlpha(100);
        setRippleColor(Color.BLACK, 0.2f);
        ShapeDrawable normal = new ShapeDrawable(new RectShape());
        normal.getPaint().setColor(Color.parseColor("#00FFFFFF"));
        StateListDrawable states = new StateListDrawable();

        states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, normal);
        states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, normal);
        states.addState(new int[]{android.R.attr.state_enabled}, normal);
        states.addState(new int[]{-android.R.attr.state_enabled}, normal);
        setBackgroundDrawable(states);
    }

    public void setRippleColor(int rippleColor, float alphaFactor) {
        this.mRippleColor = rippleColor;
        this.mAlphaFactor = alphaFactor;
    }

    @Override
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && !isAnimating) {
            mDownX = event.getX();
            mDownY = event.getY();

            ObjectAnimator radAnim = ObjectAnimator.ofFloat(this, "radius", 0, getWidth() * 3.0f);
            radAnim.setDuration(500);
            radAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setRadius(0);
                    setAlpha(1);
                    isAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            radAnim.start();

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
            mRadialGradient = new RadialGradient(
                    mDownX,
                    mDownY,
                    mRadius * 3,
                    adjustAlpha(mRippleColor, mAlphaFactor),
                    mRippleColor,
                    Shader.TileMode.MIRROR
            );
            mPaint.setShader(mRadialGradient);
        }
        invalidate();
    }

    private Path mPath = new Path();
    private Path mPath2 = new Path();

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);

		if (isInEditMode()) {
			return;
		}
		
        mPath2.reset();
        mPath2.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW);

        canvas.clipPath(mPath2);

        mPath.reset();
        mPath.addCircle(mDownX, mDownY, mRadius / 3, Path.Direction.CW);

        canvas.clipPath(mPath, Region.Op.INTERSECT);
        canvas.drawCircle(mDownX, mDownY, mRadius, mPaint);
    }

}
