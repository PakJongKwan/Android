package com.example.come.come;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    public EditText _emailText;

    @BindView(R.id.input_password)
    public EditText _passwordText;

    @BindView(R.id.btn_login)
    public Button _loginButton;

    @BindView(R.id.link_signup)
    public TextView _signupLink;

    @BindView(R.id.checkBox_autologin)
    public CheckBox autoLoginBox;

    @OnClick(R.id.btn_login)
    public void onLoginButtonClicked() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        login(email, password);

        _loginButton.setEnabled(true);
    }

    public void login(final String id, final String pw) {
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "", "서버와 통신중입니다.", true, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.ID, id);
        param.put(ServerConstants.PW, pw);

        Network.LoginService loginService = retrofit.create(Network.LoginService.class);
        Call<Network.LoginRepo> call = loginService.call(param);

        call.enqueue(new Callback<Network.LoginRepo>() {
            @Override
            public void onResponse(Call<Network.LoginRepo> call, Response<Network.LoginRepo> response) {
                progressDialog.dismiss();

                if(response.isSuccessful()) {
                    if (ServerConstants.OK.equals(response.body().result)) {

                        if (autoLoginBox.isChecked()) {
                            Datas.insertIDPW(id, pw);
                            Datas.saveLastestIDPW(LoginActivity.this, id, pw);
                            Datas.setAutoLogin(LoginActivity.this, true);
                        } else {
                            Datas.insertIDPW(id, pw);
                            Datas.setAutoLogin(LoginActivity.this, false);
                        }
                        onLoginSuccess();
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Network.LoginRepo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (Datas.isAutoLogin(LoginActivity.this)) {
            HashMap<String, String> id = Datas.getLastestIDPW(LoginActivity.this);

            if (id == null) {
                Toast.makeText(LoginActivity.this, "자동 로그인 실패", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, id.get(Datas.ID)  + " : "+ id.get(Datas.PW), Toast.LENGTH_SHORT).show();
                login(id.get(Datas.ID), id.get(Datas.PW));
            }
        }

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String password = _passwordText.getText().toString();

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}