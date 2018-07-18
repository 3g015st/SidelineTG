package com.example.benedictlutab.sidelinetg.modules.welcomeUser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.modules.login.loginActivity;
import com.example.benedictlutab.sidelinetg.modules.signup.signupActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class entranceActivity extends AppCompatActivity
{
    @BindView(R.id.etTerms) EditText etTerms;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnSignup) Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomeuser_activity_entrance);
        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/ralewayRegular.ttf");
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
}
