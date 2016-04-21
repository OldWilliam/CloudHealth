package activity;

import java.util.ArrayList;
import java.util.HashMap;

import service.UploadDataService;
import util.BluetoothClient;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.health_community.R;

public class HealthActivity extends Activity {

	private ListView lv;
	private TextView tabName;
	private ImageButton start1, start2, start3;
	private Button stauBtn, uploadBtn;

	private BluetoothDevice device;
	private BluetoothClient mRfcommClient;

	public static final int RETURN_DEV = 1;
	public static final int MESSAGE_STATE_CHANGE = 2;
	public static final int MESSAGE_DEVICE_NAME = 3;
	public static final int MESSAGE_TOAST = 4;
	public static final int MESSAGE_WRITE = 5;

	private final String[] dataType = { "血氧", "心电", "血糖", "体温", "空气质量",
			"脑电(待定)", "血压（待定)", "测试" };
	public static final String DEVICE_NAME = "device";
	public static final String TOAST = "toast";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothClient.STATE_CONNECTING:
					stauBtn.setText(R.string.connecting);
					break;
				case BluetoothClient.STATE_CONNECTED:
					stauBtn.setText(R.string.connected);
					break;
				case BluetoothClient.STATE_NONE:
					stauBtn.setText(R.string.no_connect);
				}
				break;
			case MESSAGE_DEVICE_NAME:
				showToast(msg.getData().getString(DEVICE_NAME));
				break;
			case MESSAGE_TOAST:
				showToast(msg.getData().getString(TOAST));
				break;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case BluetoothDevListAty.SUCCESSED_RETURN_DEV:
			device = data.getExtras().getParcelable("device");
			showToast("SelectedDeviceInfo\n" + device.getName() + " "
					+ device.getAddress());
			mRfcommClient.connect(device);
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_health);



		mRfcommClient = new BluetoothClient(getApplication(), mHandler);
		lv = (ListView) findViewById(R.id.dataTypeLv);
		tabName = (TextView) findViewById(R.id.tv1);
		tabName.setBackgroundColor(Color.WHITE);

		stauBtn = (Button) findViewById(R.id.statuBtn);
		// ????????
		stauBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HealthActivity.this,
						BluetoothDevListAty.class);
				startActivityForResult(intent, RETURN_DEV);
			}
		});
		// ??????
		uploadBtn = (Button) findViewById(R.id.sync_to_web);
		uploadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HealthActivity.this,
						UploadDataService.class);
				startService(intent);
			}
		});
		start1 = (ImageButton) findViewById(R.id.activity_img1);
		start1.setBackgroundColor(Color.WHITE);
		start2 = (ImageButton) findViewById(R.id.activity_img2);
		start2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HealthActivity.this, LocateAty.class);
				startActivity(intent);
				// finish();
			}
		});
		start3 = (ImageButton) findViewById(R.id.activity_img3);
		start3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HealthActivity.this, SettingAty.class);
				startActivity(intent);
				finish();
			}
		});


		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 7; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", 0x7f020000+i);
			map.put("ItemTitle", dataType[i]);
			
			listItem.add(map);
		}
		SimpleAdapter sAdapter = new SimpleAdapter(this, listItem,
				R.layout.item, new String[] { "ItemImage", "ItemTitle" },
				new int[] { R.id.itemImage, R.id.itemTitle });
		lv.setAdapter(sAdapter);
		// ????listview????????
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				// ???
				case 0:
					startActivity(new Intent(HealthActivity.this,
							XueYangAty.class));
					break;
				// ???
				case 1:
					startActivity(new Intent(HealthActivity.this,
							XinDianAty.class));
					break;
				// ???
				case 2:
					startActivity(new Intent(HealthActivity.this,
							XueTangAty.class));
					break;
				// ????
				case 3:
					startActivity(new Intent(HealthActivity.this,
							BodyTemperaAty.class));
					break;
				// ????????
				case 4:
					startActivity(new Intent(HealthActivity.this,
							AirQualityAty.class));
					break;
				// ???
				case 5:
					break;
				// ??
				case 6:
					break;
				// ????
				case 7:
					break;
				}
			}

		});
	}

	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
