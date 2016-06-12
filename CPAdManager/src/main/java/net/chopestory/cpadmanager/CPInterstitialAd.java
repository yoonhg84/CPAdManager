package net.chopestory.cpadmanager;

/**
 * CPAdManager
 * Created by Chope on 16. 6. 11..
 */
public interface CPInterstitialAd extends CPAd {
    void requestAd();
    void destroy();
    void showAd();
    boolean isLoaded();
    void setOnInterstitialAdRequestListener(OnInterstitialAdRequestListener listener);
}
