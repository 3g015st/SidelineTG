package com.example.benedictlutab.sidelinetg.helpers;

import android.app.Activity;
import android.util.Log;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Benedict Lutab on 10/11/2018.
 */

public class swalDialogUtil
{
    public Activity activity;

    public swalDialogUtil(Activity activity)
    {
        this.activity = activity;
    }

    public void showNetworkErrorDialog()
    {
        Log.e("showNetworkError:", "START!");
        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("Network Error").setContentText("It seems there is a problem in our servers please try again later :(")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // Exit application.
                        Log.e("ShowNetworkError: ", "Exiting...");
                        activity.finish();
                        System.exit(0);
                    }
                })
                .show();
    }

}
