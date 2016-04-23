package adapter;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by ein on 2016/4/23.
 */
public class MyXAxis extends XAxis {
    @Override
    public void setTextSize(float size) {
        this.mTextSize = Utils.convertDpToPixel(size);
    }
}
