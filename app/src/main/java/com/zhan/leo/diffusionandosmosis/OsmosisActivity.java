package com.zhan.leo.diffusionandosmosis;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Tianli on 2/21/2016.
 */
public class OsmosisActivity extends AppCompatActivity {

    private View decorView;
    private SurfaceView surfaceView;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.osmosis_layout);
        decorView = getWindow().getDecorView();
        surfaceView = (SurfaceView) findViewById(R.id.osmosisSurfaceView);
        surfaceView.setOnTouchListener(new Input());
        game = new Game(GameState.Osmosis);
        surfaceView.getHolder().addCallback(game);
    }

    private void handleOnBackPress()
    {
        game.stop();
        //Not needed since we don't need to pass any information back
        //Intent goBackToMainScreen = new Intent();
        game = null;
        finish();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    }
    private void cleanUI() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
            surfaceView.getHolder().setSizeFromLayout();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanUI();
    }
}
