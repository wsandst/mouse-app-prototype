package com.wsandst.mouseapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.mouseapp.R;
import com.wsandst.mouseapp.bluetooth.senders.RelativeMouseSender;

public class MouseButtonsListener {

    RelativeMouseSender rMouseSender;

    enum ClickBehaviour {
        CLASSIC, // Touch down to press down the mouse
        INVERTED, // Touch up to press down the mouse. More ergonomic but a bit confusing
        CLASSIC_FAST, // Click on touch up -> touch down. More logical but holding down is impossible now
        DRAG, // Drag your finger down to press
        CLASSIC_FAST_AND_DRAG // DRAG and CLASSIC_FAST combined. Logical but still allows for non-logical dragging
    }

    public ClickBehaviour clickBehaviour = ClickBehaviour.CLASSIC_FAST_AND_DRAG;

    public boolean rightButtonPressed = false;
    public boolean leftButtonPressed = false;

    private float leftPressY = 0;
    private float rightPressY = 0;

    MouseButtonsListener(RelativeMouseSender rMouseSender, View leftMouseButton, View rightMouseButton) {
        this.rMouseSender = rMouseSender;
        registerListeners(leftMouseButton, rightMouseButton);
    }

    // Bluetooth HID Mouse actions

    private void leftButtonClick() {
        Log.i("mouse", "Left mouse button click");
        rMouseSender.sendLeftClick();
    }

    private void leftButtonDown() {
        Log.i("mouse", "Left mouse button held down");
        leftButtonPressed = true;
        rMouseSender.sendLeftClickOn();
    }

    private void leftButtonUp() {
        Log.i("mouse", "Left mouse button released");
        leftButtonPressed = false;
        rMouseSender.sendLeftClickOff();
    }

    private void rightButtonClick() {
        Log.i("mouse", "Right mouse button click");
        rMouseSender.sendRightClick();
    }

    private void rightButtonDown() {
        Log.i("mouse", "Right mouse button held down");
        rightButtonPressed = true;
        rMouseSender.sendRightClickOn();
    }

    private void rightButtonUp() {
        Log.i("mouse", "Right mouse button released");
        rightButtonPressed = false;
        rMouseSender.sendRightClickOff();
    }

    // Right and left button view listeners

    private void registerListeners(View leftMouseButton, View rightMouseButton) {
        leftMouseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        touchLeftButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        touchLeftButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchLeftButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //mouse.touchLeftButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchLeftButtonMove(x, y);
                        break;
                }
                return true;
            }
        });

        rightMouseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        touchRightButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        touchRightButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchRightButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //mouse.touchRightButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchRightButtonMove(x, y);
                        break;
                }
                return true;
            }
        });
    }

    private void touchLeftButtonDown(float x, float y) {
        if (clickBehaviour == ClickBehaviour.CLASSIC) {
            leftButtonDown();
        }
        else if (clickBehaviour == ClickBehaviour.INVERTED) {
            leftButtonUp();
        }
        else if (clickBehaviour == ClickBehaviour.CLASSIC_FAST || clickBehaviour == ClickBehaviour.CLASSIC_FAST_AND_DRAG) {
            leftButtonClick();
        }
        leftPressY = y;
    }

    private void touchRightButtonDown(float x, float y) {
        if (clickBehaviour == ClickBehaviour.CLASSIC) {
            rightButtonDown();
        }
        else if (clickBehaviour == ClickBehaviour.INVERTED) {
            rightButtonUp();
        }
        else if (clickBehaviour == ClickBehaviour.CLASSIC_FAST || clickBehaviour == ClickBehaviour.CLASSIC_FAST_AND_DRAG) {
            rightButtonClick();
        }
        rightPressY = y;
    }

    private void touchLeftButtonUp(float x, float y) {
        if (clickBehaviour == ClickBehaviour.CLASSIC || clickBehaviour == ClickBehaviour.CLASSIC_FAST_AND_DRAG) {
            leftButtonUp();
        }
        else if (clickBehaviour == ClickBehaviour.INVERTED) {
            leftButtonDown();
        }
    }

    private void touchRightButtonUp(float x, float y) {
        if (clickBehaviour == ClickBehaviour.CLASSIC || clickBehaviour == ClickBehaviour.CLASSIC_FAST_AND_DRAG) {
            rightButtonUp();
        }
        else if (clickBehaviour == ClickBehaviour.INVERTED) {
            rightButtonDown();
        }
    }

    private void touchLeftButtonMove(float x, float y) {
        if (clickBehaviour == ClickBehaviour.DRAG || clickBehaviour == ClickBehaviour.CLASSIC_FAST_AND_DRAG) {
            float delta = y - leftPressY;
            if (!leftButtonPressed && delta > 70) {
                leftButtonDown();
            }
            if (leftButtonPressed && delta < 40) {
                leftButtonUp();
            }
        }
    }

    private void touchRightButtonMove(float x, float y) {
        if (clickBehaviour == ClickBehaviour.DRAG || clickBehaviour == ClickBehaviour.CLASSIC_FAST_AND_DRAG) {
            float delta = y - rightPressY;
            if (!rightButtonPressed && delta > 70) {
                rightButtonDown();
            }
            if (rightButtonPressed && delta < 40) {
                rightButtonUp();
            }
        }
    }
}
