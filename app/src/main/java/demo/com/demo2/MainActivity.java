package demo.com.demo2;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;


import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import demo.com.demo2.overlayutil.DrivingRouteOverlay;


public class MainActivity extends AppCompatActivity {

    private LocationClient locationClient;// 定位SDK核心类

    public MyLocationListenner myListener = new MyLocationListenner();//定位监听

    private MapView mapView;//百度地图控件

    private BaiduMap baiduMap;//创建百度地图对象

    boolean isFirstLoc = true; // 是否首次定位
    RoutePlanSearch mSearch=null;
    public EditText editText1,editText2;
    double lat,lng;
    LatLng fromL1,endL1,ll,startPt,endPt;
    public double mylat,mylng;
   public BikeNaviLaunchParam param;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        editText1=(EditText)findViewById(R.id.edit_text1);
        editText2=(EditText)findViewById(R.id.edit_text2);
        mapView = (MapView) findViewById(R.id.bmapView);//获取百度地图控件
        baiduMap = mapView.getMap();//获取百度地图对象
        baiduMap.setMyLocationEnabled(true);// 开启定位图层
        locationClient = new LocationClient(this); //声明定位SDK核心类
        locationClient.registerLocationListener(myListener);//注册监听

        Button button=(Button)findViewById(R.id.show_locate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearch = RoutePlanSearch.newInstance();
                fromL1=new LatLng(mylat,mylng);
                endL1=new LatLng(lat,lng);
                OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

                    @Override
                    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

                    }

                    @Override
                    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

                    }

                    @Override
                    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

                    }

                    @Override
                    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

                        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                        }
                        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                            drivingRouteResult.getSuggestAddrInfo();
                            return;
                        }
                        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                            if (drivingRouteResult.getRouteLines().size() >= 1) {
//                        DrivingRouteLine route = drivingRouteResult.getRouteLines().get(0);
                                DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                                baiduMap.setOnMarkerClickListener(overlay);
                                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            } else {
                                Log.d("route result", "结果数<0");
                                return;
                            }
                        }

                    }

                    @Override
                    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

                    }

                    @Override
                    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

                    }
                };
                mSearch.setOnGetRoutePlanResultListener(listener);
                PlanNode stNode = PlanNode.withLocation(fromL1);
                PlanNode enNode = PlanNode.withLocation(endL1);
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
                mSearch.destroy();
            }
        });

        //显示第二个点
        ImageView imageView=(ImageView)findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lat=Double.parseDouble(editText1.getText().toString());
                lng=Double.parseDouble(editText2.getText().toString());
                baiduMap=mapView.getMap();
                baiduMap.setMapType(baiduMap.MAP_TYPE_NORMAL);
                LatLng point=new LatLng(lat,lng);
                BitmapDescriptor bitmap= BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
                OverlayOptions option=new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                baiduMap.clear();
                baiduMap.addOverlay(option);
            }
        });
        //移动到自己的位置
        ImageView imageView1=(ImageView)findViewById(R.id.imageView1) ;
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(ll)
                        .zoom(19)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                baiduMap.animateMapStatus(mMapStatusUpdate);
            }
        });
   /*     ImageView imageView3=(ImageView)findViewById(R.id.imageView3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPt = new LatLng(mylat,mylng);
                endPt = new LatLng(lat,lng);
                param = new BikeNaviLaunchParam().stPt(startPt).endPt(endPt).vehicle(1);
            }
        });*/
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        locationClient.start(); //开启定位
    }



    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mylat=location.getLatitude();
            mylng=location.getLongitude();
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                 ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(19.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
}
