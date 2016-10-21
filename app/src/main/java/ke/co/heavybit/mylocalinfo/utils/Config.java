package ke.co.heavybit.mylocalinfo.utils;

/**
 * Created by heavybit on 10/17/2016.
 */
public class Config {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //root api endpoint
    public static  final String API_ENDPOINT = "http://api.geonames.org/";

    //api username
    public static  final String API_USERNAME = "?username=antiv_boy";

    //data links
    public static  final String WEATHER_LINK = "findNearByWeatherJSON";
    public static  final String LOCATION_LINK = "findNearbyJSON";

    //data endpoints
    public static  final String WEATHER_END_POINT = API_ENDPOINT + WEATHER_LINK + API_USERNAME;
    public static  final String LOCATION_END_POINT = API_ENDPOINT + LOCATION_LINK + API_USERNAME;

    //general var configs
    public static  final String CUSTOM_FONT_PATH  = "fonts/samsung_sans_regular.ttf";
    public static int MANAGE_ACCOUNT_IDENTIFIER = 5;
    public static int MY_PROFILE_ACCOUNT_IDENTIFIER = 100;
    public static int SHOW_MAP_IDENTIFIER = 6;
    public static int SHOW_WEATHER_IDENTIFIER = 7;
    public static int LOGOUT_IDENTIFIER = 8;
    public static int HOME_IDENTIFIER = 9;
    public static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

}
