package com.example.benedictlutab.sidelinetg.modules.more;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.benedictlutab.sidelinetg.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class moreFragment extends Fragment
{

    private ArrayList<String> arrlistItemNames = new ArrayList<>();

    public static moreFragment newInstance()
    {
        moreFragment moreFragment = new moreFragment();
        return moreFragment;
    }

    public moreFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.more_fragment_more, container, false);

        Log.d("moreFragment","onCreateView: on");
        initItemNames();

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView_id);
        adapterMore adapterMore = new adapterMore(getActivity(), arrlistItemNames);
        recyclerView.setAdapter(adapterMore);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        return rootView;
    }

    private void initItemNames()
    {
        Log.d("moreFragment","initItemNames: on");
        arrlistItemNames.add("Change Password");
        arrlistItemNames.add("Terms and Conditions");
        arrlistItemNames.add("History");
        arrlistItemNames.add("Sign out");
    }

}
