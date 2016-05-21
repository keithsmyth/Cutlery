package com.keithsmyth.cutlery.view;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.keithsmyth.cutlery.R;

public class MainActivity extends AppCompatActivity implements Navigates {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new DoFragment())
                .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                to(new AboutFragment());
                return true;
            case android.R.id.home:
                back();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final boolean shouldShowBackButton = getSupportFragmentManager().getBackStackEntryCount() > 0;
        showBackButton(shouldShowBackButton);
    }

    @Override
    public void to(Fragment fragment) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(fragment.getClass().getName())
            .commit();
        showBackButton(true);
    }

    @Override
    public void back() {
        onBackPressed();
    }

    private void showBackButton(boolean isVisible) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) { return; }
        actionBar.setDisplayShowHomeEnabled(isVisible);
        actionBar.setDisplayHomeAsUpEnabled(isVisible);
    }
}
