package net.chopestory.cpadmanagersample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import net.chopestory.cpadmanager.CPBannerAd;
import net.chopestory.cpadmanager.CPContextObject;
import net.chopestory.cpadmanager.OnBannerAdRequestListener;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * CPAdManager
 * Created by Chope on 16. 6. 11..
 */
public class CPAdmobBannerAd extends CPContextObject implements CPBannerAd {
    protected AdView adView = null;
    protected WeakReference<OnBannerAdRequestListener> requestListenerReference = null;

    public CPAdmobBannerAd(@NonNull Context context) {
        super(context);
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);

            Log.d("AdManager", "admob banner ad request failed");

            if (requestListenerReference != null && requestListenerReference.get() != null) {
                requestListenerReference.get().onBannerRequestFailure();
            }
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();

            if (new Random().nextBoolean()) {
                adListener.onAdFailedToLoad(0);
                return;
            }

            Log.d("AdManager", "admob banner ad request success");
        }
    };

    @Override
    public AdView getAdView() {
        Context context = getContext();

        if (context == null) {
            return null;
        }

        if (adView == null) {
            adView = new AdView(context);
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(context.getString(R.string.admob_banner_ad_unit_id));
            adView.setAdListener(adListener);

            Log.d("AdManager", "admob banner adView created");
        }
        return adView;
    }

    @Override
    public void requestAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("3E7FAD19B95B5A4B3048CF6A5ED5D7D1")
                .build();

        AdView adView = getAdView();

        if (adView != null) {
            adView.loadAd(adRequest);
            Log.d("AdManager", "request admob banner ad");
        }
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;

            Log.d("AdManager", "admob banner destroyed");
        }
    }

    @Override
    public void setOnBannerAdRequestListener(OnBannerAdRequestListener listener) {
        requestListenerReference = new WeakReference<>(listener);
    }
}
