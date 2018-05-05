/********* N22Download.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <Cordova/CDVConfigParser.h>
#import "DownloadViewController.h"
#import "LoadHtmlViewController.h"
#import "ZipArchive.h"

#define www @"www"

@interface N22Download : CDVPlugin {
  // Member variables go here.
    DownloadViewController *downloadVC;
    LoadHtmlViewController *loadHtml;
    NSString *_indexPage;
}

- (void)coolMethod:(CDVInvokedUrlCommand*)command;

-(void)incremental:(CDVInvokedUrlCommand*)command;

-(void)full:(CDVInvokedUrlCommand*)command;

@end

@implementation N22Download

- (void)coolMethod:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

//刚进来就进入这个方法，在该方法中检查指定沙盒路径下有没有想要的zip文件
- (void)pluginInitialize{
    
    [self HaveNoUpDateFile];
//    BOOL isregist = [[NSUserDefaults standardUserDefaults] boolForKey:@"isRegist"];
//    if (isregist) {
//        NSLog(@"已经注册过了");
//    }else {
//        if (![[NSUserDefaults standardUserDefaults] boolForKey:@"isFirstInstall"]) {
//            [self HaveNoUpDateFile];
//        }else {
//            [self goToRegiste];
////            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
////                [self loadCordovaCtrl];
////            });
//        }
//    }
    
}

- (void)resetIndexPageToExternalStorage {
    
//    NSString *indexPageStripped = [self indexPageFromConfigXml];
//    NSRange r = [indexPageStripped rangeOfCharacterFromSet:[NSCharacterSet characterSetWithCharactersInString:@"?#"] options:0];
//    if (r.location != NSNotFound) {
//        indexPageStripped = [indexPageStripped substringWithRange:NSMakeRange(0, r.location)];
//    }
//    NSString *path = [self wwwPath];
//    NSURL *indexPageExternalURL = [self appendWwwFolderPathToPath:indexPageStripped];
//    if (![[NSFileManager defaultManager] fileExistsAtPath:indexPageExternalURL.path]) {
//        return;
//    }

    if ([self.viewController isKindOfClass:[CDVViewController class]]) {
        ((CDVViewController *)self.viewController).wwwFolderName = [@"file://" stringByAppendingString:[self wwwPath]];
        ((CDVViewController *)self.viewController).startPage = @"index.html";
        
        
    } else {
        NSLog(@"HotCodePushError: Can't make starting page to be from external storage. Main controller should be of type CDVViewController.");
    }
    
}

//- (NSURL *)appendWwwFolderPathToPath:(NSString *)pagePath {
//    if ([pagePath hasPrefix:[self wwwPath]]) {
//        return [NSURL URLWithString:pagePath];
//    }
//
//    return [[NSURL fileURLWithPath:[self wwwPath] isDirectory:YES] URLByAppendingPathComponent:pagePath];
//}
//
//- (NSString *)indexPageFromConfigXml {
//    if (_indexPage) {
//        return _indexPage;
//    }
//
//    CDVConfigParser* delegate = [[CDVConfigParser alloc] init];
//
//    // read from config.xml in the app bundle
//    NSURL* url = [NSURL fileURLWithPath:[self pathToCordovaConfigXml]];
//
//    NSXMLParser *configParser = [[NSXMLParser alloc] initWithContentsOfURL:url];
//    [configParser setDelegate:((id <NSXMLParserDelegate>)delegate)];
//    [configParser parse];
//
//    if (delegate.startPage) {
//        _indexPage = @"index.html";
//    } else {
//        _indexPage = @"index.html";
//    }
//
//    return _indexPage;
//}
//- (NSString *)pathToCordovaConfigXml {
//    return [[NSBundle mainBundle] pathForResource:@"config" ofType:@"xml"];
//}

- (void)goToRegiste {
    [self registNotification];
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"isRegist"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void)HaveNoUpDateFile {
    NSString *wwwP = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"www"];
    NSLog(@"%@",wwwP);
    NSString *path = [self wwwPath];
    NSError *error = nil;
    NSURL *fileUrl = [NSURL fileURLWithPath:wwwP isDirectory:YES];
    NSURL *wwwUrl = [NSURL fileURLWithPath:path isDirectory:YES];
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        BOOL result = [[NSFileManager defaultManager] copyItemAtURL:fileUrl toURL:wwwUrl error:&error];
        if (result) {
            NSLog(@"copy本地的www文件到沙盒中");
            [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"isFirstInstall"];
        }
    }
    [self resetIndexPageToExternalStorage];
}


-(void)incremental:(CDVInvokedUrlCommand*)command{
    NSDictionary *dic = [command.arguments objectAtIndex:0];
    NSString *url = [dic objectForKey:@"url"];
    if([url hasSuffix:@".zip"]){
        [[NSUserDefaults standardUserDefaults] setObject:@"download" forKey:@"download"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        //增量更新 下载h5
         downloadVC = [DownloadViewController downloadUpdateH5WithUrl:url complete:^{
             [self removeDownloadView];
             //回调
             CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"downloadSuccess"];
             [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }];
    }
}

-(void)full:(CDVInvokedUrlCommand*)command {
    NSDictionary *dic = [command.arguments objectAtIndex:0];
    NSString *url = [dic objectForKey:@"url"];
    if ([url hasSuffix:@".api"]) {
        //全量更新
        NSString *item_app_url = @""; //[NSString stringWithFormat:@"itms-services://?action=download-manifest&url=%@",@" "];
        NSURL *url = [NSURL URLWithString:item_app_url];
        [[UIApplication sharedApplication] openURL:url];
        exit(0);
    }
}

//- (void)loadCordovaCtrl {
//    if (!loadHtml) {
//        loadHtml = [[LoadHtmlViewController alloc] init];
//    }
//    [loadHtml loadHtml];
//    UIWindow *window = [UIApplication sharedApplication].keyWindow;
//    [window.rootViewController presentViewController:loadHtml animated:NO completion:nil];
//}

- (void)registNotification {
    
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center addObserver:self
               selector:@selector(onNotification:)
                   name:CDVPluginResetNotification  // 开始加载
                 object:nil];
    [center addObserver:self
               selector:@selector(onNotification:)
                   name:CDVPageDidLoadNotification  // 加载完成
                 object:nil];
}
- (void)onNotification:(NSNotification *)notification {
    NSString *notificationName = [notification name];
    NSLog(@"%@",notificationName);
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if ([notificationName isEqualToString:CDVPageDidLoadNotification]) {
        if ([[userDefault objectForKey:@"download"] isEqualToString:@"download"]) {
            [userDefault setObject:@"nodownload" forKey:@"download"];
            //[self removeOrignalWWWFile];
            
        }
            [userDefault setBool:NO forKey:@"isRegist"];
            [userDefault synchronize];
    }else {
    
    }
    
}

- (void)removeDownloadView {
    UIView *maskV = [[[UIApplication sharedApplication] keyWindow] viewWithTag:101];
    [UIView animateWithDuration:2 animations:^{
        maskV.alpha = 0;
        downloadVC.view.alpha = 0;
        downloadVC.view.frame = CGRectMake(([UIScreen mainScreen].bounds.size.width - 250)/2, ([UIScreen mainScreen].bounds.size.height - 111)/2 + 100, 250, 111);
    } completion:^(BOOL finished) {
        [downloadVC.view removeFromSuperview];
        [maskV removeFromSuperview];
    }];
    
}

- (void)removeOrignalWWWFile {
    if ([[NSFileManager defaultManager] fileExistsAtPath:[self wwwPath]]) {
        BOOL result = [[NSFileManager defaultManager] removeItemAtURL:[NSURL fileURLWithPath:[self wwwPath]] error:nil];
        if (result) {
            NSLog(@"删除成功!");
        }
    }
}

- (NSString *)wwwPath{
    return [[NSHomeDirectory() stringByAppendingPathComponent:@"Documents"] stringByAppendingPathComponent:www];
}

- (NSString *)downloadPath:(NSString *)name {
    NSString *path = [[NSHomeDirectory() stringByAppendingPathComponent:@"Documents"] stringByAppendingPathComponent:name];
    NSLog(@"%@",path);
    return path;
}




@end
