package com.troshchii.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

    private int horizontalSpacing;
    private int verticalSpacing;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        try {
            horizontalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing, 0);
            verticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, 0);
        } finally {
            a.recycle();
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //max
        int llMaxWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int llWidthMode = MeasureSpec.getMode(widthMeasureSpec);

        int llWidth = 0;
        int llHeight = getPaddingTop();

        int currentWidth = getPaddingLeft();
        int currentHeight = 0;

        boolean breakLine;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            breakLine = lp.breakLine;
            if (breakLine || currentWidth + child.getMeasuredWidth() > llMaxWidthSize) {
                llHeight += currentHeight + verticalSpacing;
                llWidth = Math.max(llWidth, currentWidth);
                //Zero out
                currentWidth = getPaddingLeft();
                currentHeight = 0;
            }

            int spacing = horizontalSpacing;
            if (lp.spacing > -1) {
                spacing = lp.spacing;
            }

            lp.x = currentWidth + spacing;
            lp.y = llHeight;

            currentWidth += child.getMeasuredWidth();
            currentHeight = Math.max(currentHeight, child.getMeasuredHeight());
        }

        llWidth += getPaddingRight();
        llHeight += getPaddingBottom();

        setMeasuredDimension(resolveSize(llWidth, widthMeasureSpec), resolveSize(llHeight, heightMeasureSpec));
    }

    @Override protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }

    @Override public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    @Override protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        boolean breakLine;
        int spacing = -1;

        private int x;
        private int y;

        LayoutParams(int width, int height) {
            super(width, height);
        }

        LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout_LayoutParams);
            try {
                breakLine = a.getBoolean(R.styleable.FlowLayout_LayoutParams_breakLine, false);
                spacing = a.getDimensionPixelSize(R.styleable.FlowLayout_LayoutParams_layout_spacing, -1);
            } finally {
                a.recycle();
            }
        }
    }
}