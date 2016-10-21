package ke.co.heavybit.mylocalinfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import ke.co.heavybit.mylocalinfo.extras.Constants;
import ke.co.heavybit.mylocalinfo.utils.AppController;
import ke.co.heavybit.mylocalinfo.utils.Config;
import ke.co.heavybit.mylocalinfo.utils.CustomRequest;
import ke.co.heavybit.mylocalinfo.utils.MenusActivity;
import ke.co.heavybit.mylocalinfo.utils.PermissionUtils;

import static ke.co.heavybit.mylocalinfo.extras.keys.EndPointWeather.*;

/**
 * Created by heavybit on 10/17/2016.
 */
public class MyLocationActivity extends MenusActivity
        implements
        View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private String TAG = MyLocationActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    private CustomRequest jsObjRequest;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;

    private Button btSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_location);

        btSearch = (Button) findViewById(R.id.btSearch);

        btSearch.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, Config.LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.show_my_location) {
            startActivity(new Intent(this, MyLocationActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //get weather for this location
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("longitude", Double.toString(location.getLongitude()));
        params.put("latitude", Double.toString(location.getLatitude()));
        //fetch data
        getLocationWeather(params);
        //end get weather for this location

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getString(R.string.my_current_location_text));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSearch:
                //perform search
                //Log.e(TAG, "Click");
                mMap.clear();

                EditText searchLocation = (EditText) findViewById(R.id.etSearch);
                String txt_location = searchLocation.getText().toString();
                List<Address> addressList = null;

                if ((txt_location != null) || (!txt_location.equals(""))) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(txt_location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(MyLocationActivity.this, "No location found named '"+ txt_location +"'", Toast.LENGTH_LONG).show();
                    //e.printStackTrace();
                }

        }
    }

    private void getLocationWeather(HashMap<String, String> params){

        String LngLat = "";
        final String latitude = params.get(KEY_LATITUDE_TEXT);
        final String longitude = params.get(KEY_LONGITUDE_TEXT);
        LngLat = "&lat=" + latitude + "&lng=" + longitude;

        String url = Config.WEATHER_END_POINT + LngLat;

        jsObjRequest = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            //Log.e(TAG,"response: " + response);
                            try {

                                //response
                                JSONObject jsonObj = response.getJSONObject(KEY_WEATHER_OBSERVATION_OBJECTS);

                                if (jsonObj.length() > 0) {

                                    String elevation = Constants.NA;
                                    String longitude = Constants.NA;
                                    String latitude = Constants.NA;
                                    String observation = Constants.NA;
                                    String clouds = Constants.NA;
                                    String dewPoint = Constants.NA;
                                    String cloudsCode = Constants.NA;
                                    String datetime = Constants.NA;
                                    String countryCode = Constants.NA;
                                    String temperature = Constants.NA;
                                    String humidity = Constants.NA;
                                    String stationName = Constants.NA;
                                    String weatherCondition = Constants.NA;
                                    String windDirection = Constants.NA;
                                    String hectoPascAltimeter = Constants.NA;
                                    String windSpeed = Constants.NA;

                                    if (jsonObj.has(KEY_ELEVATION) && (!jsonObj.isNull(KEY_ELEVATION))){
                                        elevation = jsonObj.getString(KEY_ELEVATION);
                                    }

                                    if (jsonObj.has(KEY_LONGITUDE) && (!jsonObj.isNull(KEY_LONGITUDE))){
                                        longitude = jsonObj.getString(KEY_LONGITUDE);
                                    }

                                    if (jsonObj.has(KEY_LATITUDE) && (!jsonObj.isNull(KEY_LATITUDE))){
                                        latitude = jsonObj.getString(KEY_LATITUDE);
                                    }

                                    if (jsonObj.has(KEY_OBSERVATION) && (!jsonObj.isNull(KEY_OBSERVATION))){
                                        observation = jsonObj.getString(KEY_OBSERVATION);
                                    }

                                    if (jsonObj.has(KEY_CLOUDS) && (!jsonObj.isNull(KEY_CLOUDS))){
                                        clouds = jsonObj.getString(KEY_CLOUDS);
                                    }

                                    if (jsonObj.has(KEY_DEW_POINT) && (!jsonObj.isNull(KEY_DEW_POINT))){
                                        dewPoint = jsonObj.getString(KEY_DEW_POINT);
                                    }

                                    if (jsonObj.has(KEY_CLOUDS_CODE) && (!jsonObj.isNull(KEY_CLOUDS_CODE))){
                                        cloudsCode = jsonObj.getString(KEY_CLOUDS_CODE);
                                    }

                                    if (jsonObj.has(KEY_WEATHER_DATETIME) && (!jsonObj.isNull(KEY_WEATHER_DATETIME))){
                                        datetime = jsonObj.getString(KEY_WEATHER_DATETIME);
                                    }

                                    if (jsonObj.has(KEY_WEATHER_COUNTRY_CODE) && (!jsonObj.isNull(KEY_WEATHER_COUNTRY_CODE))){
                                        countryCode = jsonObj.getString(KEY_WEATHER_COUNTRY_CODE);
                                    }

                                    if (jsonObj.has(KEY_WEATHER_TEMPERATURE) && (!jsonObj.isNull(KEY_WEATHER_TEMPERATURE))){
                                        temperature = jsonObj.getString(KEY_WEATHER_TEMPERATURE);
                                    }

                                    if (jsonObj.has(KEY_WEATHER_HUMIDITY) && (!jsonObj.isNull(KEY_WEATHER_HUMIDITY))){
                                        humidity = jsonObj.getString(KEY_WEATHER_HUMIDITY);
                                    }

                                    if (jsonObj.has(KEY_STATION_NAME) && (!jsonObj.isNull(KEY_STATION_NAME))){
                                        stationName = jsonObj.getString(KEY_STATION_NAME);
                                    }

                                    if (jsonObj.has(KEY_WEATHER_CONDITIONS) && (!jsonObj.isNull(KEY_WEATHER_CONDITIONS))){
                                        weatherCondition = jsonObj.getString(KEY_WEATHER_CONDITIONS);
                                    }

                                    if (jsonObj.has(KEY_WIND_DIRECTION) && (!jsonObj.isNull(KEY_WIND_DIRECTION))){
                                        windDirection = jsonObj.getString(KEY_WIND_DIRECTION);
                                    }

                                    if (jsonObj.has(KEY_HECTO_PASC_ALTIMETER) && (!jsonObj.isNull(KEY_HECTO_PASC_ALTIMETER))){
                                        hectoPascAltimeter = jsonObj.getString(KEY_HECTO_PASC_ALTIMETER);
                                    }

                                    if (jsonObj.has(KEY_WIND_SPEED) && (!jsonObj.isNull(KEY_WIND_SPEED))){
                                        windSpeed = jsonObj.getString(KEY_WIND_SPEED);
                                    }

                                    if (!datetime.equals(Constants.NA)) {
                                        //create weather object

                                    }

                                }


                            } catch (JSONException e) {
                                //Log.e(TAG, "json parsing error: " + e.getMessage());
                                //Toast.makeText(getActivity().getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                });

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

}