package com.carkzis.arkyris;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.carkzis.arkyris.about.AboutActivity;
import com.carkzis.arkyris.accounts.LoginActivity;
import com.carkzis.arkyris.settings.SettingsActivity;
import com.carkzis.arkyris.settings.SettingsViewModel;
import com.example.arkyris.R;
import com.google.android.material.tabs.TabLayout;

/**
 * The main activity after logging into the app, which attaches two separate
 * fragments for viewing entries.
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SettingsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        // Check if the member has been logged out
        mViewModel.loggedIn();

        checkLoggedOut();
        
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "-------");
        Log.d(LOG_TAG, "onCreate");

        // Initialise toolbar.
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create instance of the tab layout, along with the tabs and names.
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.arke));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.iris));
        // Make tabs fill the layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Set up the viewpage for swiping left and right between the attached fragments.
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        // Set up a click listener for the tabs.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Doesn't do anything.
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Doesn't do anything.
            }
        });

    }

    /**
     * Inflate the menu options from the res/menu/menu_catalog.xml file.
     * This adds menu items to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Sets the menu options for MainActivity, namely so the user can access the
     * SettingsActivity and the AboutActivity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.menu_about:
                Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAbout);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks if the user is logged out (or has not been logged in), and if so, directs
     * them to the LoginActivity.
     */
    public void checkLoggedOut() {
        mViewModel.getAutoLoggedOut().observe(this, loggedOut -> {
            if (loggedOut) {
                Toast.makeText(
                        getApplicationContext(),
                        "You have been logged out!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                // Need to prevent the user from going back to the logged in area!
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

}