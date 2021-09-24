package com.example.keepwalking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.jetbrains.annotations.NotNull;

public final class CustomCollapsingToolbarLayout extends CollapsingToolbarLayout {

    private CustomCollapsingToolbarLayout.Listener mListener;
    private View appBarLayoutSub;

    private void setAppBarLayoutSub(int height) {
        appBarLayoutSub.getLayoutParams().width = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        appBarLayoutSub.getLayoutParams().height = height;
        appBarLayoutSub.requestLayout();
    }

    public void setScrimsShown(boolean shown, boolean animate) {
        super.setScrimsShown(shown, animate);
        if (animate && this.mListener != null) {
            CustomCollapsingToolbarLayout.Listener var10000 = this.mListener;
            var10000.onContentScrimAnimationStarted(shown);
        }
    }

    public final void setListener(@NotNull CustomCollapsingToolbarLayout.Listener listener) {
        this.mListener = listener;
    }

    public CustomCollapsingToolbarLayout(@NotNull Context context) {
        super(context);
    }

    public CustomCollapsingToolbarLayout(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCollapsingToolbarLayout(@NotNull Context context, @NotNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface Listener {
        void onContentScrimAnimationStarted(boolean var1);
    }
}