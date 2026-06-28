package com.example.prayertimeqiblalocator.ui;
import com.example.prayertimeqiblalocator.database.DatabaseHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prayertimeqiblalocator.R;
import com.example.prayertimeqiblalocator.util.PreferencesHelper;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {

    private PreferencesHelper preferences;
    private boolean suppressCallbacks;

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = new PreferencesHelper(requireContext());
        databaseHelper = new DatabaseHelper(requireContext());

        Spinner tasbihTargetSpinner = view.findViewById(R.id.spinner_tasbih_target);
        MaterialSwitch switch24Hour = view.findViewById(R.id.switch_time_format);
        MaterialSwitch switchNotifications = view.findViewById(R.id.switch_notifications);
        MaterialSwitch switchHaptic = view.findViewById(R.id.switch_tasbih_haptic);

        suppressCallbacks = true;
        switch24Hour.setChecked(preferences.is24HourFormat());
        switchNotifications.setChecked(preferences.areNotificationsEnabled());
        switchHaptic.setChecked(preferences.isTasbihHapticEnabled());
        selectTasbihTargetSpinner(tasbihTargetSpinner, preferences.getTasbihTarget());
        suppressCallbacks = false;

        tasbihTargetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemView, int position, long id) {
                if (!suppressCallbacks) {
                    String value = parent.getItemAtPosition(position).toString();
                    preferences.setTasbihTarget(Integer.parseInt(value));
                    databaseHelper.updateTasbihTarget(Integer.parseInt(value));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        switch24Hour.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!suppressCallbacks) {
                preferences.set24HourFormat(isChecked);
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!suppressCallbacks) {
                preferences.setNotificationsEnabled(isChecked);
            }
        });

        switchHaptic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!suppressCallbacks) {
                preferences.setTasbihHapticEnabled(isChecked);
            }
        });
    }

    private void selectTasbihTargetSpinner(Spinner spinner, int target) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (Integer.parseInt(spinner.getItemAtPosition(i).toString()) == target) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}
