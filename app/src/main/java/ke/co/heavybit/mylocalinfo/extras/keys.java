package ke.co.heavybit.mylocalinfo.extras;

/**
 * Created by heavybit on 2/20/2016.
 */
public interface keys {

    public  interface  EndPointWeather{

        public static final String KEY_WEATHER_OBSERVATION_OBJECTS = "weatherObservation";
        public static final String KEY_ELEVATION = "elevation";
        public static final String KEY_LONGITUDE = "lng";
        public static final String KEY_LATITUDE = "lat";
        public static final String KEY_LONGITUDE_TEXT = "longitude";
        public static final String KEY_LATITUDE_TEXT = "latitude";
        public static final String KEY_OBSERVATION = "observation";
        public static final String KEY_CLOUDS = "clouds";
        public static final String KEY_DEW_POINT = "dewPoint";
        public static final String KEY_CLOUDS_CODE = "cloudsCode";
        public static final String KEY_WEATHER_DATETIME = "datetime";
        public static final String KEY_WEATHER_COUNTRY_CODE = "countryCode";
        public static final String KEY_WEATHER_TEMPERATURE = "temperature";
        public static final String KEY_WEATHER_HUMIDITY = "humidity";
        public static final String KEY_STATION_NAME = "stationName";
        public static final String KEY_WEATHER_CONDITIONS = "weatherCondition";
        public static final String KEY_WIND_DIRECTION = "windDirection";
        public static final String KEY_HECTO_PASC_ALTIMETER = "hectoPascAltimeter";
        public static final String KEY_WIND_SPEED = "windSpeed";
        public static final String KEY_LOCATION_SET = "location";

    }

    public  interface  EndPointLocation{

        public static final String KEY_LOCATION_OBJECTS = "geonames";
        public static final String KEY_ADMIN_CODE1 = "adminCode1";
        public static final String KEY_TOPONYM_NAME = "toponymName";
        public static final String KEY_COUNTRY_CODE = "countryCode";
        public static final String KEY_LOCATION_NAME = "name";
        public static final String KEY_COUNTRY_NAME = "countryName";
        public static final String KEY_ADMIN_NAME1 = "adminName1";
        public static final String KEY_FCL_NAME = "fclName";
        public static final String KEY_FCODE_NAME = "fcodeName";

    }


}
