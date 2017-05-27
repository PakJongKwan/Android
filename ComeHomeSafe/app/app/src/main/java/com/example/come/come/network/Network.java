package com.example.come.come.network;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by dsm on 2017-05-15.
 */

public class Network {

    public interface LoginService {
        @POST(ServerConstants.LOGIN_HANDLER)
        Call<LoginRepo> call(@Body HashMap<String, String> parameter);
    }

    public class LoginRepo {
        @SerializedName(ServerConstants.RESULT)
        public String result;
    }

    public interface RegisterService {
        @POST(ServerConstants.REGISTER_HANDLER)
        Call<LoginRepo> call(@Body HashMap<String, String> parameter);
    }

    public class RegisterRepo {
        @SerializedName(ServerConstants.RESULT)
        public String result;
    }

    public interface PersonalService {
        @POST(ServerConstants.PERSONAL_HANDLER)
        Call<PersonalRepo> call(@Body HashMap<String, String> parameter);
    }

    public class PersonalRepo {
        @SerializedName(ServerConstants.RESULT)
        public String result;

        @SerializedName(ServerConstants.DATA)
        public List<PersonalData> data;
    }

    public class PersonalData {
        public String emergency_phone;
    }

    public interface UserSearchService {
        @POST(ServerConstants.USERSEARCH_HANDLER)
        Call<UserSearchRepo> call(@Body HashMap<String, String> parameter);
    }

    public class UserSearchRepo {
        @SerializedName(ServerConstants.RESULT)
        public String result;

        @SerializedName(ServerConstants.DATA)
        public List<UserSearchData> data;
    }

    public static class UserSearchData {
        public String id;
    }


    public interface FriendsService {
        @POST(ServerConstants.FRIENDS_HANDLER)
        Call<FriendsRepo> call(@Body HashMap<String, String> parameter);
    }

    public class FriendsRepo {
        @SerializedName(ServerConstants.RESULT)
        public String result;
    }

    public interface FriendsListService {
        @POST(ServerConstants.FRIENDS_LIST_HANDLER)
        Call<FriendsListRepo> call(@Body HashMap<String, String> parameter);
    }

    public class FriendsListRepo {
        @SerializedName(ServerConstants.RESULT)
        public String result;

        @SerializedName(ServerConstants.DATA)
        public List<FriendsListData> data;

    }

    public class FriendsListData {
        public String sender;
        public String receiver;
        public String accepted;
    }

    public interface LocationService {
        @POST(ServerConstants.LOCATION_HANDLER)
        Call<LocationRepo> call(@Body HashMap<String, String> parameter);
    }

    public class LocationRepo {
        @SerializedName(ServerConstants.RESULT)
        public String result;

        @SerializedName(ServerConstants.DATA)
        public List<LocationData> data;
    }

    public class LocationData {
        /*
        "x": "36.3917276",
      "y": "127.3633153",
      "_id": "10",
      "user": "blogcin"
         */
        public String x;
        public String y;
        public String _id;
        public String user;
    }

}
