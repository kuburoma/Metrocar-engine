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
import java.util.ArrayList;
import java.util.UUID;

import metrocar.commands.MessageATE0;
import metrocar.commands.MessageATSP0;
import metrocar.commands.MessageATZ;
import metrocar.commands.ObdMessage;
import metrocar.commands.PidMessage;
import metrocar.labels.HandlerLabels;
import metrocar.labels.ModLabels;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Used for connection to OBD unit.
 * 
 * @author Roman Kubù
 * 
 */
public class UnitEngine {

	// Unikátní UUID zde musí být nastaveno jelikož se pøipojuji k bluetooth
	// zaøízení.
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static final String TAG = ModLabels.TAG_UNIT_ENGINE;
	private boolean doLog = false;

	private final Handler mHandler;

	private ThreadEngine thread;
	private ArrayList<PidMessage> tmpMessages;

	private BluetoothDevice device;


	/**
	 * Constructor
	 * 
	 * @param context Context of main application.
	 * @param handler Handler where all messages will be sent.
	 * @param device  BluetoothDevice of OBD unit	
	 * @param obdMessages PidMessages that will be sent on OBD unit
	 * @param doLog Logging states	
	 */
	public UnitEngine(Context context, Handler handler, BluetoothDevice device,
			ArrayList<PidMessage> obdMessages, boolean doLog) {
		mHandler = handler;
		tmpMessages = obdMessages;
		this.device = device;
		this.doLog = doLog;
	}

	/**
	 * Start connection to OBD unit. 
	 * 
	 */
	public void start() {
		thread = new ThreadEngine(device, tmpMessages);
		thread.start();
	}

	/**
	 * Add PidMessage that will be sent on OBD unit.
	 * 
	 * @param obd PidMessage that will be added
	 */
	public void addObdMessage(PidMessage obd) {
		thread.addObdMessage(obd);
	}

	/**
	 * Remove message that will be sent on OBD Unit.
	 * 
	 * @param obd Remove PidMessage
	 */
	public void removeObdMessage(PidMessage obd) {
		thread.removeObdMessage(obd);
	}

	public synchronized void stop() {
		if (thread != null) {
			thread.cancel();
			thread = null;
		}
	}

	private void connectionLost() {
		handlerMessage(HandlerLabels.OBD_STATE_CONNECTED_LOST,
				"Lost connection to device");
		thread = new ThreadEngine(device, tmpMessages);
		thread.start();
		if (doLog)
			Log.d(TAG, "Connection Lost");
	}

	private void connectionFailed() {
		thread = new ThreadEngine(device, tmpMessages);
		thread.start();
		if (doLog)
			Log.d(TAG, "Connection Failed");
	}

	private void handlerMessage(int state, String message) {
		mHandler.obtainMessage(HandlerLabels.OBD, state, -1, message)
				.sendToTarget();
	}

	private class ThreadEngine extends Thread {
		private InputStream inStream;
		private OutputStream outStream;
		private ArrayList<PidMessage> messages = new ArrayList<PidMessage>();
		private int position = 0;
		private int control = 0;
		private boolean threadRun = true;
		private BluetoothSocket socket = null;
		private BluetoothDevice device;

		protected ThreadEngine(BluetoothDevice device,
				ArrayList<PidMessage> obdMessages) {
			if (obdMessages != null) {
				messages = obdMessages;
				this.device = device;
			}
		}

		protected void addObdMessage(PidMessage obd) {
			synchronized (messages) {
				messages.add(obd);
			}
		}

		protected void removeObdMessage(PidMessage obd) {
			synchronized (messages) {
				messages.remove(obd);
			}
		}

		protected void cycleCompleted() {
			handlerMessage(HandlerLabels.OBD_CYCLE_COMPLETED, "Cycle completed");
		}

