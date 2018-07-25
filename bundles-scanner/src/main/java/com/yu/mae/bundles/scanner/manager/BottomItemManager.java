package com.yu.mae.bundles.scanner.manager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yu.mae.bundles.scanner.MAEScannerParams;
import com.yu.mae.bundles.scanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyu20 on 2017/10/24.
 */

class BottomItemManager {
    private Activity activity;
    private List<ScannerBottomItem> bottomItemList;
    private List<ViewItems> bottomViewList;
    private ScannerManager.OnBottomClickListener onBottomClickListener;
    private ScannerManager scannerManager;

    BottomItemManager(Activity activity, ScannerManager scannerManager, List<ScannerBottomItem> bottomItemList) {
        this.bottomItemList = bottomItemList;
        this.activity = activity;
        this.scannerManager = scannerManager;
    }

    public void initBottomBar(){
        ViewGroup bottomBarContainer = activity.findViewById(R.id.bottom_bar_container);
        List<ScannerBottomItem> itemList = MAEScannerParams.getInstance().bottomItemList;
        if(itemList == null || itemList.size() == 0){
            bottomBarContainer.setVisibility(View.GONE);
            return;
        }
        bottomViewList = new ArrayList<>();
        for (int i = 0; i < bottomItemList.size(); i++){
            View view = LayoutInflater.from(activity).inflate(R.layout.mae_bundles_scanner_bottom_item, bottomBarContainer, false);
            ViewItems viewItems = new ViewItems(view, itemList.get(i));
            bottomViewList.add(viewItems);
            bottomBarContainer.addView(view);
        }
    }

    private class ViewItems implements View.OnClickListener{
        private TextView bottomItemText;
        private ImageView bottomItemImg;

        public ViewItems(View view, ScannerBottomItem scannerBottomItem) {
            bottomItemText = view.findViewById(R.id.bottom_item_text);
            bottomItemImg = view.findViewById(R.id.bottom_item_img);

            bottomItemText.setText(scannerBottomItem.title);
            bottomItemImg.setImageResource(scannerBottomItem.iconRes);

            if(scannerBottomItem.isShowColor){
                bottomItemText.setTextColor(MAEScannerParams.getInstance().mainColor);
                bottomItemImg.setColorFilter(MAEScannerParams.getInstance().mainColor);
            }
            view.setTag(scannerBottomItem.title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onBottomClickListener != null){
                onBottomClickListener.onBottomClick(scannerManager, (String) v.getTag());
            }
        }
    }

    public void setBottomClickListener(ScannerManager.OnBottomClickListener onBottomClickListener) {
        this.onBottomClickListener = onBottomClickListener;
    }
}
