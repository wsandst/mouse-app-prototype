package com.example.mouseclicktest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MouseActivity extends AppCompatActivity {

    MouseClickHelper mouse = new MouseClickHelper();

    void leftMouseClick() {
        Log.d("mouse", "Left mouse button clicked");
    }

    void rightMouseClick() {
        Log.d("mouse", "Right mouse button clicked");
    }

    void mouseScroll(int amount) {
        Log.d("mouse", String.format("Mouse scrolled %s amount", amount));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        View palmView = (View) findViewById(R.id.palmView);
        palmView.setFocusable(false);
        palmView.setFocusableInTouchMode(false);
        palmView.setClickable(false);

        View leftClickView = (View) findViewById(R.id.leftClickView);
        leftClickView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mouse.touchLeftButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mouse.touchLeftButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mouse.touchLeftButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //mouse.touchLeftButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mouse.touchLeftButtonMove(x, y);
                        break;
                }
                return true;
            }
        });


        View rightClickView = (View) findViewById(R.id.rightClickView);
        rightClickView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mouse.touchRightButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mouse.touchRightButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mouse.touchRightButtonDown(x, y);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //mouse.touchRightButtonUp(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mouse.touchRightButtonMove(x, y);
                        break;
                }
                return true;
            }
        });

    }
}