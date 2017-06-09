package com.example.administrator.godface.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class Myapplication extends Application {
    private static  Context context;



    @Override
    public void onCreate() {
        context=getApplicationContext();
        LitePal.initialize(context);
    }
    public static Context getContext(){
        return context;
    }
}
