package com.example.prayertimeqiblalocator.ui;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.IslamicCalendar;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.CalculationParameters;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.Madhab;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.data.DateComponents;
import com.example.prayertimeqiblalocator.R;
import com.example.prayertimeqiblalocator.database.DatabaseHelper;
import com.example.prayertimeqiblalocator.util.PreferencesHelper;
import com.google.android.material.chip.Chip;

import java.util.Date;
import java.util.Locale;

public class PrayerTimesFragment extends Fragment {

    private static final String[][] PRAYER_KEYS = {
            {"fajr", "prayer_fajr"},
            {"sunrise", "prayer_sunrise"},
            {"dhuhr", "prayer_dhuhr"},
            {"asr", "prayer_asr"},
            {"maghrib", "prayer_maghrib"},
            {"isha", "prayer_isha"}
    };

    private static final double[][] UAE_CITY_COORDS = {
            {25.2048, 55.2708}, // Dubai
            {24.4539, 54.3773}, // Abu Dhabi
            {25.3463, 55.4209}, // Sharjah
            {25.4052, 55.5136}, // Ajman
            {25.5647, 55.5533}, // Umm Al Quwain
            {25.7895, 55.9432}, // Ras Al Khaimah
            {25.1288, 56.3265}, // Fujairah
            {24.2075, 55.7447}  // Al Ain
    };

    private String[][] calculatedPrayerTimes = new String[6][2];

    private PreferencesHelper preferences;
    private DatabaseHelper databaseHelper;

    private TextView textGregorian;
    private TextView textHijri;
    private TextView textNextPrayer;

    private Chip chipLocation;

