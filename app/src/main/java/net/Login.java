package net;

import android.util.Log;
import util.MConfig;

public class Login {
	public Login(String username, String password, final SucessCallback sucessCallback, final FailCallback failCallback) {
		new NetConn(MConfig.SERVER_ADDRESS, HttpMethod.POST, null, new NetConn.SucessCallback() {
			
			@Override
			public void onSuccess(String result) {
				int code = 1024;
				try {
					code = Integer.parseInt(result);
				} catch (NumberFormatException e) {
					Log.d("Login$onFail", "data cant resolve"+result);
					failCallback.onFail(1);
				}
				if (code == 0) {
					Log.d("Login$onFail", "login success");
					sucessCallback.onSuccess();
				}
				else {
					if (code < 10) {
						Log.d("Login$onFail", "login fail statucode"+code);
						failCallback.onFail(code);
					}
				}
			}
		}, new NetConn.FailCallback() {
			
			@Override
			public void onFail(int code) {
				failCallback.onFail(code);
			}
		}, MConfig.KEY_USERNAME, username, MConfig.KEY_PASSWORD, password);
	}
	
	public static interface SucessCallback{
		void onSuccess();
	}

	public static interface FailCallback{
		void onFail(int code);
	}
}
