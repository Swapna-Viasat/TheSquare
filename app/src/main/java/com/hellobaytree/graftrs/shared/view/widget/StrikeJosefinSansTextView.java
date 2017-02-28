package com.hellobaytree.graftrs.shared.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.hellobaytree.graftrs.R;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

public class StrikeJosefinSansTextView extends JosefinSansTextView {

    public Paint paint;
    private boolean addStrike = false;

    public StrikeJosefinSansTextView(Context context) {
        super(context);
        init(context);
    }

    public StrikeJosefinSansTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StrikeJosefinSansTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.redSquareColor));
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (addStrike) {
            for (int i = 1; i <= getLineCount(); i++) {
                canvas.drawLine(0, getHeight() / 2,
                        getWidth(), getHeight() / 2, paint);
            }
        }
    }

    public void setStrikeVisibility(boolean addStrike) {
        this.addStrike = addStrike;
        invalidate();
    }
}
