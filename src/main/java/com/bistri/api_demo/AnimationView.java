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

import com.bistri.api_demo.SettingsDialogTransmitter;

public class AnimationView extends View {
	PacketFilter filter;
	Message message;

	static int x, y, r = 255, g = 0, b = 0;
	final static int radius = 5;
	Paint paint, backGround; // using this, we can draw on canvas
	ArrayList<Integer> pointsX = new ArrayList<Integer>();
	ArrayList<Integer> pointsY = new ArrayList<Integer>();

	private ArrayList<String> messages = new ArrayList<String>();
	private SettingsDialogTransmitter mDialog;
	private Handler mHandler = new Handler();
	private XMPPConnection connection;
	public static String msg, posX, posY;
	public static String DEBUG_TAG = AnimationView.class.getSimpleName();
	public static String[] parts;
	public static boolean enableDraw;
	AnimationView aanimationView;

	public AnimationView(Context context) {
		super(context);

		aanimationView = this;

		new SettingsDialogTransmitter(aanimationView).execute();

		paint = new Paint();
		paint.setAntiAlias(true); // for smooth rendering

		enableDraw = false;
	}

	public  void setConnection(XMPPConnection connection) {
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

						Log.d(DEBUG_TAG, "msg is " + msg);
						if (msg.length() != 0 || msg != null) {
							parts = msg.split("&");

							posX = parts[0].replace("[", "").replace("]", "");
							posY = parts[1].replace("[", "").replace("]", "");

						} else {
							Log.d(DEBUG_TAG, "msg is null");
						}
						Log.d(DEBUG_TAG, "Received x:" + posX);
						Log.d(DEBUG_TAG, "Received y:" + posY);

						// Add the incoming message to the list view
						mHandler.post(new Runnable() {
							public void run() {
								if (posX.length() > 2 && posY.length() > 2) {
									enableDraw = true;
									invalidate();
								}
							}
						});
					}
				}
			}, filter);
		}
	}

	@Override
	public  void onDraw(Canvas canvas) {

		paint.setARGB(255, r, g, b);
		Integer[] n1 = null;
		Integer[] n2 = null;

		if (enableDraw == true) {

			if (posX.length() != 0 || posX != null) {
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

			if (posY.length() != 0 || posY != null) {
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
			if (posX != null && posY != null) {
				for (int i = 0; i < n1.length; i++) {
					canvas.drawCircle(n1[i], n2[i], radius, paint);
				}
			}

		}
	}

	/*public synchronized void CleanDraw() {
		posX = "";
		posY = "";
		
	}
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