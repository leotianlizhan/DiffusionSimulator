package com.zhan.leo.diffusionandosmosis;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static int height;
    public static int width;
    public static int realHeight;
    public static float scale;
    public static float densityDpi;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Display d = getWindowManager().getDefaultDisplay();
        DisplayMetrics m = new DisplayMetrics();
        d.getMetrics(m);
        scale = m.density;
        height = m.heightPixels;
        width = m.widthPixels;
        densityDpi = m.densityDpi;
        if(Build.VERSION.SDK_INT >= 17) {
            d.getRealMetrics(m);
        }
        realHeight = m.heightPixels;

        decorView = getWindow().getDecorView();
        showSystemUI();
    }

    public void startDiffusion(View v)
    {
        //start a new activity
        Intent diffusionIntent = new Intent(this, DiffusionActivity.class);
        startActivity(diffusionIntent);
    }
    public void startOsmosis(View v)
    {
        Intent osmosisIntent = new Intent(this, OsmosisActivity.class);
        startActivity(osmosisIntent);
    }
    public void startExplanation(View v)
    {
        Intent explanationIntent = new Intent(this, ExplanationActivity.class);
        startActivity(explanationIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
