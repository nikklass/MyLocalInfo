package ke.co.heavybit.mylocalinfo.utils;

/**
 * Created by heavybit on 2/15/2016.
 */
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.WeathericonsModule;

import ke.co.heavybit.mylocalinfo.MainActivity;
import ke.co.heavybit.mylocalinfo.helper.MyPreferenceManager;

public class AppController extends Application{

    public  static  final  String TAG  = AppController.class.getSimpleName();

    private RequestQueue myRequest;

    private ImageLoader mImageLoader;

    private static  AppController mInstance;
    private MyPreferenceManager pref;

    @Override
    public  void  onCreate(){
        super.onCreate();
        mInstance = this;

        //init iconify
        Iconify
                .with(new WeathericonsModule())
                .with(new FontAwesomeModule());
        //end iconify

        //initialize and create the image loader logic

    }

    //prefs manager
    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }
        return pref;
    }
    //end prefs manager

    //CHECK ACTIVITY STATE
    public static boolean activityVisible; // Variable that will check the
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }
    //END CHECK ACTIVITY STATE

    public  static Context getAppContext(){
        return mInstance.getApplicationContext();
    }

    public  static  synchronized  AppController getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue()  {
        if (myRequest == null){

            myRequest = Volley.newRequestQueue(getApplicationContext());
        }

        return myRequest;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){

        req.setTag((TAG));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if (myRequest != null){
            myRequest.cancelAll(tag);
        }
    }

    public void launchHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
