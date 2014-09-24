package com.bistri.api_demo;

import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bistri.api.Conference;
import com.bistri.api.Conference.*;
import com.bistri.api.MediaStream;
import com.bistri.api.PeerStream;
import com.bistri.api_demo.R;
import com.bistri.api_demo.utils.NetworkConnectivityReceiver;
import com.bistri.api_demo.AnimationView;

public class MainActivity extends Activity {
	public static TextView status;
	private MediaStreamLayout call_layout;
	public BistriConfig bistriConfig;
	public static final int RESULT_SETTINGS = 1;
	public static MainActivity mainInstance;
	AnimationView animationView;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		status = (TextView) findViewById(R.id.status);
		call_layout = (MediaStreamLayout) findViewById(R.id.call_layout);

		mainInstance = this;
		bistriConfig = new BistriConfig(this, call_layout);

		animationView = new AnimationView(this);
		addContentView(animationView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	}

	@Override
	public void onDestroy() {
		bistriConfig.onDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			openOptionsMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item;

		item = menu.findItem(R.id.call).setVisible(
				bistriConfig.getCallMenuItemStatus());
		item = menu.findItem(R.id.endcall).setVisible(
				bistriConfig.getEndCallMenuItemStatus());
		item = menu.findItem(R.id.quit).setVisible(true);

		return super.onPrepareOptionsMenu(menu);
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection.
		switch (item.getItemId()) {
		case R.id.room1:
			// send
			bistriConfig.StartCall(getString(R.string.room1));
			animationView.setRoomName(getString(R.string.room1));
			return true;

		case R.id.room2:
			// send
			bistriConfig.StartCall(getString(R.string.room2));
			animationView.setRoomName(getString(R.string.room2));
			return true;

		case R.id.room3:
			// send
			bistriConfig.StartCall(getString(R.string.room3));
			animationView.setRoomName(getString(R.string.room3));
			return true;

		case R.id.room4:
			// send
			bistriConfig.StartCall(getString(R.string.room4));
			animationView.setRoomName(getString(R.string.room4));
			return true;

		case R.id.endcall:
			// end call
			animationView.clearDraw();
			bistriConfig.onDestroy();
			return true;

		case R.id.quit:
			// finish();
			System.exit(0);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
