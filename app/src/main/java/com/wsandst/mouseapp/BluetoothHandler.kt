package com.wsandst.mouseapp

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.content.Context
import android.os.Handler
import android.util.Log
import com.wsandst.mouseapp.bluetooth.senders.RelativeMouseSender

class BluetoothHandler {

    var device : BluetoothDevice? = null
    var hidDevice : BluetoothHidDevice? = null

    var rMouseSender : RelativeMouseSender? = null

    public fun onStart(ctx : Context, onConnectionCallback : () -> Unit) {
        Log.i("bluetooth", "Initializing")

        BluetoothController.init(ctx)
        BluetoothController.getSender { hidd, device ->
            Log.i("bluetooth", "Callback called")
            this.device = device;
            this.hidDevice = hidd;
            val mainHandler = Handler(ctx.mainLooper)
            mainHandler.post(object : Runnable {
                override fun run() {
                    rMouseSender        = RelativeMouseSender(hidd,device)
                }
            })
        }

        BluetoothController.getDisconnector{
            Log.i("bluetooth", "Disconnected")

            val mainHandler = Handler(ctx.mainLooper)
            mainHandler.post(object : Runnable {
                override fun run() {
                    // Handle bluetooth disconnection
                }
            })
        }

        // Detects if the connection truly is established
        val loadHandler = Handler(ctx.mainLooper);
        loadHandler.post(object : Runnable {
            override fun run() {
                if (rMouseSender != null && BluetoothController.connectionComplete) {
                    Log.d("bluetooth", "Hid Device Connection Complete");
                    onConnectionCallback();
                }
                else {
                    loadHandler.postDelayed(this, 50);
                }
            }
        })

        Log.i("bluetooth", "Initialization complete" );
    }
}