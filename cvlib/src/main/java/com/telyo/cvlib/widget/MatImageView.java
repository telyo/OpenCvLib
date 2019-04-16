package com.telyo.cvlib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;

@SuppressLint("AppCompatCustomView")
public class MatImageView extends ImageView implements ReactPoint {

    private static String TAG = MatImageView.class.getSimpleName();
    private ReactPoint mMat;
    private Bitmap mBitmap;

    private static double TOUCH_ENABLE_RADIUS = 50;
    private static double TOUCH_INSIDE_DISTANCE = 25;
    private int mCurrentPointPosition;
    private double coefficientX = 1;
    private double coefficientY = 1;
    private double width;
    private Paint paint;
    private float height;
    private boolean isOnPoint;
    private boolean isInsideRect;
    private double startX;
    private double startY;

    public MatImageView(Context context) {
        this(context, null);
    }

    public MatImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
        setScaleType(ScaleType.FIT_XY);
    }

    public void setMat(Mat mat) {
        if (!(mat instanceof ReactPoint)) {
            throw new IllegalArgumentException("your mat mast implements ReactPoint");
        }
        this.mMat = (ReactPoint) mat;
        mBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        upDateBitmap();
    }

    private void upDateBitmap() {
        Utils.matToBitmap((Mat) mMat, mBitmap);
        setImageBitmap(mBitmap);
        invalidate();
        if (mMat != null) {
            coefficientX = width / mBitmap.getWidth();
            coefficientY = height / mBitmap.getHeight();
        }

        log("coefficientX = " + coefficientX + "\n" + " coefficientY = " + coefficientY);

    }

    private void log(String s) {
        Log.d(TAG, s);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMat == null) {
            return;
        }
        for (int i = 0; i < getCorners().size(); i++) {
            canvas.drawLine((float) (getCorners().get(i).x * coefficientX)
                    , (float) (getCorners().get(i).y * coefficientY)
                    , (float) (getCorners().get((i + 1) % getCorners().size()).x * coefficientX)
                    , (float) (getCorners().get((i + 1) % getCorners().size()).y * coefficientY)
                    , paint);
            canvas.drawCircle((float) (getCorners().get(i).x * coefficientX)
                    , (float) (getCorners().get(i).y * coefficientY)
                    , 5
                    , paint
            );
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mMat == null) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOnPoint = false;
                isInsideRect = false;
                startX = event.getX();
                startY = event.getY();
                isOnPoint = isMoveEnable(startX, startY);
                isInsideRect = isInsideRect(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                double dx = event.getX() - startX;
                double dy = event.getY() - startY;
                startX = event.getX();
                startY = event.getY();
                log("dx = " + dx + "  dy = " + dy);
                if (isInsideRect) {
                    for (Point point : getCorners()) {
                        double[] vals = new double[]{point.x + dx, point.y + dy};
                        point.set(vals);
                    }
                    invalidate();
                    break;
                }
                if (isOnPoint) {
                    double[] vals = new double[]{event.getX() / coefficientX, event.getY() / coefficientY};
                    getCorners().get(mCurrentPointPosition).set(vals);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                isOnPoint = false;
                isInsideRect = false;
                break;

        }
        return true;

    }

    private boolean isInsideRect(float x, float y) {
        return getCorners().get(1).x + TOUCH_INSIDE_DISTANCE < x / coefficientX
                && x / coefficientX < getCorners().get(3).x - TOUCH_INSIDE_DISTANCE
                && getCorners().get(1).y + TOUCH_INSIDE_DISTANCE < y / coefficientY
                && y / coefficientY < getCorners().get(3).y - TOUCH_INSIDE_DISTANCE;
    }

    private boolean isMoveEnable(double dx, double dy) {
        if (mMat == null) {
            return false;
        }
        for (int i = 0; i < getCorners().size(); i++) {
            Point point = getCorners().get(i);
            double a = dx / coefficientX - point.x;
            double b = dy / coefficientY - point.y;

            double distance = Math.sqrt(a * a + b * b);
            if (distance < TOUCH_ENABLE_RADIUS) {
                mCurrentPointPosition = i;
                return true;
            }
        }
        return false;
    }


    @Override
    public Point getPoint0() {
        return mMat != null ? mMat.getPoint0() : null;
    }

    @Override
    public Point getPoint1() {
        return mMat != null ? mMat.getPoint1() : null;
    }

    @Override
    public Point getPoint2() {
        return mMat != null ? mMat.getPoint2() : null;
    }

    @Override
    public Point getPoint3() {
        return mMat != null ? mMat.getPoint3() : null;
    }

    @Override
    public List<Point> getCorners() {
        return mMat != null ? mMat.getCorners() : null;
    }

    @Override
    public void setCorners(List<Point> corners) {
        if (mMat != null) {
            mMat.setCorners(corners);
        }
    }
}
