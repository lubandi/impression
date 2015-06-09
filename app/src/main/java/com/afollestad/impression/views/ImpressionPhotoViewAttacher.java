package com.afollestad.impression.views;


import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImpressionPhotoViewAttacher extends PhotoViewAttacher {
    public ImpressionPhotoViewAttacher(ImageView imageView) {
        super(imageView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        try {
            return super.onTouch(v, ev);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        if ((velocityY > 3000 || velocityY < -3000) &&
                (velocityX < 7000 && velocityX > -7000)) {
            ((Activity) getImageView().getContext()).onBackPressed();
        } else {
            super.onFling(startX, startY, velocityX, velocityY);
        }
    }
}
