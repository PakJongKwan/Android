package com.example.come.come;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class FriendsSearchActivity extends AppCompatActivity {

    @BindView(R.id.listview_friends_search)
    public ListView friendsSearchList;

    @BindView(R.id.btn_search)
    public Button searchButton;

    @BindView(R.id.id_number)
    public EditText keywordEdit;

    private FriendsListAdapter friendsListAdapter;

    @OnClick(R.id.btn_search)
    public void onSearchButtonClicked() {
        showDialog();

        String keyword = keywordEdit.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.ID, keyword);

        Network.UserSearchService userService = retrofit.create(Network.UserSearchService.class);
        Call<Network.UserSearchRepo> call = userService.call(param);

        call.enqueue(new Callback<Network.UserSearchRepo>() {
            @Override
            public void onResponse(Call<Network.UserSearchRepo> call, Response<Network.UserSearchRepo> response) {
                String myId = Datas.getIDPW().get(Datas.ID);

                closeDialog();

                if (response.isSuccessful()) {

                    friendsListAdapter.clearItems();

                    if (ServerConstants.OK.equals(response.body().result)) {
                        for(Network.UserSearchData userSearchData : response.body().data) {
                            if (!userSearchData.id.equals(myId)) {
                                friendsListAdapter.addSearchItem(userSearchData);
                            }
                        }
                    } else {
                        Toast.makeText(FriendsSearchActivity.this, "실패!", Toast.LENGTH_SHORT).show();
                    }

                    friendsListAdapter.update();
                }
            }

            @Override
            public void onFailure(Call<Network.UserSearchRepo> call, Throwable t) {
                closeDialog();
                Log.e("ERROR!", t.getMessage());
                Toast.makeText(FriendsSearchActivity.this, "실패!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ProgressDialog progressDialog;

    private void showDialog() {
        progressDialog = ProgressDialog.show(this, "통신중입니다.", "통신중입니다.", true, false);
    }

    private void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_search);
        ButterKnife.bind(this);

        friendsListAdapter = new FriendsListAdapter(FriendsListAdapter.SEARCH_MODE);
        friendsSearchList.setAdapter(friendsListAdapter);
    }
}
