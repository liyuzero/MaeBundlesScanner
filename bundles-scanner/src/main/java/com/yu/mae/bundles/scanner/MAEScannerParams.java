package com.yu.mae.bundles.scanner;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.yu.mae.bundles.scanner.manager.ScannerBottomItem;
import com.yu.mae.bundles.scanner.manager.ScannerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyu20 on 2017/10/24.
 */

public class MAEScannerParams {
    //toolbar初始化接口
    public MAEScanner.ToolbarInitInterface toolbarInitInterface;
    //扫描框四角的线长
    public int rectEdgeLineLen;
    //扫描框四角的线宽
    public int rectEdgeLineWidth;
    //扫描框的颜色
    public int mainColor;

    //扫描框宽度占屏幕宽度的比例
    public double rectEdgeWidthRate;
    //扫描框高度相对宽度的比例，1表示宽高一致
    public double rectEdgeHeightRateToWidth;

    // 扫描框中的中间线的宽度
    public int middleLineWidth;
    //扫描框中的中间线的与扫描框左右的间隙
    public int middleLinePadding;
    //扫描线移动刷新时间
    public int middleLineScanTime;
    //底部bar内容
    public List<ScannerBottomItem> bottomItemList = new ArrayList<>();
    public ScannerManager.OnBottomClickListener onBottomClickListener;
    //扫描回调
    public MAEScanner.ScannerCallBack scannerCallBack;
    //相册
    public AlbumInterface albumInterface;

    private static MAEScannerParams MAEScannerParams;

    public static MAEScannerParams getInstance(){
        if(MAEScannerParams == null){
            synchronized (MAEScannerParams.class){
                if(MAEScannerParams == null){
                    MAEScannerParams = new MAEScannerParams();
                }
            }
        }
        return MAEScannerParams;
    }

    private MAEScannerParams() {}

    void reset(Context context){
        rectEdgeLineLen = dip2px(context, 20);
        rectEdgeLineWidth = dip2px(context, 3);
        mainColor = ContextCompat.getColor(context, R.color.mae_bundles_scanner_main_color);
        rectEdgeWidthRate = 0.618f;
        rectEdgeHeightRateToWidth = 1;
        middleLineWidth = dip2px(context, 3);
        middleLinePadding = dip2px(context, 20);
        middleLineScanTime = 20;
        bottomItemList.clear();
        onBottomClickListener = null;
        scannerCallBack = null;
        toolbarInitInterface = null;
    }

    public void setAlbumInterface(AlbumInterface albumInterface) {
        this.albumInterface = albumInterface;
    }

    void setRectEdgeLineLen(int rectEdgeLineLen) {
        this.rectEdgeLineLen = rectEdgeLineLen;
    }

    void setRectEdgeLineWidth(int rectEdgeLineWidth) {
        this.rectEdgeLineWidth = rectEdgeLineWidth;
    }

    void setMainColor(int mainColor) {
        this.mainColor = mainColor;
    }

    void setRectEdgeWidthRate(double rectEdgeWidthRate) {
        this.rectEdgeWidthRate = rectEdgeWidthRate;
    }

    void setRectEdgeHeightRateToWidth(double rectEdgeHeightRateToWidth) {
        this.rectEdgeHeightRateToWidth = rectEdgeHeightRateToWidth;
    }

    void setMiddleLineWidth(int middleLineWidth) {
        this.middleLineWidth = middleLineWidth;
    }

    void setMiddleLinePadding(int middleLinePadding) {
        this.middleLinePadding = middleLinePadding;
    }

    void setBottomItemList(List<ScannerBottomItem> bottomItemList) {
        this.bottomItemList = bottomItemList;
    }

    void setOnBottomClickListener(ScannerManager.OnBottomClickListener onBottomClickListener) {
        this.onBottomClickListener = onBottomClickListener;
    }

    void setScannerCallBack(MAEScanner.ScannerCallBack scannerCallBack) {
        this.scannerCallBack = scannerCallBack;
    }

    void setMiddleLineScanTime(int middleLineScanTime) {
        this.middleLineScanTime = middleLineScanTime;
    }

    void setToolbarInitInterface(MAEScanner.ToolbarInitInterface initInterface){
        this.toolbarInitInterface = initInterface;
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public interface AlbumInterface {
        void openAlbum(Activity activity, AlbumInterfaceListener albumInterfaceListener);

        interface AlbumInterfaceListener {
            void onSelected(String codePath);
        }
    }
}
