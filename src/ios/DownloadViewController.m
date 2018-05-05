//
//  DownloadViewController.m
//  mytest
//
//  Created by 郑声强 on 2018/4/24.
//

#import "DownloadViewController.h"
#import "YLProgressBar.h"
#import "ZipArchive.h"
#import "LoadHtmlViewController.h"

@interface DownloadViewController ()<NSURLSessionDownloadDelegate>
{
    LoadHtmlViewController *loadHtml;
}
@property (nonatomic, copy) NSString *urlString;//下载url
@property (nonatomic, strong) YLProgressBar *progressView;//进度条
@property (nonatomic, strong) UIImageView *backImageView;//背景
@property (nonatomic, strong) UILabel *progressLabel;//进度条数据说明
@property (nonatomic, copy) DownloadCompleteBlock completeBlock;
@end

@implementation DownloadViewController

- (instancetype)init {
    self = [super init];
    if (self) {
        self.view.layer.cornerRadius = 15;
        self.view.layer.masksToBounds = YES;
    }
    return self;
}

+ (instancetype)downloadUpdateH5WithUrl:(NSString *)urlString  complete:(DownloadCompleteBlock)completeBlock{
    DownloadViewController *download = [[DownloadViewController alloc] init];
    download.urlString = urlString;
    download.completeBlock = completeBlock;
    [download show];
    [download downloadZipFile];
    return download;
}

- (UIImageView *)backImageView {
    if (!_backImageView) {
        _backImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 250, 111)];
        _backImageView.backgroundColor = [UIColor whiteColor];
    }
    return _backImageView;
}
- (UILabel *)progressLabel {
    if (!_progressLabel) {
        _progressLabel = [[UILabel alloc] init];
        _progressLabel.frame = CGRectMake(10, 20,CGRectGetWidth(self.backImageView.frame)-20, 50);
        _progressLabel.font = [UIFont systemFontOfSize:15];
        _progressLabel.textColor = [UIColor blackColor];
        _progressLabel.textAlignment = NSTextAlignmentCenter;
        _progressLabel.numberOfLines = 0;
        _progressLabel.text = @"正在下载新版本,请稍等...";
    }
    return _progressLabel;
}

- (YLProgressBar *)progressView {
    if (!_progressView) {
        _progressView = [[YLProgressBar alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(self.progressLabel.frame) + 10, CGRectGetWidth(self.backImageView.frame)-20, 6)];
        _progressView.progress = 0;
        _progressView.progressStretch = NO;
        _progressView.stripesAnimated = NO;
        _progressView.hideStripes = YES;
        _progressView.hideGloss = YES;
        _progressView.trackTintColor = [UIColor lightGrayColor];
        _progressView.hidden = NO;
    }
    return _progressView;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    [self addDownloadUI];
    // Do any additional setup after loading the view.
}

- (void)addDownloadUI {
    [self.view addSubview:self.backImageView];
    [self.backImageView addSubview:self.progressView];
    [self.backImageView addSubview:self.progressLabel];
    NSArray *tintColors = @[[UIColor colorWithRed:33/255.0f green:180/255.0f blue:162/255.0f alpha:1.0f],
                            [UIColor colorWithRed:3/255.0f green:137/255.0f blue:166/255.0f alpha:1.0f],
                            [UIColor colorWithRed:91/255.0f green:63/255.0f blue:150/255.0f alpha:1.0f],
                            [UIColor colorWithRed:87/255.0f green:26/255.0f blue:70/255.0f alpha:1.0f],
                            [UIColor colorWithRed:126/255.0f green:26/255.0f blue:36/255.0f alpha:1.0f],
                            [UIColor colorWithRed:149/255.0f green:37/255.0f blue:36/255.0f alpha:1.0f],
                            [UIColor colorWithRed:228/255.0f green:69/255.0f blue:39/255.0f alpha:1.0f],
                            [UIColor colorWithRed:245/255.0f green:166/255.0f blue:35/255.0f alpha:1.0f],
                            [UIColor colorWithRed:165/255.0f green:202/255.0f blue:60/255.0f alpha:1.0f],
                            [UIColor colorWithRed:202/255.0f green:217/255.0f blue:54/255.0f alpha:1.0f],
                            [UIColor colorWithRed:111/255.0f green:188/255.0f blue:84/255.0f alpha:1.0f]];

    self.progressView.progressTintColors = tintColors;
    
    
}


