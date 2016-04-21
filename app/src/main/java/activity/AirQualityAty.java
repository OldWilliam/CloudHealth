
package activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.health_community.R;

import java.util.Date;

import util.FileManager;
import util.MConfig;
import view.DustDetector;

public class AirQualityAty extends BaseActivity {
    private ActionBar actionBar;
    private TextView numTv;
    private Button chartBt;
    private DustDetector dustView;

    private FileManager mFileMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_air);
        initViews();
        init();
    }

    @Override
    public void initViews() {
        dustView = (DustDetector) findViewById(R.id.dust_level);
        numTv = (TextView) findViewById(R.id.dust_number);
        chartBt = (Button) findViewById(R.id.aq_chart);
        chartBt.setOnClickListener(this);

        actionBar = getActionBar();
        actionBar.setLogo(R.drawable.a4);
        actionBar.setTitle("粉尘浓度");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void init() {
        mFileMan = getFileManager();
        dustView.setCallBack(new DustDetector.dustLevelCallBack() {
            @Override
            public void callBack(float level) {
                numTv.setText(String.format("%.2f",level));
            }
        });

        byte[] dataPac = mFileMan.getByteFile(MConfig.FenChen, new Date());
        for (int i = 0; i < dataPac.length; i++) {
            Log.d("number", dataPac[i] + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aq_chart:
                Intent i = new Intent(AirQualityAty.this, TrendChartAty.class);
                i.putExtra("dataType", MConfig.FenChen);
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
                    byte[] dataPac = mFileMan.getByteFile(MConfig.FenChen, date);
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
