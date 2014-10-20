package com.bistri.api_demo;

import java.util.ArrayList;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.graphics.PorterDuff.Mode;

import com.bistri.api_demo.SettingsDialogTransmitter;

public class AnimationView extends View {
	PacketFilter filter;
	Message message;

	static int x, y, r = 255, g = 0, b = 0;
	final static int radius = 5;
	Paint paint, backGround, shapePaint; // using this, we can draw on canvas
	ArrayList<Integer> pointsX = new ArrayList<Integer>();
	ArrayList<Integer> pointsY = new ArrayList<Integer>();

	private ArrayList<String> messages = new ArrayList<String>();
	private SettingsDialogTransmitter mDialog;
	private Handler mHandler = new Handler();
	private XMPPConnection connection;
	public static String msg, posX, posY, shapes, circles, rects, partXY, text;
	public static String DEBUG_TAG = AnimationView.class.getSimpleName();
	public static String[] parts;
	public static String[] partsxyAndShapes;
	public static String[] partsShapes;
	public static boolean enableDraw;
	public boolean clear;
	AnimationView aanimationView;

	private static String roomName = "room1";

	public AnimationView(Context context) {
		super(context);

		aanimationView = this;
		clear = false;
		new SettingsDialogTransmitter(aanimationView).execute();

		paint = new Paint();
		paint.setAntiAlias(true); // for smooth rendering

		shapePaint = new Paint();
		shapePaint.setAntiAlias(true);
		shapePaint.setStyle(Paint.Style.STROKE);
		shapePaint.setARGB(255, r, g, b);
		shapePaint.setStrokeWidth(5);

		enableDraw = false;
	}

