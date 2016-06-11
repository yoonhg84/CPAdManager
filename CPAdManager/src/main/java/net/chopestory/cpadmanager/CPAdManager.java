package net.chopestory.cpadmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

/**
 * CPAdManager
 * Created by Chope on 16. 6. 11..
 */
public class CPAdManager extends CPContextObject implements OnAdRequestListener {
    private CPBannerAd[] bannerAds = null;

    private ViewGroup adLayout = null;
    private int currentBannerAdIndex = 0;

    public CPAdManager(@NonNull Context context, @NonNull ViewGroup layout, @NonNull IBannerAdsCreator bannerAdsCreator) {
        super(context);

        this.adLayout = layout;

        bannerAds = bannerAdsCreator.createBannerAds(context);
        Assert.assertTrue(bannerAds.length > 0);

        Log.d("AdManager", "init banner ads : " + bannerAds.length);

        for (CPBannerAd bannerAd : bannerAds) {
            bannerAd.setOnRequestListener(this);
        }

        Log.d("AdManager", "bannerAdIndex : " + currentBannerAdIndex);
    }

    public void destroy() {
        if (adLayout != null) {
            adLayout.removeAllViews();
        }

        for (CPBannerAd bannerAd : bannerAds) {
            bannerAd.destroy();
        }

        Log.d("AdManager", "banner ads destroyed");
    }

    public void requestBanner() {
        Assert.assertNotNull(bannerAds);

        if (adLayout == null) {
            Log.d("AdManager", "adLayout null!");
            return;
        }

        adLayout.removeAllViews();

        CPBannerAd bannerAd = bannerAds[currentBannerAdIndex];
        View view = bannerAd.getAdView();

        if (view != null) {
            adLayout.addView(view);
            bannerAd.requestAd();
        } else {
            Log.d("AdManager", "adView null!");
        }
    }

    @Override
    public void onRequestFailure() {
        currentBannerAdIndex = (currentBannerAdIndex + 1) % bannerAds.length;
        Log.d("AdManager", "bannerAdIndex : " + currentBannerAdIndex);
        requestBanner();
    }

    public interface IBannerAdsCreator {
        CPBannerAd[] createBannerAds(Context context);
    }
}
