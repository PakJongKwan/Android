package com.example.come.come;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Network;
import com.example.come.come.network.ServerConstants;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsAcceptActivity extends AppCompatActivity {

    @BindView(R.id.listview_friends_accept)
    public ListView acceptList;

    private FriendsListAdapter friendsListAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_accept);
        ButterKnife.bind(this);

        friendsListAdapter = new FriendsListAdapter(FriendsListAdapter.ACCEPT_MODE);
        acceptList.setAdapter(friendsListAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.ID, Datas.getIDPW().get(Datas.ID));
        param.put(ServerConstants.TYPE, ServerConstants.UNACCEPTED);

        Network.FriendsListService friendsListService = retrofit.create(Network.FriendsListService.class);
        Call<Network.FriendsListRepo> call = friendsListService.call(param);

        showProgressDialog();

        call.enqueue(new Callback<Network.FriendsListRepo>() {
            @Override
            public void onResponse(Call<Network.FriendsListRepo> call, Response<Network.FriendsListRepo> response) {
                closeProgressDialog();

                if (response.isSuccessful() && ServerConstants.OK.equals(response.body().result)) {
                    friendsListAdapter.clearItems();

                    for(Network.FriendsListData friendsListData : response.body().data) {
                        friendsListAdapter.addFriendsListItem(friendsListData);
                    }

                    friendsListAdapter.update();
                } else {
                    Toast.makeText(FriendsAcceptActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Network.FriendsListRepo> call, Throwable t) {
                closeProgressDialog();
                Toast.makeText(FriendsAcceptActivity.this, "실패", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, "통신", "서버와 통신중입니다.", true, false);
    }

    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
