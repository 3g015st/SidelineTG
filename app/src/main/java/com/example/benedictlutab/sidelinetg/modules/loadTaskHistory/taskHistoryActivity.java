package com.example.benedictlutab.sidelinetg.modules.loadTaskHistory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
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
import butterknife.OnClick;

public class taskHistoryActivity extends AppCompatActivity
{
    @BindView(R.id.rv_taskhistory) RecyclerView rv_taskhistory;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    @BindView(R.id.llShow) LinearLayout llShow;
    @BindView(R.id.llEmpty) LinearLayout llEmpty;

    private LinearLayoutManager layoutManager;

    private int listSize;
    private List<Task> taskHistoryList = new ArrayList<>();
    private adapterTaskHistory adapterTaskHistory;

    private String USER_ID;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadtaskhistory_activity_task_history);

        ButterKnife.bind(this);
        replaceFontStyle();

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        initSwipeRefLayout();
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    public void replaceFontStyle()
    {
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    private void initRecyclerView()
    {
        Log.d("listSize: ", String.valueOf(listSize));

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_taskhistory.setLayoutManager(layoutManager);

        adapterTaskHistory = new adapterTaskHistory(getApplicationContext(), taskHistoryList);
        rv_taskhistory.setAdapter(adapterTaskHistory);

        if (listSize == 0)
        {
            Log.e("initRecyclerView: ", "GONE-VISIBLE");
            llShow.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("initRecyclerView: ", "VISIBLE-GONE");
            llShow.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchTaskHistory();
            }
        });
        swipeRefLayout.setColorSchemeResources(R.color.colorPrimaryDark, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        swipeRefLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefLayout.setRefreshing(true);
                // Fetching data from server
                fetchTaskHistory();
                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void fetchTaskHistory()
    {
        Log.e("fetchTaskHistory: ", "STARTED !");
        taskHistoryList.clear();

        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_FETCH_TASK_HISTORY, new Response.Listener<String>()
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
                        taskHistoryList.add(new Task(jsonObject.getString("task_id"),
                                jsonObject.getString("title"),
                                jsonObject.getString("category_name"),
                                jsonObject.getString("date_time_end"))
                        );
                        listSize = taskHistoryList.size();
                        Log.e("listSize: ", String.valueOf(listSize));
                    }
                    initRecyclerView();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());
                    swipeRefLayout.setRefreshing(false);
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("Error Response: ", volleyError.toString());
                        swipeRefLayout.setRefreshing(false);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("USER_ID", USER_ID);
                Parameter.put("role", "Task Giver");

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);

        if (swipeRefLayout.isRefreshing()) {
            swipeRefLayout.setRefreshing(false);
        }
    }

}
