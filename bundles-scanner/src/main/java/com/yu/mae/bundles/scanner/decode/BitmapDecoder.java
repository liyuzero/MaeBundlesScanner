package com.yu.mae.bundles.scanner.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 从bitmap解码
 * 
 * @author hugo
 * 
 */
public class BitmapDecoder {

	private final MultiFormatReader multiFormatReader;

	private Bitmap curBitmap;

	public BitmapDecoder(Context context) {

		multiFormatReader = new MultiFormatReader();

		// 解码的参数
		Hashtable<DecodeHintType, Object> hints = new Hashtable<>(
				2);
		// 可以解析的编码类型
		Vector<BarcodeFormat> decodeFormats = new Vector<>();
		if (decodeFormats.isEmpty()) {
			decodeFormats = new Vector<>();

			//支持Product 1D，QR_Code，DataMatrix
			decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		}
		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		// 设置继续的字符编码格式为UTF8
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

		// 设置解析配置参数
		multiFormatReader.setHints(hints);

	}

	/**
	 * 获取解码结果
	 * 
	 * @param bitmap
	 * @return
	 */
	public Result getRawResult(Bitmap bitmap, boolean isParseLocalImg) {
		if (bitmap == null) {
			return null;
		}
		Result result = getResult(bitmap, isParseLocalImg? 3: 1, false);
		if(result == null) {
			result = getResult(curBitmap, isParseLocalImg? 3: 1, true);
		}
		if(curBitmap != null && !curBitmap.isRecycled()){
			curBitmap.recycle();
		}
		return result;
	}

	private Result getResult(Bitmap bitmap, int rotateCount, boolean isInvert){
		Result result;
		for (int i=0; i<=rotateCount; i++){
			result = getResult(bitmap, isInvert);
			if(result != null){
				return result;
			}
			multiFormatReader.reset();
			if(i < rotateCount){
				bitmap = rotateBitmap(bitmap, 90);
				curBitmap = bitmap;
			}
		}
		return null;
	}

	private Result getResult(Bitmap bitmap, boolean isInvert) {
		try {
			int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
			bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
			RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), data);
			return multiFormatReader.decodeWithState(new BinaryBitmap(
					new HybridBinarizer(isInvert? rgbLuminanceSource.invert(): rgbLuminanceSource)));
		} catch (NotFoundException e){
			return null;
		}
	}

	private Bitmap rotateBitmap(Bitmap origin, float alpha) {
		if (origin == null) {
			return null;
		}
		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(alpha);
		// 围绕原地进行旋转
		Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		if (newBM.equals(origin)) {
			return newBM;
		}
		if(!origin.isRecycled()){
			origin.recycle();
		}
		return newBM;
	}
}
