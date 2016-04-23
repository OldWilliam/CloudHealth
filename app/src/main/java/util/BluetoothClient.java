/*
2015-9-23 ����12:38:43
 */
package util;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import application.MyApplication;


import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import activity.HealthActivity;

/**
 * 
 **/
public class BluetoothClient {

	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private FileManager mFileMan;
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;

	public static final int STATE_NONE = 0;       
	//public static final int STATE_LISTEN = 1;     
	public static final int STATE_CONNECTING = 1; 
	public static final int STATE_CONNECTED = 2;  

	public BluetoothClient(Application app, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
		mFileMan = ((MyApplication)app).getFileManager();

	}
	private synchronized void setState(int state) {
		mState = state;

		mHandler.obtainMessage(HealthActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	public synchronized int getState() {
		return mState;
	}
	
	public synchronized void start() {
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		setState(STATE_NONE);
	}

	public synchronized void connect(BluetoothDevice device) {

		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		}

		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		Message msg = mHandler.obtainMessage(HealthActivity.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(HealthActivity.DEVICE_NAME, device.getName()+" has been conneced");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	public synchronized void stop() {
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
		setState(STATE_NONE);
	}

	public void write(byte[] out) {
		ConnectedThread r;
		synchronized (this) {
			if (mState != STATE_CONNECTED) return;
			r = mConnectedThread;
		}
		r.write(out);
	}

	private void connectionFailed() {
		setState(STATE_NONE);
		Message msg = mHandler.obtainMessage(HealthActivity.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(HealthActivity.TOAST, "无法连接到设备");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private void connectionLost() {
		setState(STATE_NONE);
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(HealthActivity.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(HealthActivity.TOAST, "连接丢失");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				//
			}
			mmSocket = tmp;
		}

		@Override
		public void run() {
			setName("ConnectThread");
			mAdapter.cancelDiscovery();
			try {
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				try {
					mmSocket.close();
				} catch (IOException e2) {
				}
				BluetoothClient.this.start();
				return;
			}
			synchronized (BluetoothClient.this) {
				mConnectThread = null;
			}
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ConnectedThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		@Override
		public void run() {
			byte b;
			byte[] validDat = null;
			int dataLen = 0;
			int dataPos = 0;
			byte dataSum = 0;
			int dataTyp = 0;

			//这个线程什么时候停啊！
			while (true) {
				try {
					//帧头1
					if (dataPos == 0) {
						b = (byte) mmInStream.read();	
						if (b == (byte)0xDC) {
							dataPos = 1;
							dataSum += b;
						}
						else {
							dataPos = 0;
							dataSum = 0;
							Log.d("帧头1", "失败");
						}
					}
					//帧头2
					else if(dataPos == 1){
						b = (byte) mmInStream.read();
						if (b == (byte)0xBA) {
							dataPos = 2;
							dataSum += b;
						}  
						else {
							dataPos = 0;
							dataSum = 0;
							Log.d("帧头2", "失败");
						}
					}
					//数据类型位
					else if (dataPos == 2) {
						b = (byte) mmInStream.read();
						switch (b) {
						case (byte) 0xE1:
							dataTyp = 1;
						dataSum += (byte)0xE1;
						break;
						case (byte) 0xE2:
							dataTyp = 2;
						dataSum += (byte)0xE2;
						break;
						case (byte) 0xE3:
							dataTyp = 3;
						dataSum += (byte)0xE3;
						break;
						case (byte) 0xE4:
							dataTyp = 4;
						dataSum += (byte)0xE4;
						break;
						case (byte) 0xE5:
							dataTyp = 5;
						dataSum += (byte)0xE5;
						break;
						case (byte) 0xE6:
							dataTyp = 6;
						dataSum += (byte)0xE6;
						break;
						case (byte) 0xE7:
							dataTyp = 7;
						dataSum += (byte)0xE7;
						break;
						default:
							dataPos = 0;
							dataSum = 0;
							Log.d("获取数据类型", "未知类型");
							break;
						}
						if (dataPos != 0) {
							dataPos = 3;
							dataSum += b;
							Log.d("获取数据类型", dataTyp+"");
						}
					}
					//长度位 双字节
					else if (dataPos == 3) {
						int highLen = mmInStream.read();
						int lowLen = mmInStream.read();
						dataLen = highLen*256 + lowLen;
						dataPos = 4;
						dataSum += (byte)highLen;
						dataSum += (byte)lowLen;
					}
					//真实数据.
					else if(dataPos == 4){
						validDat = new byte[dataLen];
						int revLen = mmInStream.read(validDat);
						//接受到的数据长度与原长不符  丢弃
						if (revLen != dataLen) {
							dataPos = 0;
							dataSum = 0;
							dataLen = 0;
							Log.d("检验数据长度", "不符");
						}
						else {
							dataPos = 5;
							for (int i = 0; i < validDat.length; i++) {
								dataSum += validDat[i];
								Log.d("真实数据", validDat[i]+"");
							}
						}
					}
					else if(dataPos == 5){
						int preSum = (byte)mmInStream.read();
						WriteFileThread writeTofile = new WriteFileThread(dataTyp, validDat);
						writeTofile.start();
						if (dataSum == preSum) {
							Log.d("校验和", "成功");
						}
						else {
							Log.d("校验和", "失败");
							Log.d("预制校验和", preSum+"");
							Log.d("算术校验和", dataSum+"");
							dataPos = 0;
							dataLen = 0;
							dataSum = 0;
						}
					}
				} catch (IOException e) {
					connectionLost();
					break;
				}
			}
		}

		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
				mHandler.obtainMessage(HealthActivity.MESSAGE_WRITE, -1, -1, buffer)
				.sendToTarget();
			} catch (IOException e) {

			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}


	private class WriteFileThread extends Thread{
		private int dataType;
		private byte[] buffer;
		public WriteFileThread(int dataType, byte[] buffer) {
			this.dataType = dataType;
			this.buffer = buffer;
		}
		@Override
		public void run() {
			super.run();
			mFileMan.saveByteFile(dataType, buffer);
		}
	}
}

