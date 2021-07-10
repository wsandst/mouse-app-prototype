package com.wsandst.mouseapp;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.kircherelectronics.fsensor.filter.averaging.LowPassFilter;
import com.kircherelectronics.fsensor.filter.averaging.MeanFilter;
import com.kircherelectronics.fsensor.observer.SensorSubject;
import com.kircherelectronics.fsensor.sensor.FSensor;
import com.kircherelectronics.fsensor.sensor.acceleration.LowPassLinearAccelerationSensor;
import com.wsandst.mouseapp.bluetooth.senders.RelativeMouseSender;

public class MouseSensorListener extends AppCompatActivity {
    private RelativeMouseSender rMouseSender;

    private FSensor fSensor;
    private MeanFilter meanFilter = new MeanFilter();
    private LowPassFilter lowPassFilter = new LowPassFilter();

    MouseSensorProcessing sensorProcessing = new MouseSensorProcessing();

    public MouseSensorListener(Context ctx, RelativeMouseSender rMouseSender){
        this.rMouseSender = rMouseSender;
        fSensor = new LowPassLinearAccelerationSensor(ctx);
        fSensor.register(sensorObserver);
        fSensor.start();
        meanFilter.setTimeConstant(0.2f);
        lowPassFilter.setTimeConstant(0.2f);

        // Update velocity handler
        Handler handler = new Handler(ctx.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                sendMouseMoveByBluetooth();
                handler.postDelayed(this, 10);
            }
        });
    }

    private SensorSubject.SensorObserver sensorObserver = new SensorSubject.SensorObserver() {
        @Override
        public void onSensorChanged(float[] values) {
            //float[] filteredValues = meanFilter.filter(lowPassFilter.filter(values));
            float[] filteredValues = lowPassFilter.filter(values);
            //float[] filteredValues = meanFilter.filter(values);
            sensorProcessing.updateAcceleration(filteredValues);
        }
    };

    void sendMouseMoveByBluetooth() {
        int x = (int) (sensorProcessing.getVelocityX() * 300);
        int y = (int) (sensorProcessing.getVelocityY() * 300);
        Log.i("mouse", String.format("Sending mouse move %s %s", x, y));
        rMouseSender.sendMove(x, -y);
    }

    @Override
    public void onResume() {
        super.onResume();
        fSensor.register(sensorObserver);
        fSensor.start();
    }

    @Override
    public void onPause() {
        fSensor.unregister(sensorObserver);
        fSensor.stop();

        super.onPause();
    }
}
