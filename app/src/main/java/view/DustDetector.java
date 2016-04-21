package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ein on 2016/4/18.
 */
public class DustDetector extends View {
    public static final String TAG = "DustDetector";
    private dustLevelCallBack callBack;

    private Paint paint;
    private Paint textPaint;

    private int xOrigin;
    private float xAnima;
    private int xReal;

    private int startX;
    private int scale;

    public DustDetector(Context context) {
        super(context);
        Log.d(TAG, "DustDetector: one");
        init();
    }

    public DustDetector(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "DustDetector: two");
        init();
    }

    public DustDetector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "DustDetector: three");
        init();
    }

    private void init() {
        xOrigin = 60;
        xAnima = xOrigin;
        xReal = xOrigin;

        //字体画笔
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(8);
        //其他视图画笔
        paint = new Paint();
    }

    private void setDustLevel(int level) {
        this.xReal = level;
        setxAnima(xReal);
    }


    public void setxAnima(float xAnima) {
        this.xAnima = xAnima;
        callBack.callBack((xAnima - 60)/(768/300));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: ");
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));

        startX = 60;
        int width = getRight() - getLeft();
        int height = getBottom() - getTop();
        scale = width/300;
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //EXACTLY为指定size或match_parent
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //wrap_content时的默认大小
            result = 600;
            //AT_MOST为wrap_content
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(specSize, result);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 100;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: ");
        //线宽直接为灰尘检测器的宽度
        paint.setStrokeWidth(80);
        canvas.drawColor(Color.parseColor("#EDEDED"));
        paint.setColor(Color.GREEN);
        canvas.drawLine(startX, 50, startX + scale * 20, 50, paint);
        canvas.drawText("0", startX, 100, textPaint);
        paint.setColor(Color.YELLOW);
        canvas.drawLine(startX + scale * 20, 50, startX + scale * 40, 50, paint);//20
        canvas.drawText("20", startX + scale * 20, 100, textPaint);
        paint.setColor(Color.GRAY);
        canvas.drawLine(startX + scale * 40, 50, startX + scale * 80, 50, paint);//40
        canvas.drawText("40", startX + scale * 40, 100, textPaint);
        paint.setColor(Color.RED);
        canvas.drawLine(startX + scale * 80, 50, startX + scale * 140, 50, paint);//60
        canvas.drawText("80", startX + scale * 80, 100, textPaint);
        paint.setColor(Color.CYAN);
        canvas.drawLine(startX + scale * 140, 50, startX + scale * 220, 50, paint);//80
        canvas.drawText("140", startX + scale * 140, 100, textPaint);
        paint.setColor(Color.BLUE);
        canvas.drawLine(startX + scale * 220, 50, startX + scale * 320, 50, paint);//100
        canvas.drawText("220", startX + scale * 220, 100, textPaint);
        canvas.drawText("320", startX + scale * 320, 100, textPaint);

        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        canvas.drawLine(xAnima, 0, xAnima, 100, paint);
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                Log.d(TAG, "onTouchEvent: " + x);
                if (x >= 60 && x <= (60 + (768 / 300) * 320)) {
                    setxAnima(x);
                }
                break;
            case MotionEvent.ACTION_UP:
                setDustLevel(xReal);
                break;
        }
        postInvalidate();
        return true;
    }

    public void setCallBack(dustLevelCallBack callBack){
        this.callBack = callBack;
    }

    public static interface dustLevelCallBack {
        void callBack(float level);
    }
}
