package example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/6/13.
 */

public class SuspendLayout extends RelativeLayout {

    private ViewDragHelper mDragger;

    private View mDragView;

    private Point initPointPosition = new Point();

    private boolean isFirst;

    public float mPercent=0.7f;//显示百分比

    public SuspendLayout(Context context) {
        this(context,null);
    }

    public SuspendLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SuspendLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SuspendLayout);
        mPercent=typedArray.getFloat(R.styleable.SuspendLayout_showPercent,mPercent);
        typedArray.recycle();
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId){
                return child == mDragView;
                //   tryCaptureView如何返回ture则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx){

                // clampViewPositionHorizontal,clampViewPositionVertical
                // 可以在该方法中对child移动的边界进行控制，
                // left , top 分别为即将移动到的位置，
                // 比如横向的情况下，我希望只在ViewGroup的内部移动，
                // 即：最小>=paddingleft，
                // 最大<=ViewGroup.getWidth()-paddingright-child.getWidth。
                // 就可以按照如下代码编写：

                //取得左边界的坐标
                final int leftBound = getPaddingLeft();
                //取得右边界的坐标
                final int rightBound = getWidth() - child.getWidth() - leftBound;
                //这个地方的含义就是 如果left的值 在leftbound和rightBound之间 那么就返回left
                //如果left的值 比 leftbound还要小 那么就说明 超过了左边界 那我们只能返回给他左边界的值
                //如果left的值 比rightbound还要大 那么就说明 超过了右边界，那我们只能返回给他右边界的值
                return Math.min(Math.max(left, leftBound), rightBound);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - child.getHeight() - topBound;
                return Math.min(Math.max(top, topBound), bottomBound);
            }


            /**
             * 手指释放的时候回调
             */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {


                if(releasedChild == mDragView){
                    int mY=releasedChild.getTop();
                    if(releasedChild.getTop()<0){
                        mY=0;
                    }
                    if(releasedChild.getBottom()>getHeight()){
                        mY=getHeight()-releasedChild.getMeasuredHeight();
                    }
                    if(releasedChild.getRight()-releasedChild.getMeasuredWidth()/2<getWidth()/2){
                        initPointPosition.x= (int) (getWidth()-mDragView.getWidth()*mPercent);
                        initPointPosition.y=mY;
                        mDragger.settleCapturedViewAt(initPointPosition.x,initPointPosition.y);

                    } else{
                        initPointPosition.x= (int) (0-mDragView.getWidth()*(1-mPercent));
                        initPointPosition.y=mY;
                        mDragger.settleCapturedViewAt(initPointPosition.x, initPointPosition.y );

                    }

                    invalidate();
                }
            }

            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId){
                mDragger.captureChildView(mDragView, pointerId);
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth()-child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight()-child.getMeasuredHeight();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }
        });
        mDragger.setEdgeTrackingEnabled(ViewDragHelper.DIRECTION_ALL);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDragger.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if(mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!isFirst){
            initPointPosition.x=mDragView.getLeft()+mDragView.getMeasuredWidth()/2;
            initPointPosition.y=mDragView.getTop()-mDragView.getMeasuredWidth();
            isFirst=true;
            mDragView.setBackgroundResource(R.mipmap.aaa);
        }

        mDragView.layout(initPointPosition.x,initPointPosition.y,
                initPointPosition.x+mDragView.getMeasuredWidth(),
                initPointPosition.y+mDragView.getMeasuredHeight());

    }

    /**
     * 设置显示百分比
     * @param percent
     */
    public void sethowPercent(float percent){
        this.mPercent=percent;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView = getChildAt(getChildCount()-1);
    }


}