package com.chinaoly.histogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyf on 2018/12/4.
 */
public class HistogramView extends View {

    private Paint mPaint, mChartPaint;
    private Rect mBound;
    private int mWidth, mHeight;
    private int mStartWidth, mChartWidth, mSize;
    private int lineColor, topColor, bottomColor, selectColor;

    private OnNumberListener onNumberListener;
    private int selectIndex = -1;
    private int heightUnit = 0;
    private List<Float> list = new ArrayList<>();

    public HistogramView(Context context) {
        this(context, null);
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ChartView, defStyleAttr, 0);
        lineColor = array.getColor(R.styleable.ChartView_xyColor, Color.BLACK);
        topColor = array.getColor(R.styleable.ChartView_topColor, Color.BLACK);
        bottomColor = array.getColor(R.styleable.ChartView_bottomColor, Color.BLACK);
        selectColor = array.getColor(R.styleable.ChartView_selectColor, Color.RED);
        array.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBound = new Rect();
    }

    public void setList(List<Float> list) {
        this.list = list;
        //如果视图不变，不会调用layout
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = widthMode == MeasureSpec.EXACTLY ? widthSize : widthSize / 2;
        int height = heightMode == MeasureSpec.EXACTLY ? heightSize : heightSize / 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mSize = mWidth / 25;
        mStartWidth = getWidth() / 13;
        mChartWidth = getWidth() / 13 - mSize / 2;
        heightUnit = getHeight() / 120;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int space = mWidth / 13;
        mStartWidth = mWidth / 13;
        mChartWidth = mWidth/ 13 - mSize / 2;

        mPaint.setColor(lineColor);
        //画坐标轴
        canvas.drawLine(0, mHeight - 100, mWidth, mHeight - 100, mPaint);

        mPaint.setTextSize(35);
        mPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < 12; i++) {
            //画刻度线
            canvas.drawLine(mStartWidth, mHeight - 100, mStartWidth, mHeight - 80, mPaint);
            String text = String.valueOf(i + 1) + "月";
            mPaint.getTextBounds(text, 0, text.length(), mBound);
            float x = mStartWidth;
            float y = mHeight - 60 + mBound.height() / 2;
            canvas.drawText(text, x, y, mPaint);
            mStartWidth += space;
        }

        for (int i = 0; i < list.size(); i++) {
            int x0 = mChartWidth;
            int y0 = mHeight - 100;
            int x1 = mChartWidth + mSize;
            int y1 = mHeight - 100 - (int)(list.get(i) * heightUnit);

            if (i == selectIndex) {
                mChartPaint.setShader(null);
                mChartPaint.setColor(selectColor);
            } else {
                LinearGradient gradient = new LinearGradient(x0, y0, x1, y1,
                        bottomColor, topColor, Shader.TileMode.MIRROR);
                mChartPaint.setShader(gradient);
            }
            RectF rect = new RectF(x0, y0, x1, y1);
            canvas.drawRoundRect(rect, 20, 20, mChartPaint);
            mChartWidth += space;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int left = 0;
        int top = 0;
        int right = mWidth / 12;
        int bottom = mHeight - 100;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < 12; i++) {
                    Rect rect = new Rect(left, top, right, bottom);
                    left += mWidth / 12;
                    right += mWidth / 12;
                    //top = bottom - (int)(list.get(i)* heightUnit);
                    if (rect.contains(x, y)) {
                        selectIndex = i;
                        if (onNumberListener != null) {
                            onNumberListener.onNum(list.get(i), x, y);
                        }
                        invalidate();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            mStartWidth = getWidth() / 13;
            mChartWidth = getWidth() / 13 - mSize / 2;
        }
    }

    public interface OnNumberListener{
        void onNum(float num, int x, int y);
    }

    public void setOnNumberListener(OnNumberListener onNumberListener) {
        this.onNumberListener = onNumberListener;
    }
}
