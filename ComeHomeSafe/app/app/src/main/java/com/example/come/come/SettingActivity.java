package com.example.come.come;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Network;
import com.example.come.come.network.ServerConstants;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.input_name)
    public EditText nameEdit;

    @BindView(R.id.input_address)
    public EditText addressEdit;

    @BindView(R.id.setting_input_phone)
    public EditText phoneEdit;

    @BindView(R.id.setting_input_emergency_phone)
    public EditText emergencyPhoneEdit;

    @BindView(R.id.backButton)
    public ImageButton backButton;

    @OnClick(R.id.backButton)
    public void onBackButtonClicked() {
        finish();
    }

    @BindView(R.id.btn_signup)
    public Button editButton;

    @OnClick(R.id.btn_signup)
    public void onSignUpClicked() {
        String name = nameEdit.getText().toString();
        String address = addressEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String emergencyPhone = emergencyPhoneEdit.getText().toString();

        HashMap<String, String> userData = Datas.getIDPW();

        if (userData == null) {
            Toast.makeText(SettingActivity.this, "null", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = userData.get(Datas.ID);

        if (!name.replaceAll(" ", "").equals("")) {
            Toast.makeText(SettingActivity.this, "이름", Toast.LENGTH_SHORT).show();
            changeInfo(id, "name", name);
        }

        if (!address.replaceAll(" ", "").equals("")) {
            Toast.makeText(SettingActivity.this, "주소", Toast.LENGTH_SHORT).show();
            changeInfo(id, "address", address);
        }

        if (!phone.replaceAll(" ", "").equals("")) {
            Toast.makeText(SettingActivity.this, "휴대폰", Toast.LENGTH_SHORT).show();
            changeInfo(id, "phone", phone);
        }

        if (!emergencyPhone.replaceAll(" ", "").equals("")) {
            Toast.makeText(SettingActivity.this, "휴대폰", Toast.LENGTH_SHORT).show();
            changeInfo(id, "emergency_phone", emergencyPhone);
            Datas.insertEmergencyPhone(SettingActivity.this, emergencyPhone);
        }
    }

    private ProgressDialog progressDialog;

    private void showDialog() {

        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog = ProgressDialog.show(this, "통신중입니다.", "통신중입니다.", true, false);
            }
        } else {
            progressDialog = ProgressDialog.show(this, "통신중입니다.", "통신중입니다.", true, false);
        }
    }

    private void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void changeInfo(String id, String column, String data) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        showDialog();
        HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.TYPE, ServerConstants.EDIT);
        param.put(ServerConstants.ID, id);
        param.put(ServerConstants.DATA, data);
        param.put(ServerConstants.COLUMN, column);

        Network.PersonalService personalService = retrofit.create(Network.PersonalService.class);
        Call<Network.PersonalRepo> call = personalService.call(param);

        call.enqueue(new Callback<Network.PersonalRepo>() {
            @Override
            public void onResponse(Call<Network.PersonalRepo> call, Response<Network.PersonalRepo> response) {
                closeDialog();
                if (response.isSuccessful()) {
                    if (ServerConstants.OK.equals(response.body().result)) {
                        Toast.makeText(SettingActivity.this, "성공!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingActivity.this, "실패!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Network.PersonalRepo> call, Throwable t) {
                closeDialog();
                Toast.makeText(SettingActivity.this, "실패!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "메뉴", Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
}
