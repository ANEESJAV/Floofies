package com.anees.floofies;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends Activity {

    // ===== TEST AD UNIT IDs (Google official). REPLACE with your real IDs before release =====
    private static final String BANNER_ID       = "ca-app-pub-3940256099942544/9214589741";
    private static final String INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_ID     = "ca-app-pub-3940256099942544/5224354917";

    private WebView webView;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private boolean rewardEarned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#FCB69F"));
        root.setFitsSystemWindows(true);

        webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);           // localStorage = best score save
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.addJavascriptInterface(new Bridge(), "Android");
        webView.loadUrl("file:///android_asset/index.html");
        root.addView(webView);

        adView = new AdView(this);
        adView.setAdUnitId(BANNER_ID);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int widthDp = (int) (dm.widthPixels / dm.density);
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, widthDp));
        root.addView(adView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);

        MobileAds.initialize(this, status -> {});
        adView.loadAd(new AdRequest.Builder().build());
        loadInterstitial();
        loadRewarded();
    }

    // ---------- JS bridge ----------
    private class Bridge {
        @JavascriptInterface
        public void showInterstitial() { runOnUiThread(MainActivity.this::showInterstitialAd); }

        @JavascriptInterface
        public void showRewarded() { runOnUiThread(MainActivity.this::showRewardedAd); }
    }

    private void callJsReward(boolean ok, String reason) {
        runOnUiThread(() -> webView.evaluateJavascript(
                "window.onRewardResult && window.onRewardResult(" + ok + ",'" + reason + "')", null));
    }

    // ---------- Interstitial ----------
    private void loadInterstitial() {
        InterstitialAd.load(this, INTERSTITIAL_ID, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override public void onAdLoaded(InterstitialAd ad) { interstitialAd = ad; }
                    @Override public void onAdFailedToLoad(LoadAdError e) { interstitialAd = null; }
                });
    }

    private void showInterstitialAd() {
        if (interstitialAd == null) { loadInterstitial(); return; }
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override public void onAdDismissedFullScreenContent() { interstitialAd = null; loadInterstitial(); }
            @Override public void onAdFailedToShowFullScreenContent(AdError e) { interstitialAd = null; loadInterstitial(); }
        });
        interstitialAd.show(this);
    }

    // ---------- Rewarded ----------
    private void loadRewarded() {
        RewardedAd.load(this, REWARDED_ID, new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override public void onAdLoaded(RewardedAd ad) { rewardedAd = ad; }
                    @Override public void onAdFailedToLoad(LoadAdError e) { rewardedAd = null; }
                });
    }

    private void showRewardedAd() {
        if (rewardedAd == null) {
            loadRewarded();
            callJsReward(false, "unavailable");
            return;
        }
        rewardEarned = false;
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override public void onAdDismissedFullScreenContent() {
                rewardedAd = null;
                loadRewarded();
                callJsReward(rewardEarned, rewardEarned ? "ok" : "skipped");
            }
            @Override public void onAdFailedToShowFullScreenContent(AdError e) {
                rewardedAd = null;
                loadRewarded();
                callJsReward(false, "unavailable");
            }
        });
        rewardedAd.show(this, item -> rewardEarned = true);
    }

    // ---------- Lifecycle ----------
    @Override protected void onPause()   { if (adView != null) adView.pause();  webView.onPause();  super.onPause(); }
    @Override protected void onResume()  { super.onResume(); webView.onResume(); if (adView != null) adView.resume(); }
    @Override protected void onDestroy() { if (adView != null) adView.destroy(); webView.destroy(); super.onDestroy(); }
}
