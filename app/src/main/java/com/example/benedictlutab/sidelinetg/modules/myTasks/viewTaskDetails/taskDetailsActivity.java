package com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.modules.myTasks.myTaskList.myTasksFragment;
import com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskOffers.taskOffersActivity;
import com.example.benedictlutab.sidelinetg.modules.viewHome.homeActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class taskDetailsActivity extends AppCompatActivity
{
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnCancel) Button btnCancel;
    @BindView(R.id.btnViewOffers) Button btnViewOffers;

    @BindView(R.id.tvTaskTitle) TextView tvTaskTitle;
    @BindView(R.id.tvTaskGiver) TextView tvTaskGiver;
    @BindView(R.id.tvTaskPostedDate) TextView tvTaskPostedDate;
    @BindView(R.id.tvTaskAddress) TextView tvTaskAddress;
    @BindView(R.id.tvTaskDueDate) TextView tvTaskDueDate;
    @BindView(R.id.tvTaskFee) TextView tvTaskFee;
    @BindView(R.id.tvTaskCategory) TextView tvTaskCategory;
    @BindView(R.id.tvTaskDescription) TextView tvTaskDescription;
    @BindView(R.id.tvTaskStatus) TextView tvTaskStatus;

    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    @BindView(R.id.civTaskGiverPhoto) CircleImageView civTaskGiverPhoto;

    @BindView(R.id.vfTaskImages) ViewFlipper vfTaskImages;

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private Date postedDate = new Date();

    private String TASK_ID, USER_ID, TASK_STATUS, message;
    private SharedPreferences sharedPreferences;
    private String[] taskImages = new String[2];

    final apiRouteUtil apiRouteUtil = new apiRouteUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtaskdetails_activity_task_details);
        ButterKnife.bind(this);

        changeFontFamily();
        fetchPassedValues();

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        initSwipeRefLayout();
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    @OnClick(R.id.btnViewOffers)
    public void viewOffers()
    {
        Intent intent = new Intent(this, taskOffersActivity.class);
        intent.putExtra("TASK_ID", TASK_ID);
        startActivityForResult(intent, 420);
    }

    @OnClick(R.id.btnCancel)
    public void showCancelTaskPrompt()
    {
        Log.e("showCancelTaskPrompt: ", "STARTED!");

        // Show alert dialog (ACCEPT OR NOT)
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("ARE YOU SURE?")
                .setContentText(" Do you want to cancel this task? ")
                .setCancelText(" CANCEL ")
                .setConfirmText(" CONFIRM ")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        Log.e("showCancelTaskPrompt: ", "CANCEL!");
                        sDialog.hide();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        // Send POST Request to Cancel this task.
                        Log.e("showCancelTaskPrompt: ", "CONFIRMED!");
                        Log.e("showCancelTaskPrompt: ", "TASK STATUS - " + TASK_STATUS);

                        cancelTask(TASK_STATUS);
                    }
                })
                .show();
    }

    public void cancelTask(final String TASK_STATUS)
    {
        final String updateTaskStatus;

        Log.e("cancelTask: ", "STARTED!");
        if(TASK_STATUS.equals("ASSIGNED"))
        {
            updateTaskStatus = "CANCELLED (ASSIGNED)";
            Log.e("cancelTask: ", updateTaskStatus);
        }
        else
        {
            updateTaskStatus = "CANCELLED";
            Log.e("cancelTask: ", updateTaskStatus);
        }

        // Send POST Request.

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_CANCEL_TASK,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        swalDialog.hide();
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        try
                        {
                            if(SERVER_RESPONSE.equals("SUCCESS"))
                            {
                                TastyToast.makeText(getApplicationContext(), "Task successfully cancelled!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();

                                Intent intent = new Intent();
                                setResult(69, intent);
                                finish();
                            }
                            else
                            {
                                TastyToast.makeText(getApplicationContext(), "There has been an error in cancelling your task.", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            swalDialog.hide();
                            Log.e("cancelTask (CATCH): ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        // Showing error message if something goes wrong.
                        swalDialog.hide();
                        Log.e("Error Response:", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("status", updateTaskStatus);
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(taskDetailsActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode == 420)
        {
            // Refresh activity.
            initSwipeRefLayout();
        }
    }

    private void fetchPassedValues()
    {
        TASK_ID = getIntent().getStringExtra("TASK_ID");
        Log.e("fetchPValues: ", "Got the data!");
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchTaskDetails();
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
                fetchTaskDetails();
                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void fetchTaskDetails()
    {
        Log.e("fetchTaskDetails: ", "STARTED !");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_TASK_DETAILS, new Response.Listener<String>()
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

                        //Convert date to ...ago
                        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                        try
                        {
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
                            postedDate = simpleDateFormat.parse(jsonObject.getString("date_time_posted"));
                            tvTaskPostedDate.setText(prettyTime.format(postedDate));
                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }

                        // Load task giver's prof pic.
                        Picasso.with(taskDetailsActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).
                                fit().centerInside().into(civTaskGiverPhoto);

                        tvTaskTitle.setText(jsonObject.getString("title"));
                        tvTaskGiver.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name"));
                        tvTaskDescription.setText(jsonObject.getString("description"));
                        tvTaskAddress.setText(jsonObject.getString("line_one") +", "+ jsonObject.get("city"));
                        tvTaskDueDate.setText(jsonObject.getString("date_time_end"));
                        tvTaskCategory.setText(jsonObject.getString("category_name"));
                        tvTaskFee.setText("PHP " + jsonObject.getString("task_fee"));

                        TASK_STATUS = jsonObject.getString("status");

                        disableViewOffers(TASK_STATUS);

                        tvTaskStatus.setText(jsonObject.getString("status"));

                        // Fetch task photos
                        taskImages[0] = jsonObject.getString("image_one");
                        taskImages[1] = jsonObject.getString("image_two");

                        // Embed Photos
                        for (int i = 0; i < taskImages.length; i++)
                        {
                            setImageInViewFlipper(apiRouteUtil.DOMAIN + taskImages[i]);
                            Log.e("TASKIMGS: ", apiRouteUtil.DOMAIN + taskImages[i]);
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());

                }
                swipeRefLayout.setRefreshing(false);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.d("Error Response: ", volleyError.toString());
                        swipeRefLayout.setRefreshing(false);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("TASK_ID", TASK_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void disableViewOffers(String TASK_STATUS)
    {
        if(TASK_STATUS.equals("ASSIGNED"))
        {
            btnViewOffers.setVisibility(View.GONE);
        }
        else
        {
            btnViewOffers.setVisibility(View.VISIBLE);
        }
    }

    private void setImageInViewFlipper(String imgUrl)
    {

        ImageView image = new ImageView(getApplicationContext());
        Picasso.with(this).load(imgUrl).into(image);
        vfTaskImages.addView(image);

        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type's to ViewFlipper
        vfTaskImages.setInAnimation(in);
        vfTaskImages.setOutAnimation(out);

        // set interval time for flipping between views
        vfTaskImages.setFlipInterval(5000);
        // set auto start for flipping between views
        vfTaskImages.setAutoStart(true);
        vfTaskImages.startFlipping();
    }
}

