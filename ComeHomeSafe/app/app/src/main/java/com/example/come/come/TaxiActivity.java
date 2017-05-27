package com.example.come.come;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Message;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaxiActivity extends AppCompatActivity {

    @BindView(R.id.input_taxiNum)
    public EditText taxiInput;

    @BindView(R.id.sendButton)
    public ImageButton sendButton;

    @BindView(R.id.textView_taxi_status)
    public TextView texiStatus;

    @OnClick(R.id.sendButton)
    public void onSendButtonClicked() {
        String taxi = taxiInput.getText().toString();
        String emergencyPhone = Datas.getEmergencyPhone(this);

        if (rideStatus) {
            Datas.insertRide(TaxiActivity.this, false);
            Datas.clearTaxi(TaxiActivity.this);
            Datas.offTaxi(this);
            Message.sendSMS(this, emergencyPhone, taxi + " 택시 하차하였습니다!");
            finish();
        } else {
            Datas.insertRide(TaxiActivity.this, true);
            Datas.insertTaxi(TaxiActivity.this, taxi);
            Datas.onTaxi(this);
            Message.sendSMS(this, emergencyPhone, taxi + " 택시 탑승하였습니다!");
            finish();
        }
    }

    public boolean rideStatus;

    private final String RIDE_MODE = "탑승";
    private final String EXIT_MODE = "하차";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);
        ButterKnife.bind(this);

        rideStatus = Datas.getRide(this);

        if (rideStatus) {
            texiStatus.setText(EXIT_MODE);
            Toast.makeText(this, "하차선택 모드입니다.", Toast.LENGTH_SHORT).show();
        } else {
            texiStatus.setText(RIDE_MODE);
            Toast.makeText(this, "탑승선택 모드입니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
