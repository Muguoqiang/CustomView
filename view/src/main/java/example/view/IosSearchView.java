package example.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/6/13.
 */

public class IosSearchView  extends EditText implements TextWatcher, TextView.OnEditorActionListener {
    private Bitmap bitmapLeft;
    private Bitmap bitmapDelete;
    private int viewWidth;
    private int viewHeight;
    private int drawableWidth;
    private int drawableHeight;
    private int drawablePaddingLeft;//图片距离左边距离
    private int drawablePaddingTop;//图片距离顶部距离
    private int textPaddingLeft;
    private int deleteWidth;
    private Paint drawablePaint;
    private Paint textPaint;
    private String drawText = "";
    private Rect mBounds;
    private boolean isShow = false;//是否在左边
    private boolean isDelete = false;
    private AnimatorSet animatorSet;//动画集合
    private Paint deletePaint;
    private int deleteClickMatrix;
    private Context mContext;
    private int textWidth;
    private OnSearchBack onSearchBack;

    public void setOnSearchBack(OnSearchBack onSearchBack) {
        this.onSearchBack = onSearchBack;
    }

    public IosSearchView(Context context) {

        super(context);
        mContext = context;
        initPaint();
    }

    public IosSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
    }

    public IosSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText = (String) getHint();
        if (drawText.isEmpty()){
            drawText="搜索";
        }
        textPaint.getTextBounds(drawText, 0, drawText.length(), mBounds);
        textWidth = mBounds.width();

        if (!isShow) {
            drawablePaddingLeft = (viewWidth - (textWidth + drawableWidth)) / 2;//图片距离左边距离
            textPaddingLeft = (viewWidth - (textWidth + drawableWidth)) / 2 + drawableWidth + 10;
        }
        drawablePaddingTop = viewHeight / 2 - drawableHeight / 2;//图片距离顶部距离
        canvas.drawBitmap(bitmapLeft, drawablePaddingLeft, drawablePaddingTop, drawablePaint);
        setPadding(textPaddingLeft, 0, 0, 0);
        if (isDelete) {
            deleteClickMatrix = viewWidth - deleteWidth - 20;
            canvas.drawBitmap(bitmapDelete, deleteClickMatrix, drawablePaddingTop, deletePaint);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测了拿到宽高
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);//view宽度
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);//view高德
        drawableWidth = bitmapLeft.getWidth();//图片宽度
        drawableHeight = bitmapLeft.getHeight();//图片高度
        deleteWidth = bitmapDelete.getWidth();


    }


    private void initPaint() {
        bitmapDelete = BitmapFactory.decodeResource(getResources(), R.mipmap.delete);
        bitmapLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.search_new);
        drawablePaint = new Paint();
        deletePaint = new Paint();
        textPaint = new Paint();
        mBounds = new Rect();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.unCheckColor));
        textPaint.setTextSize(getTextSize());
        setFocusable(false);
        setFocusableInTouchMode(false);
        animatorSet = new AnimatorSet();
        addTextChangedListener(this);
        setOnEditorActionListener(this);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (isDelete) {
                    if ((x < getWidth() && x > deleteClickMatrix) && (y > 0 && y < getHeight())) {
                        setText("");
                        setFocusable(false);
                        setFocusableInTouchMode(false);
                        InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        isShow = true;
                        animatorSet.play(drawableRight()).with(textRight());
                        animatorSet.start();
                    }
                } else {
                    if ((x > 0 && x < getWidth()) && (y > 0 && y < getHeight())) {
                        setFocusable(true);
                        setFocusableInTouchMode(true);
                        InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.showSoftInput(this, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        isShow = true;
                        animatorSet.play(drawableLeft()).with(textLeft());
                        animatorSet.start();
                    }
                }


            case MotionEvent.ACTION_UP:
                x = 0;
                y = 0;
                break;


        }
        return super.onTouchEvent(event);
    }

    private ValueAnimator drawableLeft() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat(drawablePaddingLeft, 0);
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                drawablePaddingLeft = (int) t;
                Log.e("11drawableLeft", drawablePaddingLeft + "");
                invalidate();
            }
        });
        return floatAnimator;

    }

    private ValueAnimator textLeft() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat(textPaddingLeft, drawableWidth + 10);
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                textPaddingLeft = (int) t;
                Log.e("11textLeft", textPaddingLeft + "");
                invalidate();
            }
        });
        return floatAnimator;
    }

    private ValueAnimator drawableRight() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat(0, (viewWidth - (textWidth + drawableWidth)) / 2);
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                drawablePaddingLeft = (int) t;
                Log.e("11drawableLeft", drawablePaddingLeft + "");
                invalidate();
            }
        });
        return floatAnimator;

    }

    private ValueAnimator textRight() {
        ValueAnimator floatAnimator = ValueAnimator.ofFloat(drawableWidth + 10, (viewWidth - (textWidth + drawableWidth)) / 2 + drawableWidth + 10);
        floatAnimator.setDuration(500);
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = (float) valueAnimator.getAnimatedValue();
                textPaddingLeft = (int) t;
                Log.e("11textLeft", textPaddingLeft + "");
                invalidate();
            }
        });
        return floatAnimator;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 0) {
            isDelete = false;

        } else {
            isDelete = true;
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            if (getText().toString().isEmpty()) {
                Toast.makeText(mContext, "搜索条件不能为空", Toast.LENGTH_SHORT).show();
                return true;

            } else {
                InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (onSearchBack != null) {
                    onSearchBack.back(getText().toString());
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public interface OnSearchBack {
        void back(String back);
    }
}