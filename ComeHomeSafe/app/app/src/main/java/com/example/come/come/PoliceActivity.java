package com.example.come.come;

import android.Manifest;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.come.come.network.Network;
import com.example.come.come.network.ServerConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PoliceActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "PoliceActivity";

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap = null;
    private Marker currentMarker = null;
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;

    @BindView(R.id.button_nearest_police)
    public Button policeButton;

    private class GetNearestPolice extends Thread {
        private HttpURLConnection httpURLConnection;
        private String data;

        private JSONObject jsonObject;
        private JSONArray results; ;
        private JSONObject nearestPolice;

        public GetNearestPolice(String location, String key) {
            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/place/search/json?location=" + location +"&rankby=distance&types=police&sensor=false&key="+ key);
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
            return readFully(inputStream).toString(encoding);
        }

        private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos;
        }

        @Override
        public void run() {

            try {
                httpURLConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            try {
                int code = httpURLConnection.getResponseCode();
                InputStream inputStream = httpURLConnection.getInputStream();

                this.data = readFullyAsString(inputStream, "utf-8");
                jsonInit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getData() {
            return data;
        }

        private void jsonInit() {
            try {
                jsonObject = new JSONObject(data);
                results = jsonObject.getJSONArray("results");
                nearestPolice = (JSONObject) results.get(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String centerName() {
            try {
                return nearestPolice.get("name").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public Double getCenterLatitude() {
            try {
                JSONObject geometryObject = (JSONObject) nearestPolice.get("geometry");
                JSONObject locationObject = (JSONObject) geometryObject.get("location");
                return locationObject.getDouble("lat");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Double getCenterLongtitude() {
            try {
                JSONObject geometryObject = (JSONObject) nearestPolice.get("geometry");
                JSONObject locationObject = (JSONObject) geometryObject.get("location");
                return locationObject.getDouble("lng");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @OnClick(R.id.button_nearest_police)
    public void onPoliceButtonClicked() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        double latitude = LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLatitude();
        double longtitude = LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLongitude();

        GetNearestPolice getNearestPolice = new GetNearestPolice(latitude + "," + longtitude, "AIzaSyC4_GXqV4rY02OZ80wtSMhAZlG4ab-A6uY");
        getNearestPolice.start();
        try {
            getNearestPolice.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        LatLng currentLocation = new LatLng(getNearestPolice.getCenterLatitude(),
                getNearestPolice.getCenterLongtitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title(getNearestPolice.centerName());
        markerOptions.snippet(getNearestPolice.centerName());
        markerOptions.draggable(false);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        if (googleMap != null) {
            currentMarker = googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);
        ButterKnife.bind(this);

        if (googleApiClient == null) {
            buildGoogleApiClient();
        }

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (googleApiClient != null) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(googleApiClient, this);
                googleApiClient.disconnect();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) {
            currentMarker.remove();
        }


        Log.e("location", "location : " + markerSnippet);
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = googleMap.addMarker(markerOptions);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));

    }


    public void setCurrentLocation(LatLng currentLocation, String markerTitle, String markerSnippet) {


        if (currentMarker != null) {
            currentMarker.remove();
        }


        Log.e("location", "location : " + markerSnippet);
        if (currentLocation != null) {

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = googleMap.addMarker(markerOptions);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));

    }

    @Override
    protected void onStop() {

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (googleApiClient != null && googleApiClient.isConnected()) {

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

            googleApiClient.disconnect();
        }

        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMyLocationEnabled(true);

        if (ActivityCompat.checkSelfPermission(PoliceActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(PoliceActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PoliceActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if (!googleMap.isMyLocationEnabled()) {
                googleMap.setMyLocationEnabled(true);
            }
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Log.e("Longtitude", String.valueOf(LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLongitude()));
        Log.e("Latitude", String.valueOf(LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLatitude()));

        double latitude = LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLatitude();
        double longtitude = LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLongitude();

        LatLng currentLocation = new LatLng(latitude, longtitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title("현재");
        markerOptions.snippet("현재");
        markerOptions.draggable(false);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        if (googleMap != null) {
            currentMarker = googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d( TAG, "onLocationChanged");

        String markerTitle = getCurrentAddress(location);
        String markerSnippet = "위도:"+String.valueOf(location.getLatitude())
                + " 경도:"+String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성
        setCurrentLocation(location, markerTitle, markerSnippet );
    }

    public String getCurrentAddress(Location location){

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

}