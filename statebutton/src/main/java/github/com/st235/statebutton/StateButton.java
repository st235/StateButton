// Copyright (с) 2017 by Alexander Dadukin (st235@yandex.ru)
// All rights reserved.

package github.com.st235.statebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import github.com.st235.statebutton.events.EndAnimationListener;
import github.com.st235.statebutton.events.OnStateChangedListener;

public class StateButton extends FrameLayout implements View.OnClickListener {

    private final int DEFAULT_STATE = 0;

    private int statesAmount;
    private int currentState = DEFAULT_STATE;

    private AppCompatImageView[] imageViews;

    private int marginTop;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;

    private boolean hasDisabledState = false;

    private long duration = 400L;

    private OnStateChangedListener listener;

    public StateButton(Context context) {
        this(context, null);
    }

    public StateButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClickable(true);
        setOnClickListener(this);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateButton);

            marginTop = typedArray.getDimensionPixelSize(R.styleable.StateButton_sb_marginTop, 0);
            marginBottom = typedArray.getDimensionPixelSize(R.styleable.StateButton_sb_marginBottom, 0);
            marginLeft = typedArray.getDimensionPixelSize(R.styleable.StateButton_sb_marginLeft, 0);
            marginRight = typedArray.getDimensionPixelSize(R.styleable.StateButton_sb_marginRight, 0);
            hasDisabledState = typedArray.getBoolean(R.styleable.StateButton_sb_hasDisabledState, false);

            typedArray.recycle();
        }
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int previousState = currentState;
        currentState = (currentState + 1) % statesAmount;

        if (hasDisabledState) {
            if (previousState == 0) {
                changeWithoutAnimation(currentState, previousState);
            } else {
                animateChange(currentState, previousState);
            }

            if (listener != null) {
                listener.onStateChanged(currentState != 0, currentState);
            }

            return;
        }

        animateChange(currentState, previousState);
        if (listener != null) {
            listener.onStateChanged(true, currentState);
        }
    }

    public void addStatesDrawable(@DrawableRes int defaultState,
                                  @DrawableRes int... anotherStates) {
        statesAmount = anotherStates.length + 1 + (hasDisabledState ? 1 : 0);
        imageViews = new AppCompatImageView[statesAmount];

        imageViews[DEFAULT_STATE] = addImageView(defaultState, VISIBLE);

        if (hasDisabledState) {
            imageViews[DEFAULT_STATE + 1] = addImageView(defaultState, GONE);
        }

        for (int i = 0; i < anotherStates.length; i++) {
            imageViews[i + 1 + (hasDisabledState ? 1 : 0)] = addImageView(anotherStates[i], GONE);
        }

        invalidate();
    }

    public void setCurrentState(int state) {
        imageViews[currentState].setVisibility(GONE);
        currentState = state;
        imageViews[currentState].setVisibility(VISIBLE);
    }

    private void changeWithoutAnimation(final int currentPosition, int previousPosition) {
        final AppCompatImageView topView = imageViews[previousPosition];
        final AppCompatImageView bottomView = imageViews[currentPosition];

        topView.setVisibility(GONE);
        bottomView.setVisibility(VISIBLE);
    }

    private void animateChange(final int currentPosition, int previousPosition) {
        final AppCompatImageView topView = imageViews[previousPosition];
        final AppCompatImageView bottomView = imageViews[currentPosition];

        final Animation centerToTop = new TranslateAnimation(0, 0, 0, getHeight());
        centerToTop.setDuration(duration);

        Animation bottomToCenter = new TranslateAnimation(0, 0, -getHeight(), 0);
        bottomToCenter.setDuration(duration);

        centerToTop.setAnimationListener(new EndAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                topView.setVisibility(GONE);
            }
        });

        bottomToCenter.setAnimationListener(new EndAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                bottomView.setVisibility(VISIBLE);
            }
        });

        topView.startAnimation(centerToTop);
        bottomView.startAnimation(bottomToCenter);
    }

    private AppCompatImageView addImageView(@DrawableRes int res, int visibility) {
        AppCompatImageView imageView = new AppCompatImageView(getContext());

        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        );

        params.setMargins(marginLeft, marginTop, marginRight, marginBottom);

        imageView.setImageResource(res);
        imageView.setVisibility(visibility);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        addView(imageView, params);
        return imageView;
    }
}
