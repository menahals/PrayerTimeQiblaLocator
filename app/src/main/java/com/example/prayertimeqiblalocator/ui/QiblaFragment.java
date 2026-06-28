package com.example.prayertimeqiblalocator.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prayertimeqiblalocator.R;

public class QiblaFragment extends Fragment implements SensorEventListener {

    private ImageView imageNeedle;
    private TextView textDegrees;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private float currentDegree = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qibla, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageNeedle = view.findViewById(R.id.image_qibla_needle);
        textDegrees = view.findViewById(R.id.text_qibla_degrees);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            float[] R_matrix = new float[9];
            float[] I_matrix = new float[9];
            if (SensorManager.getRotationMatrix(R_matrix, I_matrix, lastAccelerometer, lastMagnetometer)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R_matrix, orientation);
                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

                // Qibla direction for UAE is approximately 255-260 degrees from North
                // For simplicity, we'll use a fixed angle or just show the compass working
                float qiblaAngle = 258f; 
                float needleDegree = qiblaAngle - azimuthInDegrees;

                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        needleDegree,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                ra.setDuration(210);
                ra.setFillAfter(true);

                imageNeedle.startAnimation(ra);
                currentDegree = needleDegree;
                
                textDegrees.setText(String.format("%.0f°", azimuthInDegrees));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
