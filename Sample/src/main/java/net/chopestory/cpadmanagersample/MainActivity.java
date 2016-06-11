package net.chopestory.cpadmanagersample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import junit.framework.Assert;

import net.chopestory.cpadmanager.CPAdManager;

public class MainActivity extends AppCompatActivity {
    private CPAdManager adManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup adLayout = (ViewGroup) findViewById(R.id.adLayout);
        Assert.assertNotNull(adLayout);

        adManager = new CPAdManager(this, adLayout, new BannerAdsCreator());
        adManager.requestBanner();

        Button nextButton = (Button) findViewById(R.id.nextActivityButton);
        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, NextActivity.class));
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        adManager.destroy();
        super.onDestroy();
    }
}
