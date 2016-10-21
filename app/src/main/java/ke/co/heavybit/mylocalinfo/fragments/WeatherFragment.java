package ke.co.heavybit.mylocalinfo.fragments;

import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import ke.co.heavybit.mylocalinfo.DrawerActivity;
import ke.co.heavybit.mylocalinfo.R;
import ke.co.heavybit.mylocalinfo.extras.Constants;
import ke.co.heavybit.mylocalinfo.models.MyLocation;
import ke.co.heavybit.mylocalinfo.utils.AppController;
import ke.co.heavybit.mylocalinfo.utils.Config;
import ke.co.heavybit.mylocalinfo.utils.CustomRequest;
import ke.co.heavybit.mylocalinfo.utils.WeatherUtils;

import static ke.co.heavybit.mylocalinfo.extras.keys.EndPointWeather.*;
import static ke.co.heavybit.mylocalinfo.extras.keys.EndPointLocation.*;

/**
 * Created by heavybit on 3/24/2016.
 */
public class WeatherFragment extends Fragment {

    private String TAG = WeatherFragment.class.getSimpleName();

    private TextView tvLocationName, tvTemperature, tvWindDirection, tvClouds, tvHumidity, tvWindSpeed;
    private TextView tvCountryCode, tvCountryName;
    private TextView tvLocationNameTitle, tvTemperatureTitle, tvWindDirectionTitle, tvCloudsTitle, tvHumidityTitle, tvWindSpeedTitle;
    private TextView tvCountryCodeTitle, tvCountryNameTitle;

    private ImageView ivWeather;
    private ImageButton ibRefresh;
    //private ProgressBar loadingProgressBar;

    private String longitude, latitude;

    private HashMap<String, String> params;
    private String url;

    private AlertDialog dialog;

    private CustomRequest jsObjRequest;

    private int fragVal;

