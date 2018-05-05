# cordova-plugin-update
更新插件
```
// 添加插件
cordova plugin add https://github.com/zltech/cordova-plugin-update.git

// 增量更新的方法
cordova.plugins.N22Download.incremental({ url:'下载地址'}, success, fail)

// 全量更新的方法
cordova.plugins.N22Download.full({ url:'下载地址'}, success, fail)
```
