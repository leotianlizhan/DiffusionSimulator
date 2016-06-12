package com.zhan.leo.diffusionandosmosis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.support.v7.widget.Toolbar;

/**
 * Created by Tianli on 2/14/16
 */
public class DiffusionActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private Game game;
    private View decorView;
    public static int toolbarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diffusion_layout);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.diffusionToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        decorView = getWindow().getDecorView();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnBackPress();
            }
        });
        toolbarHeight = toolbar.getHeight();
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setOnTouchListener(new Input());
        game = new Game(GameState.Diffusion);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
