package com.example.come.come;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Network;
import com.example.come.come.network.ServerConstants;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ProgressDialog progressDialog;

    private double lastestLongtitude = 0;
    private double lastestLatitude = 0;
    private LocationManager lm;
    @BindView(R.id.settingButton)
    public ImageButton settingButton;

    @OnClick(R.id.settingButton)
    public void onSettingButtonClicked() {
        Toast.makeText(getApplicationContext(), "계정 설정", Toast.LENGTH_LONG).show();

        // 액티비티 전환 코드
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setEmergencyData(Datas.getIDPW().get(Datas.ID));

        startService(new Intent(MainActivity.this, ShakeService.class));

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("안심귀가 지정"));
        tabLayout.addTab(tabLayout.newTab().setText("택시번호 지정"));
        tabLayout.addTab(tabLayout.newTab().setText("가까운 경찰서"));
        tabLayout.addTab(tabLayout.newTab().setText("친구 찾기"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        Toast.makeText(MainActivity.this, Datas.getIDPW().get(Datas.ID), Toast.LENGTH_SHORT).show();

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        sendLocation();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, ShakeService.class));
        super.onDestroy();
    }

    private void showDialog() {
        progressDialog = ProgressDialog.show(this, "통신중입니다.", "통신중입니다.", true, false);
    }

    private void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String setEmergencyData(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.TYPE, ServerConstants.GET);
        param.put(ServerConstants.ID, id);
        param.put(ServerConstants.COLUMN, "emergency_phone");

        Network.PersonalService personalService = retrofit.create(Network.PersonalService.class);
        Call<Network.PersonalRepo> call = personalService.call(param);
        showDialog();
        call.enqueue(new Callback<Network.PersonalRepo>() {
            @Override
            public void onResponse(Call<Network.PersonalRepo> call, Response<Network.PersonalRepo> response) {
                closeDialog();

                if (response.isSuccessful() && ServerConstants.OK.equals(response.body().result)) {
                    Datas.insertEmergencyPhone(MainActivity.this, response.body().data.get(0).emergency_phone);
                } else {
                    Toast.makeText(MainActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Network.PersonalRepo> call, Throwable t) {
                closeDialog();
                Toast.makeText(MainActivity.this, "실패", Toast.LENGTH_SHORT).show();
            }
        });
        return null;
    }

    private void sendLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            lastestLongtitude = location.getLongitude(); //경도
            lastestLatitude = location.getLatitude();   //위도

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ServerConstants.SERVER_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            HashMap<String, String> param = new HashMap<>();
            param.put(ServerConstants.ID, Datas.getIDPW().get(Datas.ID));
            param.put(ServerConstants.TYPE, ServerConstants.INSERT_LOCATION);
            param.put(ServerConstants.X, String.valueOf(lastestLatitude));
            param.put(ServerConstants.Y, String.valueOf(lastestLongtitude));

            Network.LocationService locationService = retrofit.create(Network.LocationService.class);
            Call<Network.LocationRepo> call = locationService.call(param);

            call.enqueue(new Callback<Network.LocationRepo>() {
                @Override
                public void onResponse(Call<Network.LocationRepo> call, Response<Network.LocationRepo> response) {
                    if (response.isSuccessful() && ServerConstants.OK.equals(response.body().result)) {
                        Log.e("Location", "Success");
                    } else {
                        Log.e("Location", "Failed");
                    }
                }

                @Override
                public void onFailure(Call<Network.LocationRepo> call, Throwable t) {
                    Log.e("Location", "Failed");
                }
            });

        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
}


