package ke.co.heavybit.mylocalinfo;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ke.co.heavybit.mylocalinfo.utils.Functions;
import ke.co.heavybit.mylocalinfo.utils.MenusActivity;

public class MainActivity extends MenusActivity implements View.OnClickListener{

    private String TAG = MainActivity.class.getSimpleName();
    private Button btGetStarted;
    private TextView tvhomestr;
    private LocationManager locationManager;
    private boolean GpsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btGetStarted = (Button) findViewById(R.id.btGetStarted);

        btGetStarted.setOnClickListener(this);

        //check gps status
        if (!CheckGpsStatus()){
            //no gps enabled, display error msg
            Toast.makeText(this, R.string.enable_gps_msg, Toast.LENGTH_LONG).show();
        }

    }

    public boolean CheckGpsStatus(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btGetStarted:
                //Log.e(TAG, "clicked");
                startActivity(new Intent(MainActivity.this, DrawerActivity.class));
                overridePendingTransition(R.anim.push_left_enter, R.anim.push_left_exit);
                break;
        }
    }

}
