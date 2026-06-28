package com.example.prayertimeqiblalocator.ui;
import com.example.prayertimeqiblalocator.database.DatabaseHelper;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prayertimeqiblalocator.R;
import com.example.prayertimeqiblalocator.util.PreferencesHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class TasbihFragment extends Fragment {

    private PreferencesHelper preferences;
    private TextView countView;
    private TextView progressView;
    private LinearProgressIndicator progressBar;
    private int count;
    private int target;

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasbih, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = new PreferencesHelper(requireContext());
        databaseHelper = new DatabaseHelper(requireContext());

        countView = view.findViewById(R.id.text_tasbih_count);
        progressView = view.findViewById(R.id.text_tasbih_progress);
        progressBar = view.findViewById(R.id.progress_tasbih);
        FrameLayout tapArea = view.findViewById(R.id.button_tasbih_tap);
        MaterialButton incrementButton = view.findViewById(R.id.button_tasbih_increment);
        MaterialButton resetButton = view.findViewById(R.id.button_tasbih_reset);

        loadState();
        updateUi();

        View.OnClickListener incrementListener = v -> increment();
        tapArea.setOnClickListener(incrementListener);
        incrementButton.setOnClickListener(incrementListener);
        resetButton.setOnClickListener(v -> {
            count = 0;
            databaseHelper.updateTasbihCount(count);
            updateUi();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences != null) {
            target = preferences.getTasbihTarget();
            updateUi();
        }
    }

    private void loadState() {

        target = databaseHelper.getTasbihTarget();
        count = databaseHelper.getTasbihCount();
    }

    private void increment() {

        count++;

        databaseHelper.updateTasbihCount(count);

        if (preferences.isTasbihHapticEnabled()) {
            requireView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

        updateUi();
    }

    private void updateUi() {
        countView.setText(String.valueOf(count));
        progressBar.setMax(target);
        progressBar.setProgress(Math.min(count, target));
        progressView.setText(getString(R.string.tasbih_progress, count, target));
    }
}
