package com.example.benedictlutab.sidelinetg.modules.messages.loadChatRooms;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.models.ChatRoom;
import com.example.benedictlutab.sidelinetg.models.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class chatRoomsFragment extends Fragment
{
    @BindView(R.id.rv_chatroom) RecyclerView recyclerView;
    @BindView(R.id.tvEmpty) TextView tvEmpty;
    @BindView(R.id.llShow) LinearLayout llShow;
    @BindView(R.id.llEmpty) LinearLayout llEmpty;

    private View rootView;
    private int listSize;
    private List<ChatRoom> chatRoomList = new ArrayList<>();

    private adapterChatRooms adapterChatRooms;
    private SharedPreferences sharedPreferences;

    private String USER_ID;

    public static chatRoomsFragment newInstance()
    {
        chatRoomsFragment chatRoomsFragment = new chatRoomsFragment();
        return chatRoomsFragment;
    }

    public chatRoomsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("onCreateView: ","STARTED!");

        rootView = inflater.inflate(R.layout.loadchatrooms_fragment_chat_rooms, container, false);
        ButterKnife.bind(this, rootView);

        // Get USER_ID
        sharedPreferences = getActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID: ", USER_ID);
        }

        // Change Font Style.
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/avenir.otf");
        tvEmpty.setTypeface(font);

        fetchChatRooms();

        return rootView;
    }

    private void initRecyclerView()
    {
        Log.e("initRecyclerView: ", "STARTED!");

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapterChatRooms = new adapterChatRooms(getActivity(), chatRoomList);
        recyclerView.setAdapter(adapterChatRooms);

        if (listSize == 0)
        {
            Log.e("initRecyclerView: ", "No messages loaded!");
            llShow.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("initRecyclerView: ", "Messages are loaded!");
            llShow.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void fetchChatRooms()
    {
        Log.e("fetchChatRooms: ", "STARTED!");

        apiRouteUtil apiRouteUtil = new apiRouteUtil();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOAD_CHAT_ROOMS, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    JSONArray jsonArray = new JSONArray(ServerResponse);
                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        // Adding the jsonObject to the List.
                        chatRoomList.add(new ChatRoom(jsonObject.getString("task_id"),
                                jsonObject.getString("tasker_id"),
                                jsonObject.getString("task_giver_id"),
                                jsonObject.getString("profile_picture"),
                                jsonObject.getString("first_name"),
                                jsonObject.getString("last_name"),
                                jsonObject.getString("title"))
                        );
                        listSize = chatRoomList.size();
                        Log.e("listSize: ", String.valueOf(listSize));
                    }
                    initRecyclerView();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("CATCH RESPONSE: ", e.toString());
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("task_giver_id", USER_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(stringRequest);
    }
}
