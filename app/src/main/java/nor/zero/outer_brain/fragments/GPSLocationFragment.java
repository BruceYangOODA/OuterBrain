package nor.zero.outer_brain.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import nor.zero.outer_brain.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class GPSLocationFragment extends Fragment implements OnMapReadyCallback {

    private LocationManager locationManager;
    private Location location;
    private MyGpsListener myGpsListener;
    private GoogleMap googleMap;
    private UiSettings uiSettings;

    // LocationManager 距離最少移動多遠更新一次資料
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // LocationManager 時間最少經過多久更新一次資料
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private double latitude, longitude;


    private Button btnShowLocation;


    public GPSLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(myGpsListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps_location, container, false);

        btnShowLocation = view.findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(btnShowLocationClick);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map2);

        // fragment註冊 OnMapReadyCallback, 它的 onMapReady方法會回傳一個 GoogleMap 物件
        mapFragment.getMapAsync(this);


        // Inflate the layout for this fragment
        return view;
    }


    View.OnClickListener btnShowLocationClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            googleMap.clear();

        }
    };



    private void init() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        myGpsListener = new MyGpsListener();
        location = getLocation();
    }
    //取得目前GPS位置資訊,經度 與 緯度
    private Location getLocation(){
        Location result = null;
        // 獲得GPS 資訊連接許可 ACCESS_FINE_LOCATION 精確位置(GPS) ; ACCESS_COARSE_LOCATION 粗略位置(NET)
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},123);
        }
        else {  //已經獲得許可
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isNetworkEnabled){   //GPS位置資訊 從 網路訊號取得
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, myGpsListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(isGPSEnabled){   //GPS位置資訊 從 GPS訊號取得,GPS比較精確,如果GPS服務有開啟,優先從GPS訊號取得
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, myGpsListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {

        }
    }

    //OnMapReadyCallback method
    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        uiSettings = googleMap.getUiSettings();
        LatLng tainan = new LatLng(22.996261,120.218168);
        LatLng ggg = new LatLng(23.000935,120.218502);
        float zoomLevel = 16.0f;
        googleMap.addMarker(new MarkerOptions().position(tainan).title("Marker in Tainan"));
        googleMap.addMarker(new MarkerOptions().position(ggg).title("Marker in NCKU"));
    //    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tainan,zoomLevel));


        googleMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        if(location!= null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng center = new LatLng(latitude,longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,zoomLevel));
        }

    }



    private class MyGpsListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Log.v("aaa",lat + " : " +lng);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
