package com.example.benedictlutab.sidelinetg.modules.viewHome;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.modules.viewHome.postTask.loadTaskCategories.taskCategoryFragment;

public class homeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewhome_activity_home);

        //Bottom navigation controls
        BottomNavigationView btmNavigationBar = findViewById(R.id.btmNavigationBar);
        btmNavigationBar.setOnNavigationItemSelectedListener (new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                FragmentManager fragmentManager = getSupportFragmentManager();
                switch (item.getItemId())
                {
                    case R.id.action_post_task:
                        fragmentManager.beginTransaction().replace(R.id.frmlayout_fragment, taskCategoryFragment.newInstance()).commit();
                        break;
                }
                return true;
            }
        });

        // Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frmlayout_fragment, taskCategoryFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
