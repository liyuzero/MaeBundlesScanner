mae-bundles-scanner
====
扫一扫模块封装

##功能类别
- 提供基于ZXing的扫一扫功能封装模块

## 使用方法 （详情见demo）
> 在项目的根目录gradle新增仓库如下：
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

> 使用module依赖，新增依赖：
```
implementation 'com.github.liyuzero:MaeBundlesScanner:1.0.1'
```

> 打开扫一扫：

1、通过onActivityResult获取扫描参数

```

    //设置扫一扫底部显示Item，包含图片和文字
    ArrayList<ScannerBottomItem> list = new ArrayList<>();
    //list.add(new ScannerBottomItem(R.drawable.scan, "扫描", true));
    list.add(new ScannerBottomItem(R.drawable.album, "相册", false));
    list.add(new ScannerBottomItem(R.drawable.my_code, "我的二维码", false));

    //设置扫一扫底部显示Item的点击事件，其中scannerManager包含扫一扫界面中所有可调用的开放性功能
    ScannerManager.OnBottomClickListener onBottomClickListener = new ScannerManager.OnBottomClickListener() {
                        @Override
                        public void onBottomClick(ScannerManager scannerManager, String title) {
                            if(title.equals("相册")){
                                scannerManager.openAlbum();
                            }
                        }
                    };

    ZXingScanner.from(MainActivity.this)
            //扫描框宽度相对于屏幕宽度的比例
            .setRectEdgeWidthRate(0.68)
            //扫描框长度相对于其宽度的比例，1标识长宽相等
            .setRectEdgeHeightRate(1)
            //扫描界面的主颜色，扫一扫框的颜色
            .setMainColor(R.color.mae_scanner_main_color)
            //扫描框内动态扫描线的padding值
            .setMiddleLinePadding(10)
            //扫描框内扫描线的每次移动的时间间隔，值越小，扫描速度越快
            .setMiddleLineScanTime(10)
            //设置扫一扫内，需要使用相册图片做二维码解析时，用到的相册的主题颜色风格
            .setAlbumStyleRes(R.style.Album_Main)
            //设置底部自定义Item
            .setBottomItemList(list, onBottomClickListener)
            //111为requestCode，onRequestResult内使用
           .forResult(111);

```

2、通过CallBack回调获取扫描参数：该种回调方式 会在扫描界面 理扫描结果，
    需要调用operator.dispatcherFinished()来关闭扫描界面，表示处理完成，关闭摄像头扫描界面。
```

    .forResult(new ZXingScanner.ScannerCallBack() {
                        @Override
                        public void scannerCallBack(final String info,final CaptureResultDispatcher.CaptureOperator operator) {
                            //模拟获取扫描结果后的费时操作
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //关闭扫描界面
                                    operator.dispatcherFinished();
                                    Toast.makeText(MainActivity.this, "回调2秒："+info, Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        }
                    });

```


##Thanks
- [ZXing](https://github.com/zxing/zxing)
