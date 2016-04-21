package bean;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ein on 2016/3/26.
 */
public class HealthItemBean {
    private int ivId;
    private String name;
    private String pro;

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getPro() {
        return pro;
    }

    public HealthItemBean(int ivId, String name) {
        this.ivId = ivId;
        this.name = name;
    }

    public int getIvResId() {
        return ivId;
    }
    public String getName() {
        return name;
    }
}
