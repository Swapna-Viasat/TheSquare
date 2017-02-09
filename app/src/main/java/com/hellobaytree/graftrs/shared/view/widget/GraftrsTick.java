package com.hellobaytree.graftrs.shared.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hellobaytree.graftrs.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maizaga on 13/11/16.
 */

public class GraftrsTick extends RelativeLayout {

    @BindView(R.id.tick_border)
    ImageView border;
    @BindView(R.id.tick_filled_circle)
    ImageView filledCircle;
    @BindView(R.id.tick)
    ImageView tick;

    private boolean checked;

    public GraftrsTick(Context context) {
        super(context);
        init();
    }

    public GraftrsTick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraftrsTick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_tick, this);
        ButterKnife.bind(this, this);
        setChecked(false);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        border.setVisibility(checked ? GONE : VISIBLE);
        filledCircle.setVisibility(checked ? VISIBLE : GONE);
        tick.setVisibility(checked ? VISIBLE : GONE);
    }

    public boolean isChecked() {
        return checked;
    }
}