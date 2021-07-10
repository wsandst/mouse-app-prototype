package com.wsandst.mouseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.mouseapp.R;
import com.wsandst.mouseapp.bluetooth.senders.RelativeMouseSender;

import kotlin.Unit;

public class MouseActivity extends AppCompatActivity {

    MouseButtonsListener mouseButtonsListener;

    BluetoothHandler bluetoothHandler = new BluetoothHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        // Ignore all touches in the palm area
        View palmView = (View) findViewById(R.id.palmView);
        palmView.setFocusable(false);
        palmView.setFocusableInTouchMode(false);
        palmView.setClickable(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Start the bluetooth. This will call onBluetoothConnectionEstablished once finished
        // Takes quite some time, ~5s. Can this be improved?
        // TODO: Add visual indicator for bluetooth connection loading
        bluetoothHandler.onStart(this, this::onBluetoothConnectionEstablished);
    }

    // Callback from BluetoothHandler
    protected Unit onBluetoothConnectionEstablished() {
        View leftClickView = (View) findViewById(R.id.leftClickView);
        View rightClickView = (View) findViewById(R.id.rightClickView);

        mouseButtonsListener = new MouseButtonsListener(bluetoothHandler.getRMouseSender(), leftClickView, rightClickView);
        return null;
    }

}