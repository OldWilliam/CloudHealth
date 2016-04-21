package activity;

import com.example.health_community.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingAty extends Activity{
	private ImageButton start1, start2, start3;
	private TextView tv3;
	private ListView setLv;
	private final String[] str = {"修改密码","友情捐助","关于我们"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		setLv = (ListView) findViewById(R.id.setLv);
		setLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,str));
		setLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					showToast("���ڿ����������ڴ�");
					break;
				case 1:
					showToast("���ڿ����������ڴ�");
					break;
				case 2:
					startActivity(new Intent(SettingAty.this, AboutUs.class));
					break;

				}
			}
		});
		start3 = (ImageButton) findViewById(R.id.activity_img3);
		tv3 = (TextView) findViewById(R.id.tv3);
		tv3.setBackgroundColor(Color.WHITE);
		start3.setBackgroundColor(Color.WHITE);
		start1 = (ImageButton) findViewById(R.id.activity_img1);
		start1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingAty.this, HealthActivity.class);
				startActivity(intent);
				finish();
			}
		});
		start2 = (ImageButton) findViewById(R.id.activity_img2);
		start2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingAty.this, LocateAty.class);
				startActivity(intent);
			}
		});
		
			
	}

	private void showToast(String info) {
		Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
	}
	
	

}
