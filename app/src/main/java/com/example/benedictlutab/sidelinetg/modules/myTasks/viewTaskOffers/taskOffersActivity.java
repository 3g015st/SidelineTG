package com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskOffers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Offer;

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

public class taskOffersActivity extends AppCompatActivity
{
    @BindView(R.id.rv_offers) RecyclerView recyclerView;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.tvOffers) TextView tvOffers;
    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    @BindView(R.id.tvEmpty) TextView tvEmpty;
    @BindView(R.id.llShow) LinearLayout llShow;
    @BindView(R.id.llEmpty) LinearLayout llEmpty;

    private String TASK_ID, USER_ID;
    private adapterDisplayOffers adapterDisplayOffers;

    private int listSize;
    private List<Offer> offerList = new ArrayList<>();

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtaskoffers_activity_taskoffers);
        ButterKnife.bind(this);
        replaceFontStyle();

        Bundle Extras = getIntent().getExtras();
        if (Extras != null)
        {
            TASK_ID = Extras.getString("TASK_ID");
            Log.e("TASK_ID: ", TASK_ID);
        }

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/avenir.otf");
        tvEmpty.setTypeface(font);

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
        Log.d("initRecyclerView: ", "STARTED!");

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapterDisplayOffers = new adapterDisplayOffers(this.getWindow().getContext(), offerList, USER_ID);
        recyclerView.setAdapter(adapterDisplayOffers);

        if (listSize == 0)
        {
            Log.e("initRecyclerView: ", "No task offers loaded!");
            llShow.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("initRecyclerView: ", "Offers loaded!");
            llShow.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }

        tvOffers.setText("OFFERS" +" ("+Integer.toString(listSize)+")");
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchTaskOffers();
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
                fetchTaskOffers();
                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void fetchTaskOffers()
    {
        Log.e("fetchTaskOffers  : ", "STARTED !");
        offerList.clear();

        apiRouteUtil apiRouteUtil = new apiRouteUtil();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_TASK_OFFERS, new Response.Listener<String>()
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
                        offerList.add(new Offer(jsonObject.getString("task_id"),
                                jsonObject.getString("tasker_id"),
                                jsonObject.getString("profile_picture"),
                                jsonObject.getString("first_name"),
                                jsonObject.getString("last_name"),
                                jsonObject.getString("amount"),
                                jsonObject.getString("message"),
                                jsonObject.getString("comm_fee"))
                        );
                        listSize = offerList.size();
                        Log.e("listSize: ", String.valueOf(listSize));
                    }
                    initRecyclerView();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("CATCH RESPONSE: ", e.toString());

                }
                swipeRefLayout.setRefreshing(false);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                        swipeRefLayout.setRefreshing(false);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
