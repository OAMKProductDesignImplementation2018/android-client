package android.productdesignmobile;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set HomeFragment as default
        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = HomeFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        //Sidemenu
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Switch fragment on menuitemclick
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();

            Fragment fragment1 = null;
            Class fragmentClass1;
            switch(menuItem.getItemId()) {
                case R.id.nav_home_fragment:
                    fragmentClass1 = HomeFragment.class;
                    break;
                case R.id.nav_profile_settings_fragment:
                    fragmentClass1 = ProfileSettingsFragment.class;
                    break;
                case R.id.nav_display_settings_fragment:
                    fragmentClass1 = DisplaySettingsFragment.class;
                    break;
                case R.id.nav_logout:
                    Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(logoutIntent);
                    finish();
                default:
                    fragmentClass1 = HomeFragment.class;
                    break;
            }
            try {
                fragment1 = (Fragment) fragmentClass1.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.content_frame, fragment1)
                    //Adds current fragment to stack so androids backbutton works properly
                    .addToBackStack(null)
                    .commit();
            return true;
        });
    }
}
