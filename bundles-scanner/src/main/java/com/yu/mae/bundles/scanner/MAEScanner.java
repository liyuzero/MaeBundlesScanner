package com.yu.mae.bundles.scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.yu.bundles.monitorfragment.MAEMonitorFragment;
import com.yu.bundles.monitorfragment.MAEPermissionCallback;
import com.yu.mae.bundles.scanner.main.CaptureActivity;
import com.yu.mae.bundles.scanner.main.CaptureResultDispatcher;
import com.yu.mae.bundles.scanner.manager.ScannerBottomItem;
import com.yu.mae.bundles.scanner.manager.ScannerManager;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by liyu20 on 2017/10/25.
 */

public class MAEScanner {
    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private MAEScannerParams maeScannerParams;

    private MAEScanner(Activity activity, Fragment fragment){
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
        maeScannerParams = MAEScannerParams.getInstance();
        maeScannerParams.reset(activity);
    }

    public static MAEScanner from(Activity activity){
        return new MAEScanner(activity, null);
    }

    public static MAEScanner from(Fragment fragment){
        return new MAEScanner(fragment.getActivity(), fragment);
    }

    public MAEScanner setRectEdgeLineLen(int rectEdgeLineLen) {
        maeScannerParams.setRectEdgeLineLen(dip2px(mContext.get(), rectEdgeLineLen));
        return this;
    }

    public MAEScanner setRectEdgeLineWidth(int rectEdgeLineWidth) {
        maeScannerParams.setRectEdgeLineWidth(dip2px(mContext.get(), rectEdgeLineWidth));
        return this;
    }

    public MAEScanner setMainColor(int rectEdgeLineColor) {
        maeScannerParams.setMainColor(ContextCompat.getColor(mContext.get(), rectEdgeLineColor));
        return this;
    }

    public MAEScanner setRectEdgeWidthRate(double rectEdgeWidthRate) {
        maeScannerParams.setRectEdgeWidthRate(rectEdgeWidthRate);
        return this;
    }

    public MAEScanner setRectEdgeHeightRate(double rectEdgeHeightRate) {
        maeScannerParams.setRectEdgeHeightRateToWidth(rectEdgeHeightRate);
        return this;
    }

    public MAEScanner setMiddleLineWidth(int middleLineWidth) {
        maeScannerParams.setMiddleLineWidth(dip2px(mContext.get(), middleLineWidth));
        return this;
    }

    public MAEScanner setMiddleLinePadding(int middleLinePadding) {
        maeScannerParams.setMiddleLinePadding(dip2px(mContext.get(), middleLinePadding));
        return this;
    }

    public MAEScanner setMiddleLineScanTime(int middleLineScanTime){
        maeScannerParams.setMiddleLineScanTime(middleLineScanTime);
        return this;
    }

    public MAEScanner setAlbumListener (MAEScannerParams.AlbumInterface albumInterface){
        maeScannerParams.setAlbumInterface(albumInterface);
        return this;
    }

    public MAEScanner setBottomItemList(List<ScannerBottomItem> bottomItemList, ScannerManager.OnBottomClickListener onBottomClickListener) {
        maeScannerParams.setBottomItemList(bottomItemList);
        maeScannerParams.setOnBottomClickListener(onBottomClickListener);
        return this;
    }

    public void forResult(final int requestCode){
        final Activity activity = mContext.get();
        if(activity == null){
            return ;
        }
        requestPermission(activity, new MAEPermissionCallback() {
            @Override
            public void onPermissionApplySuccess() {
                Intent intent = new Intent(activity, CaptureActivity.class);
                Fragment fragment = mFragment.get();
                if(fragment != null){
                    fragment.startActivityForResult(intent, requestCode);
                } else {
                    activity.startActivityForResult(intent, requestCode);
                }
            }

            @Override
            public void onPermissionApplyFailure(List<String> list, List<Boolean> list1) {
                Toast.makeText(activity, activity.getString(R.string.mae_bundles_scanner_no_permission), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void forResult(final ScannerCallBack scannerCallBack){
        final Activity activity = mContext.get();
        if(activity == null){
            return ;
        }
        requestPermission(activity, new MAEPermissionCallback() {
            @Override
            public void onPermissionApplySuccess() {
                maeScannerParams.setScannerCallBack(scannerCallBack);
                activity.startActivity(new Intent(activity, CaptureActivity.class));
            }

            @Override
            public void onPermissionApplyFailure(List<String> list, List<Boolean> list1) {
                Toast.makeText(activity, activity.getString(R.string.mae_bundles_scanner_no_permission), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermission(Activity activity, MAEPermissionCallback callback){
        MAEMonitorFragment.getInstance(activity).requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, callback);
    }

    public interface ScannerCallBack {
        //回调默认在扫描界面进行处理，需要利用传入的handler发出回调处理完成的停止信号，调用sendEmptyMessage即可，参数值任意
        void scannerCallBack(String info, CaptureResultDispatcher.CaptureOperator handler);
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
