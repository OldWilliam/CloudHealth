package activity;

import service.ObeserveMessageService;
import util.Geocoding;
import util.TransCoord;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.health_community.R;


public class LocateAty extends Activity{
	private ImageButton start1, start2, start3;
	private TextView tv2;

	private Toast mToast;

	private boolean isRequest = false;// 是否手动触发请求定位
	private boolean isFirstLoc = true;// 是否首次定位

	private MapController mMapController = null;
	private BMapManager mBMapManager;
	private LocationClient mLocClient;

	private MapView mMapView = null; // 基本地图
	private PopupOverlay mPopupOverlay = null;// 弹出泡泡图层，浏览节点时使用
	private View viewCache;

	// 定位接口，需要实现两个方法
	public class BDLocationListenerImpl implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			if (isFirstLoc || isRequest) {
				// 地图动画式加载到 定位到的位置
				mMapController.animateTo(new GeoPoint((int) (location
						.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6)));
				// 弹出图层
				showPopupOverlay(location);
				isRequest = false;
			}
			isFirstLoc = false;
		}


		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 使用地图sdk前需先初始化BMapManager，这个必须在setContentView()先初始化
		mBMapManager = new BMapManager(this);
		// 第一个参数是API key,
		// 第二个参数是常用事件监听，用来处理通常的网络错误，授权验证错误等
		mBMapManager.init("FEs03r3edXc0wmzb0ojGPFR6",//百度地图定位ak，可用20151208
				new MKGeneralListenerImpl());
		setContentView(R.layout.map);
		Log.d("avtivity", "onCreatExecuted");
		// 点击按钮手动请求定位

		((Button) findViewById(R.id.request)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences("coordinate", MODE_PRIVATE);
				String longtitude = pref.getString("longtitude", "");
				String latitude = pref.getString("latitude", "");
				new TransCoord(latitude, longtitude,new TransCoord.SuccessCallback() {

					@Override
					public void onSuccess(String latitude,String longtitude) {
						Log.d("转码", "成功");
						mMapController.animateTo(new GeoPoint((int) (Double.parseDouble(latitude) * 1E6),(int) (Double.parseDouble(longtitude) * 1E6)));
						showPopupOverlayFromShortMsg(longtitude, latitude);
					}
				}, new TransCoord.FailCallback() {

					@Override
					public void onFail() {
						Log.d("转码", "失败");
					}
				});

			}
		});

		((Button) findViewById(R.id.request2)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				requestLocation();
			}
		});

		mMapView = (MapView) findViewById(R.id.bmapView); // 获取百度地图控件实例
		mMapController = mMapView.getController(); // 获取地图控制器
		mMapController.enableClick(true); // 设置地图是否响应点击事件
		mMapController.setZoom(14); // 设置地图缩放级别
		mMapView.setBuiltInZoomControls(true); // 显示内置缩放控件
		viewCache = LayoutInflater.from(this)
				.inflate(R.layout.map_pop_layout, null);

		// 创建弹窗覆盖物实例
		mPopupOverlay = new PopupOverlay(mMapView, new PopupClickListener() {
			@Override
			public void onClickedPopup(int arg0) {
				// 点击结果为 隐藏
				mPopupOverlay.hidePop();
			}
		});

		// 实例化定位服务，LocationClient类必须在主线程中声明
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(new BDLocationListenerImpl());// 注册定位监听接口
		// 回调处理方法

		// 设置定位参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.disableCache(false);// 禁止启用缓存定位
		// option.setPoiNumber(5); //最多返回POI个数
		// option.setPoiDistance(1000); //poi查询距离
		// option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
		mLocClient.setLocOption(option);// 装载参数
		// 不调用此方法相当于未开启定位客户端 LocationClient
		mLocClient.start(); // 调用此方法开始定位
	}

	private void showPopupOverlay(BDLocation location) {
		TextView popText = ((TextView) viewCache.findViewById(R.id.location_tips));
		popText.setText("[我的位置]\n" + location.getAddrStr());
		mPopupOverlay.showPopup(getBitmapFromView(popText),	new GeoPoint((int) (location.getLatitude() * 1e6),(int) (location.getLongitude() * 1e6)), 10);
	}

	private void showPopupOverlayFromShortMsg(final String longtitude, final String latitude) {
		final TextView popText2 = ((TextView) viewCache.findViewById(R.id.location_tips));
		new Geocoding(latitude, longtitude, new Geocoding.SuccessCallback() {

			@Override
			public void onSucess(String address) {
				Log.d("解析地址", "成功");
				popText2.setText("[老人的位置]\n" + address);
				mPopupOverlay.showPopup(getBitmapFromView(popText2),new GeoPoint((int) (Double.parseDouble(latitude) * 1e6), (int) (Double.parseDouble(longtitude) * 1e6)), 10);
			}
		}, new Geocoding.FailCallback() {

			@Override
			public void onFail() {
			}
		});
	}

	// 重新回调BDLocationListener里的onRecive。。。
	public void requestLocation() {
		isRequest = true;

		if (mLocClient != null && mLocClient.isStarted()) {
			showToast("正在定位......");
			mLocClient.requestLocation();
		} else {
			Log.d("LocSDK3", "locClient is null or not started");
		}
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onResume();
		super.onResume();
		Log.d("activity", "resume");

	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
		Log.d("activity", "pause");

	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.destroy();

		// 退出应用调用BMapManager的destroy()方法
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}

		// 退出时销毁定位
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onDestroy();
		Log.d("activity", "destory");
	}


	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}


	public static Bitmap getBitmapFromView(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}


	public class MKGeneralListenerImpl implements MKGeneralListener {

		/**
		 * 一些网络状态的错误处理回调函数
		 */
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				showToast("您的网络出错啦！");
			}
		}

		/**
		 * 授权错误的时候调用的回调函数
		 */
		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				showToast("API KEY错误, 请检查！");
			}
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("avtivity1", "stop excuted");
		Intent intent = new Intent(this, ObeserveMessageService.class);
		startService(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("avti", "restart excuted");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("avtivity3", "start excuted");
		Intent intent = new Intent(this, ObeserveMessageService.class);
		stopService(intent);
	}

}
