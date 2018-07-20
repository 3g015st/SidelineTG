package com.example.benedictlutab.sidelinetg.helpers;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class fontStyleCrawler
{
    private Typeface typeface;

    public fontStyleCrawler(Typeface typeface)
    {
        this.typeface = typeface;
    }

    public fontStyleCrawler(AssetManager assets, String assetsFontFileName)
    {
        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
    }

    public void replaceFonts(ViewGroup viewTree)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                replaceFonts((ViewGroup)child);
            }
            else if(child instanceof TextView)
            {
                ((TextView) child).setTypeface(typeface);
            }
            else if(child instanceof TextInputLayout)
            {
                ((TextInputLayout) child).setTypeface(typeface);
            }

        }
    }
}
