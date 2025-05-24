package com.bpmskm.projectgeoc;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class StepsManager implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int initialStepCount = -1;

    public StepsManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
    }

    public void startListening() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == -1) {
                initialStepCount = (int) event.values[0];
            }
            int stepsSinceStart = (int) event.values[0] - initialStepCount;

            User currentUser = UserManager.getCurrentUser();
            if (currentUser != null) {
                currentUser.setSteps(stepsSinceStart);
            }
        }//TUTAJ ZAPISANIE DO FIREBASE
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

