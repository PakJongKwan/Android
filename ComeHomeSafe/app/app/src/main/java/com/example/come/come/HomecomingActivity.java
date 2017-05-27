package com.example.come.come;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.come.come.network.Datas;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomecomingActivity extends AppCompatActivity {

    @BindView(R.id.home_seekBar)
    public SeekBar homeSeekBar;

    @BindView(R.id.backButton)
    public ImageView backButton;

    @BindView(R.id.sendButton)
    public ImageView sendButton;

    @BindView(R.id.textView_home_title)
    public TextView homeTitle;

    @OnClick(R.id.sendButton)
    public void onSendButtonClicked() {

        Intent intent = new Intent(HomecomingActivity.this, MessageService.class);

        if (rideStatus) {
            Toast.makeText(HomecomingActivity.this, "완료!", Toast.LENGTH_SHORT).show();
            Datas.insertRide(HomecomingActivity.this, false);
            stopService(intent);
        } else {
            Toast.makeText(HomecomingActivity.this, "완료!", Toast.LENGTH_SHORT).show();
            intent.putExtra(MessageService.ALERT_TIME, comeHomeTime);
            startService(intent);
            Datas.insertRide(HomecomingActivity.this, true);
        }
    }

    @OnClick(R.id.backButton)
    public void onBackButtonClicked() {
        finish();
    }

    private long comeHomeTime = 0;
    private final long HALF_HOUR = 90000;
    private boolean rideStatus = false;

    private final String RIDE_MODE = "출발";
    private final String EXIT_MODE = "도착";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homecoming);
        ButterKnife.bind(this);

        rideStatus = Datas.getRide(this);

        if (rideStatus) {
            homeTitle.setText(EXIT_MODE);
            Toast.makeText(this, "도착선택 모드입니다.", Toast.LENGTH_SHORT).show();
        } else {
            homeTitle.setText(RIDE_MODE);
            Toast.makeText(this, "출발선택 모드입니다.", Toast.LENGTH_SHORT).show();
        }

        homeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (rideStatus) {
                    Toast.makeText(HomecomingActivity.this, "도착모드는 선택이 불가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    long time = System.currentTimeMillis();
                    time += (HALF_HOUR * progress);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);

                    Toast.makeText(HomecomingActivity.this, calendar.get(Calendar.HOUR) + "시 " +
                            calendar.get(Calendar.MINUTE) + "분", Toast.LENGTH_SHORT).show();
                    comeHomeTime = time;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
