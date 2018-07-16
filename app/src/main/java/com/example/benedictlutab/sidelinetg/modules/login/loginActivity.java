package com.example.benedictlutab.sidelinetg.modules.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class loginActivity extends AppCompatActivity
{
    @BindView(R.id.etRecoverAccount) EditText etRecoverAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_login);

        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/ralewayRegular.ttf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        // Make uneditable.
        etRecoverAccount.setFocusable(false);
    }
}
