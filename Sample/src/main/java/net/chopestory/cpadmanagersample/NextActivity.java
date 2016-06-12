package net.chopestory.cpadmanagersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import junit.framework.Assert;

import net.chopestory.cpadmanager.CPAdManager;

/**
 * CPAdManagerSample
 * Created by Chope on 16. 6. 11..
 */
public class NextActivity extends AppCompatActivity implements CPAdManager.OnAdManagerInterstitialListener {
    private CPAdManager adManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        ViewGroup adLayout = (ViewGroup) findViewById(R.id.adLayout);
        Assert.assertNotNull(adLayout);

        getSupportActionBar().hide();

        adManager = new CPAdManager(this, adLayout, new AdsCreator(), new AdsCreator());
        adManager.setOnAdManagerInterstitialListener(this);
        adManager.requestBannerAd();
        adManager.requestInterstitialAd();

        Button button = (Button) findViewById(R.id.showInterstitialButton);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adManager.isInterstitialAdLoaded()) {
                        adManager.showInterstitialAd();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        adManager.destroy();
        super.onDestroy();
    }

    @Override
    public void onAllRequestsFailed(CPAdManager adManager) {
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClosed(CPAdManager adManager) {
        Toast.makeText(this, "closed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSucccess(CPAdManager adManager) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
    }
}
