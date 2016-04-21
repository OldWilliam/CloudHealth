/*
2015-8-26 ����1:43:08
 */
package service;

import java.io.File;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import activity.LocateAty;

import com.example.health_community.R;

public class ObeserveMessageService extends Service {
	Uri SMS_INBOX = Uri.parse("content://sms/");
	SmsObserver smsObserver;
	class SmsObserver extends ContentObserver{
		Context mContext;
		public SmsObserver(Handler handler) {
			super(handler);
		}
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			try {
				//ΪʲôҪͣһ��  ��Ϊ�����һ����Ļ�  ����ᱻֹͣ
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			saveSmsFromPhone();
		}
	}
	public void saveSmsFromPhone(){
		ContentResolver cr = getContentResolver();
//		String where = "address = '10086' AND date > " + (System.currentTimeMillis() - 10 * 1000);
		String where = "address = '+8618291013054'";
		Cursor cur = cr.query(SMS_INBOX, null, where, null, null);
		String reciveString = null;
		if (cur.moveToNext()){
//			String number = cur.getString(cur.getColumnIndex("address"));
//			String name = cur.getString(cur.getColumnIndex("person"));
			String body = cur.getString(cur.getColumnIndex("body"));
			reciveString = body;
		}
		Log.d("rext", reciveString);
		if (null == reciveString) {
			return;
		}
		int[] mark = new int[4];
		for(int i = 0, j = 0; i < reciveString.length(); i++){
			if (reciveString.charAt(i) == ',') {
				mark[j++] = i;
			}
		}
		String longtitude = reciveString.substring(mark[0]+1, mark[1]);
		String latitude = reciveString.substring(mark[2]+1);  
		if (reciveString.substring(0, 8).equals("Location")) {
			SharedPreferences.Editor editor = getSharedPreferences("coordinate", MODE_PRIVATE).edit();
			editor.putString("longtitude", longtitude);
			editor.putString("latitude", latitude);
			editor.commit();
			ObeserveMessageService.this.startForeground(1, notification);
		}

	}
	Notification notification;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		smsObserver = new SmsObserver(new Handler());
		getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
		notification = new Notification(R.drawable.ic_launcher, "notification comes", System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, LocateAty.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "title", "content", pi);
		long [] vibrates = {0, 1000, 1000, 1000};
		notification.vibrate = vibrates;
		Uri soundUri = Uri.fromFile(new File("/system/media/audio/ringtones/Angel.mp3"));
		notification.sound = soundUri;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