	public synchronized void setConnection(XMPPConnection connection) {
		this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet packet) {
					message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						messages.add(fromName + ":");
						messages.add(message.getBody());

						msg = message.getBody();
						Log.d(DEBUG_TAG, "msg is =" + msg);
						if (msg.trim().startsWith(roomName)) {

							if (msg.length() > 0 || msg != null) {
								try {
									partsxyAndShapes = msg.split("#");

									partXY = partsxyAndShapes[0];
									shapes = partsxyAndShapes[1];
									text = partsxyAndShapes[2];

									parts = partXY.split("&");

									posX = parts[0].replace("[", "")
											.replace("]", "")
											.replace(roomName, "");
									posY = parts[1].replace("[", "").replace(
											"]", "");

									partsShapes = shapes.split(":");
									circles = partsShapes[0];
									rects = partsShapes[1];
								} catch (Exception e) {
									Log.d(DEBUG_TAG,
											"exception " + e.getMessage());
								}
							} else {
								Log.d(DEBUG_TAG, "msg is null");
							}
						} else {
							Log.d(DEBUG_TAG, "room name not matched");
						}
						Log.d(DEBUG_TAG, "Received x:" + posX);
						Log.d(DEBUG_TAG, "Received y:" + posY);
						Log.d(DEBUG_TAG, "Received circle:" + circles);
						Log.d(DEBUG_TAG, "Received rect:" + rects);

						if (posX != null && posY != null) {
							// Add the incoming message to the list view
							mHandler.post(new Runnable() {
								public void run() {
									// if (posX.length() > 2 && posY.length() >
									// 2) {
									enableDraw = true;
									invalidate();
									// }
								}
							});
						}
					}
				}
			}, filter);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {

		paint.setARGB(255, r, g, b);
		Integer[] n1 = null;
		Integer[] n2 = null;

		if (enableDraw) {
			Log.d(DEBUG_TAG, "posX.length() " + posX.length());
			if (posX.length() > 0) {
				String[] s1 = posX.split((","));

				n1 = new Integer[s1.length];
				if (s1.length != 0) {
					for (int o1 = 0; o1 < s1.length; o1++) {
						if (s1[o1] != null)
							n1[o1] = Integer.parseInt(s1[o1].trim());

						Log.d(DEBUG_TAG, "x:" + n1[o1]);
					}
				} else {
					Log.d(DEBUG_TAG, "S1 is null");
				}
			}

			Log.d(DEBUG_TAG, "posY.length() " + posY.length());
			if (posY.length() > 0) {
				String[] s2 = posY.split((","));
				n2 = new Integer[s2.length];
				if (s2.length != 0) {
					for (int o2 = 0; o2 < s2.length; o2++) {
						if (s2[o2] != null)
							n2[o2] = Integer.parseInt(s2[o2].trim());

						Log.d(DEBUG_TAG, "y:" + n2[o2]);
					}
				} else {
					Log.d(DEBUG_TAG, "S2 is null");
				}

			}

			// draw
			if (posX.length() > 0 && posY.length() > 0) {
				for (int i = 0; i < n1.length; i++) {
					canvas.drawCircle(n1[i], n2[i], radius, paint);
				}
			}

			// draw circle
			if (circles != null || circles.length() > 0) {
				String circlrParam[] = circles.split("~");
				for (int i = 0; i < circlrParam.length; i++) {
					String seperatedCircleParams[] = circlrParam[i].split(",");
					canvas.drawCircle(
							Float.parseFloat(seperatedCircleParams[0]),
							Float.parseFloat(seperatedCircleParams[1]),
							Float.parseFloat(seperatedCircleParams[2]),
							shapePaint);
				}
			}

			// draw rect
			if (rects != null || rects.length() > 0) {
				String rectParam[] = rects.split("~");
				for (int i = 0; i < rectParam.length; i++) {
					String seperatedRectParams[] = rectParam[i].split(",");
					Log.d(DEBUG_TAG,
							"received rect s 0="
									+ Float.parseFloat(seperatedRectParams[0])
									+ " 1="
									+ Float.parseFloat(seperatedRectParams[1])
									+ " 2="
									+ Float.parseFloat(seperatedRectParams[2])
									+ " 3="
									+ Float.parseFloat(seperatedRectParams[3]));

					/*
					 * canvas.drawRect(Float.parseFloat(seperatedRectParams[0]),
					 * Float.parseFloat(seperatedRectParams[1]),
					 * Float.parseFloat(seperatedRectParams[2]),
					 * Float.parseFloat(seperatedRectParams[3]), shapePaint);
					 */
					canvas.drawRect(
							Float.parseFloat(seperatedRectParams[0]),
							Float.parseFloat(seperatedRectParams[1]),
							Float.parseFloat(seperatedRectParams[0])
									+ Float.parseFloat(seperatedRectParams[2]),
							Float.parseFloat(seperatedRectParams[1])
									+ Float.parseFloat(seperatedRectParams[3]),
							shapePaint);
				}
			}

			// for text card
			if (!text.equalsIgnoreCase("notext")) {
				Toast.makeText(this.getContext(),
						text,
						Toast.LENGTH_LONG).show();
			}
		} else if (!enableDraw && clear) {
			canvas.drawColor(0, Mode.CLEAR);
			clear = false;
		}
	}

	public static String getRoomName() {
		return roomName;
	}

	public static void setRoomName(String roomName) {
		AnimationView.roomName = roomName;
	}

	public void clearDraw() {
		clear = true;
		enableDraw = false;
		invalidate();
	}

	/*
	 * public synchronized void CleanDraw() { synchronized (posX){ posX = "0"; }
	 * synchronized (posY){ posY = "0"; } }
	 */

	// public void sendMessage() {
	// String to = "receiverid123@gmail.com";
	// String text = "auto1";
	//
	// Log.i("XMPPClientPar", "Sending text [" + text + "] to [" + to + "]");
	// Message msg = new Message(to, Message.Type.chat);
	// msg.setBody(text);
	// connection.sendPacket(msg);
	// messages.add(connection.getUser() + ":");
	// messages.add(text);
	// }

}