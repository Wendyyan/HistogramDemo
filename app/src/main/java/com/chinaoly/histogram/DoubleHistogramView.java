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
 * Created by zyf on 2018/12/11.
 */
public class DoubleHistogramView extends View {

    private Paint mPaint, mChartPaint, mShadowPaint;
    private Rect mBound;
    private int mWidth, mHeight;
    private int mStartWidth, mChartWidth, mSize, heightUnit = 0;;
    private int lineColor, leftColor, leftBottomColor, selectLeftColor,
            rightColor, rightBottomColor, selectRightColor;

    //选中标记
    private List<Integer> selectIndexRoles = new ArrayList<>();
    private List<Integer> list = new ArrayList<>();
    private OnNumberListener onNumberListener;

    public DoubleHistogramView(Context context) {
        this(context, null);
    }

    public DoubleHistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleHistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ChartView, defStyleAttr, 0);
        lineColor = array.getColor(R.styleable.ChartView_xyColor, Color.BLACK);
        leftColor = array.getColor(R.styleable.ChartView_leftColor, Color.BLACK);
        leftBottomColor = array.getColor(R.styleable.ChartView_leftBottomColor, Color.BLACK);
        selectLeftColor = array.getColor(R.styleable.ChartView_selectLeftColor, Color.RED);
        rightColor = array.getColor(R.styleable.ChartView_rightColor, Color.BLACK);
        rightBottomColor = array.getColor(R.styleable.ChartView_rightBottomColor, Color.BLACK);
        selectRightColor = array.getColor(R.styleable.ChartView_selectRightColor, Color.RED);
        array.recycle();
    }

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.WHITE);
        mBound = new Rect();
    }

    public void setList(List<Integer> list) {
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
        mSize = mWidth / 39;
        mStartWidth = getWidth() / 13;
        mChartWidth = getWidth() / 13 - mSize;
        heightUnit = getHeight() / 120;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int space = mWidth / 13;
        mStartWidth = mWidth / 13;
        mChartWidth = mWidth/ 13 - mSize;

        mPaint.setColor(lineColor);
        mPaint.setTextSize(35);
        mPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < 12; i++) {
            //画刻度线
//            canvas.drawLine(mStartWidth, mHeight - 100, mStartWidth, mHeight - 80, mPaint);
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
            int y1 = mHeight - 100 - (list.get(i) * heightUnit);

            if (selectIndexRoles.contains(i)) {
                mChartPaint.setShader(null);
                mChartPaint.setColor(i % 2 == 0 ? selectLeftColor : selectRightColor);
            } else {
                LinearGradient gradient;
                if (i % 2 == 0) {
                    gradient = new LinearGradient(x0, y0, x1, y1, leftBottomColor, leftColor,
                            Shader.TileMode.MIRROR);
                } else {
                    gradient = new LinearGradient(x0, y0, x1, y1, rightBottomColor, rightColor,
                            Shader.TileMode.MIRROR);
                }
                mChartPaint.setShader(gradient);
            }
            RectF rect = new RectF(x0, y0, x1, y1);
            canvas.drawRoundRect(rect, 20, 20, mChartPaint);
//            canvas.drawText(String.valueOf(list.get(i)), (x0 + x1) / 2, y1 - 10, mPaint);
            mChartWidth += (i % 2 == 0) ? (3 + mSize) : (space - 3 - mSize);
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
                    int max = Math.max(list.get(i * 2), list.get(i * 2 + 1));
                    top = (bottom - max * heightUnit);
                    Rect rect = new Rect(left, top, right, bottom);
                    left += mWidth / 12;
                    right += mWidth / 12;
                    if (rect.contains(x, y)) {
                        if (onNumberListener != null) {
                            onNumberListener.onNum(i, x, y);
                        }
                        selectIndexRoles.clear();
                        selectIndexRoles.add(i * 2);
                        selectIndexRoles.add(i * 2 + 1);
                        invalidate();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public interface OnNumberListener{
        void onNum(float num, int x, int y);
    }

    public void setOnNumberListener(OnNumberListener onNumberListener) {
        this.onNumberListener = onNumberListener;
    }
}
