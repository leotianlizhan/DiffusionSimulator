package com.zhan.leo.diffusionandosmosis;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class ExplanationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explanation_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.explanationToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnBackPress();
            }
        });
    }
    private void handleOnBackPress()
    {
        //Not needed since we don't need to pass any information back
        //Intent goBackToMainScreen = new Intent();
        finish();
    }
}
