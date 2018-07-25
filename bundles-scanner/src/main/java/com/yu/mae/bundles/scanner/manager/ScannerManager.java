package com.yu.mae.bundles.scanner.manager;

import android.app.Activity;
import android.graphics.Rect;

import com.yu.mae.bundles.scanner.camera.CameraManager;
import com.yu.mae.bundles.scanner.main.CaptureActivity;

import java.util.List;

/**
 * Created by liyu20 on 2017/10/25.
 */

/*
* 理论上应该包含对外提供所有扫描界面的开放方法，包括功能和UI方法
* */
public class ScannerManager {
    /**
     * 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
     */
    private BeepManager beepManager;
    /**
     * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
     */
    private LightSensorManager lightSensorManager;
    /*
    *  解析本地图片管理器
    * */
    private ParseLocalImgManager parseLocalImgManager;
    private BottomItemManager bottomItemManager;
    private Activity activity;

    public ScannerManager(CaptureActivity activity) {
        this.activity = activity;
        beepManager = new BeepManager(activity);
        lightSensorManager = new LightSensorManager(activity);
        parseLocalImgManager = new ParseLocalImgManager(activity);
    }

    /*------------------------------------声音---------------------------------------*/
    public void updateBeepPrefs(){
        beepManager.updatePrefs();
    }

    public void closeBeep(){
        beepManager.close();
    }

    public void playBeepSoundAndVibrate(){
        beepManager.playBeepSoundAndVibrate();
    }

    /*---------------------------------------底部Item---------------------------------------------*/
    public void initBottomBar(List<ScannerBottomItem> bottomItemList, OnBottomClickListener onBottomClickListener){
        bottomItemManager = new BottomItemManager(activity, this, bottomItemList);
        bottomItemManager.initBottomBar();
        bottomItemManager.setBottomClickListener(onBottomClickListener);
    }

    public interface OnBottomClickListener {
        void onBottomClick(ScannerManager scannerManager, String title);
    }

    /*---------------------------------------光线传感器-------------------------------------*/
    public void setLightCameraManager(CameraManager cameraManager){
        lightSensorManager.setCameraManager(cameraManager);
    }

    public void setLightTranslationY(Rect frame){
        lightSensorManager.setTranslationY(frame);
    }

    public void onLightDestroy(){
        lightSensorManager.onDestroy();
    }

    /*-----------------------------------解析本地图片--------------------------------------*/
    public void openAlbum(){
        parseLocalImgManager.openAlbum();
    }

}
