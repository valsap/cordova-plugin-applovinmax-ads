package com.lakgames.plugins.applovinmax;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

public class AppLovinMaxRewardedAd implements MaxRewardedAdListener {

    private static final String VIDEO_LOADED_EVENT = "rewardedVideoLoaded";
    private static final String VIDEO_LOADING_FAILED_EVENT = "rewardedVideoLoadingFailed";

    private static final String VIDEO_FAILED_DISPLAY_EVENT = "rewardedVideoDisplayFailed";
    private static final String VIDEO_START_DISPLAY_EVENT = "rewardedVideoDisplayStart";

    private static final String VIDEO_STARTED_EVENT = "rewardedVideoOpened";
    private static final String VIDEO_ENDED_EVENT = "rewardedVideoEnded";
    private static final String VIDEO_ENDED_HIDDEN = "rewardedVideoHidden";

    private static final String VIDEO_REWARD_RECEIVED_EVENT = "rewardedVideoRewardReceived";

    private static final String VIDEO_CLICKED = "rewardedVideoClicked";

    private AppLovinMaxPlugin plugin;
    private boolean isInit = false;
    private MaxRewardedAd rewardedAd;
    private int retryAttempt;

    public AppLovinMaxRewardedAd(AppLovinMaxPlugin plugin) {
        this.plugin = plugin;
    }

    public void createRewardedAd(String unitId) {
        if(isInit){
            return;
        }
        rewardedAd = MaxRewardedAd.getInstance(unitId, plugin.cordova.getActivity());
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
        isInit = true;
    }

    public void showRewardedVideo(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(!isInit){
            return;
        }
        String placement = args.getString(0);
        plugin.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (!rewardedAd.isReady()){
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                }else{
                    rewardedAd.showAd(placement);
                    callbackContext.success();
                }
            }
        });
    }

    public void hasRewardedVideo(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(!isInit){
            return;
        }
        plugin.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, rewardedAd.isReady()));
            }
        });
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
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
        // Rewarded ad failed to load. We recommend re-trying in 3 seconds.
        retryAttempt++;
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                rewardedAd.loadAd();
            }
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
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd.loadAd();
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
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd.loadAd();
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", maxAd.getAdUnitId());
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_ENDED_HIDDEN, data);
    }

    @Override
    public void onRewardedVideoStarted(final MaxAd maxAd) {
        JSONObject data = new JSONObject();
        try{
            data.put("unitId", maxAd.getAdUnitId());
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_STARTED_EVENT, data);
    }

    @Override
    public void onRewardedVideoCompleted(final MaxAd maxAd) {
        JSONObject data = new JSONObject();
        try {
            data.put("unitId", maxAd.getAdUnitId());
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.emitWindowEvent(VIDEO_ENDED_EVENT, data);
    }

    @Override
    public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward) {
        JSONObject data = new JSONObject();
        try {
            data.put("unitId", maxAd.getAdUnitId());
            data.put("label", maxReward.getLabel());
            data.put("amount", maxReward.getAmount());
        }catch(JSONException e){
            e.printStackTrace();
        }
        // Rewarded ad was displayed and user should receive the reward
        this.emitWindowEvent(VIDEO_REWARD_RECEIVED_EVENT, data);
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