		/*
		 * public int setUp() { Log.d(tag, "ConnectedThread run()"); // Èekej na
		 * pøíchozí správy control = new MessageATZ().run(inStream, outStream);
		 * if (control == ObdMessage.STATE_FAILED) { return control; }
		 * Log.d(tag, "ATZMessage");
		 * 
		 * control = new MessageATSP0().run(inStream, outStream); if (control ==
		 * ObdMessage.STATE_FAILED) { return control; } Log.d(tag,
		 * "ATSP0Message");
		 * 
		 * control = new MessageATE0().run(inStream, outStream); if (control ==
		 * ObdMessage.STATE_FAILED) { return control; } Log.d(tag,
		 * "ATE0Message");
		 * 
		 * control = new MessageStart().run(inStream, outStream); if (control ==
		 * ObdMessage.STATE_FAILED) { return control; }
		 * 
		 * control = new MessageATDPN().run(inStream, outStream); if (control ==
		 * ObdMessage.STATE_FAILED) { return control; } Log.d(tag,
		 * "ATDPNMessage");
		 * 
		 * if (control >= 23 && control <= 25) { control = new
		 * MessageATSW20().run(inStream, outStream); Log.d(tag,
		 * "ATSW20Message"); } return ObdMessage.STATE_GOOD; }
		 */

		protected void closeConnection() {
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

		public synchronized void run() {
			if (doLog)
				Log.d(TAG, "start engine");
			handlerMessage(HandlerLabels.OBD_STATE_CONNECTING,
					"Connecting to unit");
			while (threadRun) {
				try {
					socket = device.createRfcommSocketToServiceRecord(MY_UUID);
					socket.connect();
					inStream = socket.getInputStream();
					outStream = socket.getOutputStream();
					break;
				} catch (IOException e) {
					closeConnection();
					handlerMessage(
							HandlerLabels.OBD_STATE_CONNECTING_FAILED_RECCONECT,
							"Failed to connect trying again");
					if (doLog)
						Log.d(TAG, "Failed to connect");
				}
			}
			if (!threadRun) {
				return;
			}
			handlerMessage(HandlerLabels.OBD_STATE_SET_UP, "Setting up unit");
			control = new MessageATZ().run(inStream, outStream);
			if (control == ObdMessage.STATE_FAILED) {
				closeConnection();
				connectionFailed();
				if (doLog)
					Log.d(TAG, "State failed recieved");
			}
			control = new MessageATE0().run(inStream, outStream);
			if (control == ObdMessage.STATE_FAILED) {
				if (doLog)
					Log.d(TAG, "State failed recieved");
				closeConnection();
				connectionFailed();	
			}
			control = new MessageATSP0().run(inStream, outStream);
			if (control == ObdMessage.STATE_FAILED) {
				if (doLog)
					Log.d(TAG, "State failed recieved");
				closeConnection();
				connectionFailed();	
			}
			// if (setUp() == ObdMessage.STATE_FAILED) {
			// //closeConnection();
			// connectionFailed();
			// }
			handlerMessage(HandlerLabels.OBD_STATE_CONNECTED,
					"Unit set up and running");
			if (doLog)
				Log.d(TAG, "Connected");
			while (threadRun) {
				if (messages.size() > 0) {
					if (messages.size() <= position) {
						cycleCompleted();
						position = 0;
					}
					control = 0;
					control = messages.get(position).run(inStream, outStream);
					
					if (doLog)
						Log.d(TAG, "return: "+control);

					if (control == ObdMessage.STATE_FAILED) {
						connectionLost();
						return;
					} else if (control == ObdMessage.STATE_NO_DATA
							|| control == ObdMessage.STATE_UNABLE_TO_CONNECT) {
						control = new MessageATZ().run(inStream, outStream);
						control = new MessageATE0().run(inStream, outStream);
						handlerMessage(HandlerLabels.OBD_STATE_CONNECTED_LOST,
								"Lost connection to device");
						if (doLog)
							Log.d(TAG, "NO DATA or UNABLE TO CONNECT received");
					} else {
						position++;
					}
				}
			}
			if (doLog)
				Log.d(TAG, "Closed");
			closeConnection();
			handlerMessage(HandlerLabels.OBD_STATE_CLOSED, "Closed");
		}

		protected void cancel() {
			threadRun = false;
		}
	}
}
