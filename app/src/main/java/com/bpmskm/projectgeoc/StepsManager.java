package com.bpmskm.projectgeoc;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepsManager implements SensorEventListener {

    private static final String TAG = "StepsManager";
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private int deviceBootSteps = -1;
    private int stepsAtSessionStart = 0;

    public StepsManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor == null) {
                Log.w(TAG, "Sensor licznika kroków (TYPE_STEP_COUNTER) nie jest dostępny na tym urządzeniu.");
            }
        } else {
            Log.e(TAG, "Nie udało się uzyskać dostępu do SensorManager.");
        }
    }

    public void startListening() {
        if (stepCounterSensor != null && sensorManager != null) {
            deviceBootSteps = -1;
            User currentUser = UserManager.getCurrentUser();
            if (currentUser != null) {
                stepsAtSessionStart = currentUser.getSteps();
            } else {
                stepsAtSessionStart = 0;
            }
            Log.d(TAG, "Rozpoczynanie nasłuchiwania. Kroki użytkownika na starcie sesji: " + stepsAtSessionStart);

            boolean registered = sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            if (registered) {
                Log.d(TAG, "Rozpoczęto nasłuchiwanie sensora kroków.");
            } else {
                Log.e(TAG, "Nie udało się zarejestrować listenera dla sensora kroków.");
            }
        } else {
            Log.w(TAG, "Nie można rozpocząć nasłuchiwania: sensor kroków lub manager nie jest dostępny.");
        }
    }

    public void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Zakończono nasłuchiwanie sensora kroków.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int currentDeviceSteps = (int) event.values[0];

            if (deviceBootSteps == -1) {
                deviceBootSteps = currentDeviceSteps;
                Log.d(TAG, "Pierwszy odczyt sensora w sesji. Wartość deviceBootSteps: " + deviceBootSteps);
            }

            int stepsMadeThisSensorSession = currentDeviceSteps - deviceBootSteps;

            if (stepsMadeThisSensorSession < 0) {
                Log.w(TAG, "Wykryto reset sensora kroków lub restart urządzenia podczas sesji. Resetowanie deviceBootSteps.");
                deviceBootSteps = currentDeviceSteps;
                stepsMadeThisSensorSession = 0;
            }

            User currentUser = UserManager.getCurrentUser();
            if (currentUser != null) {
                int totalUserSteps = stepsAtSessionStart + stepsMadeThisSensorSession;
                totalUserSteps = Math.max(0, totalUserSteps);

                if (currentUser.getSteps() != totalUserSteps) {
                    currentUser.setSteps(totalUserSteps);
                    Log.d(TAG, "Kroki zaktualizowane w UserManager: " + totalUserSteps +
                            " (Na starcie sesji: " + stepsAtSessionStart +
                            ", Zrobione przez sensor w tej sesji: " + stepsMadeThisSensorSession +
                            ", Odczyt z sensora: " + currentDeviceSteps +
                            ", deviceBootSteps: " + deviceBootSteps + ")");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}