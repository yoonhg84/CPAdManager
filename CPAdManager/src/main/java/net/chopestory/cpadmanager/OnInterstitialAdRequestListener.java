package net.chopestory.cpadmanager;

/**
 * CPAdManager
 * Created by Chope on 16. 6. 11..
 */
public interface OnInterstitialAdRequestListener {
    void onInterstitialRequestFailed();
    void onInterstitialRequestSuccess();
    void onInterstitialClose();
}
