package com.example.splitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    private SwitchCompat mySwitch;
    View view;

    private static String MY_PREFS = "switch_prefs";
    private static String SWITCH_STATUS = "switch_status";

    boolean switch_status;

    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        mySwitch = view.findViewById(R.id.switch1);

        myPreferences = this.getActivity().getSharedPreferences(MY_PREFS,MODE_PRIVATE);
        myEditor = this.getActivity().getSharedPreferences(MY_PREFS,MODE_PRIVATE).edit();

        switch_status = myPreferences.getBoolean(SWITCH_STATUS, false); //default false switch

        mySwitch.setChecked(switch_status);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myEditor.putBoolean(SWITCH_STATUS, true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    myEditor.putBoolean(SWITCH_STATUS, false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

            }
        });
        return view;
    }
}
