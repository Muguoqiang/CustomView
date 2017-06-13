package example.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Administrator on 2017/6/13.
 */

public class SingleSelectTitleView extends View {
    private String leftString="";//左侧文字
    private String rightString="";//右侧文字
    private float textSize;//文字大小
    private float lineHigh;//滑动的线宽
    private int lineMarTop;//线距上边距离
    private int lineMarLeft=0;//线距左边距离
    private int lineMarRight=0;//线距右边距离
    private Paint leftBack;//左边块画笔
    private Paint leftText;//左边文字画笔
    private Paint rightText;//右边文字画笔
    private Paint rightBack;//右边块画笔
    private  Paint line;//线的画笔
    private Rect mBounds;//矩形
    private AnimatorSet animatorSet;//动画集合
    private boolean leftFlag=false;
    private boolean rightFlag=true;
    private int leftAlpha=255;//左边文字透明度
    private int  rightAlpha=255/2;//右边文字透明度
    private int leftColor;//左边字颜色
    private int rightColor;//右边字颜色
    private int lineColor;//线颜色
    private   OnViewClickLisListener onViewClickLisListener;

    public void setOnViewClickLisListener(OnViewClickLisListener onViewClickLisListener) {
        this.onViewClickLisListener = onViewClickLisListener;
    }

    public SingleSelectTitleView(Context context) {
        super(context);
        initPaint();
    }

    public SingleSelectTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValues(context,attrs);
        initPaint();
    }

    public SingleSelectTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValues(context,attrs);

    }


    public void initValues(Context context,AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SingleSelectTitleView);
        leftString=array.getString(R.styleable.SingleSelectTitleView_leftText);
        rightString=array.getString(R.styleable.SingleSelectTitleView_rightText);
        textSize=array.getDimension(R.styleable.SingleSelectTitleView_textSize,50);
        lineHigh=array.getDimension(R.styleable.SingleSelectTitleView_lineHigh,10);
        leftColor=array.getColor(R.styleable.SingleSelectTitleView_leftColor, Color.BLACK);
        rightColor=array.getColor(R.styleable.SingleSelectTitleView_rightColor,Color.BLACK);
        lineColor=array.getColor(R.styleable.SingleSelectTitleView_lineColor,Color.BLACK);
        array.recycle();

    }
    private void initPaint(){
        leftBack=new Paint();
        leftBack.setColor(Color.WHITE);
        leftBack.setFlags(Paint.ANTI_ALIAS_FLAG);

        leftText=new Paint();
        leftText.setTextSize(textSize);
        leftText.setColor(leftColor);
        leftText.setFlags(Paint.ANTI_ALIAS_FLAG);
        leftText.setAlpha(leftAlpha);

        rightBack=new Paint();
        rightBack.setFlags(Paint.ANTI_ALIAS_FLAG);
        rightBack.setColor(Color.WHITE);


        rightText=new Paint();
        rightText.setTextSize(textSize);
        rightText.setFlags(Paint.ANTI_ALIAS_FLAG);
        rightText.setColor(rightColor);
        rightText.setAlpha(rightAlpha);


        line=new Paint();
        line.setFlags(Paint.ANTI_ALIAS_FLAG);
        line.setColor(lineColor);
        line.setStrokeWidth(lineHigh);
        mBounds = new Rect();
        animatorSet = new AnimatorSet();


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth()/2, getHeight(), leftBack);

        leftText.getTextBounds(leftString, 0, leftString.length(), mBounds);
        leftText.setAlpha(leftAlpha);
        float leftWidth = mBounds.width();
        float leftHeight = mBounds.height();
        canvas.drawText(leftString, getWidth() / 4 - leftWidth / 2, getHeight() / 2 + leftHeight / 2, leftText);



        canvas.drawRect(getWidth()/2, 0, getWidth(), getHeight(), rightBack);

        rightText.getTextBounds(rightString, 0, rightString.length(), mBounds);
        rightText.setAlpha(rightAlpha);
        float rightWidth = mBounds.width();
        float rightHeight= mBounds.height();
        canvas.drawText(rightString, getWidth()/2+getWidth()/4- rightWidth/ 2, getHeight() / 2 + rightHeight / 2, rightText);


        canvas.drawLine(lineMarLeft, lineMarTop, lineMarRight, lineMarTop, line);
        Log.e("canvas.drawLine", lineMarLeft + "==" + lineMarRight);




    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        lineMarRight= MeasureSpec.getSize(widthMeasureSpec)/2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lineMarTop=h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x=event.getX();
                if (x<getWidth()/2){
                    //点击左边块
                    if (leftFlag==true){
                        if (onViewClickLisListener!=null){
                            onViewClickLisListener.left();
                        }
                        leftFlag=false;
                        animatorSet.play(right1()).with(right2());
                        animatorSet.start();
                        animatorSet.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                rightFlag=true;
                                leftFlag=false;
                                rightAlpha= 255/2;
                                leftAlpha=255;

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                    }

                }else {
                    if (rightFlag==true){
                        if (onViewClickLisListener!=null){
                            onViewClickLisListener.right();
                        }
                        rightFlag=false;
                        animatorSet.play(left1()).with(left2());
                        animatorSet.start();
                        animatorSet.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                leftFlag=true;
                                rightFlag=false;
                                rightAlpha= 255;
                                leftAlpha=255/2;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                    }


                }
                break;
            case MotionEvent.ACTION_UP:
                x=0;
                break;
        }
        return super.onTouchEvent(event);


    }



    private ValueAnimator left1() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat(0, getWidth()/2);
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                lineMarLeft= (int) t;
                invalidate();
            }
        });
        return floatAnimator;
    }

    private ValueAnimator left2() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat(getWidth()/2, getWidth());
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                lineMarRight= (int) t;
                invalidate();
            }
        });
        return  floatAnimator;
    }
    private ValueAnimator right1() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat( getWidth()/2,0);
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                lineMarLeft= (int) t;
                invalidate();
            }
        });
        return floatAnimator;
    }

    private ValueAnimator right2() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat(getWidth(), getWidth()/2);
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                lineMarRight= (int) t;
                invalidate();
            }
        });
        return  floatAnimator;
    }


    public   interface  OnViewClickLisListener{
        void left();
        void right();
    }
    public void setLeftContent(String mLefString){
        this.leftString=mLefString;
    }
    public void setRightContent(String mRightString){
        this.rightString=mRightString;
    }
    public  void setTextSize(float size){
        this.textSize=size;
    }
    public void  setLingHigh(float high){
        this.lineHigh=high;
    }

    public void setLeftColor( int color){
        this.leftColor=color;
    }
    public void setRightColor(int color){
        this.rightColor=color;
    }
    public void setLineColor(int color){
        this.lineColor=color;
    }
}