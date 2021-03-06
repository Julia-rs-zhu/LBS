package com.example.lbs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity2 extends AppCompatActivity {
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient;  //定义LocationClient
    private BDLocationListener myListener = new MyLocationListener();  //定义位置监听
    private boolean isFirstLoc = true;  //定义第一次启动
    private MyLocationConfiguration.LocationMode mCurrentMode;  //定义当前定位模式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //获取地图控件引用
        mapView = findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        //可省略
        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mLocationClient = new LocationClient(getApplicationContext());  //创建LocationClient类
        mLocationClient.registerLocationListener(myListener);   //注册监听函数
        initLocation();  //调用initLocation()方法，实现初始化定位
    }
    private void initLocation() {  //该方法实现初始化定位
        //创建LocationClientOption对象，用于设置定位方式
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");  //设置坐标类型
        option.setScanSpan(1000);      //1秒定位一次
        option.setOpenGps(true);      //打开GPS
        mLocationClient.setLocOption(option);  //保存定位参数与信息
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;  //设置定位模式
        //设置自定义定位图标
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.dingwei);
        //位置构造方式，将定位模式，定义图标添加其中
        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
        baiduMap.setMyLocationConfiguration(config);  //地图显示定位图标
    }
    public class MyLocationListener implements BDLocationListener{  //设置定位监听器
        @Override
        public void onReceiveLocation(BDLocation location) {
            //销毁后不再处理新接收的位置
            if (location == null || mapView == null)
                return;
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {  //如果是第一次定位,就定位到以自己为中心
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude()); //获取用户当前经纬度
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);  //更新坐标位置
                baiduMap.animateMapStatus(u);                            //设置地图位置
                isFirstLoc = false;                                      //取消第一次定位
            }
        }
    }
    protected void onStart() {  //地图定位与Activity生命周期绑定
        super.onStart();
        baiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
    }
    @Override
    protected void onStop() {  //停止地图定位
        super.onStop();
        baiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView = null;
        mapView.onDestroy();
    }
}
