package com.example.benedictlutab.sidelinetg.modules.signup;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.benedictlutab.sidelinetg.R;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;

public class accountKitActivity extends AppCompatActivity
{
    public static int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity_account_kit);

        startPhoneVerification();
    }

    public void startPhoneVerification()
    {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);

        // Set default country code to PH.
        configurationBuilder.setDefaultCountryCode("PH");

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE && resultCode == RESULT_OK)
        {
            createUserAccount();
        }
    }

    private void getMobileNumber()
    {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>()
        {
            @Override
            public void onSuccess(final Account account)
            {
                String accountKitId = account.getId();

                // Get mobile number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                if (phoneNumber != null)
                {
                    String phoneNumberString = phoneNumber.toString();
                    Log.e("Fetched P.Number: ", phoneNumberString);
                }
            }

            @Override
            public void onError(final AccountKitError error)
            {
                Log.e("getMobileNumber: ", error.toString());
            }
        });
    }

    private void createUserAccount()
    {
        getMobileNumber();
    }
}
