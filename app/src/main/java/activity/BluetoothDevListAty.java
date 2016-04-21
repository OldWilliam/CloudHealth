package activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.health_community.R;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothDevListAty extends Activity {
	private ListView devLv;
	private BluetoothAdapter mAdapter;
	private Set<BluetoothDevice> pairedDevices;
	private List<String> devinfoList;
	private List<BluetoothDevice> devList;

	public final static int REQUEST_ENABLE = 2;
	public final static int SUCCESSED_RETURN_DEV = 3;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.blu_deviceslist);
        devLv = (ListView) findViewById(R.id.deviceLv);
        devList = new ArrayList<BluetoothDevice>();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        accessBluetooth();
		devLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BluetoothDevice device = devList.get(position);
				showToast(device.getName()+"\n"+device.getAddress());
				Intent data = new Intent();
				Bundle bundle = new Bundle();
				bundle.putParcelable("device", device);
				data.putExtras(bundle);
				setResult(SUCCESSED_RETURN_DEV, data);
				finish();
			}

		});
	}

	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void accessBluetooth() {
		if (null == mAdapter) {
			showToast("device not support");
			finish();
		}
		if (!mAdapter.isEnabled()) {
			mAdapter.enable();
		} else {
			showDev();
		}
	}

	private void showDev() {
		pairedDevices = mAdapter.getBondedDevices();
		devinfoList = new ArrayList<String>();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				devinfoList.add(device.getName() + "\n" + device.getAddress());
				devList.add(device);
			}
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.bluetooth_item, devinfoList);
		devLv.setAdapter(arrayAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE && resultCode == Activity.RESULT_OK) {
			showDev();
		}
	}

}
