package adapter;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by ein on 2016/4/24.
 */
public class MyXaxisValueFormatter implements XAxisValueFormatter {
    private int lastIndex;
    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    @Override
    public String getXValue(String s, int i, ViewPortHandler viewPortHandler) {
        if (i == 30) {
            return s  + "日";
        }
        return s;
    }
}
