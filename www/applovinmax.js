var exec = require('cordova/exec')

function appLovinMaxExec(methodName, argsArray) {
    var className = 'AppLovinMax';
    cordova.exec(
        function () {},
        function (err) {
            console.warn('AppLovinMax:cordova.exec(' + className + '.' + methodName + '): ' + err)
        },
        className,
        methodName,
        argsArray
    );
}

module.exports = {
    init: function (config) {
        console.log("init module applovin max");
        params = defaults(params, { userId: '' });
        appLovinMaxExec('init', [config]);
    }
    showRewardedVideo : function(params) {
        params = defaults(params, { placement: 'default' });
        appLovinMaxExec('showRewardedVideo', [params]);
    }
    hasRewardedVideo : function (params) {
        params = defaults(params, {});
        appLovinMaxExec('hasRewardedVideo', [params]);
    }
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