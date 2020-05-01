# AppLovin Max mediation plugin


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
AppLovinMax.setUserId({userId : [USER_ID_HERE]});
```

### Init AD UNIT
```js
AppLovinMax.initAdUnit({unit : "REWARDED", unitId : [UNIT_ID_HERE]});
```

## Has next video loaded and ready to show
```js
AppLovinMax.hasRewardedVideo({
  onSuccess: function (available) {
    console.log("Applovin Max: ready to show rewarded video:", available);
  }
});
```

## Show rewarded video
```js
//Video completed, reward user here
function onReceived(event){
  let unitId = event.unitId;
  let label = event.label;
  let amount = event.amount;
  console.log("Applovin MAX: reward received: unitId", unitId, "label", label, "amount", amount);
}

//User cancelled video
function onCancelled(data){
    console.log("AD: reward cancelled:", data);
}

//Something went wrong
function onFailed(details){
    console.log("Applovin MAX: displaing rewarded video error:", details);
}

//Add listeners
window.addEventListener("rewardedVideoRewardReceived", onReceived);
window.addEventListener("rewardedVideoHidden", onCancelled);
window.addEventListener("rewardedVideoDisplayFailed", onFailed);
window.addEventListener("rewardedVideoLoadingFailed", onFailed);

AppLovinMax.showRewardedVideo({
    placement : [YOUR_PLACEMENT_TO_REWARD_AT_SERVER_SIDE],
    onSuccess : () => {},
    onFailure : () => {}
});
```