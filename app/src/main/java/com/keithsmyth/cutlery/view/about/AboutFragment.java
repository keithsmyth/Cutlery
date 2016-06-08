package com.keithsmyth.cutlery.view.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keithsmyth.cutlery.BuildConfig;
import com.keithsmyth.cutlery.R;
import com.keithsmyth.cutlery.view.Navigates;

public class AboutFragment extends Fragment {

    private static final Uri LICENCE_URI = Uri.parse("https://creativecommons.org/licenses/by/4.0/");

    private Navigates navigates;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigates = (Navigates) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView versionText = (TextView) view.findViewById(R.id.version_text);
        versionText.setText(getString(R.string.about_version,
            getString(R.string.app_name), BuildConfig.VERSION_NAME));

        view.findViewById(R.id.licence_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, LICENCE_URI));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (navigates != null) {
            navigates.setTitle(R.string.about_title);
        }
    }
}
