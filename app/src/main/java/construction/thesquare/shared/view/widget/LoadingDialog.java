/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.shared.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import construction.thesquare.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.loader);

        VectorDrawableCompat vector = VectorDrawableCompat.create(context.getResources(),
                R.drawable.the_square_spinner_plain, null);
        ImageView image = (ImageView) findViewById(R.id.progressImage);
        image.setBackground(vector);

        final ObjectAnimator verticalAnimator = ObjectAnimator.ofFloat(image, "rotationY", 0.0f, 360f);
        verticalAnimator.setDuration(1500);
        verticalAnimator.setInterpolator(new LinearInterpolator());

        final ObjectAnimator horizontalAnimator = ObjectAnimator.ofFloat(image, "rotationX", 0.0f, 360f);
        horizontalAnimator.setDuration(1500);
        horizontalAnimator.setInterpolator(new LinearInterpolator());

        verticalAnimator.addListener(new AnimatorListenerAdapter() {
            boolean cancelled;

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                cancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (!cancelled) {
                    horizontalAnimator.start();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cancelled = false;
            }
        });

        horizontalAnimator.addListener(new AnimatorListenerAdapter() {
            boolean cancelled;

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                cancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (!cancelled) {
                    verticalAnimator.start();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cancelled = false;
            }
        });

        verticalAnimator.start();
    }
}
