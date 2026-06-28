package com.example.prayertimeqiblalocator;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.prayertimeqiblalocator.ui.PrayerTimesFragment;
import com.example.prayertimeqiblalocator.ui.QiblaFragment;
import com.example.prayertimeqiblalocator.ui.SettingsFragment;
import com.example.prayertimeqiblalocator.ui.TasbihFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_PRAYER = "prayer";
    private static final String TAG_QIBLA = "qibla";
    private static final String TAG_TASBIH = "tasbih";
    private static final String TAG_SETTINGS = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            String tag;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_prayer) {
                fragment = new PrayerTimesFragment();
                tag = TAG_PRAYER;
            } else if (itemId == R.id.nav_qibla) {
                fragment = new QiblaFragment();
                tag = TAG_QIBLA;
            } else if (itemId == R.id.nav_tasbih) {
                fragment = new TasbihFragment();
                tag = TAG_TASBIH;
            } else if (itemId == R.id.nav_settings) {
                fragment = new SettingsFragment();
                tag = TAG_SETTINGS;
            } else {
                return false;
            }
            showFragment(fragment, tag);
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_prayer);
        }
    }

    private void showFragment(@NonNull Fragment fragment, @NonNull String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment, tag)
                .commit();
    }
}
