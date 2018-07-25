package com.yu.mae.bundles.scanner.manager;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.ImageView;

import com.yu.mae.bundles.scanner.R;
import com.yu.mae.bundles.scanner.camera.CameraManager;

/**
 * Created by liyu20 on 2017/10/24.
 */

class LightSensorManager implements SensorEventListener{
    private static final float DARK_LUX = 20.0f;

    private CameraManager cameraManager;
    private SensorManager sensorManager;
    private Activity activity;
    private View container;
    private ImageView lightView;
    private boolean isFlashlightOpen;

    LightSensorManager(Activity activity) {
        this.activity = activity;
        initView();
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, sensor, SensorManager. SENSOR_DELAY_NORMAL);
    }

    private void initView(){
        container = activity.findViewById(R.id.light_container);
        lightView = activity.findViewById(R.id.light);
        initClick();
    }

    private void initClick(){
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(container.getAlpha() == 1 && cameraManager != null){
                    cameraManager.setTorch(isFlashlightOpen = !isFlashlightOpen);
                    lightView.setImageResource(isFlashlightOpen? R.drawable.mae_bundles_scanner_light_open : R.drawable.mae_bundles_scanner_light_close);
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.values[0] <= DARK_LUX && container.getAlpha() == 0){
            changeAlpha();
        } else if(event.values[0] > DARK_LUX && container.getAlpha() == 1 && !isFlashlightOpen){
            changeAlpha();
        }
    }

    private void changeAlpha(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(container,"alpha", container.getAlpha(), (container.getAlpha() + 1) % 2);
        animator.setDuration(350);
        animator.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public void setTranslationY(Rect frame){
        container.setTranslationY(frame.bottom - container.getMeasuredHeight() - 13);
    }

    public void onDestroy(){
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        cameraManager = null;
    }
}
