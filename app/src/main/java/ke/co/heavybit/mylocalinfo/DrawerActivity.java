package ke.co.heavybit.mylocalinfo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.fastadapter.utils.RecyclerViewCacheUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import ke.co.heavybit.mylocalinfo.fragments.MainMapFragment;
import ke.co.heavybit.mylocalinfo.fragments.WeatherFragment;
import ke.co.heavybit.mylocalinfo.utils.Config;
import ke.co.heavybit.mylocalinfo.utils.CustomRequest;
import ke.co.heavybit.mylocalinfo.utils.MenusActivity;
import ke.co.heavybit.mylocalinfo.utils.PermissionUtils;

/**
 * Created by heavybit on 3/6/2016.
 */
public class DrawerActivity extends MenusActivity {

    private static final int PROFILE_SETTING = 100000;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private  String user_id, total, url;

    private CustomRequest jsObjRequest;

    private String TAG = DrawerActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //save our header or resultCenters
    private AccountHeader headerResult = null;
    private Drawer result = null;

    private String user_full_names, user_image, student_full_names, sch_name;
    private String phone_number;

    private Intent myIntent;
    private int loadFragment;

    private LocationManager locationManager;
    private boolean GpsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        //set custom fonts
        AssetManager assetManager = getAssets();
        Typeface customFont = Typeface.createFromAsset(assetManager, Config.CUSTOM_FONT_PATH);
        String fontPath = Config.CUSTOM_FONT_PATH;
        //custom fonts

        //read any passed intent
        // getIntent() is a method from the started activity
        myIntent = getIntent(); // gets the previously created intent
        loadFragment = myIntent.getIntExtra("loadFragment", -1);
        //read passed intent

        //check gps status
        //check gps status
        if (!CheckGpsStatus()){
            //no gps enabled, display error msg
            Toast.makeText(this, R.string.enable_gps_msg, Toast.LENGTH_LONG).show();
        }


        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create user profile
        final IProfile profile = new ProfileDrawerItem()
                .withName("Nikk")
                .withIcon(R.drawable.no_image_2)
                .withIdentifier(Config.MY_PROFILE_ACCOUNT_IDENTIFIER);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.myheader)
                .addProfiles(
                        profile,
                        //google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName(getString(R.string.manage_account_text)).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(Config.MANAGE_ACCOUNT_IDENTIFIER)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            int count = 100 + headerResult.getProfiles().size() + 1;
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.activity_show_map).withIcon(FontAwesome.Icon.faw_location_arrow).withIdentifier(Config.SHOW_MAP_IDENTIFIER).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.activity_show_weather).withIcon(GoogleMaterial.Icon.gmd_cloud).withIdentifier(Config.SHOW_WEATHER_IDENTIFIER).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.activity_home).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(Config.HOME_IDENTIFIER).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.activity_logout).withIcon(GoogleMaterial.Icon.gmd_power_settings_new).withIdentifier(Config.LOGOUT_IDENTIFIER).withSelectable(false)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == Config.SHOW_MAP_IDENTIFIER) {
                                setFragment(Config.SHOW_MAP_IDENTIFIER);
                            } else if (drawerItem.getIdentifier() == Config.SHOW_WEATHER_IDENTIFIER) {
                                setFragment(Config.SHOW_WEATHER_IDENTIFIER);
                            } else if (drawerItem.getIdentifier() == Config.HOME_IDENTIFIER) {
                                startActivity(new Intent(DrawerActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.push_right_enter, R.anim.push_right_exit);
                            } else if (drawerItem.getIdentifier() == Config.LOGOUT_IDENTIFIER) {
                                setFragment(Config.LOGOUT_IDENTIFIER);
                            }

                            if (intent != null) {
                                DrawerActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(), result.getDrawerItems());

        //load first fragment
        setFragment(Config.SHOW_MAP_IDENTIFIER);

    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (loadFragment != -1)
        {
            setFragment(loadFragment);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;//return true so that the menu pop up is opened
    }

    //check selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_my_location:
                setFragment(Config.SHOW_MAP_IDENTIFIER);
                return true;

            case R.id.menu_weather:
                setFragment(Config.SHOW_WEATHER_IDENTIFIER);
                return true;

            case R.id.menu_logout:
                setFragment(Config.LOGOUT_IDENTIFIER);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //end menus

    public boolean CheckGpsStatus(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;

    }


    //fragment funcs
    public void setFragment(int position) {

        if (position == Config.SHOW_MAP_IDENTIFIER) {
            FragmentManager fragmentManager;
            FragmentTransaction fragmentTransaction;
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            MainMapFragment mainMapFragment = new MainMapFragment();
            fragmentTransaction.replace(R.id.frame_container, mainMapFragment);
            fragmentTransaction.commit();
        } else if (position == Config.SHOW_WEATHER_IDENTIFIER ) {
            FragmentManager fragmentManager;
            FragmentTransaction fragmentTransaction;
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            WeatherFragment weatherFragment = new WeatherFragment();
            fragmentTransaction.replace(R.id.frame_container, weatherFragment);
            fragmentTransaction.commit();
        } else if (position == Config.LOGOUT_IDENTIFIER ) {
            //logout option selected
            Toast.makeText(this, R.string.logout_text, Toast.LENGTH_LONG).show();
        }

    }

}
