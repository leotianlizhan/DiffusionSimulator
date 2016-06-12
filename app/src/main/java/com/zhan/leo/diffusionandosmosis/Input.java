package com.zhan.leo.diffusionandosmosis;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class Input implements View.OnTouchListener {

    public static Point tap = null;

    public static boolean touchDown = false;

    @Override
    public synchronized boolean onTouch(View view, MotionEvent motionEvent) {

        if(Game.state == GameState.InGame)
        {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                touchDown = true;
                tap = new Point((int)motionEvent.getX(), (int)motionEvent.getY());
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                touchDown = false;
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
            {
                tap = new Point((int)motionEvent.getX(), (int)motionEvent.getY());
            }
        }

        return true;
    }

}
