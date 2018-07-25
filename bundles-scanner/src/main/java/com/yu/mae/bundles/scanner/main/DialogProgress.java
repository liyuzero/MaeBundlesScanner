package com.yu.mae.bundles.scanner.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.yu.mae.bundles.scanner.R;

/**
 * Created by liyu20 on 2017/10/25.
 */

class DialogProgress extends Dialog {
    private TextView view;
    private String title;

    DialogProgress(Context context){
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mae_bundles_scanner_dialog_progress);
        view = findViewById(R.id.text);
        if(title != null){
            view.setText(title);
        }
    }

    public void setMessage(String str){
        this.title = str;
        if(view != null){
            view.setText(str);
        }
    }
}