- (void)downloadZipFile {
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.urlString]];
    //创建默认会话对象
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    //后面队列的作用  如果给子线程队列则协议方法在子线程中执行 给主线程队列就在主线程中执行
    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration delegate:self delegateQueue:[NSOperationQueue mainQueue]];
    NSURLSessionDownloadTask *downloadTask = [session downloadTaskWithRequest:request];
    [downloadTask resume];//启动
    
}

#pragma mark - 下载协议
//下载进度
- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask
      didWriteData:(int64_t)bytesWritten
 totalBytesWritten:(int64_t)totalBytesWritten
totalBytesExpectedToWrite:(int64_t)totalBytesExpectedToWrite {
    //每次下载量 已经下载量 总大小
    //NSLog(@"%lld   %lld   %lld",bytesWritten,totalBytesWritten,totalBytesExpectedToWrite);
    self.progressView.progress = totalBytesWritten * 1.0/totalBytesExpectedToWrite;
    double progressd = (totalBytesWritten * 1.0/totalBytesExpectedToWrite) * 100;
    NSString *progress = [NSString stringWithFormat:@"正在下载新版本： %.f%%",progressd];
    [self updateProgressLMsg:progress];
}
//下载完成
- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask
didFinishDownloadingToURL:(NSURL *)location {
    [self updateProgressLMsg:@"正在解压新版本，请稍等..."];
    NSString *path = [[NSHomeDirectory() stringByAppendingPathComponent:@"Documents"] stringByAppendingPathComponent:@"www.zip"];
    NSURL *newLocation = [NSURL fileURLWithPath:path];//新位置
    //默认是下载到了location这个临时位置，我们要替换到我们的新位置去
    [[NSFileManager defaultManager] moveItemAtURL:location toURL:newLocation error:nil];
    //解压
    [self goToZIPArchive:path];
}

//网络请求完成
-(void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    NSLog(@"%@",error);
    if (error) {
        [self updateProgressLMsg:@"更新失败!"];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            self.completeBlock();
        });
       
    }
}

//解压zip后的路径
- (NSString *)downloadPath:(NSString *)name {
    NSString *path = [[NSHomeDirectory() stringByAppendingPathComponent:@"Documents"] stringByAppendingPathComponent:name];
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSLog(@"%@",path);
    return path;
}

//解压完成后删掉下载的zip文件
- (void)deleteZipFile {
    NSString *path = [[NSHomeDirectory() stringByAppendingPathComponent:@"Documents"] stringByAppendingPathComponent:@"www.zip"];
    if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
        [[NSFileManager defaultManager] removeItemAtPath:path error:nil];
    }
}

