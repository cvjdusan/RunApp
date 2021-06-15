package rs.ac.bg.etf.running.workouts;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.users.Session;

public class LifecycleAwareStepCounter implements DefaultLifecycleObserver {

    private SensorManager sensorManager = null;
    private Context context;

    private MainActivity mainActivity = (MainActivity) Session.getMainActivity();

    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
       //     TextView textView = mainActivity.findViewById(R.id.steps);
        //    String stepsString = textView.getText().toString();
         //   int stepsNum = Integer.parseInt(stepsString.split(":")[1].substring(1));
     //       stepsNum++;
          //  textView.setText("Steps: " + stepsNum);
            Intent local = new Intent();
            local.setAction("stepCounter");
            context.sendBroadcast(local);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Inject
    public LifecycleAwareStepCounter() {
    }

    public void start(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(context, "Sensor for step counting is missing.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }
}
