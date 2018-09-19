package com.example.benedictlutab.sidelinetg.modules.changePassword;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.benedictlutab.sidelinetg.helpers.validationUtil;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class changePasswordActivity extends AppCompatActivity
{
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.etNewPassword) EditText etNewPassword;
    @BindView(R.id.etCurrentPassword) EditText etCurrentPassword;

    private static String NewPassword, Password, USER_ID;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);

        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    @OnClick(R.id.btnSubmit)
    public void validateFields()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        // Validate fields before passing data to REST API.
        if (!validationUtil.isValidPassword(etCurrentPassword)) {
            etCurrentPassword.setError("Invalid password pattern!");
            ERROR_COUNT = true;
        }
        if (!validationUtil.isValidPassword(etNewPassword)) {
            etNewPassword.setError("Invalid password pattern");
            ERROR_COUNT = true;
        }
        if (!ERROR_COUNT)
        {
            changePassword();
        }
    }

    public void changePassword()
    {
        // Fetch new password and current password.
        NewPassword = etNewPassword.getText().toString();
        Password = etCurrentPassword.getText().toString();

        // Init loading dialog.
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("");
        pDialog.setCancelable(false);

        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_CHANGE_PASS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        pDialog.hide();

                        String RESPONSE_CODE = ServerResponse.toString().replaceAll("\\s","");
                        Log.e("RESPONSE: ", RESPONSE_CODE);

                        // Display response.
                        if(RESPONSE_CODE.equals("Passwordsuccessfullychanged!"))
                        {
                            TastyToast.makeText(changePasswordActivity.this, "Password successfully changed!",TastyToast.LENGTH_LONG,TastyToast.SUCCESS).show();
                        }
                        else
                        {
                            TastyToast.makeText(changePasswordActivity.this, "Password is invalid or incorrect!",TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
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

                        TastyToast.makeText(changePasswordActivity.this, "Password successfully changed!",TastyToast.LENGTH_LONG,TastyToast.ERROR).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                // Sending all registration fields to 'Parameter'.
                Parameter.put("user_id", USER_ID);
                Parameter.put("password", Password);
                Parameter.put("new_password", NewPassword);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(changePasswordActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        pDialog.show();
    }
}
