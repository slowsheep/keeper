package net.lzzy.keeper.utils;

import android.app.Application;
import android.content.Context;

/**
 * @author lzzy_gxy
 */
public class AppUtils extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
