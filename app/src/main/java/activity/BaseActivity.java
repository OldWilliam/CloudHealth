package activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.health_community.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import application.MyApplication;
import util.FileManager;


/**
 * Created by ein on 2016/4/4.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener{
    public static final String TAG = "BaseActivity";

    public static final int TIME_CHOOSE = 0;
    private MenuItem timeItem;
    protected abstract void initViews();
    protected abstract void init();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        timeItem = menu.findItem(R.id.time);
        timeItem.setTitle(new SimpleDateFormat("yyyy-M-dd").format(new Date()));
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * the same logic, initial the time menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.time) {
            Intent intent = new Intent(this, TimeChooseAty.class);
            startActivityForResult(intent, TIME_CHOOSE);
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        if (requestCode == TIME_CHOOSE) {
            if (resultCode == TimeChooseAty.TIME_RETURN) {
                String date = data.getStringExtra("date");
                if (date != null) {
                    timeItem.setTitle(data.getStringExtra("date"));
                }
            }else{
                return;
            }
        }
    }

    protected FileManager getFileManager(){
        MyApplication app = (MyApplication) getApplication();
        return app.getFileManager();
    }
}
