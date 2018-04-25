package com.sat.mirrorcontorl.screenshot;

/**
 * Created by Tianluhua on 2018/04/10 将来可能有不同的数据获取方式，这里先抽象获取数据的接口
 */

public interface IScreenshot {

	public void onImageData();

	public interface ScreenshotCallBack {

		public void onImageData(byte[] buf);

	}
}
