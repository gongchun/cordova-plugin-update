var exec = require('cordova/exec');

exports.file = function (arg0, success, error) {
    exec(success, error, 'N22Download', 'file', arg0);
};

exports.unpack = function (arg0, success, error) {
    exec(success, error, 'N22Download', 'unpack', arg0);
};

exports.incremental = function (arg0, success, error) {
    exec(success, error, 'N22Download', 'incremental', arg0);
};

exports.full = function (arg0, success, error) {
    exec(success, error, 'N22Download', 'full', arg0);
};
