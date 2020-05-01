package com.lakgames.plugins.applovinmax;

import com.lakgames.plugins.applovinmax.AppLovinMaxRewardedAd;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.util.concurrent.TimeUnit;

public class AppLovinMaxPlugin extends CordovaPlugin{

    private CordovaWebView cordovaWebView;
    private String sdkKey;

    //ADs
    private AppLovinMaxRewardedAd rewardedAd;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        cordovaWebView = webView;
        rewardedAd = new AppLovinMaxRewardedAd(this);
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            initAction(args, callbackContext);
            return true;
        }
        if (action.equals("setUserId")) {
            setUserId(args, callbackContext);
            return true;
        }
        if (action.equals("initAdUnit")) {
            initAdUnit(args, callbackContext);
            return true;
        }
        if (action.equals("hasRewardedVideo")) {
            rewardedAd.hasRewardedVideo(args, callbackContext);
            return true;
        }
        if (action.equals("showRewardedVideo")) {
            rewardedAd.showRewardedVideo(args, callbackContext);
            return true;
        }
        return false;
    }

    /**
     * Check config and init AppLovin Max
     * @param args
     * @param callbackContext
     * @throws JSONException
     */
    private void initAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        AppLovinSdk.getInstance( cordova.getActivity() ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( cordova.getActivity(), new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                // AppLovin SDK is initialized, start loading ads
                callbackContext.success();
            }
        });
    }

    public CordovaWebView getWebView(){
        return cordovaWebView;
    }

    /**
     * Set optional UID for s2s validation
     * @param args
     * @param callbackContext
     * @throws JSONException
     */
    private void setUserId(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final String userId = args.getString(0);
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AppLovinSdk.getInstance(cordova.getActivity()).setUserIdentifier(userId);
                callbackContext.success();
            }
        });
    }

    /**
     * Ad unit initialization
     */
    private final String UNIT_TYPE_REWARDED_AD = "REWARDED";
    private final String UNIT_TYPE_BANNER = "BANNER";
    private final String UNIT_TYPE_INTERSTITIAL = "INTERSTITIAL";
    private void initAdUnit(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final String unit = args.getString(0);
        final String unitId = args.getString(1);
        if(UNIT_TYPE_REWARDED_AD.equals(unit)){
            rewardedAd.createRewardedAd(unitId);
        }else if(UNIT_TYPE_BANNER.equals(unit)){

        }else if(UNIT_TYPE_INTERSTITIAL.equals(unit)){

        }
    }
}