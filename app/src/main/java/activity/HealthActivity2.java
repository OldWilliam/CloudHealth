package activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.health_community.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MyViewPager;
import fragment.HealthFragment;
import fragment.MapFragment;
import fragment.SettingFragment;
import util.BluetoothClient;

/**
 * Created by ein on 2016/3/26.
 * <p/>
 * is that ok that A activity support so many things.blueteeth connection、(map views include network)、upload data
 */
public class HealthActivity2 extends FragmentActivity {
    private static final String TAG = "Health";
    /**
     * declare viewpager and RadioGroup
     */
    private RadioGroup naviBtns;
    private MyViewPager viewPager;



    private FragmentPagerAdapter fragPgAda;
    private ViewPager.OnPageChangeListener pageChgLister;
    private RadioGroup.OnCheckedChangeListener chekChgListener;

    private List<Fragment> frag_list;
    private int[] rbResId;
    /**
     * declare action bar
     */
    private LinearLayout title;
    private ImageButton blu_btn, syc_btn;
    private static TextView blu_tv;
    private TextView syc_tv;

    private AnimationDrawable syncAni;

    /**
     * declare blueteeth
     */
    private BluetoothDevice device;
    private BluetoothClient mbluthClient;

    public static final int BLUTEETH_REQUEST = 0;
    public static final int RETURN_DEV = 1;
    public static final int UPLOAD_FILE = 6;

    public static final int MESSAGE_STATE_CHANGE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;
    public static final int MESSAGE_WRITE = 5;


    public static final String DEVICE_NAME = "device";
    public static final String TOAST = "toast";

    /**
     * ide prompt me that I should declare the handler as a inner static class.Otherwise it will
     * prevent the GarbageCollection recycle this activity.
     * <p/>
     * then the problem comes.The static class request static variable and static field
     */
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothClient.STATE_CONNECTING:
                            blu_tv.setText(R.string.connecting);
                            break;
                        case BluetoothClient.STATE_CONNECTED:
                            blu_tv.setText(R.string.connected);
                            break;
                        case BluetoothClient.STATE_NONE:
                            blu_tv.setText(R.string.no_connect);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    break;
                case MESSAGE_TOAST:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RETURN_DEV:
                if (resultCode == BluetoothDevListAty.SUCCESSED_RETURN_DEV) {
                    /**
                     * get the choosed bluetooth device object
                     */
                    device = data.getExtras().getParcelable("device");
                    showToast("SelectedDeviceInfo\n" + device.getName() + device.getAddress());
                    /**
                     * perform bluetooth connect
                     */
                    mbluthClient.connect(device);
                }
                break;
            case BLUTEETH_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(HealthActivity2.this, BluetoothDevListAty.class);
                    startActivityForResult(intent,RETURN_DEV);
                }else{
                    showToast("打开蓝牙失败!");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_health_other);
        initViews();
        init();
        viewPager.setAdapter(fragPgAda);
        viewPager.setOnPageChangeListener(pageChgLister);
        naviBtns.setOnCheckedChangeListener(chekChgListener);
        naviBtns.check(rbResId[0]);
    }

    private void initViews() {
        /**
         * init the FrameWork combioned by ViewPager and RadioGroup
         */
        viewPager = (MyViewPager) findViewById(R.id.viewpager_main);
        naviBtns = (RadioGroup) findViewById(R.id.rg_navigate);
        /**
         * hasn't been used
         rb_health = (RadioButton) findViewById(R.id.nav_health);
         rb_locate = (RadioButton) findViewById(R.id.nav_locate);
         rb_setting = (RadioButton) findViewById(R.id.nav_setting);
         */
        HealthFragment frag0 = new HealthFragment();
        MapFragment frag1 = new MapFragment();
        SettingFragment frag2 = new SettingFragment();

        frag_list = new ArrayList<Fragment>();
        frag_list.add(frag0);
        frag_list.add(frag1);
        frag_list.add(frag2);
        /**
         * init actionbar views
         */
        title = (LinearLayout) findViewById(R.id.title_bar);
        blu_btn = (ImageButton) findViewById(R.id.ib_bluteeth_statu);
        blu_tv = (TextView) findViewById(R.id.tv_bluteeth_statu);
        syc_btn = (ImageButton) findViewById(R.id.ib_sync_data);
        syc_tv = (TextView) findViewById(R.id.tv_upload_statu);
    }

    private void init() {
        /**
         * load the variable adapter and listener
         */
        rbResId = new int[]{R.id.nav_health, R.id.nav_locate, R.id.nav_setting};
        /**
         * init bluetooth
         */
        mbluthClient = new BluetoothClient(getApplication(), mHandler);
        blu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i,BLUTEETH_REQUEST);
//                Intent intent = new Intent(HealthActivity2.this, BluetoothDevListAty.class);
//                startActivityForResult(intent, RETURN_DEV);
            }
        });

        syc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syc_btn.setBackgroundResource(R.drawable.sync_animation);
                AnimationDrawable sync = (AnimationDrawable) syc_btn.getBackground();
                sync.start();
                syc_tv.setText("同步中");
            }
        });
        /**
         * define the FragmentPagerAdapter for ViewPager
         */
        fragPgAda = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return frag_list.get(position);
            }

            @Override
            public int getCount() {
                return frag_list.size();
            }
        };

        /**
         * monitor view page change and set the map RadioButton checked when the map fragment show
         */
        pageChgLister = new ViewPager.OnPageChangeListener() {
            String TAG = "ViewPager";

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: ");
            }

            @Override
            public void onPageSelected(int position) {
                naviBtns.check(rbResId[position]);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        /**
         * monitor radiogroup;show the fragment when the user check the map RadioButton
         */
        chekChgListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < rbResId.length; i++) {
                    if (checkedId == rbResId[i]) {
                        viewPager.setCurrentItem(i);
                        RadioButton radioButton = (RadioButton) findViewById(checkedId);
                        radioButton.setTextColor(Color.RED);
                    } else {
                        RadioButton radioButton = (RadioButton) findViewById(rbResId[i]);
                        radioButton.setTextColor(Color.BLACK);
                    }
                }
            }
        };
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: ");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }

}
