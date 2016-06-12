package net.chopestory.cpadmanagersample;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import net.chopestory.cpadmanager.CPContextObject;
import net.chopestory.cpadmanager.CPInterstitialAd;
import net.chopestory.cpadmanager.OnInterstitialAdRequestListener;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * CPAdManagerSample
 * Created by Chope on 16. 6. 12..
 */
public class CPAdmobInterstitialAd extends CPContextObject implements CPInterstitialAd {
    private InterstitialAd interstitialAd = null;
    private WeakReference<OnInterstitialAdRequestListener> requestListenerWeakReference = null;

    private AdListener adListener = new com.google.android.gms.ads.AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);

            Log.d("AdManager", "admob interstitial ad request failed");

            if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
                requestListenerWeakReference.get().onInterstitialRequestFailed();
            }
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();

            if (new Random().nextBoolean()) {
                adListener.onAdFailedToLoad(0);
                return;
            }

            Log.d("AdManager", "admob interstitial ad request loaded");

            if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
                requestListenerWeakReference.get().onInterstitialRequestSuccess();
            }
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();

            Log.d("AdManager", "admob interstitial ad closed");

            requestAd();

            if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
                requestListenerWeakReference.get().onInterstitialClose();
            }
        }
    };

    public CPAdmobInterstitialAd(Context context) {
        super(context);
    }

    @Override
    public void requestAd() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        if (interstitialAd == null) {
            interstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
            interstitialAd.setAdUnitId(context.getString(R.string.admob_interstitial_ad_unit_id));
            interstitialAd.setAdListener(adListener);

        }

        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        Log.d("AdManager", "request admob interstitial ad");
    }

    @Override
    public void destroy() {
        interstitialAd = null;

        Log.d("AdManager", "destroy admob interstitial ad");
    }

    @Override
    public void showAd() {
        if (interstitialAd != null) {
            interstitialAd.show();

            Log.d("AdManager", "show admob interstitial ad");
        }
    }

    @Override
    public boolean isLoaded() {
        return interstitialAd != null && interstitialAd.isLoaded();
    }

    @Override
    public void setOnInterstitialAdRequestListener(OnInterstitialAdRequestListener listener) {
        requestListenerWeakReference = new WeakReference<>(listener);
    }
}
