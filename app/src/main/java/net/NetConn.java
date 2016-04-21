package net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import android.os.AsyncTask;
import android.util.Log;

public class NetConn {
	public NetConn(final String url, final HttpMethod method,
			final byte[] buffer, final SucessCallback sucessCallback,
			final FailCallback failCallback, final String... kvs) {
		Log.d("NetConn", "perform network");
		if (buffer != null) {
			AjaxParams params = new AjaxParams();
			params.put("data", new ByteArrayInputStream(buffer));//upload byte stream

			FinalHttp fh = new FinalHttp();
			fh.post(url, params, new AjaxCallBack<Object>() {
				@Override
				public void onLoading(long count, long current) {
				}

				@Override
				public void onSuccess(Object t) {
					
				}

			});
		} else {
			new AsyncTask<Void, Void, String>() {
				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					if (null != result) {
						Log.d("NetConn$result", result);
						sucessCallback.onSuccess(result);
					} else {
						Log.d("NetConn$onFail", "result=null  return code = -1");
						failCallback.onFail(-1);
					}
				}

				@Override
				protected String doInBackground(Void... voids) {
					URLConnection connection = null;
					StringBuilder revData = new StringBuilder();
					StringBuilder paramStr = new StringBuilder();
					for (int i = 0, j = 0; i < kvs.length; i += 2, j++) {
						if (j > 0) {
							paramStr.append("&");
						}
						paramStr.append(kvs[i]).append("=").append(kvs[i + 1]);
					}
					switch (method) {
					case POST:
						try {
							Log.d("NetConn$POST", "get http connect object");
							connection = new URL(url).openConnection();
							Log.d("Net$POST", "has got http connect object");
							connection.setDoOutput(true);
							connection.setDoInput(true);
							int timeout = 3000;
							connection.setConnectTimeout(timeout);
							connection.setReadTimeout(timeout);

							Log.d("NetConn$POST", "open post paramers outputstream");
							BufferedWriter bw = new BufferedWriter(
									new OutputStreamWriter(
											connection.getOutputStream()));
							Log.d("Net$POST", "has open output stream send KV");
							bw.write(paramStr.toString());
							bw.flush();
						} catch (IOException e1) {
							Log.d("IO", "catched");
							return null;
						}
						break;

					default:
						try {
							Log.d("NetConn$GET", "get http commect object");
/*
 *
 * http://api.map.baidu.com/geoconv/v1/?coords=108.901680,34.156968&from=1&to=5&ak=oRcVQepfRqzt371wGhzup8MV?
 */
//							connection = new URL(url + "?"
//									+ paramStr.toString()).openConnection();
							connection = new URL(url).openConnection();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					}
					try {
						Log.d("NetConn", "get inputstream");
						InputStream is = connection.getInputStream();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(is));
						String line = null;
						while ((line = br.readLine()) != null) {
							revData.append(line);
						}
						br.close();
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return revData.toString();
				}

			}.execute();
		}
	}

	public static interface SucessCallback {
		void onSuccess(String result);
	}

	public static interface FailCallback {
		void onFail(int code);
	}
}
