package net.chopestory.cpadmanager;

import android.view.View;

/**
 * CPAdManager
 * Created by Chope on 16. 6. 11..
 */
public interface CPBannerAd extends CPAd {
    View getAdView();
    void requestAd();
    void destroy();
    void setOnRequestListener(OnAdRequestListener listener);
}