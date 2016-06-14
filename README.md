# CPAdManager
Admob, Facebook 등 다수의 광고를 함께 사용하기 위해 만들어봄.

광고 요청이 실패하면 다음 광고를 요청.

## How to use

각 광고를 어떻게 처리할지를 구현해야 한다.

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

            Log.d("AdManager", "admob banner ad request failed");

            if (requestListenerReference != null && requestListenerReference.get() != null) {
                requestListenerReference.get().onBannerRequestFailure();
            }
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();

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

Activity에서 다음과 같이 호출하여 사용함.

```java
public class MainActivity extends AppCompatActivity {
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

## To do

- 프로젝트에 적용
- 삽입 광고
