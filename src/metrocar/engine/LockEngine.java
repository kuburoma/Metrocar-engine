/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package metrocar.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import metrocar.labels.HandlerLabels;
import metrocar.labels.ModLabels;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * @author Nell
 * 
 *         Tato tøída se stará o veškerou bluetooth komunikaci.
 * 
 */
public class LockEngine {

	// Following id must be set when connecting to a Bluetooth serial board
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static final String TAG = ModLabels.TAG_LOCK_ENGINE;
	private boolean doLog = false;

	private final Handler mHandler;

	private ConnectedThread thread;

	private String key;

	private BluetoothDevice device;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            UI Activity Context
	 * @param handler
	 *            Handler pro AI aktivitu
	 */

	public LockEngine(Context context, Handler handler, BluetoothDevice device,
			String key, boolean doLog) {
		mHandler = handler;
		this.device = device;
		this.key = key;
		this.doLog = doLog;
		if (doLog)
			Log.d(TAG, "Constructor");

	}

	/**
	 * Will start thread that will try to connect to lock device
	 */
	public void start() {
		thread = new ConnectedThread(device);
		thread.start();
		if (doLog)
			Log.d(TAG, "start connection");
	}

	/**
	 * Stops connection to lock device
	 */
	public void stop() {
		if (thread != null) {
			thread.cancel();
			thread = null;
		}
		if (doLog)
			Log.d(TAG, "Stop connection");
	}

	/**
	 * Opens lock device
	 */
	public void open() {
		if (thread != null) {
			thread.open();
		}
		if (doLog)
			Log.d(TAG, "Open lock device");
	}

	/**
	 * Close lock device
	 */
	public void close() {
		if (thread != null) {
			thread.close();
		}
		if (doLog)
			Log.d(TAG, "Close lock device");
	}

	/**
	 * Sets new pass pass on device when old is correct
	 * 
	 * @param oldPass
	 * @param newPass
	 */
	public void reset(String oldPass, String newPass) {
		if (thread != null) {
			thread.reset(oldPass, newPass);
		}
		if (doLog)
			Log.d(TAG, "Set new password");
	}

	public void test(String pass) {
		if (thread != null) {
			thread.test(pass);
		}
		if (doLog)
			Log.d(TAG, "Test password");
	}

	private void handlerMessage(int state, String message) {
		mHandler.obtainMessage(HandlerLabels.LOCK, state, -1, message)
				.sendToTarget();
	}

	private class ConnectedThread extends Thread {
		private InputStream inStream;
		private OutputStream outStream;
		private BluetoothSocket socket = null;
		private BluetoothDevice device;
		private String oldPass;
		private String newPass;
		private String pass;
		private boolean open = false;
		private boolean close = false;
		private boolean test = false;
		private boolean reset = false;
		boolean threadRun = true;

		public ConnectedThread(BluetoothDevice device) {
			this.device = device;
		}

		public void open() {
			open = true;
		}

		public void close() {
			close = true;
		}

		public void reset(String oldPass, String newPass) {
			Log.d(pass, "reset");
			reset = true;
			this.oldPass = oldPass;
			this.newPass = newPass;
		}

		public void test(String pass) {
			Log.d(pass, "test: " + pass);
			test = true;
			this.pass = pass;
		}

		public void closeConnection() {
			if (doLog)
				Log.d(TAG, "close connection streams and socket");
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
				if (socket != null) {
					socket.close();
				}

			} catch (IOException e2) {
				if (doLog)
				Log.d(TAG, "Cannoct close RFCOMM socket");
			}
		}

		public void run() {
			try {
				socket = device.createRfcommSocketToServiceRecord(MY_UUID);
				socket.connect();
				inStream = socket.getInputStream();
				outStream = socket.getOutputStream();
			} catch (IOException e) {
				handlerMessage(HandlerLabels.LOCK_CANNOCT_CONNECT,
						"Cannot connect to unit");
				if (doLog)
					Log.d(TAG, "Cannot connect to unit.");
				
				return;
			}
			handlerMessage(HandlerLabels.LOCK_CONNECTED, "Connected to unit");
			if (doLog)
				Log.d(TAG, "connected to unit");
			String responce = "";
			while (threadRun) {
				try {
					if (open) {
						open = false;
						send("OPEN " + key);
						responce = read();
						if (responce.equals("OK")) {
							handlerMessage(HandlerLabels.LOCK_OPENED,
									"Lock opened");
							if (doLog)
								Log.d(TAG, "Lock opens");
						} else if (responce.equals("WRONG KEY")) {
							handlerMessage(HandlerLabels.LOCK_WRONG_KEY,
									"Wrong key");
							if (doLog)
								Log.d(TAG, "Lock wrong key");
						} else {
							handlerMessage(HandlerLabels.LOCK_UNKNOWN_RESPONCE,
									"Unknown responce");
							if (doLog)
							Log.d(TAG, "Lock unknown responce");
						}
					}
					if (close) {
						close = false;
						send("CLOSE");
						responce = read();
						if (responce.equals("OK")) {
							handlerMessage(HandlerLabels.LOCK_CLOSED,
									"Lock closed");
						} else {
							handlerMessage(HandlerLabels.LOCK_UNKNOWN_RESPONCE,
									"Unknown responce");
							if (doLog)
							Log.d(TAG, "Lock unknown responce");
						}
					}
					if (test) {
						test = false;
						send("TEST " + pass);
						responce = read();
						if (responce.equals("OK")) {
							handlerMessage(HandlerLabels.LOCK_TEST_OK,
									"Lock test OK");
						} else if (responce.equals("WRONG KEY")) {
							handlerMessage(HandlerLabels.LOCK_TEST_FAILED,
									"Lock test Failed");
						} else {
							handlerMessage(HandlerLabels.LOCK_UNKNOWN_RESPONCE,
									"Unknown responce");
							if (doLog)
							Log.d(TAG, "Lock unknown responce");
						}
					}
					if (reset) {
						reset = false;
						send("RESET " + oldPass + " " + newPass);
						responce = read();
						if (responce.equals("OK")) {
							handlerMessage(HandlerLabels.LOCK_RESET_OK,
									"Lock password ok");
						} else if (responce.equals("WRONG KEY")) {
							handlerMessage(HandlerLabels.LOCK_RESET_FAILED,
									"Lock password reset failed");
						} else {
							handlerMessage(HandlerLabels.LOCK_UNKNOWN_RESPONCE,
									"Unknown responce");
							if (doLog)
							Log.d(TAG, "Lock unknown responce");
						}
					}
				} catch (IOException e) {
					handlerMessage(HandlerLabels.LOCK_CONNECTION_LOST,
							"Connection lost");
					if (doLog)
					Log.d(TAG, "Connection lost");
					closeConnection();
					e.printStackTrace();
				}
			}
			closeConnection();
		}

		protected void send(String message) throws IOException {
			if (doLog)
			Log.d(TAG, "send("+message+")");
			message += '\n';
			byte[] send = message.getBytes();
			outStream.write(send);
		}

		protected String read() throws IOException {
			int data = inStream.read();
			String output = "";
			while (data != '\n') {
				if (data != 13) {
					output += (char) data;
				}
				data = inStream.read();
			}
			Log.d(TAG, "recieved("+output+")");
			return output;
		}

		public void cancel() {
			threadRun = false;
		}
	}
}
