package com.yu.mae.bundles.scanner.manager;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.yu.mae.bundles.scanner.MAEScannerParams;
import com.yu.mae.bundles.scanner.R;
import com.yu.mae.bundles.scanner.decode.BitmapDecoder;
import com.yu.mae.bundles.scanner.main.CaptureActivity;
import com.yu.mae.bundles.scanner.main.CaptureResultDispatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by liyu20 on 2017/10/24.
 */

@SuppressWarnings("deprecation")
class ParseLocalImgManager {
    private static final int PARSE_BARCODE_FAIL = 300;
    private static final int PARSE_BARCODE_SUC = 200;

    private final Handler parseHandler;
    private ProgressDialog dialog;
    private CaptureActivity activity;

    ParseLocalImgManager(CaptureActivity activity) {
        this.activity = activity;
        parseHandler = new MyHandler(activity);
    }

    void openAlbum(){
        if(MAEScannerParams.getInstance() != null){
            MAEScannerParams.getInstance().albumInterface.openAlbum(activity, new MAEScannerParams.AlbumInterface.AlbumInterfaceListener() {
                @Override
                public void onSelected(String codePath) {
                    if(!TextUtils.isEmpty(codePath)){
                        handleLocalImg(codePath);
                    }
                }
            });
        }
    }

    private void handleLocalImg(final String imgPath){
        showProgress(R.string.mae_bundles_scanner_scaning);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = null;
                try {
                    //使用ContentProvider通过URI获取原始图片
                    Bitmap photo = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.fromFile(new File(imgPath)));
                    if (photo != null) {
                        Bitmap bitmap;
                        BitmapDecoder decoder = new BitmapDecoder(activity);
                        //对图片进行缩放
                        float rate = photo.getHeight() / (float) photo.getWidth();
                        DisplayMetrics display = new DisplayMetrics();
                        activity.getWindowManager().getDefaultDisplay().getMetrics(display);
                        int screenWidth = display.widthPixels;
                        int screenHeight = display.heightPixels;
                        if(photo.getWidth() > screenWidth || photo.getHeight() > screenHeight){
                            int height = (int)(screenWidth * rate);
                            if(height < screenHeight){
                                bitmap = zoomBitmap(photo, screenWidth, height);
                            } else {
                                int width = (int)(screenHeight / rate);
                                bitmap = zoomBitmap(photo, width, screenHeight);
                            }
                            photo.recycle();
                        } else {
                            bitmap = photo;
                        }
                        result = decoder.getRawResult(bitmap, true);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    Message m = parseHandler.obtainMessage();
                    m.what = PARSE_BARCODE_SUC;
                    m.obj = ResultParser.parseResult(result)
                            .toString();
                    parseHandler.sendMessage(m);
                } else {
                    Message m = parseHandler.obtainMessage();
                    m.what = PARSE_BARCODE_FAIL;
                    parseHandler.sendMessage(m);
                }
            }
        }).start();
    }

    private Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w * 1.0f);
        float scaleHeight = ((float) height / h * 1.0f);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    private class MyHandler extends Handler {
        private final WeakReference<CaptureActivity> activityReference;

        public MyHandler(CaptureActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CaptureActivity activity = activityReference.get();
            if(activity == null) {
                return;
            }
            switch (msg.what) {
                case PARSE_BARCODE_SUC: // 解析图片成功
                    CaptureResultDispatcher.getDispatcher().dispatcher((String) msg.obj, activity);
                    hideProgress();
                    break;
                case PARSE_BARCODE_FAIL:// 解析图片失败
                    Toast.makeText(activity, R.string.mae_bundles_scanner_parse_image_fail,  Toast.LENGTH_SHORT).show();
                    hideProgress();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void showProgress(int strRes){
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(strRes));
        dialog.show();
    }

    private void hideProgress(){
        if (dialog != null && dialog.isShowing() && !activity.isFinishing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
