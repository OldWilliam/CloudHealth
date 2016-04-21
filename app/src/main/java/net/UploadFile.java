package net;

import java.util.Date;

import util.MConfig;
import android.content.Context;
import util.FileManager;

public class UploadFile {
	private FileManager mFileMan;
	private byte[] pac;
	public UploadFile(Context context, Date date) {
		mFileMan = new FileManager(context);
		pac = mFileMan.getPackage(date);
		
		new NetConn(MConfig.UPLOAD_ADDRESS, HttpMethod.POST, pac,
				new NetConn.SucessCallback() {

					@Override
					public void onSuccess(String result) {
					}
				}, new NetConn.FailCallback() {
					@Override
					public void onFail(int code) {
					}
				});
	}

	public static interface SuccessCallback {
		void onSuccess();
	}

	public static interface FailCallback {
		void onFail();
	}
}
