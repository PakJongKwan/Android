package com.example.come.come;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Network;
import com.example.come.come.network.ServerConstants;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name)
    public EditText _nameText;

    @BindView(R.id.input_email)
    public EditText _emailText;

    @BindView(R.id.input_password)
    public EditText _passwordText;

    @BindView(R.id.input_address)
    public EditText _addressText;

    @BindView(R.id.input_phone)
    public EditText _phoneText;

    @BindView(R.id.input_emergency_phone)
    public EditText _emergencyPhoneText;

    @BindView(R.id.btn_signup)
    public Button _signupButton;

    @BindView(R.id.link_login)
    public TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String address = _addressText.getText().toString();
        String phone = _phoneText.getText().toString();
        final String emergencyPhone = _emergencyPhoneText.getText().toString();

        final ProgressDialog progressDialog = ProgressDialog.show(SignupActivity.this, "", "서버와 통신중입니다.", true, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Network.RegisterService registerService = retrofit.create(Network.RegisterService.class);

        HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.ID, email);
        param.put(ServerConstants.NAME, name);
        param.put(ServerConstants.PW, password);
        param.put(ServerConstants.ADDRESS, address);
        param.put(ServerConstants.NAME, name);
        param.put(ServerConstants.PHONE, phone);
        param.put(ServerConstants.EMERGENCY_PHONE, emergencyPhone);

        Call<Network.LoginRepo> call = registerService.call(param);

        call.enqueue(new Callback<Network.LoginRepo>() {
            @Override
            public void onResponse(Call<Network.LoginRepo> call, Response<Network.LoginRepo> response) {
                progressDialog.dismiss();
                onSignupSuccess();
            }

            @Override
            public void onFailure(Call<Network.LoginRepo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this, "회원가입 실패!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String address = _addressText.getText().toString();
        String phone = _phoneText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty()) {
            _emailText.setError("enter a valid email");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (address.isEmpty() || address.length() < 4 ) {
            _addressText.setError("enter a valid address");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 4 ){
            _phoneText.setError("enter a valid phone number");
            valid = false;
        } else {
            _phoneText.setError(null);
        }
        return valid;
    }
}