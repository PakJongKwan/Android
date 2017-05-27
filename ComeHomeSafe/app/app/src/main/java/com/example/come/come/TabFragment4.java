package com.example.come.come;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class TabFragment4 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);
        final Context context = view.getContext();

        // 액티비티 전환
        ImageButton button1 = (ImageButton) view.findViewById(R.id.friendButton);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "친구 검색", Toast.LENGTH_LONG).show();

                // 액티비티 전환 코드
                Intent intent = new Intent(context, FriendsListActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
