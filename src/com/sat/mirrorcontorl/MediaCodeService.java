package com.sat.mirrorcontorl;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.InputDevice;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.widget.Toast;

import com.sat.mirrorcontorl.base.Config;
import com.sat.mirrorcontorl.screenshot.IScreenshot;
import com.sat.mirrorcontorl.screenshot.IScreenshot.ScreenshotCallBack;
import com.sat.mirrorcontorl.screenshot.ScreenshotImp;
import com.sat.mirrorcontorl.utils.ByteUtils;
import com.sat.mirrorcontorl.utils.DeviceUtils;
import com.sat.mirrorcontorl.utils.EventInputUtils;
import com.sat.mirrorcontorl.utils.IpUtils;
import com.sat.mirrorcontorl.utils.LogUtils;
import com.sat.mirrorcontorl.utils.ThreadPoolManager;

public class MediaCodeService extends Service implements ScreenshotCallBack {

	protected static final String TAG = "MediaCodeService";

	private MulticastSocket multicastSocket;
	private DataOutputStream dataOutputStream;
	private DataInputStream touchDataInputStream;
	private Socket socketD;
	private ServerSocket dataSocketsService;

	private ServerSocket touchSocketsService;
	private Socket socketT;
	private boolean isRun = true;

	private NetChangeReceiver netChangeReceiver = null;
	private boolean isNetConnet = false;

	private boolean receiver = false;
	private String ipAddress;

	private InetAddress broadcastAddress;

	private boolean isStart = false;
	private DatagramSocket bSocket;

	private float[] dims;

	private IScreenshot mScreenshot;

	private Handler mHandler = new MediaCodeServiceHandler(this);

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		mDisplay.getRealMetrics(mDisplayMetrics);
		dims = new float[] { mDisplayMetrics.widthPixels,
				mDisplayMetrics.heightPixels };
		mScreenshot = new ScreenshotImp(this);

		LogUtils.i(TAG, "hdb-----dims0:" + (int) dims[0] + "  dims1:"
				+ (int) dims[1]);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		registNetChangeReceiver();

