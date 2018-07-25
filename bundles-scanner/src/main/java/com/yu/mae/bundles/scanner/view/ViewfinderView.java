package com.yu.mae.bundles.scanner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.yu.mae.bundles.scanner.MAEScannerParams;
import com.yu.mae.bundles.scanner.R;
import com.yu.mae.bundles.scanner.camera.CameraManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * <p>
 * <br/>
 * <br/>
 * 该视图是覆盖在相机的预览视图之上的一层视图。扫描区构成原理，其实是在预览视图上画四块遮罩层，
 * 中间留下的部分保持透明，并画上一条激光线，实际上该线条就是展示而已，与扫描功能没有任何关系。
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
    /*
    * 中间的框的线宽和线长(DP)
    * */
    private int rectLineLength;
    private int rectLineWidth;
    private int rectLineColor;
    /**
     * 刷新界面的时间
     */
    private long ANIMATION_DELAY;
    private static final int OPAQUE = 0xFF;
    /**
     * 中间那条线每次刷新移动的距离
     */
    private static final int SPEEN_DISTANCE = 5;
    private static final int MAX_RESULT_POINTS = 10;
    private static final String TAG = ViewfinderView.class.getSimpleName();
    /**
     * 扫描框中的中间线的宽度
     */
    private static int MIDDLE_LINE_WIDTH;
    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    private static int MIDDLE_LINE_PADDING;
    /**
     * 遮掩层的颜色
     */
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    /**
     * 画笔对象的引用
     */
    private final Paint paint;
    /**
     * 第一次绘制控件
     */
    private boolean isFirst = true;
    /**
     * 中间滑动线的最顶端位置
     */
    private int slideTop;
    /**
     * 中间滑动线的最底端位置
     */
    private int slideBottom;
    private Bitmap resultBitmap;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    private CameraManager cameraManager;
    private boolean hasNotified = false;
    private Bitmap middleLineBtp;

    public OnDrawFinishListener getListener() {
        return listener;
    }

    public void setOnDrawFinishListener(OnDrawFinishListener listener) {
        this.listener = listener;
    }

    private OnDrawFinishListener listener;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        middleLineBtp = BitmapFactory.decodeResource(getResources(), R.drawable.mae_bundles_scanner_laser);
        ANIMATION_DELAY = MAEScannerParams.getInstance().middleLineScanTime;
        MIDDLE_LINE_PADDING = MAEScannerParams.getInstance().middleLinePadding;
        MIDDLE_LINE_WIDTH = MAEScannerParams.getInstance().middleLineWidth;
        rectLineLength = MAEScannerParams.getInstance().rectEdgeLineLen;
        rectLineWidth = MAEScannerParams.getInstance().rectEdgeLineWidth;
        rectLineColor = MAEScannerParams.getInstance().mainColor;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 开启反锯齿

        maskColor = ContextCompat.getColor(getContext(), R.color.mae_bundles_scanner_viewfinder_mask); // 遮掩层颜色
        resultColor = ContextCompat.getColor(getContext(), R.color.mae_bundles_scanner_result_view);
        resultPointColor = MAEScannerParams.getInstance().mainColor;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();//获取二维码扫描识别区域，区域外遮罩层
        if (frame == null) {
            return;
        }
        // 绘制遮掩层
        drawCover(canvas, frame);

        if (resultBitmap != null) { // 绘制扫描结果的图
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(0xA0);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // 画扫描框边上的角
            drawRectEdges(canvas, frame);

            // 绘制扫描线
            drawScanningLine(canvas, frame);

            List<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }

            // 只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
            if (listener != null && !hasNotified) {
                listener.onDrawFinish(frame);
                hasNotified = true;
            }
        }
    }

    /**
     * 绘制扫描线
     *
     * @param frame 扫描框
     */
    private void drawScanningLine(Canvas canvas, Rect frame) {

        // 初始化中间线滑动的最上边和最下边
        if (isFirst) {
            isFirst = false;
            slideTop = frame.top;
            slideBottom = frame.bottom;
        }

        // 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
        slideTop += SPEEN_DISTANCE;
        if (slideTop >= slideBottom) {
            slideTop = frame.top;
        }

        // 从图片资源画扫描线
        Rect lineRect = new Rect();
        lineRect.left = frame.left + MIDDLE_LINE_PADDING;
        lineRect.right = frame.right - MIDDLE_LINE_PADDING;
        lineRect.top = slideTop;
        lineRect.bottom = slideTop + MIDDLE_LINE_WIDTH;
        lineRect.bottom = lineRect.bottom > frame.bottom? frame.bottom: lineRect.bottom;
        paint.setColorFilter( new PorterDuffColorFilter(MAEScannerParams.getInstance().mainColor, PorterDuff.Mode.SRC_IN)) ;
        canvas.drawBitmap(middleLineBtp, null, lineRect, paint);
        paint.setColorFilter(null);
    }

    /**
     * 绘制遮掩层
     */
    private void drawCover(Canvas canvas, Rect frame) {
        // 获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);

        // 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        // 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
    }

    /**
     * 描绘方形的四个角
     *
     * @param canvas
     * @param frame
     */
    private void drawRectEdges(Canvas canvas, Rect frame){
        paint.setColor(rectLineColor);
        paint.setAlpha(OPAQUE);
        paint.setStrokeWidth(rectLineWidth);
        canvas.drawLine(frame.left, frame.top + rectLineWidth/2, frame.left + rectLineLength, frame.top + rectLineWidth/2, paint);
        canvas.drawLine(frame.left + rectLineWidth/2, frame.top, frame.left + rectLineWidth/2, frame.top + rectLineLength, paint);

        canvas.drawLine(frame.left + rectLineWidth/2, frame.bottom, frame.left + rectLineWidth/2, frame.bottom - rectLineLength, paint);
        canvas.drawLine(frame.left, frame.bottom - rectLineWidth/2, frame.left + rectLineLength, frame.bottom - rectLineWidth/2, paint);

        canvas.drawLine(frame.right + 1, frame.top + rectLineWidth/2, frame.right + 1 - rectLineLength, frame.top + rectLineWidth/2, paint);
        canvas.drawLine(frame.right + 1 - rectLineWidth/2, frame.top, frame.right + 1 - rectLineWidth/2, frame.top + rectLineLength, paint);

        canvas.drawLine(frame.right + 1, frame.bottom - rectLineWidth/2, frame.right + 1 - rectLineLength, frame.bottom - rectLineWidth/2, paint);
        canvas.drawLine(frame.right + 1 - rectLineWidth/2, frame.bottom, frame.right + 1 - rectLineWidth/2, frame.bottom - rectLineLength, paint);
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

    public interface OnDrawFinishListener {
        void onDrawFinish(Rect frame);
    }
}
