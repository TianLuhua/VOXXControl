package com.sat.mirrorcontorl.core;

import java.io.InputStream;
import java.net.Socket;

import android.graphics.Bitmap;

public class FileReceiver extends BaseTransfer implements Runnable {

	private final String TAG = "FileReceiver";

	public FileReceiver(Socket mSocket) {
		this.mSocket = mSocket;
	}

	/**
	 * Socket的输入输出流
	 */
	private Socket mSocket;
	private InputStream mInputStream;

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

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	/**
	 * 文件接收的监听
	 */
	OnReceiveListener mOnReceiveListener;

	public void setOnReceiveListener(OnReceiveListener mOnReceiveListener) {
		this.mOnReceiveListener = mOnReceiveListener;
	}

	/**
	 * 文件接收的监听
	 */
	public interface OnReceiveListener {
		void onStart();

		void onGetFileInfo(Bitmap fileInfo);

		void onGetScreenshot(Bitmap bitmap);

		void onProgress(long progress, long total);

		void onSuccess(Bitmap fileInfo);

		void onFailure(Throwable t, Bitmap fileInfo);
	}

}
