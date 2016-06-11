package net.chopestory.cpadmanagersample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import net.chopestory.cpadmanager.CPBannerAd;
import net.chopestory.cpadmanager.CPContextObject;
import net.chopestory.cpadmanager.OnAdRequestListener;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * CPAdManager
 * Created by Chope on 16. 6. 11..
 */
public class CPFacebookBannerAd extends CPContextObject implements CPBannerAd, AdListener {
    protected AdView adView = null;
    protected WeakReference<OnAdRequestListener> requestListenerReference = null;

    public CPFacebookBannerAd(@NonNull Context context) {
        super(context);
    }

    @Override
    public AdView getAdView() {
        Context context = getContext();

        if (context == null) {
            return null;
        }

        if (adView == null) {
            boolean isTablet = context.getResources().getBoolean(R.bool.is_tablet);
            AdSize adSize = isTablet ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50;
            adView = new AdView(context, context.getString(R.string.facebook_banner_ad_id), adSize);
            adView.setAdListener(this);

            Log.d("AdManager", "facebook banner ad created");
        }
        return adView;
    }

    @Override
    public void requestAd() {
        AdView adView = getAdView();

        if (adView != null) {
            getAdView().loadAd();
            Log.d("AdManager", "request facebook banner ad");
        }
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;

            Log.d("AdManager", "facebook banner ad destroyed");
        }
    }

    @Override
    public void setOnRequestListener(OnAdRequestListener listener) {
        requestListenerReference = new WeakReference<>(listener);
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d("AdManager", "facebook banner ad request failed");

        if (requestListenerReference != null && requestListenerReference.get() != null) {
            requestListenerReference.get().onRequestFailure();
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (new Random().nextBoolean()) {
            onError(null, null);
            return;
        }

        Log.d("AdManager", "facebook banner ad request success");
    }

    @Override
    public void onAdClicked(Ad ad) {

    }
}
