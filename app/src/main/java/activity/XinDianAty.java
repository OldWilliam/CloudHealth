/*
2015-9-27 ����4:22:28
 */
package activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.example.health_community.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.FileManager;
import util.MConfig;
import util.ScreenSizeUtil;

public class XinDianAty extends BaseActivity {
    private ActionBar actionBar;
    private SurfaceView heartLineView;
    private Button cancleBt;
    private SurfaceHolder mHolder;
    private boolean isRunning = true;
    private byte[] dataPac;

    private FileManager mFileMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_xindian);
        ScreenSizeUtil.getHeight(this);
        Log.e("Screen_Width", ScreenSizeUtil.getHeight(this) + "");
        ScreenSizeUtil.getWidth(this);
        Log.e("Screen_Height", ScreenSizeUtil.getWidth(this) + "");
        initViews();
        init();
    }

    @Override
    public void initViews() {
        cancleBt = (Button) findViewById(R.id.heart_cancleBt);
        heartLineView = (SurfaceView) findViewById(R.id.heartline_sf);
        mHolder = heartLineView.getHolder();
        cancleBt.setOnClickListener(this);

        actionBar = getActionBar();
        actionBar.setLogo(R.drawable.a1);
        actionBar.setTitle("心电");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void init() {
        mFileMan = getFileManager();

        mHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                drawBack();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new mThread().start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.heart_cancleBt:
                isRunning = false;
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BaseActivity.TIME_CHOOSE:
                if (resultCode == TimeChooseAty.TIME_RETURN) {
                    String date = data.getExtras().getString("date");
                    try {
                        new mThread(new SimpleDateFormat("yyyy-MM-dd").parse(date)).start();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                break;
            default:
                break;
        }
    }

    //接受时间参数，选取数据
    class mThread extends Thread {
        Date date;

        public mThread() {
            this(new Date());
        }

        public mThread(Date date) {
            this.date = date;
            isRunning = true;
        }

        @Override
        public void run() {
            //得到指定日期的数据包
            dataPac = mFileMan.getByteFile(MConfig.XinDian, date);
            for (int i = 0; i < dataPac.length; i++) {
                //				Log.d("xindian", dataPac[i]+"");
                //对负数进行，因为元数据为无符号类型
                if (dataPac[i] < 0) {
                    dataPac[i] = (byte) (dataPac[i] + 256);
                }
            }
            Canvas canvas;
            Paint paint = new Paint();
            Path mPath = new Path();

            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(3);
            paint.setPathEffect(new CornerPathEffect(10));
            paint.setStyle(Style.STROKE);
            //移到零点
            mPath.moveTo(0, 0);
            //数据下标
            int p = 0;
            //心电走完一屏的次数
            int pc = 0;
            //心电的数据虽然分为低八位与高八位，但是低八位对数据的影响可以忽略
            while (isRunning) {
                mPath.lineTo(p * 10 + 10, dataPac[pc * 100 + p] + dataPac[pc * 100 + p + 1] + 150);//加一百，因为看不到le。。。
                canvas = mHolder.lockCanvas(new Rect(10, 0, p * 10 + 10, 750));
                canvas.drawPath(mPath, paint);
                mHolder.unlockCanvasAndPost(canvas);
                p += 2;
                if (p > 100) {
                    p = 0;
                    pc++;
                    mPath.reset();
                    mPath.moveTo(10, dataPac[pc * 100 + p]);
                    drawBack();
                }
            }
            drawBack();
        }
    }

    private void drawBack() {
        final Paint paint = new Paint();
        final int xLenth = heartLineView.getWidth();
        final int yLenth = heartLineView.getHeight();
        //		final int xPoint = 10;
        //		final int yPoint = 10;
        final int xScale = 10;
        final int yScale = 10;
        //		Log.d("xLenth", "" + xLenth);
        //		Log.d("yLenth", "" + yLenth);
        Canvas canvas = mHolder.lockCanvas();
        canvas.drawColor(Color.parseColor("#EEEEEE"));
        //绘制框
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5.0f);
        paint.setStyle(Style.STROKE);
        canvas.drawRect(10, 0, xLenth - 10, yLenth - 10, paint);
        //竖线绘制
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1.0f);
        int currentX = 0;
        for (int i = 2; currentX < xLenth - 20; i++) {
            currentX = i * xScale;
            canvas.drawLine(currentX, yLenth - 12.5f, currentX, 2.5f, paint);
        }
        //横线绘制
        float currentY = 0;
        for (int j = 1; currentY < yLenth - 20; j++) {
            currentY = j * yScale + 2.5f;
            canvas.drawLine(12.5f, currentY, xLenth - 10, currentY, paint);
        }

        mHolder.unlockCanvasAndPost(canvas);
        mHolder.lockCanvas(new Rect(0, 0, 0, 0));//持久化，！！！！
        mHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    protected void onResume() {
        // 设置为横屏
//		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		}
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
