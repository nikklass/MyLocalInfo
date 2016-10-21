package ke.co.heavybit.mylocalinfo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ke.co.heavybit.mylocalinfo.R;


/**
 * Created by heavybit on 10/17/2016.
 */

public class LocationUtils {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

}
