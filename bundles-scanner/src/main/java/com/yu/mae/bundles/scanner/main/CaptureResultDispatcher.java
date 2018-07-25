package com.yu.mae.bundles.scanner.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.yu.mae.bundles.scanner.MAEScannerParams;

/**
 * Created by hufeng on 2016/8/12
 */
public class CaptureResultDispatcher{
    //private static final String TAG = CaptureResultDispatcher.class.getSimpleName();
    private static CaptureResultDispatcher dispatcher = new CaptureResultDispatcher();
    private CaptureActivity activity;

    private CaptureResultDispatcher() {

    }

    public static CaptureResultDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * @param activity 不能在构造方法中传入，因为会导致内存泄漏
     */
    public void dispatcher(final String result, final CaptureActivity activity) {
        this.activity = activity;
        if (activity == null)
            return;
        activity.startDispatcher();
        callBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MAEScannerParams.getInstance().scannerCallBack == null){
                    Intent intent = new Intent();
                    intent.putExtra("result", result);
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.dispatcherFinished();
                    CaptureResultDispatcher.this.activity = null;
                } else {
                    MAEScannerParams.getInstance().scannerCallBack.scannerCallBack(result, new CaptureOperator(callBackHandler));
                }
            }
        }, 100);
    }

    public class CaptureOperator {
        private Handler handler;

        public CaptureOperator(Handler handler) {
            this.handler = handler;
        }

        public void dispatcherFinished(){
            handler.sendEmptyMessage(0);
        }
    }

    private Handler callBackHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(activity != null){
                activity.dispatcherFinished();
                activity = null;
            }
        }
    };
}
