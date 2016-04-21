package fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import util.Geocoding;
import util.TransCoord;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private BDLocation mBDlocation;
    private static final String TAG = "MapFragment";
    private Context atyContext;
    private Toast mToast;

    private boolean isRequest = false;// 是否手动触发请求定位
    private boolean isFirstLoc = true;// 是否首次定位

    private MapController mMapController = null;
    private BMapManager mBMapManager;
    private LocationClient mLocClient;

    private MapView mMapView = null;
    private PopupOverlay mPopupOverlay = null;
    private View viewCache;

    private Button request_me, request_old;

    public MapFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        atyContext = activity;
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        mBMapManager = new BMapManager(atyContext);
        mBMapManager.init("FEs03r3edXc0wmzb0ojGPFR6", new MKGeneralListenerImpl());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initView(view);
        init();
        return view;
    }

    private void initView(View view) {
        mMapView = (MapView) view.findViewById(R.id.bmapView);

        request_me = (Button) view.findViewById(R.id.map_request);
        request_old = (Button) view.findViewById(R.id.map_request2);
    }

    private void init() {
        request_old.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String longitude;
                String latitude;
//                SharedPreferences pref = atyContext.getSharedPreferences("coordinate", Context.MODE_PRIVATE);
//                longitude = pref.getString("longitude", "");
//                latitude = pref.getString("latitude", "");
                longitude = "116.394495";
                latitude = "39.888588";
                new TransCoord(latitude, longitude, new TransCoord.SuccessCallback() {

                    @Override
                    public void onSuccess(String latitude, String longitude) {
                        Log.d("转码", "成功");
                        mMapController.setZoom(14);
                        mMapController.animateTo(new GeoPoint((int) (Double.parseDouble(latitude) * 1E6), (int) (Double.parseDouble(longitude) * 1E6)));
                        showPopupOverlayFromShortMsg(longitude, latitude);
                    }
                }, new TransCoord.FailCallback() {

                    @Override
                    public void onFail() {
                        Log.d("转码", "失败");
                    }
                });
            }
        });

        request_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });

        mMapController = mMapView.getController();
        mMapController.enableClick(true); // 设置地图是否响应点击事件
        mMapController.setZoom(14); // 设置地图缩放级别
        mMapView.setBuiltInZoomControls(false); // 显示内置缩放控件

        viewCache = LayoutInflater.from(atyContext).inflate(R.layout.map_pop_layout, null);

        // 创建弹窗覆盖物实例
        mPopupOverlay = new PopupOverlay(mMapView, new PopupClickListener() {
            @Override
            public void onClickedPopup(int arg0) {
                // 点击 隐藏
                mPopupOverlay.hidePop();
            }
        });

        // 实例化定位服务，LocationClient类必须在主线程中声明
        mLocClient = new LocationClient(atyContext);
        mLocClient.registerLocationListener(new BDLocationListenerImpl());// 注册定位监听接口

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

        mLocClient.setLocOption(option);
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


    private void showPopupOverlay(BDLocation location) {
        TextView popText = ((TextView) viewCache.findViewById(R.id.location_tips));
        popText.setText("[我的位置]\n" + location.getAddrStr());
        mPopupOverlay.showPopup(getBitmapFromView(popText), new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6)), 10);
    }

    private void showPopupOverlayFromShortMsg(final String longitude, final String latitude) {
        final TextView popText2 = ((TextView) viewCache.findViewById(R.id.location_tips));
        new Geocoding(latitude, longitude, new Geocoding.SuccessCallback() {

            @Override
            public void onSucess(String address) {
                Log.d("解析地址", "成功");
                popText2.setText("[老人的位置]\n" + address);
                mPopupOverlay.showPopup(getBitmapFromView(popText2), new GeoPoint((int) (Double.parseDouble(latitude) * 1e6), (int) (Double.parseDouble(longitude) * 1e6)), 10);
            }
        }, new Geocoding.FailCallback() {

            @Override
            public void onFail() {
            }
        });
    }

    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
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

    // 定位接口，需要实现两个方法
    public class BDLocationListenerImpl implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mBDlocation = location;
            if (location == null) {
                return;
            }
            if (isFirstLoc || isRequest) {
                // 地图动画式加载到 定位到的位置
                mMapController.setZoom(18);
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

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(atyContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mLocClient.start(); // 调用此方法开始定位
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

}
