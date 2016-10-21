package ke.co.heavybit.mylocalinfo.models;

/**
 * Created by nikk on 11/24/2015.
 */
public class MyLocation {
    public String longitude;
    public String latitude;
    //constructors
    public MyLocation(){
    }

    public MyLocation(String longitude, String latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
