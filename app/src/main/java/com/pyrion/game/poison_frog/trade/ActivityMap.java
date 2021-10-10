package com.pyrion.game.poison_frog.trade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pyrion.game.poison_frog.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActivityMap extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    private GoogleMap googleMap;
    LocationManager locationManager;
    Criteria criteria;
    Location userLocation;
    LatLng userLatLng;
    Location[] roadFrogs = new Location[5];
    String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //        내 위치 사용에 대한 동적 퍼미션
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkResult == PackageManager.PERMISSION_DENIED) {
                String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, 0);
            }
        }


// Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        비동기 방식(동시에 라는 뜻)으로 별도 스레드로 지도 데이터 읽어오도록 요척
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setCostAllowed(true); //비용지불 감수하고 best location provider
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//정확도
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);// 배터리 소모 신경안씀
        criteria.setAltitudeRequired(false); // 고도 고려 안함
        bestProvider = locationManager.getBestProvider(criteria, true);

        setRoadFrogList();

    }//create

    private void setRoadFrogList() {
//    todo 시간 보고 시간 지났으면 개구리 위치 전부 바꾸기

        //todo DB 만들기 및 체크

        if(true){
            userLocation = getCurrentUserLocation();
            for (int index = 0; index < roadFrogs.length; index++) {
                Location randomLocation = getRandomLatLng(userLocation, 100);
                roadFrogs[index] = randomLocation;
                //todo DB에 추가
            }
        }else{
            //todo 기존 DB의 개구리 사용

        }

    }

    public Location getRandomLatLng(Location location, int radius) {
        List<LatLng> randomPoints = new ArrayList<>();

        double x0 = location.getLatitude();
        double y0 = location.getLongitude();

        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111300f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLatitude = new_x + x0;
        double foundLongitude = y + y0;
        LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
        randomPoints.add(randomLatLng);
        Location l1 = new Location("");
        l1.setLatitude(randomLatLng.latitude);
        l1.setLongitude(randomLatLng.longitude);

        return l1;
    }


    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //todo 서버에서 사용자들의 개구리 가져오기
//        37.560797, 127.034571
        //랜덤 생성된 개구리 표시
        for (Location frogLocation : roadFrogs) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(frogLocation.getLatitude(), frogLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_frog))
                    .title(userLocation.distanceTo(frogLocation)+"m")
            );
            Log.i("loda", frogLocation.getLatitude() + "\n" + frogLocation.getLongitude());
        }
//        높을수록 확대:15 default
        googleMap.setMinZoomPreference(10.0f);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 15));

        UiSettings settings = googleMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);


    }

    public Location getCurrentUserLocation() {
        Location currentLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            currentLocation = null;
        }
        locationManager.requestLocationUpdates(bestProvider,500, 1,locationListener);
        if (locationManager.isProviderEnabled("gps")) {
            currentLocation = locationManager.getLastKnownLocation("gps");
        }else if(locationManager.isProviderEnabled("network")){
            currentLocation = locationManager.getLastKnownLocation("network");
        }

        if (currentLocation == null){
            Toast.makeText(this, "위치정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }else{

            Log.i("loda_user", currentLocation.getLatitude() + "\n" + currentLocation.getLongitude());
            return currentLocation;
        }
    return null;
    }


    //다이얼로그 선택하면 발동하는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//         허용안하면 샤용할 수 없도록 하기 앱 설치할 때 부터
        switch (requestCode){
            case 0:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // 위치 정보 제공에 동의한 경우
                }else{
                    Toast.makeText(this, "위치정보 사용 불가", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull @NotNull Location location) {

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//      1m 이내에 개구리 있으면 애니메이션 주기
        }
    };

}