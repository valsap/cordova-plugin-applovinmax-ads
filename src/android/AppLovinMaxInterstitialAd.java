package com.lakgames.plugins.applovinmax;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.ads.MaxInterstitialAd;

public class AppLovinMaxInterstitialAd implements MaxAdListener {

    private static final String VIDEO_LOADED_EVENT = "interstitialVideoLoaded";
    private static final String VIDEO_LOADING_FAILED_EVENT = "interstitialVideoLoadingFailed";

    private static final String VIDEO_FAILED_DISPLAY_EVENT = "interstitialVideoDisplayFailed";
    private static final String VIDEO_START_DISPLAY_EVENT = "interstitialVideoDisplayStart";

    private static final String VIDEO_ENDED_HIDDEN = "interstitialVideoHidden";

    private static final String VIDEO_CLICKED = "interstitialVideoClicked";

    private AppLovinMaxPlugin plugin;
    private boolean isInit = false;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;

    public AppLovinMaxInterstitialAd(AppLovinMaxPlugin plugin) {
        this.plugin = plugin;
    }

    public void createInterstitialAd(String unitId) {
        if(isInit){
            return;
        }
        interstitialAd = new MaxInterstitialAd(unitId, plugin.cordova.getActivity());
        interstitialAd.setListener(this);
        interstitialAd.loadAd();
        isInit = true;
    }

    public void showInterstitialVideo(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(!isInit){
            return;
        }
        String placement = args.getString(0);
        plugin.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (!interstitialAd.isReady()){
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                }else{
                    interstitialAd.showAd(placement);
                    callbackContext.success();
                }
            }
        });
    }

    public void hasInterstitialVideo(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(!isInit){
            return;
        }
        plugin.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, interstitialAd.isReady()));
            }
        });
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'
        // Reset retry attempt
        retryAttempt = 0;
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", maxAd.getAdUnitId());
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_LOADED_EVENT, data);
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final int errorCode) {
        // Interstitial ad failed to load. We recommend retrying with exponentially higher delays.
        retryAttempt++;
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() { interstitialAd.loadAd(); }
        }, 3000 );
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", adUnitId);
            data.put("errorCode", errorCode);
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_LOADING_FAILED_EVENT, data);
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final int errorCode) {
        // Interstitial ad failed to display. We recommend loading the next ad
        interstitialAd.loadAd();
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", maxAd.getAdUnitId());
            data.put("errorCode", errorCode);
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_FAILED_DISPLAY_EVENT, data);
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", maxAd.getAdUnitId());
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_START_DISPLAY_EVENT, data);
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", maxAd.getAdUnitId());
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_CLICKED, data);
    }

    @Override
    public void onAdHidden(final MaxAd maxAd) {
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd.loadAd();
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", maxAd.getAdUnitId());
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_ENDED_HIDDEN, data);
    }

    /** ----------------------- UTILS --------------------------- */
    private void emitWindowEvent(final String event) {
        final CordovaWebView view = plugin.getWebView();
        plugin.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(String.format("javascript:cordova.fireWindowEvent('%s');", event));
            }
        });
    }

    private void emitWindowEvent(final String event, final JSONObject data) {
        final CordovaWebView view = plugin.getWebView();
        plugin.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(String.format("javascript:cordova.fireWindowEvent('%s', %s);", event, data.toString()));
            }
        });
    }
}