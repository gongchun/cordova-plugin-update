//
//  LoadHtmlViewController.m
//  mytest
//
//  Created by 郑声强 on 2018/4/25.
//

#import "LoadHtmlViewController.h"

@interface LoadHtmlViewController ()

@end

@implementation LoadHtmlViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

#define CordovaFile [@"file://" stringByAppendingString:folder]
//#define VersionFolder [NSString stringWithFormat:@"%@/%@/%@",@"Documents",@"UpDate",@"www"]
#define wwwFolder [NSString stringWithFormat:@"%@/%@",@"Documents",@"www"]
- (void)loadHtml {
    NSString *firstFolder = [NSHomeDirectory() stringByAppendingPathComponent:wwwFolder];
    self.wwwFolderName = [@"file://" stringByAppendingString:firstFolder];
    //self.configFile = [firstFolder stringByAppendingPathComponent:@"/config.xml"];
    self.startPage = @"index.html";
    
    NSURL *url = [self performSelector:NSSelectorFromString(@"appUrl")];
    if (url){
        NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url];
        [self.webViewEngine loadRequest:request];
    }
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
