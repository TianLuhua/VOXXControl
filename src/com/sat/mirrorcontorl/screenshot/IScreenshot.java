package com.sat.mirrorcontorl.screenshot;

/**
 * Created by Tianluhua on 2018/04/10 ���������в�ͬ�����ݻ�ȡ��ʽ�������ȳ����ȡ���ݵĽӿ�
 */

public interface IScreenshot {

	public void onImageData();

	public interface ScreenshotCallBack {

		public void onImageData(byte[] buf);

	}
}
