package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ein on 2016/4/18.
 */
public class Thermometer extends View {
    public static final String TAG = "Thermometer";

    private Thermometer.tempCallback tempCallback;
    private int yOrigin;
    private float yAnima;
    private int yCurrent;

    public Thermometer(Context context) {
        super(context);
        init(null, 0);
        Log.d(TAG, "Thermometer: one");
    }

    public Thermometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
        Log.d(TAG, "Thermometer: two");

    }

    public Thermometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
        Log.d(TAG, "Thermometer: three");
    }

    public void init(AttributeSet attrs, int defStyle) {
        //刻度值的坐标初始值35摄氏度的y轴坐标
        yOrigin = 640;
        yAnima = yOrigin;
        yCurrent = 35;
    }

    public void setTem(int num) {
        yCurrent = num;
        setCursor(yOrigin - (num - 35) * 80);
        postInvalidate();
    }

    public void setCursor(float y) {
        this.yAnima = y;
        tempCallback.callBack((yOrigin - yAnima) / 80 + 35 + (yOrigin - Math.round(yAnima)) % 80 / 8 / 10);
    }

    public void setTempCallback(Thermometer.tempCallback tempCallback) {
        this.tempCallback = tempCallback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 250;
            if (specMode == MeasureSpec.AT_MOST) {
                //如果父布局的大小,小于默认值则取父布局大小
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 1000;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        Paint paintCircle = new Paint();
        Paint paintLine = new Paint();

        //顶部y轴坐标80
        int top = 80;
        int initNum = 35;
        //温度计矩形部分
        paint.setColor(Color.GRAY);
        RectF rectF = new RectF(80, top, 160, 760);
        canvas.drawRoundRect(rectF, 0, 16, paint);
        //温度计红色液体部分(底下球体永远为红色)
        paintCircle.setColor(Color.RED);
        canvas.drawRect(80, yAnima, 160, 760, paintCircle);//温度计红色的管子中的矩形部分
        canvas.drawCircle(120, 790, 80, paintCircle);//温度计红色管子部分的底座圆形部分

        int tem = yOrigin;
        paintLine.setColor(Color.BLUE);
        //画线不超过顶部
        while (tem >= top) {
            //每一大格为一度
            if (tem % 80 == 0) {
                canvas.drawLine(160, tem, 172, tem, paintLine);
                canvas.drawText(initNum + "", 170, tem + 4, paint);
                initNum++;
            }
            //每一度的大小为80dp,因为有10小格所以每小格80/10
            canvas.drawLine(160, tem, 167, tem, paint);
            tem = tem - 8;
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.toString());
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float tem = event.getY();
                if (tem >= 80) {
                    setCursor(event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                setTem(yCurrent);
                break;
        }
        postInvalidate();
        return true;
    }

    public static interface tempCallback {
        void callBack(double tem);
    }
}
