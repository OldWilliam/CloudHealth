/*
2015-12-5 下午7:54:45
*/
package activity;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.example.health_community.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class TrendChartAty extends Activity{
	private LineChart mLineChart;
	private String charType;
	private String descText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trend_chart);

		initView();
		init();

	}

	private void init() {
		LineData mLineData = getLineData(30, 100);//调用getLineData，定义数据集大小
		mLineChart.setData(mLineData);//选择数据集(数据源包括所有需要显示的值和信息)
		mLineChart.animateX(20);//图标沿着x轴进行动态刷新
	}

	private void initView() {
		mLineChart = (LineChart) findViewById(R.id.line_chart);
		mLineChart.setDrawBorders(true);//设置边框
		mLineChart.setDescription("描述");//处于右下角框内
		mLineChart.setNoDataTextDescription("没有取得数据");//未获取到数据时的提示
		mLineChart.setDrawGridBackground(false);//表格颜色
		mLineChart.setGridBackgroundColor(Color.WHITE & 0x70ffffff);//表格背景色，&为设置透明度
		mLineChart.setTouchEnabled(true);//可触摸
		mLineChart.setDragEnabled(true);//可拖拉
		mLineChart.setScaleEnabled(false);//可缩放
		mLineChart.setPinchZoom(false);//x,y轴同步缩放
		mLineChart.setBackgroundColor(Color.rgb(114, 188, 223));//view的背景颜色

		//legend:图例、传奇、说明、刻印文字
		Legend mLegend = mLineChart.getLegend();//Class representing the legend of the chart.
		//The legend will contain one entry per color and DataSet.
		//Multiple colors in one DataSet are grouped together.
		//legend在setData()之前是无效的
		mLegend.setForm(LegendForm.CIRCLE);
		mLegend.setFormSize(2f);
	}

	/*
	 * 	count：the number of the data
	 * 	range: the range of the data
	 * 	
	 * 	@param count 
	 *  @param range 
	 */
	private LineData getLineData(int count, int range){
		ArrayList<String> xValues = new ArrayList<String>();//x轴数据集
        for (int i = 0; i < count; i++) {
            xValues.add(Integer.toString(i + 1));
        }
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

		ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
		lineDataSets.add(lineDataSet);
		/*
		 * 为什么y轴数据集已经构建好还要再加到list集合里面，不知道。看到源码，就算使用不包装的构造函数
		 * 它也会帮你包装。
		 * super(xVals, toList(dataSet));
		 */
		LineData linedata = new LineData(xValues, lineDataSets);
		return linedata;
	}
}
