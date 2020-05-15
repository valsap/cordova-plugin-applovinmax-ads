function appLovinMaxExec(name, params, onSuccess, onFailure) {
    cordova.exec(
        function callPluginSuccess(result) {
            if (isFunction(onSuccess)) {
                onSuccess(result);
            }
        },
        function callPluginFailure(error) {
            if (isFunction(onFailure)) {
                onFailure(error)
            }
        },
        'AppLovinMaxPlugin', name, params);
}

/**
 * Helper function to do a shallow defaults (merge). Does not create a new object, simply extends it
 * @param {Object} o - object to extend
 * @param {Object} defaultObject - defaults to extend o with
 */
function defaults(o, defaultObject) {
    if (typeof o === 'undefined') {
        return defaults({}, defaultObject);
    }
    for (var j in defaultObject) {
        if (defaultObject.hasOwnProperty(j) && o.hasOwnProperty(j) === false) {
            o[j] = defaultObject[j];
        }
    }
    return o;
}

/**
 * Helper function to check if a function is a function
 * @param {Object} functionToCheck - function to check if is function
 */
function isFunction(functionToCheck) {
    var getType = {};
    var isFunction = functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
    return isFunction === true;
}

var isAppLovinMaxInit = false;
let AppLovinMax = (function (){
    return {
        init: function (params) {
            if(isAppLovinMaxInit){
                throw new Error("AppLovinMax init action error: plugin already initialized");
            }
            isAppLovinMaxInit = true;
            params = defaults(params, {});
            appLovinMaxExec('init', [], params.onSuccess, params.onFailure);
        },
        initAdUnit : function (params) {
            if(!isAppLovinMaxInit){
                throw new Error("AppLovinMax initAdUnit action error: plugin has not initialized");
            }
            if (params.hasOwnProperty("unit") === false){
                throw new Error("AppLovinMax initAdUnit action error: missing unit type [REWARDED, BANNER, INTERSTITIAL]");
            }
            var unit = params["unit"];
            const UNIT_TYPES = ["REWARDED", "BANNER", "INTERSTITIAL"];
            if(UNIT_TYPES.indexOf(unit) === -1){
                throw new Error("AppLovinMax initAdUnit action error: unknown unit type [REWARDED, BANNER, INTERSTITIAL]");
            }
            if (params.hasOwnProperty("unitId") === false){
                throw new Error("AppLovinMax initAdUnit action error: missing unitId");
            }
            var unitId = params["unitId"];
            appLovinMaxExec('initAdUnit', [unit, unitId], params.onSuccess, params.onFailure);
        },
        setUserId : function (params) {
            if(!isAppLovinMaxInit){
                throw new Error("AppLovinMax setUserId action error: plugin has not initialized");
            }
            params = defaults(params, {});
            if (params.hasOwnProperty('userId') === false){
                throw new Error("AppLovinMax setUserId action error: missing userId");
            }
            let userId = params["userId"];
            appLovinMaxExec('setUserId', [userId], params.onSuccess, params.onFailure);
        },
        showRewardedVideo : function(params) {
            if(!isAppLovinMaxInit){
                throw new Error("AppLovinMax showRewardedVideo action error: plugin has not initialized");
            }
            params = defaults(params, {});
            !params.hasOwnProperty('placement') ? params["placement"] = "" : 0;
            let placement = params["placement"];
            appLovinMaxExec('showRewardedVideo', [placement], params.onSuccess, params.onFailure);
        },
        hasRewardedVideo : function (params) {
            if(!isAppLovinMaxInit){
                throw new Error("AppLovinMax hasRewardedVideo action error: plugin has not initialized");
            }
            params = defaults(params, {});
            if (isFunction(params.onSuccess) === false) {
                throw new Error('AppLovinMax hasRewardedVideo action error: missing onSuccess callback');
            }
            appLovinMaxExec('hasRewardedVideo', [], params.onSuccess, params.onFailure);
        },
        showInterstitialVideo : function(params) {
            if(!isAppLovinMaxInit){
                throw new Error("AppLovinMax showInterstitialVideo action error: plugin has not initialized");
            }
            params = defaults(params, {});
            !params.hasOwnProperty('placement') ? params["placement"] = "" : 0;
            let placement = params["placement"];
            appLovinMaxExec('showInterstitialVideo', [placement], params.onSuccess, params.onFailure);
        },
        hasInterstitialVideo : function (params) {
            if(!isAppLovinMaxInit){
                throw new Error("AppLovinMax hasInterstitialVideo action error: plugin has not initialized");
            }
            params = defaults(params, {});
            if (isFunction(params.onSuccess) === false) {
                throw new Error('AppLovinMax hasInterstitialVideo action error: missing onSuccess callback');
            }
            appLovinMaxExec('hasInterstitialVideo', [], params.onSuccess, params.onFailure);
        }
    }
})();

if (typeof module !== undefined && module.exports) {
    module.exports = AppLovinMax;
}