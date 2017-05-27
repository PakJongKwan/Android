package com.example.come.come;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class FriendsListActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FriendsListAdapter friendsListAdapter;

    @BindView(R.id.listview_friends_list)
    public ListView friendsList;

    @BindView(R.id.imageButton_friend_accept)
    public ImageButton acceptButton;

    @BindView(R.id.ImageButton_friend_search)
    public ImageButton searchButton;

    @OnClick(R.id.imageButton_friend_accept)
    public void onAcceptButtonClicked() {
        startActivity(new Intent(FriendsListActivity.this, FriendsAcceptActivity.class));
    }

    @OnClick(R.id.ImageButton_friend_search)
    public void onFriendSearchButtonClicked() {
        startActivity(new Intent(FriendsListActivity.this, FriendsSearchActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        Toast.makeText(FriendsListActivity.this, "친구 목록입니다.", Toast.LENGTH_SHORT).show();

        friendsListAdapter = new FriendsListAdapter(FriendsListAdapter.LIST_MODE);
        friendsList.setAdapter(friendsListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUi(true);
    }

    private void refreshUi(final boolean noUI) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.TYPE, ServerConstants.ACCEPTED);
        param.put(ServerConstants.ID, Datas.getIDPW().get(Datas.ID));

        Network.FriendsListService friendsListService = retrofit.create(Network.FriendsListService.class);
        Call<Network.FriendsListRepo> call = friendsListService.call(param);

        if (!noUI) {
            showProgressDialog();
        }
        friendsListAdapter.clearItems();

        call.enqueue(new Callback<Network.FriendsListRepo>() {
            @Override
            public void onResponse(Call<Network.FriendsListRepo> call, Response<Network.FriendsListRepo> response) {
                if (!noUI) {
                    closeProgressDialog();
                }
                if (response.isSuccessful() && ServerConstants.OK.equals(response.body().result)) {
                    for(final Network.FriendsListData friendsListData : response.body().data) {
                        Toast.makeText(FriendsListActivity.this, friendsListData.receiver + " : " + friendsListData.sender, Toast.LENGTH_SHORT).show();

                        friendsListAdapter.addFriendsListItem(friendsListData);
                    }
                    friendsListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FriendsListActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Network.FriendsListRepo> call, Throwable t) {
                if (!noUI) {
                    closeProgressDialog();
                }
                Toast.makeText(FriendsListActivity.this, "실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, "통신", "통신 중 입니다.", true, false);
    }

    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
