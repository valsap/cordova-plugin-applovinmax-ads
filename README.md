# AppLovin Max mediation plugin

Interstitial and Rewarded video ads for Android

## Install
```bash
cordova plugin add https://github.com/valsap/cordova-plugin-applovinmax-ads.git
```

### Add SDK_KEY to your config.xml file
```bash
<config-file parent="./application" target="AndroidManifest.xml">
  <meta-data android:name="applovin.sdk.key" android:value=[SDK_KEY_HERE]/>
</config-file>
```

## Usage
### To initialize plugin
```js
AppLovinMax.init({
  onSuccess : () => {
    console.log("Applovin MAX: plugin init success");
  },
  onFailure : (details) => {
    console.log("Applovin MAX: plugin init fail:", details);
  }
});
```

### Set custom user id
```js
AppLovinMax.setUserId({userId : USER_ID_HERE});
```

### Init AD UNITs.
```js
AppLovinMax.initAdUnit({unit : "REWARDED", unitId : UNIT_ID_HERE});
AppLovinMax.initAdUnit({unit : "INTERSTITIAL", unitId : UNIT_ID_HERE});
```

## Rewarded video ads

### Has next rewarded video loaded and ready to show
```js
AppLovinMax.hasRewardedVideo({
  onSuccess: function (available) {
    console.log("Applovin Max: ready to show rewarded video:", available);
  }
});
```

### Show rewarded video
If you don't use S2S rewarding, just ignore "placement" field
```js
AppLovinMax.showRewardedVideo({
    placement : YOUR_PLACEMENT_TO_REWARD_AT_SERVER_SIDE,
    onSuccess : () => {},
    onFailure : () => {}
});
```

### Rewarded video events
Rewarded video loading events
```js
window.addEventListener("rewardedVideoLoaded", (data) => {
    console.log("Applovin Max: rewarded video loaded:", data["unitId"]);
});
window.addEventListener("rewardedVideoLoadingFailed", (data) => {
    console.log("Applovin Max: rewarded video loading error:", data["unitId"], data["errorCode"]);
});
```
Rewarded video displaying events
```js
window.addEventListener("rewardedVideoDisplayFailed", (data) => {
    console.log("Applovin Max: rewarded video display failed:", data["unitId"], data["errorCode"]);
});
window.addEventListener("rewardedVideoDisplayStart", (data) => {
    console.log("Applovin Max: rewarded video display start:", data["unitId"]);
});
```
Rewarded video completed and fired REWARD event
```js
window.addEventListener("rewardedVideoRewardReceived", (data) => {
        let unitId = data["unitId"];
        let label = data["label"];
        let amount = data["amount"];
        console.log("Applovin Max: reward received:", unitId, label, amount);
});
```
Rewarded video watching process events
```js
window.addEventListener("rewardedVideoOpened", (data) => {
    console.log("Applovin Max: rewarded video opened:", data["unitId"]);
});
window.addEventListener("rewardedVideoEnded", (data) => {
    console.log("Applovin Max: rewarded video ended:", data["unitId"]);
});
window.addEventListener("rewardedVideoHidden", (data) => {
    console.log("Applovin Max: rewarded video closed:", data["unitId"]);
});
```
Rewarded video clicked
```js
window.addEventListener("rewardedVideoClicked", (data) => {
    console.log("Applovin Max: rewarded video clicked:", data["unitId"]);
});
```

## Interstitial video
### Has next interstitial video loaded and ready to show
```js
AppLovinMax.hasInterstitialVideo({
  onSuccess: function (available) {
    console.log("Applovin Max: ready to show interstitial video:", available);
  }
});
```

### Show interstitial video
```js
AppLovinMax.showInterstitialVideo({
    onSuccess : () => {},
    onFailure : () => {}
});
```

### Interstitial video events
Interstitial video loading events
```js
window.addEventListener("interstitialVideoLoaded", (data) => {
    console.log("Applovin Max: interstitial video loaded:", data["unitId"]);
});
window.addEventListener("interstitialVideoLoadingFailed", (data) => {
    console.log("Applovin Max: interstitial video loading error:", data["unitId"], data["errorCode"]);
});
```
Interstitial video displaying events
```js
window.addEventListener("interstitialVideoDisplayFailed", (data) => {
    console.log("Applovin Max: interstitial video display failed:", data["unitId"], data["errorCode"]);
});
window.addEventListener("interstitialVideoDisplayStart", (data) => {
    console.log("Applovin Max: interstitial video display start:", data["unitId"]);
});
```
Interstitial video watching process events
```js
window.addEventListener("interstitialVideoHidden", (data) => {
    console.log("Applovin Max: interstitial video closed:", data["unitId"]);
});
```
Interstitial video clicked
```js
window.addEventListener("interstitialVideoClicked", (data) => {
    console.log("Applovin Max: interstitial video clicked:", data["unitId"]);
});
```