- (void)goToZIPArchive:(NSString *)path {
    NSString *afterpath = [self downloadPath:@"UpDate"];
    BOOL result = [self zipArchiveFrom:path to:afterpath];
    if (result) {
        
        [self replayWWWFile];
        
    }else {
        [self updateProgressLMsg:@"更新失败!"];
    }
    
}
//用下载的文件替换原www里的文件
- (void)replayWWWFile {
    NSFileManager *filManager = [NSFileManager defaultManager];
    NSString *wwwPath = [self downloadPath:@"www"];
    NSString *updatePath = [self downloadPath:@"UpDate/www"];
    NSString *indexPath = [wwwPath stringByAppendingPathComponent:@"index.html"];
    NSString *updateIndex = [updatePath stringByAppendingPathComponent:@"index.html"];
    NSString *wwwStatic = [wwwPath stringByAppendingPathComponent:@"static"];
    NSString *updateStatic = [updatePath stringByAppendingPathComponent:@"static"];
    BOOL moveIndex = YES;
    BOOL moveStatic = YES;
    if ([filManager fileExistsAtPath:updateIndex]) {
        [filManager removeItemAtPath:indexPath error:nil];
        moveIndex = [filManager copyItemAtPath:updateIndex toPath:indexPath error:nil];
    }
    if ([filManager fileExistsAtPath:updateStatic]) {
        [filManager removeItemAtPath:wwwStatic error:nil];
        moveStatic = [filManager copyItemAtPath:updateStatic toPath:wwwStatic error:nil];
    }
    if (moveStatic && moveIndex) {
        [self updateProgressLMsg:@"更新完成!"];
        [self deleteZipFile];
        self.completeBlock();
    }else {
        [self updateProgressLMsg:@"更新失败!"];
    }
//    NSURL *wwwUrl = [NSURL fileURLWithPath:wwwPath isDirectory:YES];
//    NSURL *updateUrl = [NSURL fileURLWithPath:updatePath isDirectory:YES];
//    BOOL re = [[NSFileManager defaultManager] copyItemAtURL:updateUrl toURL:wwwUrl error:nil];
////    [self clearAllFile:[wwwPath stringByAppendingPathComponent:@"index.html"]];
////    [self clearAllFile:[wwwPath stringByAppendingPathComponent:@"static"]];
//    //BOOL result = [self moveReult:updatePath to:wwwPath];
//    if (re) {
//        [self updateProgressLMsg:@"更新完成!"];
//        [self deleteZipFile];
//        self.completeBlock();
//    }else {
//        [self updateProgressLMsg:@"更新失败!"];
//    }
}

- (void)clearAllFile:(NSString *)path {
    if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
        [[NSFileManager defaultManager] removeItemAtPath:path error:nil];
    }
}

- (BOOL)moveReult:(NSString *)updatePath to:(NSString *)wwwPath {
    BOOL result1 =  [self moveFile:[updatePath stringByAppendingString:@"index.html"] to:wwwPath];
    BOOL result2 = [self moveFile:[updatePath stringByAppendingString:@"static"] to:wwwPath];
    if (result1 && result2) {
        return YES;
    }else {
        return NO;
    }
}
- (BOOL)moveFile:(NSString *)updatePath to:(NSString *)wwwPath {
    if ([[NSFileManager defaultManager] fileExistsAtPath:updatePath]) {
        return [[NSFileManager defaultManager] moveItemAtPath:updatePath toPath:wwwPath error:nil];
    }else {
        return YES;
    }
}

//zipPath zip存放路径。 goalPath 解压的目标路径
- (BOOL)zipArchiveFrom:(NSString *)zipPath to:(NSString *)goalPath {
    ZipArchive *za = [[ZipArchive alloc] init];
    if ([za UnzipOpenFile: zipPath]) {
        BOOL ret = [za UnzipFileTo: goalPath overWrite: YES];
        if (NO == ret){
            return NO;
        } return [za UnzipCloseFile];
    }
    return NO;
}

- (void)updateProgressLMsg:(NSString *)msg {
    self.progressLabel.text = msg;
}


- (void)show {
    UIView *maskV = [[UIView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    maskV.tag = 101;
    maskV.backgroundColor = [UIColor blackColor];
    maskV.alpha = 0;
    //CGFloat x = ([UIScreen mainScreen].bounds.size.width - 250)/2;
    self.view.frame = CGRectMake([UIScreen mainScreen].bounds.size.width/2, [UIScreen mainScreen].bounds.size.height/2, 0, 0);
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    [window addSubview:maskV];
    [window addSubview:self.view];
    [self addAnimation:maskV];
}

- (void)addAnimation:(UIView *)maskV {
    
    [UIView animateWithDuration:.5
                          delay:0.0
         usingSpringWithDamping:.8
          initialSpringVelocity:20
                        options:(UIViewAnimationOptionCurveEaseInOut)
                     animations:^{
                         maskV.alpha = 0.25;
                         self.view.frame = CGRectMake(([UIScreen mainScreen].bounds.size.width - 250)/2, ([UIScreen mainScreen].bounds.size.height - 111)/2, 250, 111);
                     } completion:^(BOOL finished) {
                         
                     }];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
