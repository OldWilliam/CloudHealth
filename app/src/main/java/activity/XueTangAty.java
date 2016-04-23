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

public class XueTangAty extends BaseActivity {
    private ActionBar actionBar;
    private TextView xueTangTv;
    private Button chartBt;

    private FileManager mFileMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_xuetang);
        initViews();
        init();
    }

    @Override
    public void initViews() {
        actionBar = getActionBar();
        actionBar.setLogo(R.drawable.a2);
        actionBar.setTitle("血糖");
        actionBar.setDisplayHomeAsUpEnabled(true);

        xueTangTv = (TextView) findViewById(R.id.xuetangTv);
        chartBt = (Button) findViewById(R.id.xuetang_chart_bt);
        chartBt.setOnClickListener(this);

    }

    public void init() {
        mFileMan = getFileManager();
        byte[] dataPac = mFileMan.getByteFile(MConfig.XueTang, new Date());
        for (int i = 0; i < dataPac.length; i++) {
            Log.d("number", dataPac[i] + "");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xuetang_chart_bt:
                Intent i = new Intent(XueTangAty.this, TrendChartAty.class);
                i.putExtra("datatype", MConfig.XueTang);
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
                    byte[] dataPac = mFileMan.getByteFile(MConfig.XueTang, date);
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
