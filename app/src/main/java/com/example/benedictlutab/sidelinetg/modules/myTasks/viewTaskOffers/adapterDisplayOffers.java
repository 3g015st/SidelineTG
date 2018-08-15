package com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskOffers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Offer;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Benedict Lutab on 8/6/2018.
 */

public class adapterDisplayOffers extends RecyclerView.Adapter<adapterDisplayOffers.ViewHolder>
{
    private Context context;
    private List<Offer> offerList;
    private String USER_ID;

    public adapterDisplayOffers(Context context, List<Offer> offerList, String USER_ID)
    {
        this.context = context;
        this.offerList = offerList;
        this.USER_ID = USER_ID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.viewtaskoffers_layout_rv_offers, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        Offer offer = offerList.get(position);

        // Bind data.
        holder.TASKER_ID = offer.getTasker_id();
        holder.TASK_ID   = offer.getTask_id();
        holder.amount    = offer.getAmount();

        holder.tvTaskerName.setText(offer.getFirst_name() +" "+ offer.getLast_name().substring(0, 1));
        holder.tvNumReviews.setText(offer.getReviews() + " Reviews");
        holder.tvAmount.setText("PHP " + offer.getAmount());
        holder.tvMessage.setText(offer.getMessage());
        holder.ratingBar.setRating(Float.parseFloat(offer.getRating() + 2.5f));

        holder.IMAGE_URL = apiRouteUtil.DOMAIN + offer.getProfile_picture();
        Log.e("IMAGE URL: ", holder.IMAGE_URL);

        //Bind fetched image url from server
        Picasso.with(context).load(holder.IMAGE_URL).fit().centerInside().into(holder.civTaskerPhoto);

        holder.llAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Go to Task Details.
                Log.e("TASKER ID: ", holder.TASKER_ID);

                // Show alert dialog (ACCEPT OR NOT)
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setTitleText("ARE YOU SURE?")
                        .setContentText(" Do you want to accept the offer and assign " + holder.tvTaskerName.getText().toString() +" for this task? ")
                        .setCancelText(" CANCEL ")
                        .setConfirmText(" ASSIGN ")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.hide();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                               // Send POST Request to Accept Tasker
                                Log.e("ACCEPT-TASKER ID: ", holder.TASKER_ID);
                                Log.e("ACCEPT-TASK ID: ", holder.TASK_ID);
                                Log.e("ACCEPT-AMOUNT: ", holder.tvAmount.getText().toString());
                                assignTasker(holder.TASKER_ID, holder.TASK_ID, holder.amount);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return offerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvTaskerName) TextView tvTaskerName;
        @BindView(R.id.tvNumReviews) TextView tvNumReviews;
        @BindView(R.id.tvAmount) TextView tvAmount;
        @BindView(R.id.tvMessage) TextView tvMessage;
        @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;
        @BindView(R.id.ratingBar) RatingBar ratingBar;
        @BindView(R.id.llAccept) LinearLayout llAccept;

        private String TASK_ID, TASKER_ID, IMAGE_URL, amount;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void assignTasker(final String TASKER_ID, final String TASK_ID, final String amount)
    {
        Log.e("sendOffer:", "START!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_ASSIGN_TASKER,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        if(SERVER_RESPONSE.equals("SUCCESS"))
                        {
                            // Exit this activity then prompt success
                            swalDialog.hide();
                            TastyToast.makeText(context, "Tasker successfully assigned!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            Intent intent = new Intent();
                            ((Activity)context).setResult(420, intent);
                            ((Activity)context).finish();
                        }
                        else
                        {
                            // Prompt error
                            swalDialog.hide();
                            TastyToast.makeText(context, "There has been an error in assigning the tasker!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
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

                Parameter.put("tasker_id", TASKER_ID);
                Parameter.put("task_id", TASK_ID);
                Parameter.put("task_fee", amount);
                Parameter.put("task_giver_id", USER_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }
}
