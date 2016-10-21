package ke.co.heavybit.mylocalinfo.utils;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import ke.co.heavybit.mylocalinfo.extras.Constants;

import static ke.co.heavybit.mylocalinfo.extras.keys.EndPointWeather.*;

/**
 * Created by heavybit on 10/18/2016.
 */
public class Functions {
    //DATABASE REQUESTS OBJECT
    private CustomRequest jsObjRequest;

    private String TAG = Functions.class.getSimpleName();

    private LocationManager locationManager ;
    private boolean GpsStatus ;

    public boolean CheckGpsStatus(Context ctx){

        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GpsStatus;

    }


}
