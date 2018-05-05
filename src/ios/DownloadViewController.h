//
//  DownloadViewController.h
//  mytest
//
//  Created by 郑声强 on 2018/4/24.
//

#import <UIKit/UIKit.h>
typedef void (^DownloadCompleteBlock)();
@interface DownloadViewController : UIViewController
+ (instancetype)downloadUpdateH5WithUrl:(NSString *)urlString complete:(DownloadCompleteBlock)completeBlock;
@end
