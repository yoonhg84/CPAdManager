# CPAdManager
Admob, Facebook 등 다수의 광고를 함께 사용하기 위해 만들어봄.

광고 요청이 실패하면 다음 광고를 요청.

## How to use

각 광고를 어떻게 처리할지를 구현해야 한다.

### Banner

```java
public class AdmobBannerAd extends CPContextObject implements CPBannerAd {
    protected AdView adView = null;
    protected WeakReference<OnBannerAdRequestListener> requestListenerReference = null;

    public AdmobBannerAd(@NonNull Context context) {
        super(context);
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);

            if (requestListenerReference != null && requestListenerReference.get() != null) {
                requestListenerReference.get().onBannerRequestFailure();
            }
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
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
        }
        return adView;
    }

    @Override
    public void requestAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        AdView adView = getAdView();

        if (adView != null) {
            adView.loadAd(adRequest);
        }
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    @Override
    public void setOnBannerAdRequestListener(OnBannerAdRequestListener listener) {
        requestListenerReference = new WeakReference<>(listener);
    }
}

```

### Interstitial

```java
public class LSAdmobInterstitialAd extends CPContextObject implements CPInterstitialAd {
    private InterstitialAd interstitialAd = null;
    private WeakReference<OnInterstitialAdRequestListener> requestListenerWeakReference = null;

    private AdListener adListener = new com.google.android.gms.ads.AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);

            if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
                requestListenerWeakReference.get().onInterstitialRequestFailed();
            }
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();

            if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
                requestListenerWeakReference.get().onInterstitialRequestSuccess();
            }
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();

            if (requestListenerWeakReference != null && requestListenerWeakReference.get() != null) {
                requestListenerWeakReference.get().onInterstitialClose();
            }
        }
    };

    public LSAdmobInterstitialAd(Context context) {
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
    }

    @Override
    public void destroy() {
        interstitialAd = null;
    }

    @Override
    public void showAd() {
        if (interstitialAd != null) {
            interstitialAd.show();
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
```

어떤 광고들을 보여줄지 정한다.

추가된 순서로 배너를 보여준다.

```java
public class AdsCreator implements CPAdManager.BannerAdsCreator, CPAdManager.InterstitialAdsCreator {
    @Override
    public CPBannerAd[] createBannerAds(Context context) {
        return new CPBannerAd[] { new FacebookBannerAd(context), new AdmobBannerAd(context) };
    }

    @Override
    public CPInterstitialAd[] createInterstitialAds(Context context) {
        return new CPInterstitialAd[] { new FacebookInterstitialAd(context), new AdmobInterstitialAd(context) };
    }
}
```

### Activity

실제 Activity 에서 광고를 표시하는 방법입니다.

banner, interstitial 둘 중 하나만 사용하시면 됩니다.

**banner만 표시하는 경우**

```java
public class BannerAdActivity extends AppCompatActivity {
    private CPAdManager adManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup adLayout = (ViewGroup) findViewById(R.id.adLayout);
        Assert.assertNotNull(adLayout);

        adManager = new CPAdManager(this, adLayout, new AdsCreator());
        adManager.requestBanner();
    }

    @Override
    protected void onDestroy() {
        adManager.destroy();
        super.onDestroy();
    }
}
```

**banner + interstitial**

배너와 전면광고를 함께 사용하는 경우

전면광고는 OnAdManagerInterstitialListener 을 설정하지 않으면 광고를 가져오면 바로 보여줍니다.

```java
public class TwoAdsActivity extends AppCompatActivity implements CPAdManager.OnAdManagerInterstitialListener {
    private ViewGroup adLayout;
    private CPAdManager adManager = null;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_activity);
        
        adLayout = (ViewGroup) findViewById(R.id.adLayout);

        adManager = new CPAdManager(this, adLayout, new LSAdsCreator(), new LSAdsCreator());
        adManager.setOnAdManagerInterstitialListener(this);
        adManager.requestInterstitialAd();
        adManager.requestBannerAd();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (adManager == null) {
            configureAdManager();
        }
    }

    @Override
    protected void onDestroy() {
        if (adManager != null) {
            adManager.destroy();
        }

        super.onDestroy();
    }
    

    @Override
    public void onAllRequestsFailed(CPAdManager adManager) {
        
    }

    @Override
    public void onAdClosed(CPAdManager adManager) {
        
    }

    @Override
    public void onRequestSucccess(CPAdManager adManager) {
        adManager.showInterstitialAd();
    }
}

```

## To do

- 프로젝트에 적용
- 삽입 광고
