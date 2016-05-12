package com.keithsmyth.cutlery.view;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

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
        return super.onCreateOptionsMenu(menu);
        // TODO: Add About Page with feedback/bug, licences
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
    }

    @Override
    public void back() {
        onBackPressed();
    }
}
