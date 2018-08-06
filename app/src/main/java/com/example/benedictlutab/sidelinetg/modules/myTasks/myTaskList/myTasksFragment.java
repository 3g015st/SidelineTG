package com.example.benedictlutab.sidelinetg.modules.myTasks.myTaskList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
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
public class myTasksFragment extends Fragment
{
    @BindView(R.id.rv_mytasks) RecyclerView recyclerView;
    @BindView(R.id.tvItems) TextView tvItems;

    private View rootView;
    private int listSize;
    private List<Task> taskList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private String USER_ID;

    public static myTasksFragment newInstance()
    {
        myTasksFragment myTasksFragment = new myTasksFragment();
        return myTasksFragment;
    }

    public myTasksFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("onCreateView:","STARTED!");

        rootView = inflater.inflate(R.layout.mytasks_fragment_my_tasks, container, false);
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
        tvItems.setTypeface(font);

        // Behave like ViewPager
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        fetchMyTasks();

        return rootView;
    }

    private void initRecyclerView()
    {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapterMyTasks adapterMyTasks = new adapterMyTasks(getActivity().getApplicationContext(), taskList);
        recyclerView.setAdapter(adapterMyTasks);
        if (listSize == 0)
        {
            Log.e("initRecyclerView: ", "GONE-VISIBLE");
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            Log.e("initRecyclerView: ", "VISIBLE-GONE");
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);
                    int itemNo;
                    if(layoutManager.findFirstCompletelyVisibleItemPosition() != -1)
                    {
                        itemNo = layoutManager.findFirstCompletelyVisibleItemPosition() + 1;
                        Log.e("recyclerView: ", "CurrItem: "+Integer.toString(itemNo) + "TotalItems: "+taskList.size());

                        // Display current item no and all items no.
                        tvItems.setText(itemNo + " of " + taskList.size());
                    }
                }
            });
        }
    }

    private void fetchMyTasks()
    {
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_MY_TASKS, new Response.Listener<String>()
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
                        taskList.add(new Task(jsonObject.getString("task_id"),
                                jsonObject.getString("title"),
                                jsonObject.getString("image_one"),
                                jsonObject.getString("date_time_end"),
                                jsonObject.getString("address"),
                                jsonObject.getString("task_fee"),
                                jsonObject.getString("status"))
                        );
                        listSize = taskList.size();
                        Log.e("listSize: ", String.valueOf(listSize));
                    }
                    initRecyclerView();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("Catch Response: ", e.toString());
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.d("Error Response: ", volleyError.toString());
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
