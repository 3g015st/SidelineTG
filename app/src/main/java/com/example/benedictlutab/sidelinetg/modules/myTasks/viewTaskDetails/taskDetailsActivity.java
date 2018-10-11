package com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskDetails;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskOffers.taskOffersActivity;
import com.example.benedictlutab.sidelinetg.modules.viewTaskerProfile.taskerProfileActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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
    @BindView(R.id.btnMarkComplete) Button btnMarkComplete;

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

    @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;
    @BindView(R.id.tvTasker) TextView tvTasker;

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private Date postedDate = new Date();

    private String TASK_ID, USER_ID, TASK_STATUS, TASKER_ID, MOBILE_NUMBER;
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

    @OnClick(R.id.civTaskerPhoto)
    public void viewTaskerProfile()
    {
        Intent intent = new Intent(this, taskerProfileActivity.class);
        intent.putExtra("USER_ID", TASKER_ID);
        startActivity(intent);
        finish();
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
                fetchAssignedTasker();
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
                fetchAssignedTasker();

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
                        Picasso.with(taskDetailsActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE).fit().centerInside().into(civTaskGiverPhoto);

                        tvTaskTitle.setText(jsonObject.getString("title"));
                        tvTaskGiver.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name"));
                        tvTaskDescription.setText(jsonObject.getString("description"));
                        tvTaskAddress.setText(jsonObject.getString("line_one") +", "+ jsonObject.get("city"));
                        tvTaskDueDate.setText(jsonObject.getString("due_date"));
                        tvTaskCategory.setText(jsonObject.getString("category_name"));


                        TASK_STATUS = jsonObject.getString("status");

                        if(TASK_STATUS.equals("AVAILABLE"))
                        {
                            Log.e("TASL FEE:", "NO COMM FEE");
                            tvTaskFee.setText("PHP " + jsonObject.getString("task_fee"));
                        }
                        else
                        {
                            Log.e("TASL FEE:", "WITH COMM FEE");
                            Float OVERALL_FEE = Float.parseFloat(jsonObject.getString("task_fee")) + Float.parseFloat(jsonObject.getString("comm_fee")) ;
                            tvTaskFee.setText("PHP " + String.valueOf(OVERALL_FEE));
                        }

                        triggerVisibility(TASK_STATUS);

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

    private void fetchAssignedTasker()
    {
        Log.e("fetchAssignedTasker: ", "STARTED !");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_ASSIGNED_TASKER, new Response.Listener<String>()
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

                        // Load assigned tasker prof pic.
                        Picasso.with(taskDetailsActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).
                                fit().centerInside().into(civTaskerPhoto);

                        TASKER_ID = jsonObject.get("user_id").toString();
                        MOBILE_NUMBER = jsonObject.get("mobile_number").toString();
                        Log.e("TASKER-ID: ", TASKER_ID);
                        Log.e("MOBILE_NUMBER: ", MOBILE_NUMBER);
                        tvTasker.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name").substring(0, 1)+".");
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

    @OnClick(R.id.btnMarkComplete)
    public void markTaskCompleted()
    {
        Log.e("markTaskCompleted: ", "STARTED!");
        Log.e("MOBILE_NUMBER: ", MOBILE_NUMBER);

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        //Hide button for the mean time
        btnMarkComplete.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_MARK_COMPLETE, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                swalDialog.hide();
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    if(ServerResponse.contains("SUCCESS"));
                    {
                        Log.e("markTaskCompleted:", "TASK IS COMPLETED!!!");
                        TastyToast.makeText(getApplicationContext(), "Task successfully completed!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();

                        //Show button
                        btnMarkComplete.setVisibility(View.VISIBLE);

                        showPromptTaskCompleted();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());

                    //Show button
                    btnMarkComplete.setVisibility(View.VISIBLE);

                    swalDialog.hide();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("ERROR RESPONSE: ", volleyError.toString());

                        //Show button
                        btnMarkComplete.setVisibility(View.VISIBLE);

                        swalDialog.hide();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("mobile_number", MOBILE_NUMBER);
                Parameter.put("task_id", TASK_ID);
                Parameter.put("taskgiver", tvTaskGiver.getText().toString());

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void triggerVisibility(String TASK_STATUS)
    {
        if(TASK_STATUS.equals("AVAILABLE"))
        {
            btnViewOffers.setVisibility(View.VISIBLE);
            tvTasker.setText("NONE");
        }
        else
        {
            btnViewOffers.setVisibility(View.GONE);
        }

        if(!TASK_STATUS.equals("AVAILABLE"))
        {
            civTaskerPhoto.setVisibility(View.VISIBLE);
        }
        else
        {
            civTaskerPhoto.setVisibility(View.GONE);
        }

        if(TASK_STATUS.equals("ON-GOING"))
        {
            btnMarkComplete.setVisibility(View.VISIBLE);
        }
        else
        {
            btnMarkComplete.setVisibility(View.GONE);
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

    private void showPromptTaskCompleted()
    {
        Log.e("showPromptTskCompleted:", "START!");
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Task Completed").setContentText("You have marked the task as completed, please wait for the tasker to collect your payment.")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.hide();
                    }
                })
                .show();
    }
}

