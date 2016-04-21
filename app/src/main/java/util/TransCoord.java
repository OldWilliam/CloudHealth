package util;

import net.HttpMethod;
import net.NetConn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TransCoord {
	private static String BD_changePosition_API;

	public TransCoord(String latitude, String longtitude,  final SuccessCallback sucessCallback, final FailCallback failCallback) {
		BD_changePosition_API = "http://api.map.baidu.com/geoconv/v1/?coords="
				+ longtitude + "," + latitude
				+ "&from=1&to=5&ak=oRcVQepfRqzt371wGhzup8MV";
		new NetConn(BD_changePosition_API, HttpMethod.GET, null, new NetConn.SucessCallback() {

			@Override
			public void onSuccess(String result) {
				JSONObject total;
				try {
					total = new JSONObject(result);
					Log.d("transcoord respond statu code", total.getInt("status") + "");
					JSONArray array1 = total.getJSONArray("result");
					JSONObject result1 = array1.getJSONObject(0);
					if (result1.getString("x") != null && result1.getString("y") != null) {
						sucessCallback.onSuccess(result1.getString("y"), result1.getString("x"));
					}
					else {
						failCallback.onFail();
					}
					Log.d("X", result1.getString("x"));
					Log.d("Y", result1.getString("y"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new NetConn.FailCallback() {
			@Override
			public void onFail(int code) {
				Log.d("Trans Coord Fail", "no data response");
			}
		});

	}

	public static interface SuccessCallback {
		void onSuccess(String latitude, String longtitude);
	}

	public static interface FailCallback {
		void onFail();
	}
}
