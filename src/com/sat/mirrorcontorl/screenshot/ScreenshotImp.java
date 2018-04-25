package com.sat.mirrorcontorl.screenshot;

public class ScreenshotImp implements IScreenshot {

	private ScreenshotCallBack callBack;

	@Override
	public void onImageData() {
		// TODO Auto-generated method stub

	}

	public ScreenshotImp(ScreenshotCallBack callBack) {
		this.callBack = callBack;
	}

}
