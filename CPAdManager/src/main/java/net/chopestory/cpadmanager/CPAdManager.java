package net.chopestory.cpadmanager;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import java.lang.ref.WeakReference;

/**
 * CPAdManager
 * Created by Chope on 16. 6. 11..
 */
public class CPAdManager extends CPContextObject implements OnBannerAdRequestListener, OnInterstitialAdRequestListener {
    private CPBannerAd[] bannerAds = null;
    private CPInterstitialAd[] interstitialAds = null;
    private int currentBannerAdIndex = 0;
    private int currentInterstitialAdIndex = 0;
    private ViewGroup adLayout = null;

    private WeakReference<OnAdManagerInterstitialListener> adManagerInterstitialListenerWeakReference = null;

    public long reloadDelayMillisAfterAllBannerAdRequestsFailed = 30000;

    private CPAdManager(@NonNull Context context, @NonNull ViewGroup layout) {
        super(context);

        this.adLayout = layout;
    }

    public CPAdManager(@NonNull Context context, @NonNull ViewGroup layout, @NonNull BannerAdsCreator bannerAdsCreator) {
        this(context, layout);

        configureBannerAds(bannerAdsCreator);
    }

    public CPAdManager(@NonNull Context context, @NonNull InterstitialAdsCreator interstitialAdsCreator) {
        super(context);

        configureInterstitialAds(interstitialAdsCreator);
    }

    public CPAdManager(@NonNull Context context, @NonNull ViewGroup layout, @NonNull InterstitialAdsCreator interstitialAdsCreator, @NonNull BannerAdsCreator bannerAdsCreator) {
        this(context, layout);

        configureBannerAds(bannerAdsCreator);
        configureInterstitialAds(interstitialAdsCreator);
    }

    private void configureBannerAds(BannerAdsCreator creator) {
        bannerAds = creator.createBannerAds(getContext());
        Assert.assertTrue(bannerAds.length > 0);

        Log.d("AdManager", "init banner ads : " + bannerAds.length);

        for (CPBannerAd bannerAd : bannerAds) {
            bannerAd.setOnBannerAdRequestListener(this);
        }

        Log.d("AdManager", "bannerAdIndex : " + currentBannerAdIndex);
    }

    private void configureInterstitialAds(InterstitialAdsCreator creator) {
        interstitialAds = creator.createInterstitialAds(getContext());
        Assert.assertTrue(interstitialAds.length > 0);

        Log.d("AdManager", "init interstitial ads : " + interstitialAds.length);

        for (CPInterstitialAd interstitialAd : interstitialAds) {
            interstitialAd.setOnInterstitialAdRequestListener(this);
        }

        Log.d("AdManager", "interstitialAdIndex : " + currentInterstitialAdIndex);
    }

    public void destroy() {
        if (adLayout != null) {
            adLayout.removeAllViews();
        }

        if (bannerAds != null) {
            for (CPBannerAd bannerAd : bannerAds) {
                bannerAd.destroy();
            }
        }

        if (interstitialAds != null) {
            for (CPInterstitialAd interstitialAd : interstitialAds) {
                interstitialAd.destroy();
            }
        }

        Log.d("AdManager", "banner ads destroyed");
    }

    public void setOnAdManagerInterstitialListener(OnAdManagerInterstitialListener listener) {
        this.adManagerInterstitialListenerWeakReference = new WeakReference<>(listener);
    }

    public void requestBannerAd() {
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

    public void requestInterstitialAd() {
        Assert.assertNotNull(interstitialAds);

        CPInterstitialAd interstitialAd = interstitialAds[currentInterstitialAdIndex];
        interstitialAd.requestAd();
    }

    public void showInterstitialAd() {
        Assert.assertTrue(currentInterstitialAdIndex < interstitialAds.length);

        final CPInterstitialAd interstitialAd = interstitialAds[currentInterstitialAdIndex];
        interstitialAd.showAd();
    }

    public boolean isInterstitialAdLoaded() {
        Assert.assertTrue(currentInterstitialAdIndex < interstitialAds.length);

        CPInterstitialAd interstitialAd = interstitialAds[currentInterstitialAdIndex];
        return interstitialAd.isLoaded();
    }

    @Override
    public void onBannerRequestFailure() {
        currentBannerAdIndex = (currentBannerAdIndex + 1) % bannerAds.length;
        Log.d("AdManager", "bannerAdIndex : " + currentBannerAdIndex);

        if (currentBannerAdIndex == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestBannerAd();
                }
            }, reloadDelayMillisAfterAllBannerAdRequestsFailed);
        } else {
            requestBannerAd();
        }
    }

    @Override
    public void onInterstitialRequestFailed() {
        currentInterstitialAdIndex = (currentInterstitialAdIndex + 1) % interstitialAds.length;

        if (currentInterstitialAdIndex == 0) {
            if (adManagerInterstitialListenerWeakReference != null && adManagerInterstitialListenerWeakReference.get() != null) {
                adManagerInterstitialListenerWeakReference.get().onAllRequestsFailed(this);
            }
        } else {
            requestInterstitialAd();
        }
    }

    @Override
    public void onInterstitialRequestSuccess() {
        if (adManagerInterstitialListenerWeakReference != null && adManagerInterstitialListenerWeakReference.get() != null) {
            adManagerInterstitialListenerWeakReference.get().onRequestSucccess(this);
        } else {
            CPInterstitialAd interstitialAd = interstitialAds[currentInterstitialAdIndex];
            interstitialAd.showAd();
        }
    }

    @Override
    public void onInterstitialClose() {
        if (adManagerInterstitialListenerWeakReference != null && adManagerInterstitialListenerWeakReference.get() != null) {
            adManagerInterstitialListenerWeakReference.get().onAdClosed(this);
        }
    }

    public interface BannerAdsCreator {
        CPBannerAd[] createBannerAds(Context context);
    }

    public interface InterstitialAdsCreator {
        CPInterstitialAd[] createInterstitialAds(Context context);
    }

    public interface OnAdManagerInterstitialListener {
        void onAllRequestsFailed(CPAdManager adManager);
        void onAdClosed(CPAdManager adManager);
        void onRequestSucccess(CPAdManager adManager);
    }
}
