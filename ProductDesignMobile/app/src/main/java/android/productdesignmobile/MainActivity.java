package android.productdesignmobile;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // Switch fragment on menuitemclick
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        Fragment fragment = null;
                        Class fragmentClass;
                        switch(menuItem.getItemId()) {
                            case R.id.nav_home_fragment:
                                fragmentClass = HomeFragment.class;
                                break;
                            case R.id.nav_profile_settings_fragment:
                                fragmentClass = ProfileSettingsFragment.class;
                                break;
                            case R.id.nav_display_settings_fragment:
                                fragmentClass = DisplaySettingsFragment.class;
                                break;
                            case R.id.nav_logout:
                                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(logoutIntent);
                                finish();
                            default:
                                fragmentClass = HomeFragment.class;
                        }
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                //Adds current fragment to stack so androids backbutton works properly
                                .addToBackStack(null)
                                .commit();
                        return true;
                    }
                });
    }
}
