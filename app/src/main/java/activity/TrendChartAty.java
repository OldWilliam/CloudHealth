/*
2015-12-5 下午7:54:45
*/
package activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.health_community.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import adapter.MyXaxisValueFormatter;
import adapter.MyYAxisValueFormatter;
import util.MConfig;
import view.ChartMenuProvider;

public class TrendChartAty extends Activity implements ChartMenuProvider.OnSelectedListener {
    public static final String TAG = "TrendChart";

    private Button button;
    private MenuItem totalItem;

    private LineChart mLineChart;
    private LineData mDataOfMonth;
    private LineData mDataOfYear;

    private String charType;
    private String descText;

    private int datatype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.trend_chart);
        initView();
        init();
    }

    private void initView() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("健康");
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initChart();
    }

    private void initChart() {
        mLineChart = (LineChart) findViewById(R.id.line_chart);
        //尝试重写XAxis（x轴）改变x轴字体大小，原XAxis有最小字体为6dp的约束
//        MyXAxis mX = new MyXAxis();
//        mX.setTextSize(1f);
//        ViewPortHandler viewHandler = new ViewPortHandler();
//        Transformer tran = new Transformer(viewHandler);
//        XAxisRenderer xRender = new XAxisRenderer(viewHandler, mX, tran);
//        mLineChart.setXAxisRenderer(xRender);
        mLineChart.setDrawBorders(true);//设置边框
        mLineChart.setDescription("描述");//处于右下角框内
        mLineChart.setNoDataTextDescription("没有数据");//未获取到数据时的提示
        mLineChart.setNoDataText("");
        mLineChart.setDrawGridBackground(true);//表格颜色
        mLineChart.setGridBackgroundColor(Color.WHITE & 0x70ffffff);//表格背景色，&为设置透明度
        mLineChart.setTouchEnabled(true);//可触摸
        mLineChart.setDragEnabled(true);//可拖拉
        mLineChart.setScaleEnabled(false);//可缩放
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setPinchZoom(true);//x,y轴同步缩放
        mLineChart.setBackgroundColor(Color.rgb(114, 188, 223));//view的背景颜色
        mLineChart.setLogEnabled(true);
        mLineChart.setScaleXEnabled(true);
//        mLineChart.setMaxVisibleValueCount(10);//设置以后没有数据点上没有字了
//        mLineChart.setScaleX(10f);//所有东西x轴方向拉长，字体也变胖
//        mLineChart.setDragOffsetX(100f);//x轴左右产生空白，也没有图
//        mLineChart.setHighlightPerDragEnabled(true);
//        mLineChart.setClickable(true);//可剪切的


        //x轴的样式
        XAxis x = mLineChart.getXAxis();
        x.setTextColor(Color.RED);
        x.setTextSize(6f);//最小的字体，这样可以缩小x轴刻度值
        x.setSpaceBetweenLabels(0);//就是它！大幅缩小x轴刻度间的距离
        x.setDrawLabels(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setValueFormatter(new MyXaxisValueFormatter());

        //右y轴样式
        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);//右边y轴没有数据

        //左y轴样式
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setAxisMaxValue(150f);
//        leftAxis.setShowOnlyMinMax(true);//只显示0和100
        leftAxis.setLabelCount(30, true);
        leftAxis.setValueFormatter(new MyYAxisValueFormatter());//y轴数据单位

        //左下角数据标识，用来标识不同类型的数据（legend:图例、传奇、说明、刻印文字）
        //Class representing the legend of the chart.
        //The legend will contain one entry per color and DataSet.
        //Multiple colors in one DataSet are grouped together.
        //legend在setData()之前是无效的
        Legend mLegend = mLineChart.getLegend();
        mLegend.setForm(LegendForm.SQUARE);
        mLegend.setFormSize(6f);

        //一开始LineChart里面没有LineData,这里返回null
//        LineData lineData = mLineChart.getLineData();

        //初始化数据，x轴为12，一年12月
        List<String> xMonths = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            xMonths.add(Integer.toString(i));
        }
        mDataOfYear = new LineData(xMonths);

        //初始化数据，x轴为31,一个月最多31，y轴为空
        List<String> xDays = new ArrayList<String>();
        for (int i = 1; i <= 31; i++) {
            xDays.add(Integer.toString(i));
        }
        mDataOfMonth = new LineData(xDays);

        mLineChart.setData(mDataOfMonth);//选择数据集(数据源包括所有需要显示的值和信息)
        mLineChart.animateX(20);//图标沿着x轴进行动态刷新
        mLineChart.getLineData();
    }

    private void init() {
        Intent intent = getIntent();
        datatype = intent.getIntExtra("datatype", MConfig.PACKAGE);
        Toast.makeText(this, datatype + "", Toast.LENGTH_SHORT).show();

        button = (Button) findViewById(R.id.bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineDataSet dataSet = loadDataSet(20, 100);
                if (mDataOfMonth.getDataSetCount() != 0) {
                    mDataOfMonth.clearValues();
                }
                mDataOfMonth.addDataSet(dataSet);
                mLineChart.notifyDataSetChanged();
                mLineChart.animateX(20);
//                mLineChart.invalidate();
            }
        });
    }

    /*
     * 	count：the number of the data,xAxis
     * 	range: the range of the data,the max of the YAxis
     */

    private LineDataSet loadDataSet(int count, int range) {
        ArrayList<Entry> yValues = new ArrayList<Entry>();//y轴数据集
        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range);
            if (value >= 50) {
                value -= 35;
            } else {
                value += 20;
            }
            yValues.add(new Entry(value, i));
        }

        LineDataSet lineDataSet = new LineDataSet(yValues, "标签");//标签处于左下角框图外
        lineDataSet.setLineWidth(1.0f);//表的线条的宽度
        lineDataSet.setCircleSize(1.5f);//数据指示点的大小
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setHighLightColor(Color.WHITE);
        return lineDataSet;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //NullPointException
//            case android.R.id.home:
//                Intent upIntent = NavUtils.getParentActivityIntent(this);
//                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//                } else {
//                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    NavUtils.navigateUpTo(this,upIntent);
//                }
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.phy_para, menu);
        totalItem = menu.findItem(R.id.paraItem);
        totalItem.setIcon(getIconId(datatype));
        ChartMenuProvider provider = (ChartMenuProvider) totalItem.getActionProvider();
        provider.setOnSelectedListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSubMenuItemSelected(MenuItem item, int fileType) {
        totalItem.setTitle(item.getTitle());
        totalItem.setIcon(item.getIcon());
    }

    private int getIconId(int datatype) {
        int[] resIds = new int[]{R.drawable.ic_launcher, R.drawable.a0, R.drawable.a1
                , R.drawable.a2, R.drawable.a3, R.drawable.a4};
        return resIds[datatype];
    }
}
