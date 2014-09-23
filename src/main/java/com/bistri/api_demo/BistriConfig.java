package com.bistri.api_demo;

import com.bistri.api_demo.MainActivity;
import com.bistri.api_demo.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bistri.api.Conference;
import com.bistri.api.Conference.*;
import com.bistri.api.MediaStream;
import com.bistri.api.PeerStream;
import com.bistri.api_demo.R;
import com.bistri.api_demo.utils.NetworkConnectivityReceiver;
import android.widget.LinearLayout;

public class BistriConfig implements
		NetworkConnectivityReceiver.ConnectivityChangeListener,
		Conference.Listener, PeerStream.Handler, MediaStream.Handler {
	public static final String TAG = "BistriConfiguration";

	public Conference conference;
	public String room_name;
	public NetworkConnectivityReceiver networkConnectivityReceiver;
	public SharedPreferences settings;
	public static boolean room1Status;
	public static boolean endCallStatus;
	public static boolean callStatus;
	Context con;
	private MediaStreamLayout call_layout;
	AnimationView animationView;

	public BistriConfig(Context con, MediaStreamLayout call_layout) {
		this.con = con;
		room1Status = true;
		endCallStatus = false;
		callStatus = true;
		this.call_layout = call_layout;
	}

	/**
	 * My api info appId: "862ac595" appKey: "bec8330aeaaec89b3b2b3669bd4c8a75"
	 * 
	 * Bistri default appId: "38077edb" appKey:
	 * "4f304359baa6d0fd1f9106aaeb116f33"
	 */

	public void StartCall(String name) {

		Log.d(TAG, "init");
		conference = Conference.getInstance(con);
		conference.setInfo("862ac595", "bec8330aeaaec89b3b2b3669bd4c8a75");

		conference.setVideoOption(VideoOption.MAX_WIDTH, 640);
		conference.setVideoOption(VideoOption.MAX_HEIGHT, 360);
		conference.setVideoOption(VideoOption.MAX_FRAME_RATE, 20);

		conference.setAudioOption(AudioOption.PREFERRED_CODEC, AudioCodec.ISAC);
		conference.setAudioOption(AudioOption.PREFERRED_CODEC_CLOCKRATE, 16000);

		networkConnectivityReceiver = new NetworkConnectivityReceiver(con);

		loadSettings(name);
		conference.addListener(this);
		networkConnectivityReceiver.setListener(this);

		// Force
		statusUpdate(conference.getStatus());
		
		
	}

	@SuppressWarnings("static-access")
	public synchronized void onDestroy() {
		networkConnectivityReceiver.setListener(null);
		conference.removeListener(this);

		if (conference.isInRoom()) {
			conference.leave();
			call_layout.removeAllViews();
			
//			animationView.setClearCanvas(true);
//			animationView.clearCanvas=true;
//			animationView.CleanDraw();
		}
		if (conference.getStatus() == Conference.Status.CONNECTED) {
			conference.disconnect();

			// end call
			callStatus = true;
			endCallStatus = false;
			MainActivity.status.setText("");
		}
	}

	@Override
	public void onConnectivityChange(boolean hasNetwork) {
		Log.d(TAG, "onConnectivityChange");
		if (hasNetwork) {
			statusUpdate(conference.getStatus());
		}
	}

	@Override
	public void onConnectionEvent(Conference.Status state) {
		Log.d(TAG, "onConnectionEvent");
		statusUpdate(state);
	}

	@Override
	public void onError(Conference.ErrorEvent error) {
		if (error == Conference.ErrorEvent.CONNECTION_ERROR) {
			Toast.makeText(con, "Connection error", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onRoomJoined(String room_name) {
	}

	@Override
	public void onRoomQuitted() {
		// call end
		callStatus = true;
		endCallStatus = false;
		//call_layout.removeAllViews();
	}

	@Override
	public void onNewPeer(PeerStream peerStream) {
		// peerStream.setHandler( this );
		peerStream.setHandler(this);
	}

	@Override
	public void onRemovedPeer(PeerStream peerStream) {
		if (!peerStream.hasMedia())
			return;

		MediaStream mediaStream = peerStream.getMedia();
		call_layout.removeMediaStream(mediaStream);
		
	}

	@Override
	public void onMediaStream(String peerId, MediaStream mediaStream) {
		
		call_layout.addMediaStream(mediaStream);
	}

	@Override
	public void onVideoRatioChange(String peer_id, MediaStream mediaStream,
			float ratio) {

	}

	public void statusUpdate(Conference.Status conf_status) {

		Log.d(TAG, "statusUpdate");
		boolean network = networkConnectivityReceiver.hasNetwork();

		String str = "stats="
				+ (conf_status == Conference.Status.CONNECTED ? "CONNECTED"
						: conf_status == Conference.Status.CONNECTING ? "CONNECTING"
								: conf_status == Conference.Status.CONNECTING_SENDREQUEST ? "CONNECTING_SENDREQUEST"
										: conf_status == Conference.Status.DISCONNECTED ? "DISCONNECTED"
												: "<unknown>") + " network=="
				+ network;
		Log.d(TAG, str);

		String status_str = con
				.getString((conf_status == Conference.Status.CONNECTED && network) ? R.string.connected
						: ((conf_status == Conference.Status.CONNECTING || conf_status == Conference.Status.CONNECTING_SENDREQUEST) && network) ? R.string.connecting
								: (!network) ? R.string.no_network
										: R.string.disconnected);
		Log.d(TAG, "new status : " + status_str);

		MainActivity.status.setText(status_str);

		if (network) {

			switch (conf_status) {
			case DISCONNECTED:
				// Auto reconnect
				conference.connect();
				break;
			case CONNECTED:
				if (!conference.isInRoom()) {
					conference.join(room_name);

					// call establish
					callStatus = false;
					endCallStatus = true;
				}
				break;
			}

		}
	}

	public void loadSettings(String roomName) {

		// Log.d(TAG, "loadSettings");

		if (settings == null) {
			settings = PreferenceManager.getDefaultSharedPreferences(con);
		}
		// room name
		room_name = roomName;
		// settings.getString(con.getString(R.string.api_room_key),
		// con.getString(R.string.api_room_value));
	}

	@Override
	public void onPresence(String a, Presence p) {

	}

	public boolean getEndCallMenuItemStatus() {
		return endCallStatus;
	}

	public boolean getCallMenuItemStatus() {
		return callStatus;
	}

}
