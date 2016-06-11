package net.chopestory.cpadmanagersample;

import android.content.Context;

import net.chopestory.cpadmanager.CPAdManager;
import net.chopestory.cpadmanager.CPBannerAd;

/**
 * CPAdManagerSample
 * Created by Chope on 16. 6. 11..
 */
public class BannerAdsCreator implements CPAdManager.IBannerAdsCreator {
    @Override
    public CPBannerAd[] createBannerAds(Context context) {
        return new CPBannerAd[] { new CPFacebookBannerAd(context), new CPAdmobBannerAd(context)};
    }
}
