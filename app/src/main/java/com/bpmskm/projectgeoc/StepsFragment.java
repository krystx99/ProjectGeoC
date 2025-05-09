package com.bpmskm.projectgeoc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class StepsFragment extends Fragment implements SensorEventListener {

    private TextView stepCountTextView;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;
    private boolean isCounterSensorPresent;
    private static final int ACTIVITY_RECOGNITION_REQUEST_CODE = 100;
    private static final String TAG = "StepsFragment";
    private ProfileFragment profileFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);
        stepCountTextView = view.findViewById(R.id.stepCountTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            stepCountTextView.setText("Krokomierz niedostępny na tym urządzeniu");
            isCounterSensorPresent = false;
        }

        // Pobierz instancję ProfileFragment
        FragmentManager fragmentManager = getParentFragmentManager(); // lub getChildFragmentManager(), w zależności od struktury
        profileFragment = (ProfileFragment) fragmentManager.findFragmentByTag("profile"); // Użyj tagu, jeśli masz

        checkActivityRecognitionPermission();
    }

    private void checkActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Poproś o uprawnienie
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        ACTIVITY_RECOGNITION_REQUEST_CODE);
            } else {
                // Uprawnienie już jest, zarejestruj listener
                registerStepCounter();
            }
        } else {
            // Na starszych wersjach nie jest wymagane, zarejestruj listener
            registerStepCounter();
        }
    }

    private void registerStepCounter() {
        if (isCounterSensorPresent && sensorManager != null && stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerStepCounter(); // Zarejestruj listener ponownie, gdy fragment wraca do widoczności
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this); // Wyrejestruj listener, gdy fragment nie jest widoczny
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) event.values[0];
            stepCountTextView.setText("Liczba kroków: " + stepCount);
            Log.d(TAG, "Liczba kroków: " + stepCount);
            // Aktualizuj ProfileFragment
            if (profileFragment != null) {
                profileFragment.setKrokCount(stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Dokładność sensora zmieniona: " + accuracy);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Uprawnienie zostało przyznane, zarejestruj listener
                registerStepCounter();
            } else {
                Toast.makeText(requireContext(), "Wymagane uprawnienie do zliczania kroków", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
