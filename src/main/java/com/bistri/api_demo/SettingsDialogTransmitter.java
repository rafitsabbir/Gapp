
package com.bistri.api_demo;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.os.AsyncTask;
import android.util.Log;

import com.bistri.api_demo.AnimationView;

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class SettingsDialogTransmitter extends AsyncTask<String, Void, String> {
	private static AnimationView animationView;
	static String username;
	static String password;

	static ConnectionConfiguration connConfig;
	static XMPPConnection connection;

	@SuppressWarnings("static-access")
	public SettingsDialogTransmitter(AnimationView animationView) {
		this.animationView = animationView;
	}

	

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		String host = "talk.google.com";
		String port = "5222";
		String service = "gmail.com";
		username = "receiverid123@gmail.com";
		password = "receiver123";

		// Create a connection
		connConfig = new org.jivesoftware.smack.ConnectionConfiguration(host,
				Integer.parseInt(port), service);
		// new ConnectionConfiguration(host, Integer.parseInt(port), service);
		connection = new XMPPConnection(connConfig);
		System.out.println("sssstart "+connection.getUser());
		try {
			connection.connect();
			connection.login(username, password);
			Presence presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			animationView.setConnection(connection);
			Log.i("XMPPClientPar", "[SettingsDialogPar] Connected to "
					+ connection.getHost());
			System.out.println("sss");
		} catch (XMPPException ex) {
			Log.e("XMPPClientPar", "[SettingsDialogPar] Failed to connect to "
					+ connection.getHost());
			Log.e("XMPPClientPar", ex.toString());
			animationView.setConnection(null);
			System.out.println("ssse");
		}
//		try {
//			System.out.print(connection+" u and p "+username +" "+password);
//			
//			Log.i("XMPPClientPar", "Logged in as " + connection.getUser());
//
//			// Set the status to available
//			
//			System.out.println("sssl");
//		} catch (XMPPException ex) {
//			Log.e("XMPPClientPar", "[SettingsDialogPar] Failed to log in as "
//					+ username);
//			Log.e("XMPPClientPar", ex.toString());
//			animationView.setConnection(null);
//			System.out.println("sssle");
//		}
		System.out.println("sssend");
		
		return null;
	}

}
