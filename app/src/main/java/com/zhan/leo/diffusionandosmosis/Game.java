package com.zhan.leo.diffusionandosmosis;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Game implements SurfaceHolder.Callback {

    public static final ConcurrentLinkedQueue<Particle> particles = new ConcurrentLinkedQueue<>();

    private SurfaceHolder holder;

    private Thread gameThread;

    private GameLoop gm;

    public static GameState state = GameState.None;

    public Game(GameState gameState)
    {
        state = gameState;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        this.holder = holder;

        start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        stop();

    }

    public void start() {

        gm = new GameLoop(holder, particles, state);
        gameThread = new Thread(gm);
        gameThread.start();
        state = GameState.InGame;
    }

    public void stop() {

        gm.stop();

        state = GameState.None;

        gameThread = null;

        //try {
        //    gameThread.join();
        //} catch (InterruptedException ie) {
        //    ie.printStackTrace();
        //}

    }
}