package com.example.mouseclicktest;

import android.util.Log;

// Represents the mouse
public class MouseClickHelper {

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


    // Callbacks

    void rightButtonClick() {
        Log.d("mouse", "Right mouse button click");
    }

    void rightButtonDown() {
        rightButtonPressed = true;
        Log.d("mouse", "Right mouse button held down");
    }

    void rightButtonUp() {
        rightButtonPressed = false;
        Log.d("mouse", "Right mouse button released");
    }

    void leftButtonClick() {
        Log.d("mouse", "Left mouse button click");
    }

    void leftButtonDown() {
        leftButtonPressed = true;
        Log.d("mouse", "Left mouse button held down");
    }

    void leftButtonUp() {
        leftButtonPressed = false;
        Log.d("mouse", "Left mouse button released");
    }

    // Interface for mouse activity

    public void touchLeftButtonDown(float x, float y) {
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

    public void touchRightButtonDown(float x, float y) {
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

    public void touchLeftButtonUp(float x, float y) {
        if (clickBehaviour == ClickBehaviour.CLASSIC) {
            leftButtonUp();
        }
        else if (clickBehaviour == ClickBehaviour.INVERTED) {
            rightButtonDown();
        }
    }

    public void touchRightButtonUp(float x, float y) {
        if (clickBehaviour == ClickBehaviour.CLASSIC) {
            rightButtonUp();
        }
        else if (clickBehaviour == ClickBehaviour.INVERTED) {
            rightButtonDown();
        }
    }

    public void touchLeftButtonMove(float x, float y) {
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

    public void touchRightButtonMove(float x, float y) {
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
