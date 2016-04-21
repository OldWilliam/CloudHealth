package util;

import net.HttpMethod;
import net.NetConn;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Geocoding {
	private String API;

	public Geocoding(String latitude, String longtitude,
			final SuccessCallback successCallback, FailCallback failCallback) {
		API = "http://api.map.baidu.com/geocoder/v2/?" +
				"ak=oRcVQepfRqzt371wGhzup8MV&" +
				"callback=renderReverse&" +
				"location="+latitude+","+longtitude+
				"&output=json&" +
				"pois=0";
		new NetConn(API, HttpMethod.GET, null, new NetConn.SucessCallback() {
			
			@Override
			public void onSuccess(String result) {
				int begin = result.indexOf('(');
				String sonString = result.substring(begin + 1, result.length()-1);
				try {
					JSONObject totalJson = new JSONObject(sonString);
//					int status = totalJson.getInt("status");
					JSONObject resultObject = totalJson.getJSONObject("result");
					String address = resultObject.getString("formatted_address");
					successCallback.onSucess(address);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("Geocoding", sonString);
			
			}
		}, new NetConn.FailCallback() {
			
			@Override
			public void onFail(int code) {
				Log.d("����λ�ý���ʧ��", "û�д����緵�����");
			}
		});
	}

	public static interface SuccessCallback {
		void onSucess(String address);
	}

	public static interface FailCallback {
		void onFail();
	}
}
