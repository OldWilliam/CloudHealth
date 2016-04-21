/*
2015-10-6 ����9:52:42
*/
package activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.health_community.R;

import java.util.Date;

import util.FileManager;
import util.MConfig;

public class XueYangAty extends BaseActivity {
    private ActionBar actionBar;
    private Button chartBt;
    private TextView xYangTv, mBoTv;

    private FileManager mFileMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_xueyang);
        initViews();
        init();
    }

    @Override
    public void initViews() {
        actionBar = getActionBar();
        actionBar.setLogo(R.drawable.a0);
        actionBar.setTitle("血氧");
        actionBar.setDisplayHomeAsUpEnabled(true);
        xYangTv = (TextView) findViewById(R.id.xueyangTv);
        mBoTv = (TextView) findViewById(R.id.maiboTv);
        chartBt = (Button) findViewById(R.id.xueyang_chart_bt);
        chartBt.setOnClickListener(this);
    }

    public void init() {
        mFileMan = getFileManager();

        byte[] dataPac = mFileMan.getByteFile(MConfig.XueYang, new Date());
        for (int i = 0; i < dataPac.length; i++) {
            Log.d("number", dataPac[i] + "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BaseActivity.TIME_CHOOSE:
                if (resultCode == TimeChooseAty.TIME_RETURN) {
                    String date = data.getExtras().getString("date");
                    byte[] dataPac = mFileMan.getByteFile(MConfig.XueYang, date);
                    for (int i = 0; i < dataPac.length; i++) {
                        Log.d("number", dataPac[i] + "");
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xueyang_chart_bt:
                Intent i = new Intent(XueYangAty.this, TrendChartAty.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
