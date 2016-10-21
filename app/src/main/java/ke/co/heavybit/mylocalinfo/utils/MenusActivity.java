package ke.co.heavybit.mylocalinfo.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ke.co.heavybit.mylocalinfo.R;

/**
 * Created by heavybit on 4/21/2016.
 */
public class MenusActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action0:
                //startActivity(new Intent(getApplicationContext(), MainMenusActivity.class));
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