		return START_STICKY;
	}

	private void initDataAndTouchServer() {
		initTouchServer();
		initDataServer();

	}

	private void initDataServer() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				// TODO Auto-generated method stub

				try {
					dataSocketsService = new ServerSocket(
							Config.PortGlob.DATAPORT);
					socketD = dataSocketsService.accept();
					dataOutputStream = new DataOutputStream(
							socketD.getOutputStream());
					LogUtils.i("tlh", "dataSocketsService.accept()");
					if (dataOutputStream != null) {
						LogUtils.i("tlh", "dataOutputStream != null");
						mHandler.sendEmptyMessageDelayed(
								Config.HandlerGlod.DATA_CONNECT_SUCCESS, 0);
					}
				} catch (Exception ex) {
					ex.toString();
					if (mHandler.hasMessages(Config.HandlerGlod.CONNECT_FAIL)) {
						LogUtils.i("tlh", "CONNECT_FAIL----11111");
						return;
					}
					mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
					LogUtils.e("tlh", "dataSocketsService.accept()--error:"
							+ ex.toString());
				}

			}
		}).start();

	}

	private void initTouchServer() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				// TODO Auto-generated method stub
				try {

					touchSocketsService = new ServerSocket(
							Config.PortGlob.TOUCHPORT);
					socketT = touchSocketsService.accept();

					touchDataInputStream = new DataInputStream(
							socketT.getInputStream());

					LogUtils.i("tlh", "touchSocketsService.accept()");

					if (touchDataInputStream != null) {
						mHandler.sendEmptyMessageDelayed(
								Config.HandlerGlod.TOUCH_CONNECT_SUCCESS, 0);
					}

				} catch (Exception ex) {
					LogUtils.e("tlh", "touchSocketsService.accept()--error:"
							+ ex.toString());
					if (mHandler.hasMessages(Config.HandlerGlod.CONNECT_FAIL)) {
						return;
					}
					mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
				}

			}
		}).start();

		// ThreadPoolManager.newInstance().addExecuteTask(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// try {
		//
		// touchSocketsService = new ServerSocket(
		// Config.PortGlob.TOUCHPORT);
		// socketT = touchSocketsService.accept();
		//
		// LogUtils.i("tlh", "touchSocketsService.accept()");
		//
		// touchDataInputStream = new DataInputStream(socketT
		// .getInputStream());
		//
		// if (touchDataInputStream != null) {
		// mHandler.sendEmptyMessageDelayed(
		// Config.HandlerGlod.TOUCH_CONNECT_SUCCESS, 0);
		// }
		//
		// } catch (Exception ex) {
		// mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
		// LogUtils.e("tlh", "touchSocketsService.accept()--error:"
		// + ex.toString());
		// }
		//
		// }
		// });

	}

	// protected byte[] piexs;
	private void writeFileData() {
		LogUtils.i(TAG, "hdb-----writeFileData--start---:");
		new Thread(new Runnable() {

			@Override
			public void run() {

				isRun = true;
				while (isRun) {

					try {
						Long start = System.currentTimeMillis();
						Bitmap bm = SurfaceControl.screenshot((int) dims[0],
								(int) dims[1]);
						bm.setHasAlpha(false);
						LogUtils.e("time", ""
								+ (System.currentTimeMillis() - start));
						Long start111 = System.currentTimeMillis();
						if (bm != null) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
							byte[] datas = baos.toByteArray();
							onImageData(datas);
							baos = null;
						}
						LogUtils.e("time111", ""
								+ (System.currentTimeMillis() - start));

					} catch (Exception e) {
						e.printStackTrace();
						LogUtils.i(TAG, "hdb-----writeFileData---error:");
					}

				}

			}
		}).start();

		// ThreadPoolManager.newInstance().addExecuteTask(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// isRun = true;
		// while (isRun) {
		//
		// try {
		// Long start = System.currentTimeMillis();
		// Bitmap bm = SurfaceControl.screenshot((int) dims[0],
		// (int) dims[1]);
		// bm.setHasAlpha(false);
		// LogUtils.e("time", ""
		// + (System.currentTimeMillis() - start));
		// Long start111 = System.currentTimeMillis();
		// if (bm != null) {
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
		// byte[] datas = baos.toByteArray();
		// onImageData(datas);
		// baos = null;
		// }
		// LogUtils.e("time111", ""
		// + (System.currentTimeMillis() - start));
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// LogUtils.i(TAG, "hdb-----writeFileData---error:");
		// }
		//
		// }
		//
		// }
		// });
	}

	protected void receiverTouchData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] buffer = new byte[1];
				isRun = true;
				LogUtils.i(TAG, "hdb-------isRun:" + isRun + "  socket:"
						+ socketT.getInetAddress());
				try {
					while (isRun) {
						int readLine = touchDataInputStream.read(buffer);

						// LogUtils.i(TAG, "hdb-------readLine:" + readLine
						// + "   buffer:" + buffer[0]);

						if (readLine > 0) {

							byte[] data = new byte[buffer[0]];
							// LogUtils.i(TAG, "hdb--------data:" + new
							// String(data));
							touchDataInputStream.readFully(data);
							String point = new String(data, 0, data.length);

							if (point != null) {
								// LogUtils.i(TAG, "hdb--------point:" + point);
								JSONObject jObject = new JSONObject(point);
								int action = jObject
										.getInt(Config.MotionEventKey.JACTION);
								int x = jObject
										.getInt(Config.MotionEventKey.JX);
								int y = jObject
										.getInt(Config.MotionEventKey.JY);

								EventInputUtils.injectMotionEvent(
										InputDevice.SOURCE_TOUCHSCREEN, action,
										SystemClock.uptimeMillis(), x, y, 1.0f);
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LogUtils.i(TAG, "hdb-----receiverTouchData---error:");

				}

			}

		}).start();

	}

	private void startReceiverUdpBrodcast() {
		try {
			broadcastAddress = IpUtils.getBroadcastAddress();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		ThreadPoolManager.newInstance().addExecuteTask(new Runnable() {

			@Override
			public void run() {

				try {
					if (multicastSocket == null) {
						multicastSocket = new MulticastSocket(
								Config.PortGlob.MULTIPORT);
					}
					multicastSocket.joinGroup(broadcastAddress);

				} catch (Exception e) {
					e.getStackTrace();
				}

			}
		});

		ipAddress = IpUtils.getIpAddress(getApplicationContext());
		LogUtils.i(TAG, "hdb----ipAddress:" + ipAddress);
		if (ipAddress != null && broadcastAddress != null) {
			receiver = true;
			receiverBroadPackage();
		}
	}

	private void registNetChangeReceiver() {
		if (netChangeReceiver == null) {
			netChangeReceiver = new NetChangeReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			registerReceiver(netChangeReceiver, filter);
		}

	}

	private void receiverBroadPackage() {
		IpUtils.openWifiBrocast(getApplicationContext()); // for some phone can

		ThreadPoolManager.newInstance().addExecuteTask(new Runnable() {

			@Override
			public void run() {

				try {
					while (receiver) {
						if (multicastSocket != null) {
							LogUtils.i(TAG, "hdb----ipAddress:" + ipAddress);
							byte[] data = new byte[30];
							DatagramPacket pack = new DatagramPacket(data,
									data.length);
							multicastSocket.receive(pack);
							String receiverData = new String(pack.getData(),
									pack.getOffset(), pack.getLength());
							LogUtils.i(TAG, "hdb-------phoneIP:" + receiverData
									+ "  ipAddress:" + ipAddress);
							if (receiverData != null
									&& receiverData
											.startsWith(Config.ActionKey.CLIENT_IP_KEY)) {
								sendBack(receiverData);

							}
							if (isStart == false) {
								isStart = true;
								initDataAndTouchServer();
							}
							// if (receiverData != null
							// && receiverData
							// .startsWith(Config.ActionKey.SERVICE_START_KEY))
							// {
							// LogUtils.i(TAG, "hdb-------receiverData:"
							// + receiverData);
							//
							// if (isStart == false) {
							// isStart = true;
							// initDataAndTouchServer();
							// }
							// }

						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

	protected void sendBack(String iP) throws SocketException {
		if (bSocket == null) {
			bSocket = new DatagramSocket();
		}
		byte[] data = ("serverip:" + ipAddress + ":" + DeviceUtils
				.getSerialNumber()).getBytes();
		try {
			String phoneIp = iP.substring(8);
			LogUtils.i(
					TAG,
					"hdb-----phoneIp:" + phoneIp + ":"
							+ DeviceUtils.getSerialNumber());
			InetAddress host = InetAddress.getByName(phoneIp);
			DatagramPacket pack = new DatagramPacket(data, data.length, host,
					Config.PortGlob.BACKPORT);
			bSocket.send(pack);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void remoteClientHasConnection() {
		// Do Some things
		Toast.makeText(getApplicationContext(),
				R.string.remote_client_connection, Toast.LENGTH_SHORT).show();

	}

	private void closeConnect() {
		LogUtils.i("tlh", "hdb----closeConnect");
		Toast.makeText(getApplicationContext(), R.string.remote_client_closed,
				Toast.LENGTH_SHORT).show();
		isRun = false;
		isStart = false;

		try {
			if (touchDataInputStream != null) {
				touchDataInputStream.close();
				LogUtils.i("tlh", "----touchDataInputStream.close()");
			}

			if (dataOutputStream != null) {
				dataOutputStream.close();
				LogUtils.i("tlh", "----dataOutputStream.close()");
			}

			if (socketD != null && socketD.isConnected()) {
				socketD.close();
				LogUtils.i("tlh", "----socketD.close()");
			}

			if (socketT != null && socketT.isConnected()) {
				socketT.close();
				LogUtils.i("tlh", "----socketT.close()");
			}

			if (touchSocketsService != null) {
				touchSocketsService.close();
				LogUtils.i("tlh", "----touchSocketsService.close()");
			}

			if (dataSocketsService != null) {
				dataSocketsService.close();
				LogUtils.i("tlh", "----dataSocketsService.close()");
			}
		} catch (IOException e) {
			e.printStackTrace();
			LogUtils.i("tlh", "hdb----closeConnect--error");
		}

		dataOutputStream = null;
		dataOutputStream = null;
		socketD = null;
		socketT = null;
		dataSocketsService = null;
		touchSocketsService = null;

		mHandler.removeMessages(Config.HandlerGlod.RESTART_SERVER);
		mHandler.sendEmptyMessageDelayed(Config.HandlerGlod.RESTART_SERVER, 500);

	}

	private class NetChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiInfo.isConnected()) {
				LogUtils.i(TAG, "hdb-----isConnected");
				isNetConnet = true;
				startReceiverUdpBrodcast();
				// MainActivity instance = MainActivity.getInstance();

			} else {
				LogUtils.i(TAG, "hdb---not--Connect");
				isNetConnet = false;
				stopReceiverUdpBrodcast();
			}
		}

	}

	public void stopReceiverUdpBrodcast() {
		receiver = false;
		if (multicastSocket != null) {
			try {
				multicastSocket.leaveGroup(broadcastAddress);
				multicastSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public synchronized void onImageData(byte[] buf) {
		if (null != dataOutputStream) {
			try {
				byte[] bytes = new byte[buf.length + 3];
				byte[] head = ByteUtils.intToBuffer(buf.length);
				System.arraycopy(head, 0, bytes, 0, head.length);
				System.arraycopy(buf, 0, bytes, head.length, buf.length);
				dataOutputStream.write(bytes);
				dataOutputStream.flush();
				bytes = null;
				head = null;
			} catch (IOException e) {
				e.printStackTrace();
				if (mHandler.hasMessages(Config.HandlerGlod.CONNECT_FAIL)) {
					return;
				}
				mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
			}
		} else {
			if (mHandler.hasMessages(Config.HandlerGlod.CONNECT_FAIL)) {
				return;
			}
			mHandler.sendEmptyMessage(Config.HandlerGlod.CONNECT_FAIL);
		}

	}

	public static class MediaCodeServiceHandler extends Handler {
		WeakReference<MediaCodeService> weakReference;

		public MediaCodeServiceHandler(MediaCodeService mMediaCodeService) {
			weakReference = new WeakReference<MediaCodeService>(
					mMediaCodeService);

		}

		@Override
		public void handleMessage(Message msg) {
			MediaCodeService mediaCodeService = weakReference.get();
			if (mediaCodeService == null)
				return;

			switch (msg.what) {
			case Config.HandlerGlod.DATA_CONNECT_SUCCESS:
				mediaCodeService.receiver = false;
				mediaCodeService.writeFileData();
				LogUtils.i("tlh", "handleMessage ----> writeFileData()");
				mediaCodeService.remoteClientHasConnection();
				break;

			case Config.HandlerGlod.TOUCH_CONNECT_SUCCESS:
				mediaCodeService.receiver = false;
				mediaCodeService.receiverTouchData();

				break;
			case Config.HandlerGlod.CONNECT_FAIL:
				mediaCodeService.closeConnect();
				break;

			case Config.HandlerGlod.RESTART_SERVER:
				mediaCodeService.receiver = true;
				mediaCodeService.receiverBroadPackage();
				break;

			default:
				break;
			}

		}

	}

}
