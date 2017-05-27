package com.example.come.come;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Network;
import com.example.come.come.network.ServerConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.come.come.network.Datas.getIDPW;

/**
 * Created by dsm on 2017-05-19.
 */

public class FriendsListAdapter extends BaseAdapter {
    private ArrayList<Network.UserSearchData> userList;

    public static final int SEARCH_MODE = 1;
    public static final int LIST_MODE = 2;
    public static final int ACCEPT_MODE = 3;

    private String friendsX;
    private String friendsY;

    private int mode = 0;

    public FriendsListAdapter(int mode) {
        userList = new ArrayList<>();
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friendslist, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView_friendname);

        textView.setText(userList.get(position).id);

        switch (mode) {
            case SEARCH_MODE:
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(parent.getContext());
                        alert.setTitle("친구추가");
                        alert.setMessage("친구를 추가하시겠습니까?");
                        alert.setPositiveButton("친구추가", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                HashMap<String, String> data = Datas.getIDPW();
                                ProgressDialog progressDialog = ProgressDialog.show(parent.getContext(), "통신 중", "서버와 통신중입니다.", true, false);
                                addFriend(data.get(Datas.ID), userList.get(position).id, parent.getContext());
                                progressDialog.dismiss();
                                userList.remove(userList.get(position));
                                notifyDataSetChanged();
                            }
                        });
                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();

                    }
                });
                break;

            case LIST_MODE:
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parent.getContext(), FriendsLocationActivity.class);

                        getFriendsLocation(userList.get(position).id);

                        intent.putExtra(FriendsLocationActivity.LATITUDE, String.valueOf(friendsX));
                        intent.putExtra(FriendsLocationActivity.LONGTITUDE, String.valueOf(friendsY));
                        intent.putExtra(FriendsLocationActivity.NAME, userList.get(position).id);
                        parent.getContext().startActivity(intent);
                        Log.e("TAG", friendsX + " : " + friendsY);
                    }
                });

                break;
            case ACCEPT_MODE:
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(parent.getContext());
                        alert.setTitle("수락");
                        alert.setMessage("친구신청을 수락하시겠습니까?");
                        alert.setPositiveButton("친구 수락", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                ProgressDialog progressDialog = ProgressDialog.show(parent.getContext(), "통신 중", "서버와 통신중입니다.", true, false);
                                Toast.makeText(parent.getContext(), Datas.getIDPW().get(Datas.ID) + " : " + userList.get(position).id, Toast.LENGTH_SHORT).show();
                                acceptFriend(Datas.getIDPW().get(Datas.ID), userList.get(position).id, parent.getContext());
                                userList.remove(userList.get(position));
                                notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        });
                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();

                    }
                });
                break;
        }
        return convertView;


    }

    public void addSearchItem(Network.UserSearchData userSearchData) {
        userList.add(userSearchData);
    }

    public void addFriendsListItem(Network.FriendsListData friendsListData) {
        Network.UserSearchData userSearchData = new Network.UserSearchData();

        Log.e("TAG", friendsListData.receiver + " : " + friendsListData.sender);
        if (!Datas.getIDPW().get(Datas.ID).equals(friendsListData.receiver)) {
            Log.e("TAG", "Added");
            userSearchData.id = friendsListData.receiver;
            userList.add(userSearchData);
        } else if (!Datas.getIDPW().get(Datas.ID).equals(friendsListData.sender)) {
            userSearchData.id = friendsListData.sender;
            userList.add(userSearchData);
            Log.e("TAG", "Added");
        }
    }

    public void clearItems() {
        userList.clear();
    }

    public void update() {
        notifyDataSetChanged();
    }

    private void addFriend(String myId, String friendId, final Context context) {
        friendCommands(myId, friendId, ServerConstants.UNACCEPTED, context);
    }

    private void acceptFriend(String myId, String friendId, final Context context) {
        friendCommands(friendId, myId, ServerConstants.ACCEPTED, context);
    }

    private void getFriendsLocation(final String friendId) {
        Thread locationThread = new Thread(new Runnable() {

            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ServerConstants.SERVER_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                HashMap<String, String> param = new HashMap<>();
                param.put(ServerConstants.ID, friendId);
                param.put(ServerConstants.TYPE, ServerConstants.GET_LOCATION);

                Network.LocationService locationService = retrofit.create(Network.LocationService.class);
                Call<Network.LocationRepo> call = locationService.call(param);


                try {
                    Response<Network.LocationRepo> locationRepoResponse = call.execute();

                    if (locationRepoResponse.isSuccessful() && ServerConstants.OK.equals(locationRepoResponse.body().result)) {
                        if (locationRepoResponse.body().data.size() != 0) {
                            friendsX = locationRepoResponse.body().data.get(0).x;
                            friendsY =  locationRepoResponse.body().data.get(0).y;
                            Log.e("Data", friendsX + " : " + friendsY);
                        }
                    } else {
                        Log.e("Location", "Failed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        locationThread.start();
        try {
            locationThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void friendCommands(String myId, String friendId, String type, final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerConstants.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HashMap<String, String> param = new HashMap<>();
        param.put(ServerConstants.ID, myId);
        param.put(ServerConstants.DATA, friendId);
        param.put(ServerConstants.TYPE, type);

        Network.FriendsService friendsService = retrofit.create(Network.FriendsService.class);
        Call<Network.FriendsRepo> call = friendsService.call(param);

        call.enqueue(new Callback<Network.FriendsRepo>() {
            @Override
            public void onResponse(Call<Network.FriendsRepo> call, Response<Network.FriendsRepo> response) {
                if (response.isSuccessful()) {

                    if (ServerConstants.OK.equals(response.body().result)) {
                        Toast.makeText(context, "친구 추가 메시지 전송 성공", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "전송 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Network.FriendsRepo> call, Throwable t) {
                Toast.makeText(context, "전송 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
