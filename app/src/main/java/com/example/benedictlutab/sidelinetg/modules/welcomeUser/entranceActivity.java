package com.example.benedictlutab.sidelinetg.modules.welcomeUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.modules.login.loginActivity;
import com.example.benedictlutab.sidelinetg.modules.signup.signupActivity;
import com.example.benedictlutab.sidelinetg.modules.viewHome.homeActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class entranceActivity extends AppCompatActivity
{
    @BindView(R.id.etTerms) EditText etTerms;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnSignup) Button btnSignup;

    private String REQUEST = "CHECK_CONN";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomeuser_activity_entrance);
        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        // Make uneditable.
        etTerms.setFocusable(false);

        // Check network connection if available.
        if(!haveNetworkConnection())
        {
            // No network connection.
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Network Error").setContentText("It seems that there is an error in your connection, try again later :(")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // Exit application.
                            finish();
                        }
                    })
                    .show();
        }
        else
            {checkServerConnection();}
    }

    @OnClick({R.id.btnLogin, R.id.btnSignup, R.id.etTerms})
    public void setViewOnClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id.btnLogin:
                Intent intent_login = new Intent(entranceActivity.this, loginActivity.class);
                startActivity(intent_login);
                break;
            case R.id.btnSignup:
                Intent intent_su = new Intent(entranceActivity.this, signupActivity.class);
                startActivity(intent_su);
                break;
            case R.id.etTerms:
                apiRouteUtil apiRouteUtil = new apiRouteUtil();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apiRouteUtil.URL_TERMS));
                startActivity(browserIntent);
                break;
        }
    }

    private boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void checkServerConnection()
    {
        Log.e("checkServerConn:", "START!");
        // Disable buttons first.
        btnLogin.setEnabled(false);
        btnSignup.setEnabled(false);

        // Get route obj.
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_CHECK_CONNECTION,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        Log.e("RESPONSE: ", ServerResponse);

                        // If there is no connection to server,
                        if(ServerResponse.equals("ERROR"))
                        {
                            swalDialog.hide();
                           showNetworkError();
                        }
                        else
                        {
                            // Enable buttons
                            swalDialog.hide();
                            btnLogin.setEnabled(true);
                            btnSignup.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        // Showing error message if something goes wrong.
                        Log.e("Error Response:", volleyError.toString());
                        swalDialog.hide();
                        showNetworkError();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();
                // Sending all registration fields to 'Parameter'.
                Parameter.put("request", REQUEST);
                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(entranceActivity.this);
        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        swalDialog.show();
    }

    private void showNetworkError()
    {
        Log.e("showNetworkError:", "START!");
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Network Error").setContentText("It seems there is a problem in our servers please try again later :(")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // Exit application.
                        finish();
                    }
                })
                .show();
    }
}

