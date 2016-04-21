/*
2015-10-7 ����12:33:05
 */
package activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.health_community.R;

import java.util.Date;

import util.FileManager;
import util.MConfig;
import view.Thermometer;

public class BodyTemperaAty extends BaseActivity {
    private ActionBar actionBar;
    private TextView valueTv;
    private Button chartBt;

    private Thermometer thermometer;
    private FileManager mFileMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_body_temp);
        initViews();
        init();
    }

    @Override
    public void initViews() {

        valueTv = (TextView) findViewById(R.id.bd_tem_tv);
        chartBt = (Button) findViewById(R.id.bd_temp_chart_bt);
        chartBt.setOnClickListener(this);
        thermometer = (Thermometer) findViewById(R.id.thermometer);
        thermometer.setTempCallback(new Thermometer.tempCallback() {
            @Override
            public void callBack(double tem) {
                valueTv.setText(String.format("%.1f", tem));
            }
        });
        actionBar = getActionBar();
        actionBar.setLogo(R.drawable.a3);
        actionBar.setTitle("体温");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void init() {
        mFileMan = getFileManager();

        byte[] dataPac = mFileMan.getByteFile(MConfig.TiWen, new Date());
        for (int i = 0; i < dataPac.length; i++) {
            Log.d("number", dataPac[i] + "");
        }
        valueTv.setText("37");
        valueTv.setTextColor(Color.parseColor("#FFD700"));
        valueTv.setTextSize(25);

        thermometer.setTem(37);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bd_temp_chart_bt:
                Intent i = new Intent(BodyTemperaAty.this, TrendChartAty.class);
                startActivity(i);
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
                    byte[] dataPac = mFileMan.getByteFile(MConfig.TiWen, date);
                    for (int i = 0; i < dataPac.length; i++) {
                        Log.d("number", dataPac[i] + "");
                    }
                }

                break;
            default:
                break;
        }
    }
}
