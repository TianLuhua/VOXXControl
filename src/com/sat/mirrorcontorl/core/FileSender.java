package com.sat.mirrorcontorl.core;

import java.io.OutputStream;
import java.net.Socket;

import com.sat.mirrorcontorl.utils.LogUtils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class FileSender extends BaseTransfer implements Runnable {

	public static final String TAG = "FileSender";

	/**
	 * Socket�����������
	 */
	private Socket mSocket;
	private OutputStream mOutputStream;

	/**
	 * �����ļ�Ŀ��ĵ�ַ�Լ��˿�
	 */
	private String mServerIpAddress;
	private int mPort;

	/**
	 * �����ļ�����Ϣ
	 */
	private Bitmap mFileInfo;

	/**
	 * ��ǰ��Ҫѭ�������ļ�
	 */
	private Handler mHandler;

	public FileSender(String mServerIpAddress, int mPort) {
		this.mServerIpAddress = mServerIpAddress;
		this.mPort = mPort;
	}

	/**
	 * 
	 */
	public void setSenderFile() {

	}

	@Override
	public void run() {
		Looper.prepare();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				

			}
		};
		Looper.loop();
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseHeader() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseBody() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() throws Exception {
		// TODO Auto-generated method stub

	}

	private OnSendListener mOnSendListener;

	public FileSender(OnSendListener mOnSendListener) {
		this.mOnSendListener = mOnSendListener;
	}

	/**
	 * �ļ����͵ļ���
	 */
	public interface OnSendListener {

		void onStart();

		void onProgress(long progress, long total);

		void onSuccess(Bitmap fileInfo);

		void onFailure(Throwable t, Bitmap fileInfo);
	}

}
