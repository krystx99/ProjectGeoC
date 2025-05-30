
package com.bpmskm.projectgeoc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.junit.Test;

public class StepsManagerTest {
    private StepsManager stepsManager;

    @Test
    public void testStepUpdateAffectsUser() {
        User user = new User("Test", 1000);
        UserManager.setCurrentUser(user);

        // Symuluj zmianę krokomierza
        int currentValue = 1025;

        StepsManager manager = mock(StepsManager.class);
        manager.onSensorChanged(createMockSensorEvent(currentValue, Sensor.TYPE_STEP_COUNTER));

        assertEquals(25, UserManager.getCurrentUser().getSteps());
    }
    private SensorEvent createMockSensorEvent(float value, int sensorType) {
        SensorEvent event = mock(SensorEvent.class);
        Sensor sensor = mock(Sensor.class);
        when(event.sensor).thenReturn(sensor);
        when(sensor.getType()).thenReturn(sensorType);
        when(event.values).thenReturn(new float[]{value});
        return event;
    }
}
