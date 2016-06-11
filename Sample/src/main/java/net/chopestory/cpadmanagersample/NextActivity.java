package net.chopestory.cpadmanagersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import junit.framework.Assert;

import net.chopestory.cpadmanager.CPAdManager;

/**
 * CPAdManagerSample
 * Created by Chope on 16. 6. 11..
 */
public class NextActivity extends AppCompatActivity {
    private CPAdManager adManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        ViewGroup adLayout = (ViewGroup) findViewById(R.id.adLayout);
        Assert.assertNotNull(adLayout);

        adManager = new CPAdManager(this, adLayout, new BannerAdsCreator());
        adManager.requestBanner();
    }

    @Override
    protected void onDestroy() {
        adManager.destroy();
        super.onDestroy();
    }
}
