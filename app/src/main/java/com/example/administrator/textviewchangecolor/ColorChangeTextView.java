package com.example.administrator.textviewchangecolor;

import android.content.Context;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import static com.example.administrator.textviewchangecolor.ColorChangeTextView.Direction.LEFT_TO_RIGHT;


/**
 * Created by Administrator on 2017/12/24.
 */

public class ColorChangeTextView extends TextView {

    // 1. 实现一个文字两种颜色 - 绘制不变色字体的画笔
    private Paint mOriginPaint;
    // 1. 实现一个文字两种颜色 - 绘制变色字体的画笔
    private Paint mChangePaint;
    // 1. 实现一个文字两种颜色 - 当前的进度
    private float mCurrentProgress = 0.0f;
    private Rect rect;

    // 2.实现不同朝向
    private Direction mDirection = LEFT_TO_RIGHT;

    public enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LIFT
    }


    public ColorChangeTextView(Context context) {
        this(context, null);
    }

    public ColorChangeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorChangeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs);

    }

    /**
     * 1.1 初始化画笔
     */
    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ColorChangeTextView);

        int originColor = array.getColor(R.styleable.ColorChangeTextView_originColor, getTextColors().getDefaultColor());
        int changeColor = array.getColor(R.styleable.ColorChangeTextView_changeColor, getTextColors().getDefaultColor());

        mOriginPaint = getPaintByColor(originColor);
        mChangePaint = getPaintByColor(changeColor);

        // 回收
        array.recycle();
    }

    /**
     * 1.根据颜色获取画笔
     */
    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        // 设置颜色
        paint.setColor(color);
        // 设置抗锯齿
        paint.setAntiAlias(true);
        // 防抖动
        paint.setDither(true);
        // 设置字体的大小  就是TextView的字体大小
        paint.setTextSize(getTextSize());
        return paint;
    }

    //一个文字两种颜色
    //利用clipRect的api 可以裁剪效果 可以利用两个画笔
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        //裁剪区域
        //根据进度把中间值算出来

        int middle = (int) (mCurrentProgress * getWidth());
        if (mDirection == LEFT_TO_RIGHT) {
            drawText(canvas, mChangePaint, 0, middle);
            drawText(canvas, mOriginPaint, middle, getWidth());
        } else {

            drawText(canvas, mChangePaint, getWidth() - middle, getWidth());
            drawText(canvas, mOriginPaint, 0, getWidth() - middle);
        }

    }

    private void drawText(Canvas canvas, Paint paint, int start, int end) {
        //裁剪区域
        //根据进度把中间值算出来
        canvas.save();
        //绘制不变色的
        Rect rect = new Rect(start, 0, end, getHeight());

        canvas.clipRect(rect);

        //自己来画
        String text = getText().toString();
        //获取自己的宽度
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.width() / 2;
        //基线
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;
        canvas.drawText(text, x, baseLine, paint);
        canvas.restore();
    }

    public void setDirection(Direction direction) {
        this.mDirection = direction;
    }

    public void setCurrentprogress(float currentprogress) {
        this.mCurrentProgress = currentprogress;
        invalidate();
    }


    public void setChangeColor(int changeColor) {
        this.mChangePaint.setColor(changeColor);
    }

    public void setOriginColor(int originColor) {
        this.mOriginPaint.setColor(originColor);
    }
}