    public  static WeatherFragment init(int val) {
        WeatherFragment weatherFragment = new WeatherFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        weatherFragment.setArguments(args);
        return weatherFragment;
    }

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.weather_layout, container, false);

        //noinspection ConstantConditions
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(R.string.show_my_weather);


        //is location data set?
        if (AppController.getInstance().getPrefManager().isLocationSet()) {

            //read shared prefs data
            //create school object to store school data in shared prefs
            MyLocation myLocation = AppController.getInstance().getPrefManager().getLocation();
            longitude = myLocation.getLongitude();
            latitude = myLocation.getLatitude();
            //Log.e(TAG, "locn weather - longitude - " + longitude + " == latitude - " + latitude);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("longitude", longitude);
            params.put("latitude", latitude);

            //set location weather
            setLocationWeather(params);

            //set location name
            setLocationName(params);

        }

        //get fields
        //loadingProgressBar = (ProgressBar) view.findViewById(R.id.loadingProgressBar);

        tvCountryName = (TextView) view.findViewById(R.id.tvCountryName);
        tvCountryCode = (TextView) view.findViewById(R.id.tvCountryCode);
        tvLocationName = (TextView) view.findViewById(R.id.tvLocationName);
        tvTemperature = (TextView) view.findViewById(R.id.tvTemperature);
        tvWindDirection = (TextView) view.findViewById(R.id.tvWindDirection);
        tvClouds = (TextView) view.findViewById(R.id.tvClouds);
        tvHumidity = (TextView) view.findViewById(R.id.tvHumidity);
        tvWindSpeed = (TextView) view.findViewById(R.id.tvWindSpeed);
        tvCountryNameTitle = (TextView) view.findViewById(R.id.tvCountryNameTitle);
        tvCountryCodeTitle = (TextView) view.findViewById(R.id.tvCountryCodeTitle);
        tvLocationNameTitle = (TextView) view.findViewById(R.id.tvLocationNameTitle);
        tvTemperatureTitle = (TextView) view.findViewById(R.id.tvTemperatureTitle);
        tvWindDirectionTitle = (TextView) view.findViewById(R.id.tvWindDirectionTitle);
        tvCloudsTitle = (TextView) view.findViewById(R.id.tvCloudsTitle);
        tvHumidityTitle = (TextView) view.findViewById(R.id.tvHumidityTitle);
        tvWindSpeedTitle = (TextView) view.findViewById(R.id.tvWindSpeedTitle);

        //set custom fonts
        AssetManager assetManager = getActivity().getAssets();
        Typeface customFont = Typeface.createFromAsset(assetManager, Config.CUSTOM_FONT_PATH);
        tvCountryName.setTypeface(customFont);
        tvCountryCode.setTypeface(customFont);
        tvLocationName.setTypeface(customFont);
        tvTemperature.setTypeface(customFont);
        tvWindDirection.setTypeface(customFont);
        tvClouds.setTypeface(customFont);
        tvHumidity.setTypeface(customFont);
        tvWindSpeed.setTypeface(customFont);
        tvCountryNameTitle.setTypeface(customFont);
        tvCountryCodeTitle.setTypeface(customFont);
        tvLocationNameTitle.setTypeface(customFont);
        tvTemperatureTitle.setTypeface(customFont);
        tvWindDirectionTitle.setTypeface(customFont);
        tvCloudsTitle.setTypeface(customFont);
        tvHumidityTitle.setTypeface(customFont);
        tvWindSpeedTitle.setTypeface(customFont);

        return view;

    }

    private void setLocationWeather(HashMap<String, String> params){

        //loadingProgressBar.setVisibility(View.VISIBLE);

        String LngLat = "";
        final String latitude = params.get(KEY_LATITUDE_TEXT);
        final String longitude = params.get(KEY_LONGITUDE_TEXT);
        LngLat = "&lat=" + latitude + "&lng=" + longitude;

        String url = Config.WEATHER_END_POINT + LngLat;

        jsObjRequest = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //loadingProgressBar.setVisibility(View.GONE);

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

                                        //assign values
                                        String myWindDirection = WeatherUtils.getFormattedWind(Float.parseFloat(windDirection));
                                        tvLocationName.setText(stationName);
                                        tvTemperature.setText(temperature);
                                        tvWindDirection.setText(myWindDirection);
                                        tvClouds.setText(clouds);
                                        tvHumidity.setText(humidity);
                                        tvWindSpeed.setText(windSpeed);

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

    private void setLocationName(HashMap<String, String> params){

        dialog = new SpotsDialog(getActivity(), R.style.Custom);
        //dialog.setMessage("Fetching data");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        String LngLat = "";
        final String latitude = params.get(KEY_LATITUDE_TEXT);
        final String longitude = params.get(KEY_LONGITUDE_TEXT);
        LngLat = "&lat=" + latitude + "&lng=" + longitude;

        String url = Config.LOCATION_END_POINT + LngLat;

        jsObjRequest = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        dialog.dismiss();

                        if (response != null) {
                            //Log.e(TAG,"response: " + response);
                            try {

                                //response
                                JSONArray jsonLocArray = response.getJSONArray(KEY_LOCATION_OBJECTS);

                                if (jsonLocArray.length() > 0) {

                                    //fetch first array item
                                    for (int i = 0; i < 1; i++) {

                                        JSONObject jsonLocObj = jsonLocArray.getJSONObject(i);

                                        String adminCode1 = Constants.NA;
                                        String toponymName = Constants.NA;
                                        String countryCode = Constants.NA;
                                        String locationName = Constants.NA;
                                        String countryName = Constants.NA;
                                        String adminName1 = Constants.NA;
                                        String fclName = Constants.NA;
                                        String fcodeName = Constants.NA;

                                        if (jsonLocObj.has(KEY_ADMIN_CODE1) && (!jsonLocObj.isNull(KEY_ADMIN_CODE1))) {
                                            adminCode1 = jsonLocObj.getString(KEY_ADMIN_CODE1);
                                        }

                                        if (jsonLocObj.has(KEY_TOPONYM_NAME) && (!jsonLocObj.isNull(KEY_TOPONYM_NAME))) {
                                            toponymName = jsonLocObj.getString(KEY_TOPONYM_NAME);
                                        }

                                        if (jsonLocObj.has(KEY_COUNTRY_CODE) && (!jsonLocObj.isNull(KEY_COUNTRY_CODE))) {
                                            countryCode = jsonLocObj.getString(KEY_COUNTRY_CODE);
                                        }

                                        if (jsonLocObj.has(KEY_LOCATION_NAME) && (!jsonLocObj.isNull(KEY_LOCATION_NAME))) {
                                            locationName = jsonLocObj.getString(KEY_LOCATION_NAME);
                                        }

                                        if (jsonLocObj.has(KEY_COUNTRY_NAME) && (!jsonLocObj.isNull(KEY_COUNTRY_NAME))) {
                                            countryName = jsonLocObj.getString(KEY_COUNTRY_NAME);
                                        }

                                        if (jsonLocObj.has(KEY_ADMIN_NAME1) && (!jsonLocObj.isNull(KEY_ADMIN_NAME1))) {
                                            adminName1 = jsonLocObj.getString(KEY_ADMIN_NAME1);
                                        }

                                        if (jsonLocObj.has(KEY_FCL_NAME) && (!jsonLocObj.isNull(KEY_FCL_NAME))) {
                                            fclName = jsonLocObj.getString(KEY_FCL_NAME);
                                        }

                                        if (jsonLocObj.has(KEY_FCODE_NAME) && (!jsonLocObj.isNull(KEY_FCODE_NAME))) {
                                            fcodeName = jsonLocObj.getString(KEY_FCODE_NAME);
                                        }

                                        if (!countryName.equals(Constants.NA)) {

                                            String locationFullText = countryName;
                                            if (!locationName.equals(Constants.NA)) {
                                                locationFullText = locationName + ", " + countryName;
                                            }
                                            //assign values
                                            tvCountryName.setText(locationFullText);
                                            tvCountryCode.setText(countryCode);

                                        }

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
                        //Log.e(TAG, error.toString());
                    }
                });

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

}
