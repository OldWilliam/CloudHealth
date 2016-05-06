package adapter;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by ein on 2016/4/24.
 */
public class MyYAxisValueFormatter implements YAxisValueFormatter {
    private DecimalFormat mFormat;
    private int lastIndex;

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float v, YAxis yAxis) {
        return mFormat.format(v) + "$";
    }
}
