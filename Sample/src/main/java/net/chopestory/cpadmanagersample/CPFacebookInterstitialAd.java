package net.chopestory.cpadmanagersample;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import net.chopestory.cpadmanager.CPContextObject;
import net.chopestory.cpadmanager.CPInterstitialAd;
import net.chopestory.cpadmanager.OnInterstitialAdRequestListener;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * CPAdManagerSample
 * Created by Chope on 16. 6. 12..
 */
public class CPFacebookInterstitialAd extends CPContextObject implements CPInterstitialAd, InterstitialAdListener {
    private InterstitialAd interstitialAd = null;
    private WeakReference<OnInterstitialAdRequestListener> requestListenerWeakReference = null;

    public CPFacebookInterstitialAd(Context context) {
        super(context);
    }

    @Override
    public void requestAd() {
        Context context = getContext();

        if (context == null) {
            return;
        }

        if (interstitialAd == null) {
            interstitialAd = new com.facebook.ads.InterstitialAd(context, context.getString(R.string.facebook_interstitial_placement_id));
            interstitialAd.setAdListener(this);
        }

        interstitialAd.loadAd();

        Log.d("AdManager", "request facebook interstitial ad");
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }

        Log.d("AdManager", "destroy facebook interstitial ad");
    }

    @Override
    public void showAd() {
        if (interstitialAd != null) {
            interstitialAd.show();

            Log.d("AdManager", "show facebook interstitial ad");
        }
    }

    @Override
    public boolean isLoaded() {
        return interstitialAd != null && interstitialAd.isAdLoaded();
    }

    @Override
    public void setOnInterstitialAdRequestListener(OnInterstitialAdRequestListener listener) {
        requestListenerWeakReference = new WeakReference<>(listener);
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {

    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
            requestListenerWeakReference.get().onInterstitialClose();
        }

        Log.d("AdManager", "facebook interstitial ad closed");
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
            requestListenerWeakReference.get().onInterstitialRequestFailed();
        }

        Log.d("AdManager", "facebook interstitial ad request failed");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (new Random().nextBoolean()) {
            onError(null, null);
            return;
        }

        Log.d("AdManager", "facebook interstitial ad loaded");

        requestAd();

        if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
            requestListenerWeakReference.get().onInterstitialRequestSuccess();
        }
    }

    @Override
    public void onAdClicked(Ad ad) {

    }
}
