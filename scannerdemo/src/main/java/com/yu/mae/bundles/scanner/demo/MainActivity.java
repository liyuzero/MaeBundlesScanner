package com.yu.mae.bundles.scanner.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yu.bundles.album.AlbumListener;
import com.yu.bundles.album.MaeAlbum;
import com.yu.mae.bundles.scanner.MAEScanner;
import com.yu.mae.bundles.scanner.MAEScannerParams;
import com.yu.mae.bundles.scanner.main.CaptureResultDispatcher;
import com.yu.mae.bundles.scanner.manager.ScannerBottomItem;
import com.yu.mae.bundles.scanner.manager.ScannerManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaeAlbum.setImageEngine(new GlideEngine());

        findViewById(R.id.red).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ScannerBottomItem> list = new ArrayList<>();
                //list.add(new ScannerBottomItem(R.drawable.scan, "扫描", true));
                list.add(new ScannerBottomItem(R.drawable.album, "相册", false));
                list.add(new ScannerBottomItem(R.drawable.my_code, "我的二维码", false));
                MAEScanner.from(MainActivity.this)
                        .setAlbumListener(new MAEScannerParams.AlbumInterface() {
                            @Override
                            public void openAlbum(Activity activity, final AlbumInterfaceListener albumInterfaceListener) {
                                MaeAlbum.from(activity)
                                        .maxSize(9)
                                        .column(3)
                                        .forResult(new AlbumListener() {
                                            @Override
                                            public void onSelected(List<String> list) {
                                                if(list.size() > 0){
                                                    albumInterfaceListener.onSelected(list.get(0));
                                                }
                                            }

                                            @Override
                                            public void onFull(List<String> list, String s) {

                                            }
                                        });
                            }
                        })
                        .setRectEdgeWidthRate(0.68)
                        .setRectEdgeHeightRate(1)
                        .setMainColor(R.color.mae_bundles_scanner_main_color)
                        .setMiddleLinePadding(10)
                        .setMiddleLineScanTime(10)
                        .setBottomItemList(list, new ScannerManager.OnBottomClickListener() {
                            @Override
                            public void onBottomClick(ScannerManager scannerManager, String title) {
                                if(title.equals("相册")){
                                    scannerManager.openAlbum();
                                } else if(title.equals("我的二维码")){
                                    Toast.makeText(MainActivity.this, "打开我的二维码", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                       .forResult(111);
            }
        });

        findViewById(R.id.blue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ScannerBottomItem> list = new ArrayList<>();
                //list.add(new ScannerBottomItem(R.drawable.scan, "扫描", true));
                list.add(new ScannerBottomItem(R.drawable.album, "相册", false));
                ScannerManager.OnBottomClickListener onBottomClickListener = new ScannerManager.OnBottomClickListener() {
                    @Override
                    public void onBottomClick(ScannerManager scannerManager, String title) {
                        if(title.equals("相册")){
                            scannerManager.openAlbum();
                        }
                    }
                };
                MAEScanner.from(MainActivity.this)
                        .setRectEdgeWidthRate(0.68)
                        .setRectEdgeHeightRate(1)
                        .setMiddleLineWidth(4)
                        .setMiddleLinePadding(20)
                        .setMiddleLineScanTime(5)
                        .setMainColor(R.color.colorPrimary)
                        .setBottomItemList(list, onBottomClickListener)
                        .forResult(new MAEScanner.ScannerCallBack() {
                            @Override
                            public void scannerCallBack(final String info,final CaptureResultDispatcher.CaptureOperator operator) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        operator.dispatcherFinished();
                                        Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                                        loadUrl(info);
                                    }
                                }, 0);
                            }
                        });
            }
        });

        findViewById(R.id.blue2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MAEScanner.from(MainActivity.this)
                        .setRectEdgeWidthRate(0.6)
                        .setRectEdgeHeightRate(1.2)
                        .setRectEdgeLineLen(30)
                        .setRectEdgeLineWidth(2)
                        .setMainColor(R.color.mae_bundles_scanner_main_color)
                        .setMiddleLinePadding(10)
                        .forResult(new MAEScanner.ScannerCallBack() {
                            @Override
                            public void scannerCallBack(final String info,final CaptureResultDispatcher.CaptureOperator operator) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        operator.dispatcherFinished();
                                        Toast.makeText(MainActivity.this, "回调2秒："+info, Toast.LENGTH_SHORT).show();
                                        loadUrl(info);
                                    }
                                }, 2000);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 111 && data != null){
            String result = data.getStringExtra("result");
            Toast.makeText(MainActivity.this, "Request结果："+result, Toast.LENGTH_SHORT).show();
            loadUrl(result);
        }
    }

    private void loadUrl(String url){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
