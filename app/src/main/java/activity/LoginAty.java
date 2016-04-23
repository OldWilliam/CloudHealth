package activity;

import net.Login;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.health_community.R;

public class LoginAty extends Activity {
	private EditText accountEt, passwordEt;
	private Button loginBt, superLogin, oldBtn;
	private SharedPreferences spre;
	private SharedPreferences.Editor editor;
	private CheckBox rememberPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		spre = PreferenceManager.getDefaultSharedPreferences(this);
		accountEt = (EditText) findViewById(R.id.account);
		passwordEt = (EditText) findViewById(R.id.password);

		loginBt = (Button) findViewById(R.id.loginBt);
//		superLogin = (Button) findViewById(R.id.choosetimeBt);
//		oldBtn = (Button) findViewById(R.id.oldBtn);

		rememberPass = (CheckBox) findViewById(R.id.rememberPass);
		// sharedPreferences对象的get方法 第一参数为 键 第二个为 没有找到相应的键时要返回的值
		boolean isRmember = spre.getBoolean("remember_password", false);
		if (isRmember) {
			String account = spre.getString("account", "");
			String password = spre.getString("password", "");
			accountEt.setText(account);
			passwordEt.setText(password);
		}
		loginBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Log.d("LoginAty", "已点击登录按钮");
//				afterClick();
//				finish();
				startActivity(new Intent(LoginAty.this, HealthActivity2.class));
			}
		});
		
//		superLogin.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(LoginAty.this, HealthActivity2.class));
//			}
//		});

//		oldBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(LoginAty.this, HealthActivity.class));
//			}
//		});
	}

	private void afterClick() {
		final ProgressDialog pd = ProgressDialog.show(LoginAty.this, "登录中","");
		final String account = accountEt.getText().toString();
		final String password = passwordEt.getText().toString();

		editor = spre.edit();
		if (rememberPass.isChecked()) {
			editor.putBoolean("remember_password", true);
			editor.putString("account", account);
			editor.putString("password", password);
		} else {
			editor.clear();
		}
		editor.commit();
		new Login(account, password, new Login.SucessCallback() {
			@Override
			public void onSuccess() {
				pd.dismiss();
				showToast("��¼�ɹ�");
				Intent intent = new Intent(LoginAty.this,
						HealthActivity.class);
				startActivity(intent);
				LoginAty.this.finish();
			}
		}, new Login.FailCallback() {

			@Override
			public void onFail(int code) {
				pd.dismiss();
				switch (code) {
				case -1:
					showToast("连接异常，请检查网络连接");
					break;
				case 1:
					showToast("返回数据错误，服务器发生错误");
					break;
				case 2:
					showToast("帐号或密码错误");
					break;
				case 3:
//					showToast("密码错误");
					break;
				}
			}
		});
	}
	public void showToast(String info){
		Toast.makeText(LoginAty.this, info, Toast.LENGTH_SHORT).show();
	}
}
