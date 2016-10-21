package ke.co.heavybit.mylocalinfo.helper;

import android.content.Context;
import android.content.SharedPreferences;

import ke.co.heavybit.mylocalinfo.models.MyLocation;

import static ke.co.heavybit.mylocalinfo.extras.keys.EndPointWeather.*;

public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "ke.co.heavybit.mylocalinfo.prefs";

    // Constructor
    public MyPreferenceManager(Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setLocation(String longitude, String latitude) {

        editor.putString(KEY_LONGITUDE_TEXT, longitude);
        editor.putString(KEY_LATITUDE_TEXT, latitude);
        editor.putBoolean(KEY_LOCATION_SET, true);

        editor.commit();

    }

    public MyLocation getLocation() {

        if (pref.getString(KEY_LONGITUDE_TEXT, null) != null) {
            String longitude, latitude;
            longitude = pref.getString(KEY_LONGITUDE_TEXT, null);
            latitude = pref.getString(KEY_LATITUDE_TEXT, null);

            MyLocation myLocation = new MyLocation();

            myLocation.setLatitude(latitude);
            myLocation.setLongitude(longitude);

            return myLocation;
        }
        return null;

    }

    public boolean isLocationSet() {
        return pref.getBoolean(KEY_LOCATION_SET, false);
    }

    public void clear() {

        editor.clear();
        editor.commit();

    }

    public void logout() {

        editor.clear();
        editor.commit();

    }

    public void clearLocationPrefs() {

        editor.remove(KEY_LATITUDE_TEXT);
        editor.remove(KEY_LONGITUDE_TEXT);
        editor.remove(KEY_LOCATION_SET);
        editor.commit();

    }

}
