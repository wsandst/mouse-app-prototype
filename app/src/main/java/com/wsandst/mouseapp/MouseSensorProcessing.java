package com.wsandst.mouseapp;

import android.util.Log;

import java.util.ArrayList;

public class MouseSensorProcessing {
    private double xVelocity, yVelocity;

    class DataPoint {

        DataPoint(float[] accData, double t, double dT) {
            x = accData[0];
            y = accData[1];
            z = accData[2];
            this.t = t;
            this.dT = dT;
        }

        public double x;
        public double y;
        public double z;
        public double t;
        public double dT;
    }

    // Keep track of previous acceleration data
    private ArrayList<DataPoint> accelerationData = new ArrayList<DataPoint>();
    private int currentPointIndex = 0;

    private static final double NS2S = 1.0f / 1000000000.0f;
    private long prevTime = 0;

    final static double STD_TIME_CUTOFF = 0.1;
    final static double STD_CUTOFF = 0.01;
    final static double SIGN_CHANGE_CUTOFF_DUR = 0.3;
    final static double Z_AIR_CUTOFF = 0.1;
    final static double Z_AIR_CUTOFF_DUR = 0.7;

    private double xVelocityStopTime = 0;
    private double yVelocityStopTime = 0;
    private double airStopTime = 0;


    void updateAcceleration(float[] accData) {
        long timestamp = System.nanoTime();
        double dT;
        if (prevTime != 0) {
            dT = (timestamp - prevTime) * NS2S;
        }
        else {
            dT = 0;
        }
        DataPoint datapoint = new DataPoint(accData, timestamp*NS2S, dT);
        accelerationData.add(datapoint);
        if (accelerationData.size() > 1) {
            updateVelocity();
        }
        accelerationData.add(datapoint);
        prevTime = timestamp;
    }

    void updateVelocity() {
        if (Double.isNaN(xVelocity)) {
            xVelocity = 0;
        }
        if (Double.isNaN(yVelocity)) {
            yVelocity = 0;
        }
        // Trapezoid integration for velocity
        DataPoint curPoint = accelerationData.get(accelerationData.size()-1);
        //DataPoint prevPoint = accelerationData.get(accelerationData.size()-2);

        double dT = curPoint.dT;

        if (curPoint.t - airStopTime > Z_AIR_CUTOFF_DUR) {
            if (curPoint.t - xVelocityStopTime > SIGN_CHANGE_CUTOFF_DUR) {
                double prevVelocityX = xVelocity;
                xVelocity += dT * curPoint.x;
                //xVelocity += dT * 0.5 * (prevPoint.x + curPoint.x);

                // Check if xVelocity changed sign
                if (prevVelocityX * xVelocity < 0) {
                    xVelocityStopTime = curPoint.t;
                    xVelocity = 0;
                }
            }

            if (curPoint.t - yVelocityStopTime > SIGN_CHANGE_CUTOFF_DUR) {
                double prevVelocityY = yVelocity;
                yVelocity += dT * curPoint.y;
                //yVelocity += dT * 0.5 * (prevPoint.y + curPoint.y);

                // Check if xVelocity changed sign
                if (prevVelocityY * yVelocity < 0) {
                    yVelocityStopTime = curPoint.t;
                    yVelocity = 0;
                }
            }
        }

        // Set velocity to 0 if standard deviation is too low
        //applyStandardDevCutoff();
        applyAccelerationCutoff();
        applyAirCutoff();

        Log.d("sensor", String.format("Velocity: (%.4f, %.4f), dT: (%s)", xVelocity, yVelocity, dT));
        Log.d("sensor", String.format("Acceleration: (%.4f, %.4f, %.4f), dT: (%s)", curPoint.x, curPoint.y, curPoint.z, dT));
    }

    void applyAirCutoff() {
        DataPoint curPoint = accelerationData.get(accelerationData.size()-1);
        if (curPoint.z > Z_AIR_CUTOFF) {
            xVelocity = 0;
            yVelocity = 0;
            airStopTime = curPoint.t;
        }
    }

    void applyStandardDevCutoff() {
        DataPoint currentPoint = accelerationData.get(currentPointIndex);
        DataPoint lastPoint = accelerationData.get(accelerationData.size()-1);
        while (lastPoint.t - currentPoint.t > STD_TIME_CUTOFF) {
            currentPointIndex += 1;
            currentPoint = accelerationData.get(currentPointIndex);
        }
        int n = accelerationData.size() - currentPointIndex;
        if (n > 1) {
            DataPoint mean = new DataPoint(new float[]{0.0f, 0.0f, 0.0f}, 0, 0);
            for (int i = currentPointIndex; i < accelerationData.size(); i++) {
                DataPoint current = accelerationData.get(i);
                mean.x += current.x;
                mean.y += current.y;
            }
            mean.x /= n;
            mean.y /= n;
            DataPoint square_diff = new DataPoint(new float[]{0.0f, 0.0f, 0.0f}, 0, 0);
            for (int i = currentPointIndex; i < accelerationData.size(); i++) {
                DataPoint current = accelerationData.get(i);
                square_diff.x += Math.pow(mean.x - current.x, 2);
                square_diff.y += Math.pow(mean.y - current.y, 2);
            }
            double stdX = square_diff.x / (n - 1);
            double stdY = square_diff.y / (n - 1);
            Log.d("sensor", String.format("stdX: %f", stdX));
            if (stdX < STD_CUTOFF) {
                //Log.d("sensor", "X STD Cutoff");
                xVelocity = 0;
            }
            if (stdY < (STD_CUTOFF-0.005)) {
                //yVelocity = 0;
            }
        }
    }

    void applyAccelerationCutoff() {
        if (accelerationData.size() < 2) {
            return;
        }
        DataPoint lastPoint = accelerationData.get(accelerationData.size()-1);
        int n = 1;
        double meanX = 0;
        double meanY = 0;
        while (n+1 < accelerationData.size()) {
            DataPoint indexPoint = accelerationData.get(accelerationData.size() - 1 - n);
            meanX += Math.abs(indexPoint.x);
            meanY += Math.abs(indexPoint.y);
            n += 1;
            if(lastPoint.t - indexPoint.t > STD_TIME_CUTOFF) {
                break;
            }
        }
        meanX /= n;
        meanY /= n;

        if (meanX < 0.05) {
            xVelocity = 0;
        }
        if (meanY < 0.05) {
            yVelocity = 0;
        }
    }

    double calculateDeltaTime() {
        long timestamp = System.nanoTime();
        return (timestamp - prevTime) * NS2S;
    }

    public double getVelocityX() {
        return xVelocity;
    }

    public double getVelocityY() {
        return yVelocity;
    }
}
