package com.wsandst.mouseapp

import android.content.Context
import android.os.Handler
import android.util.Log
import com.wsandst.mouseapp.bluetooth.senders.RelativeMouseSender

class BluetoothHandler {

    private var rMouseSender : RelativeMouseSender? = null

    public fun onStart(ctx : Context) {
        Log.i("bluetooth", "Initializing")

        BluetoothController.init(ctx)
        BluetoothController.getSender { hidd, device ->
            Log.i("bluetooth", "Callback called")
            val mainHandler = Handler(ctx.mainLooper)
            mainHandler.post(object : Runnable {
                override fun run() {
                    rMouseSender        = RelativeMouseSender(hidd,device)

                    // Connect callbacks
                    /*val viewTouchListener   = ViewListener(hidd, device, rMouseSender)
                    val mDetector           = CustomGestureDetector(getContext(), GestureDetectListener(rMouseSender))
                    val gTouchListener      = object : View.OnTouchListener {
                        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                            Log.i(TAG, "onStart::mainLoop run::onTouch v=${v}, event=${event}")
                            return mDetector.onTouchEvent(event)
                        }
                    }

                    val composite : CompositeListener = CompositeListener()
                    composite.registerListener(gTouchListener)
                    composite.registerListener(viewTouchListener)
                    trackPadView.setOnTouchListener(composite) */
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
                    Log.d("bluetooth", "CONNECTED FINALLY!!!");
                }
                else {
                    loadHandler.postDelayed(this, 50);
                }
            }
        })

        Log.i("bluetooth", "Initialization complete" );
    }
}