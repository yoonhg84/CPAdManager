package net.chopestory.cpadmanagersample;

import android.content.Context;

import net.chopestory.cpadmanager.CPAdManager;
import net.chopestory.cpadmanager.CPBannerAd;
import net.chopestory.cpadmanager.CPInterstitialAd;

/**
 * CPAdManagerSample
 * Created by Chope on 16. 6. 11..
 */
public class AdsCreator implements CPAdManager.BannerAdsCreator, CPAdManager.InterstitialAdsCreator {
    @Override
    public CPBannerAd[] createBannerAds(Context context) {
        return new CPBannerAd[] { new CPFacebookBannerAd(context), new CPAdmobBannerAd(context) };
//        return new CPBannerAd[] { new CPFacebookBannerAd(context) };
    }

    @Override
    public CPInterstitialAd[] createInterstitialAds(Context context) {
//        return new CPInterstitialAd[] { new CPFacebookInterstitialAd(context), new CPAdmobInterstitialAd(context) };
        return new CPInterstitialAd[] { new CPAdmobInterstitialAd(context) };
    }
}
