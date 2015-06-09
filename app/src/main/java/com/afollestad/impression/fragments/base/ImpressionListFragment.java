package com.afollestad.impression.fragments.base;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.afollestad.impression.R;
import com.afollestad.impression.ui.MainActivity;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class ImpressionListFragment extends Fragment {

    // impression won't show the progress indicator until after this delay goes off
    // this is a silly fix, but a good one for people that don't want it to flash on
    // at the beginning when it is barely there for any time at all
    private static final long PROGRESS_INDICATOR_DELAY = 1000;

    private static final long ALPHA_ANIMATION_TIME = 300;
    private static final TimeInterpolator ALPHA_ANIMATION_INTERPOLATOR =
            new AccelerateDecelerateInterpolator();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private Handler progressHandler;

    protected ImpressionListFragment() {
    }

    RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    final void setListShown(boolean shown) {
        if (progressHandler != null) progressHandler.removeCallbacksAndMessages(null);

        final View v = getView();
        if (v == null || getActivity() == null) return;

        int listVisibility = v.findViewById(R.id.list).getVisibility();
        int progressVisibility = v.findViewById(R.id.empty).getVisibility();

        if (shown) {
            v.findViewById(R.id.list).setVisibility(mAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            v.findViewById(R.id.empty).setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            v.findViewById(R.id.progress).setVisibility(View.GONE);

            mAdapter.notifyDataSetChanged();
        } else {
            v.findViewById(R.id.list).setVisibility(View.GONE);
            v.findViewById(R.id.empty).setVisibility(View.GONE);
            v.findViewById(R.id.progress).setVisibility(View.GONE);
            progressHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    animateAlphaChange(v.findViewById(R.id.progress), 0.0f, 1.0f);
                }
            }, PROGRESS_INDICATOR_DELAY);
        }

        checkShouldAnimate(v.findViewById(R.id.list), listVisibility);
    }

    private void checkShouldAnimate(View view, int previousVisibility) {
        if (previousVisibility != view.getVisibility()) {
            if (view.getVisibility() == View.VISIBLE) {
                animateAlphaChange(view, 0.0f, 1.0f);
            } else {
                animateAlphaChange(view, 1.0f, 0.0f);
            }
        }
    }

    private void animateAlphaChange(final View view, float startAlpha, float stopAlpha) {
        ValueAnimator alpha = ValueAnimator.ofFloat(startAlpha, stopAlpha);
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (Float) valueAnimator.getAnimatedValue();
                view.setAlpha(val);
            }
        });
        alpha.setDuration(ALPHA_ANIMATION_TIME);
        alpha.setInterpolator(ALPHA_ANIMATION_INTERPOLATOR);
        alpha.start();
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.empty)).setText(getEmptyText());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        progressHandler = new Handler();

        invalidateLayoutManagerAndAdapter();
    }

    protected void invalidateLayoutManagerAndAdapter() {
        mRecyclerView.setLayoutManager(getLayoutManager());
        mAdapter = initializeAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onBackStackResume() {
        if (getActivity() != null) {
            MainActivity act = (MainActivity) getActivity();
            act.mRecyclerView = mRecyclerView;
        }
    }

    protected abstract String getTitle();

    protected abstract int getEmptyText();

    protected abstract GridLayoutManager getLayoutManager();

    protected abstract RecyclerView.Adapter initializeAdapter();
}
