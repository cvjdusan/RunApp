package rs.ac.bg.etf.running.location;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.LocationCustomViewBinding;
import rs.ac.bg.etf.running.workouts.LifecycleAwareLocator;
import rs.ac.bg.etf.running.workouts.WorkoutDetailsFragment;
import rs.ac.bg.etf.running.workouts.WorkoutListFragment;

public class LocationCustomView extends View {

    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private LocationCustomViewBinding binding;

    public LocationCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LocationCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LocationCustomView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = LocationCustomViewBinding.inflate(inflater);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public LocationCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = LocationCustomViewBinding.inflate(inflater);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.save();
//        canvas.translate(mPosX, mPosY);
//        canvas.scale(mScaleFactor, mScaleFactor);
//
//        Paint paint = new Paint();
//
//        canvas.drawColor(Color.LTGRAY);
//
//        paint.setColor(Color.GRAY);
//
//        for (int i = -500; i < canvas.getWidth() + 500; i += 50) {
//            for (int j = -500; j < canvas.getHeight() + 500; j += 50) {
//                canvas.drawLine((float)(-500), (float)j, (float)(canvas.getWidth() + 500), (float)j, paint);
//            }
//        }
//
//        for (int i = -500; i < canvas.getHeight() + 500; i += 50) {
//            for (int j = -500; j < canvas.getWidth() + 500; j += 50) {
//                canvas.drawLine((float)j, (float)(-500), (float)j, (float)(canvas.getHeight() + 500), paint);
//            }
//        }
//
//        double latitude = WorkoutDetailsFragment.locationDraw.get(0).getLatitude();
//        double longitude = WorkoutDetailsFragment.locationDraw.get(0).getLongitude();
//
//        double temp1y = latitude * 10000000;
//        int temp2y = (int)temp1y / 1000000;
//        latitude = ((double)temp2y) / 10;
//
//        double temp1x = longitude * 10000000;
//        int temp2x = (int)temp1x / 1000000;
//        longitude = ((double)temp2x) / 10;
//
//        double latitude1 = latitude;
//        double longitude1 = longitude;
//        double latitude2 = latitude1 + 0.1;
//        double longitude2 = longitude1 + 0.1;
//
//        paint.setStrokeWidth(1);
//        paint.setColor(Color.BLACK);
//
//        canvas.drawText(String.format("%.7f", latitude1), 10, 20, paint);
//        canvas.drawText(String.format("%.7f", longitude1), 10, 40, paint);
//
//        canvas.drawText(String.format("%.7f", latitude1), 580, 20, paint);
//        canvas.drawText(String.format("%.7f", longitude2), 580, 40, paint);
//
//        canvas.drawText(String.format("%.7f", latitude2), 10, 750, paint);
//        canvas.drawText(String.format("%.7f", longitude1), 10, 770, paint);
//
//        canvas.drawText(String.format("%.7f", latitude2), 580, 750, paint);
//        canvas.drawText(String.format("%.7f", longitude2), 580, 770, paint);
//
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(1);
//
//
//
//        List<Location> staticLocations = WorkoutDetailsFragment.locationDraw;
//
//        int mul = 10000;
//        int mod = 1000;
//
//        double x1 = staticLocations.get(0).getLongitude();
//        double y1 = staticLocations.get(0).getLatitude();
//        x1 = x1 * mul;
//        int x1int = (int) x1;
//        x1int = x1int % mod;
//        y1 = y1 * mul;
//        int y1int = (int) y1;
//        y1int = y1int % mod;
//        for (int i = 1; i < staticLocations.size(); i++) {
//            double x2 = staticLocations.get(i).getLongitude();
//            double y2 = staticLocations.get(i).getLatitude();
//            x2 = x2 * mul;
//            int x2int = (int) x2;
//            x2int = x2int % mod;
//            y2 = y2 * mul;
//            int y2int = (int) y2;
//            y2int = y2int % mod;
//
//            float x1float = (float)(x1int) / 1000 * canvas.getWidth();
//            float y1float = (float)(y1int) / 1000 * canvas.getWidth();
//            float x2float = (float)(x2int) / 1000 * canvas.getWidth();
//            float y2float = (float)(y2int) / 1000 * canvas.getWidth();
//
//            canvas.drawLine(x1float, y1float, x2float, y2float, paint);
//
//            x1int = x2int;
//            y1int = y2int;
//        }
//
//        canvas.restore();
    }


}
