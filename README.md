# CPAdManager
Admob, Facebook 광고를 함께 사용하기 위해 만들어봄.

## How to use

각 광고를 어떻게 처리할지를 구현해야 한다.

```java
public class CPAdmobBannerAd extends CPContextObject implements CPBannerAd {
    protected AdView adView = null;
    protected WeakReference<OnAdRequestListener> requestListenerReference = null;

    public CPAdmobBannerAd(@NonNull Context context) {
        super(context);

        MobileAds.initialize(context, getContext().getString(R.string.admob_app_id));
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);

            Log.d("AdManager", "admob banner ad request failed");

            if (requestListenerReference != null && requestListenerReference.get() != null) {
                requestListenerReference.get().onRequestFailure();
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

            Log.d("AdManager", "admob destroyed");
        }
    }

    @Override
    public void setOnRequestListener(OnAdRequestListener listener) {
        requestListenerReference = new WeakReference<>(listener);
    }
}

```

어떤 광고들을 보여줄지 정한다.

추가된 순서로 배너를 보여준다.

```java
public class BannerAdsCreator implements CPAdManager.IBannerAdsCreator {
    @Override
    public CPBannerAd[] createBannerAds(Context context) {
        return new CPBannerAd[] { new CPFacebookBannerAd(context), new CPAdmobBannerAd(context)};
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

        adManager = new CPAdManager(this, adLayout, new BannerAdsCreator());
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