    private LinearLayout prayerList;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());

    private Runnable timerRunnable;

    private double currentLat = 24.2075;
    private double currentLon = 55.7447;

    private String currentCity = "Al Ain";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_prayer_times,
                container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        preferences = new PreferencesHelper(requireContext());

        databaseHelper = new DatabaseHelper(requireContext());

        textGregorian = view.findViewById(R.id.text_gregorian_date);
        textHijri = view.findViewById(R.id.text_hijri_date);
        textNextPrayer = view.findViewById(R.id.text_next_prayer);

        chipLocation = view.findViewById(R.id.chip_location);

        prayerList = view.findViewById(R.id.layout_prayer_list);

        // Load saved city from SQLite
        currentCity = databaseHelper.getCity();

        if (currentCity == null || currentCity.isEmpty()) {
            currentCity = "Al Ain";
        }

        chipLocation.setText(currentCity);

        setCoordinatesFromCity(currentCity);

        updateDates();

        setupLocation();

        updatePrayerUi();

        startCountdown();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDates();

        updatePrayerUi();
    }

    private void updateDates() {

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf =
                new SimpleDateFormat("EEEE, d MMMM yyyy",
                        Locale.getDefault());

        textGregorian.setText(sdf.format(cal.getTime()));

        IslamicCalendar islamicCalendar = new IslamicCalendar();

        SimpleDateFormat hijriSdf =
                new SimpleDateFormat(
                        "d MMMM yyyy G",
                        new ULocale("ar_SA@calendar=islamic"));

        textHijri.setText(
                hijriSdf.format(islamicCalendar.getTime()));
    }

    private void setupLocation() {

        chipLocation.setOnClickListener(v -> {
            showCitySelectionDialog();
        });
    }

    private void showCitySelectionDialog() {

        String[] cities =
                getResources().getStringArray(R.array.uae_cities);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.select_city)
                .setItems(cities, (dialog, which) -> {

                    currentLat = UAE_CITY_COORDS[which][0];
                    currentLon = UAE_CITY_COORDS[which][1];

                    currentCity = cities[which];

                    // Save city to SQLite
                    databaseHelper.updateCity(currentCity);

                    chipLocation.setText(currentCity);

                    updatePrayerUi();
                })
                .show();
    }

    private void setCoordinatesFromCity(String city) {

        String[] cities =
                getResources().getStringArray(R.array.uae_cities);

        for (int i = 0; i < cities.length; i++) {

            if (cities[i].equalsIgnoreCase(city)) {

                currentLat = UAE_CITY_COORDS[i][0];
                currentLon = UAE_CITY_COORDS[i][1];

                return;
            }
        }
    }

    private void startCountdown() {

        timerRunnable = new Runnable() {

            @Override
            public void run() {

                updatePrayerUi();

                timerHandler.postDelayed(this, 60000);
            }
        };

        timerHandler.post(timerRunnable);
    }

    private void updatePrayerUi() {

        calculateTimes();

        prayerList.removeAllViews();

        LayoutInflater inflater =
                LayoutInflater.from(requireContext());

        boolean use24Hour = preferences.is24HourFormat();

        Calendar now = Calendar.getInstance();

        int currentMinutes =
                now.get(Calendar.HOUR_OF_DAY) * 60
                        + now.get(Calendar.MINUTE);

        int nextIndex = -1;

        long minDiff = Long.MAX_VALUE;

        for (int i = 0; i < calculatedPrayerTimes.length; i++) {

            String[] timeParts =
                    calculatedPrayerTimes[i][1].split(":");

            int prayerMinutes =
                    Integer.parseInt(timeParts[0]) * 60
                            + Integer.parseInt(timeParts[1]);

            int diff = prayerMinutes - currentMinutes;

            if (diff > 0 && diff < minDiff) {

                minDiff = diff;

                nextIndex = i;
            }
        }

        if (nextIndex == -1) {

            nextIndex = 0;

            String[] timeParts =
                    calculatedPrayerTimes[0][1].split(":");

            int fajrMinutes =
                    Integer.parseInt(timeParts[0]) * 60
                            + Integer.parseInt(timeParts[1]);

            minDiff =
                    (24 * 60 - currentMinutes)
                            + fajrMinutes;
        }

        for (int i = 0; i < calculatedPrayerTimes.length; i++) {

            View item =
                    inflater.inflate(
                            R.layout.item_prayer_time,
                            prayerList,
                            false);

            TextView nameView =
                    item.findViewById(R.id.text_prayer_name);

            TextView timeView =
                    item.findViewById(R.id.text_prayer_time);

            TextView badgeNext =
                    item.findViewById(R.id.badge_next);

            int nameRes =
                    getResources().getIdentifier(
                            PRAYER_KEYS[i][1],
                            "string",
                            requireContext().getPackageName());

            nameView.setText(nameRes);

            String timeStr = calculatedPrayerTimes[i][1];

            if (!use24Hour) {
                timeStr = to12Hour(timeStr);
            }

            timeView.setText(timeStr);

            if (i == nextIndex) {

                badgeNext.setVisibility(View.VISIBLE);

                item.setBackgroundResource(
                        R.drawable.bg_prayer_next);

                String prayerName = getString(nameRes);

                long hours = minDiff / 60;

                long mins = minDiff % 60;

                textNextPrayer.setText(
                        String.format(
                                "%s in %dh %dm",
                                prayerName,
                                hours,
                                mins));
            }

            prayerList.addView(item);
        }
    }

    private void calculateTimes() {

        Coordinates coordinates =
                new Coordinates(currentLat, currentLon);

        DateComponents date =
                DateComponents.from(new Date());

        CalculationParameters params =
                CalculationMethod.DUBAI.getParameters();

        params.madhab = Madhab.SHAFI;

        PrayerTimes prayerTimes =
                new PrayerTimes(coordinates, date, params);

        SimpleDateFormat sdf =
                new SimpleDateFormat("HH:mm",
                        Locale.getDefault());

        sdf.setTimeZone(
                android.icu.util.TimeZone.getDefault());

        calculatedPrayerTimes[0] =
                new String[]{"fajr",
                        sdf.format(prayerTimes.fajr)};

        calculatedPrayerTimes[1] =
                new String[]{"sunrise",
                        sdf.format(prayerTimes.sunrise)};

        calculatedPrayerTimes[2] =
                new String[]{"dhuhr",
                        sdf.format(prayerTimes.dhuhr)};

        calculatedPrayerTimes[3] =
                new String[]{"asr",
                        sdf.format(prayerTimes.asr)};

        calculatedPrayerTimes[4] =
                new String[]{"maghrib",
                        sdf.format(prayerTimes.maghrib)};

        calculatedPrayerTimes[5] =
                new String[]{"isha",
                        sdf.format(prayerTimes.isha)};
    }

    private String to12Hour(String time24) {

        try {

            String[] parts = time24.split(":");

            int hour = Integer.parseInt(parts[0]);

            int minute = Integer.parseInt(parts[1]);

            String amPm = hour >= 12 ? " PM" : " AM";

            int hour12 = hour % 12;

            if (hour12 == 0) {
                hour12 = 12;
            }

            return String.format(
                    "%d:%02d%s",
                    hour12,
                    minute,
                    amPm);

        } catch (Exception e) {

            return time24;
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        timerHandler.removeCallbacks(timerRunnable);
    }
}