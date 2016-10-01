package com.appspot.usbhidterminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.appspot.usbhidterminal.core.Calibration;
import com.appspot.usbhidterminal.core.Consts;
import com.appspot.usbhidterminal.core.events.DeviceAttachedEvent;
import com.appspot.usbhidterminal.core.events.DeviceDetachedEvent;
import com.appspot.usbhidterminal.core.events.LogMessageEvent;
import com.appspot.usbhidterminal.core.events.PrepareDevicesListEvent;
import com.appspot.usbhidterminal.core.events.SelectDeviceEvent;
import com.appspot.usbhidterminal.core.events.ShowDevicesListEvent;
import com.appspot.usbhidterminal.core.events.USBDataReceiveEvent;
import com.appspot.usbhidterminal.core.events.USBDataSendEvent;
import com.appspot.usbhidterminal.core.services.SocketService;
import com.appspot.usbhidterminal.core.services.USBHIDService;
import com.appspot.usbhidterminal.core.services.WebServerService;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

public class USBHIDTerminal extends Activity implements View.OnClickListener {

	private final static String TAG = "HIDTERMINAL-RUNNING";

	private SharedPreferences sharedPreferences;

	private Intent usbService;

	private EditText edtxtHidInput;
	private Button btnSend;
	private Button btnSelectHIDDevice;
	private RadioButton rbSendDataType;
	private SeekBar sbLED0int;
	private SeekBar sbLED1int;
	private SeekBar sbLED2int;
	private SeekBar sbRG0int;
	private SeekBar sbRG1int;
	private SeekBar sbRG2int;
	//private ImageView circletop;
	//private ImageView circlebottom;
	//private static ImageView ImageView;

	private String settingsDelimiter;

	private String receiveDataFormat;
	private String delimiter;

	protected EventBus eventBus;

	private String USB_Str = "";
	private int totalClicks;
	private int not_matches;
	private int matches;



	private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(TAG,"SharedPreferences.onSHared");
			if ("enable_socket_server".equals(key) || "socket_server_port".equals(key)) {
				socketServiceIsStart(false);
				socketServiceIsStart(sharedPreferences.getBoolean("enable_socket_server", false));
			} else if ("enable_web_server".equals(key) || "web_server_port".equals(key)) {
				webServerServiceIsStart(false);
				webServerServiceIsStart(sharedPreferences.getBoolean("enable_web_server", false));
			}
		}
	};

	private void prepareServices() {
		Log.d(TAG,"prepareServices");
		usbService = new Intent(this, USBHIDService.class);
		startService(usbService);
		webServerServiceIsStart(sharedPreferences.getBoolean("enable_web_server", false));
		socketServiceIsStart(sharedPreferences.getBoolean("enable_socket_server", false));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"ONCREATE");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			eventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).installDefaultEventBus();
		} catch (EventBusException e) {
			eventBus = EventBus.getDefault();
		}
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
		initUI();
		Log.d("pre-completed","eventbus");

		eventBus.post(new PrepareDevicesListEvent());
		Log.d("completed","eventbus");

		//eventBus.post(new PrepareDevicesListEvent());

	}

	public void cali_click(View v) {
		Intent i = new Intent(getApplicationContext(), Calibration.class);
		startActivity(i);
	}

	private void initUI() {
		Log.d(TAG,"INIT-UI");
		totalClicks = 0;
		matches = 0;
		not_matches = 0;
		setVersionToTitle();
		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);

		btnSelectHIDDevice = (Button) findViewById(R.id.btnSelectHIDDevice);
		btnSelectHIDDevice.setOnClickListener(this);

		edtxtHidInput = (EditText) findViewById(R.id.edtxtHidInput);

		rbSendDataType = (RadioButton) findViewById(R.id.rbSendData);

		rbSendDataType.setOnClickListener(this);
		//rbSendText.setOnClickListener(this);

		mLog("Initialized\nPlease select your USB HID device\n", false);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		edtxtHidInput.setText("0 0 0");

		sbLED0int = (SeekBar) findViewById(R.id.sld_LED0int);
		sbLED0int.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar sbLED0int, int progress,
										  boolean fromUser) {
				String t = String.valueOf(progress);
				USB_Str = "0 " + t + " 0";
				//edtxtHidInput.setText(msg);
				eventBus.post(new USBDataSendEvent(USB_Str));
			}


		});

		sbLED1int = (SeekBar) findViewById(R.id.sld_LED1int);
		sbLED1int.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar sbLED1int, int progress,
										  boolean fromUser) {
				String t = String.valueOf(progress);
				//String msg = "1 " + t + " 0";
				USB_Str = "1 " + t + " 0";
				//edtxtHidInput.setText(msg);
				eventBus.post(new USBDataSendEvent(USB_Str));
			}


		});

		sbLED2int =(SeekBar) findViewById(R.id.sld_LED2int);
		sbLED2int.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar sbLED2int, int progress,
										  boolean fromUser) {
				String t = String.valueOf(progress);
				//String msg = "2 " + t + " 0";
				//edtxtHidInput.setText(msg);
				USB_Str = "2 " + t + " 0";
				eventBus.post(new USBDataSendEvent(USB_Str));
			}


		});



		sbRG0int = (SeekBar) findViewById(R.id.sld_RG1);
		sbRG0int.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar sbRG0int, int progress, boolean fromUser) {

				ImageView ivcircleB = (ImageView) findViewById(R.id.iv_circleA);
				ivcircleB.setColorFilter(Color.rgb( 255 - progress, progress,0));


			}


		});


		sbRG1int = (SeekBar) findViewById(R.id.sld_RG2);
		sbRG1int.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar sbRG1int, int progress, boolean fromUser) {

				ImageView ivcircleB = (ImageView) findViewById(R.id.iv_circleB);
				ivcircleB.setColorFilter(Color.rgb( 255 - progress, progress,0));

			}


		});


		sbRG2int = (SeekBar) findViewById(R.id.sld_RG3);
		sbRG2int.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar sbRG2int, int progress, boolean fromUser) {

				ImageView ivcircle = (ImageView) findViewById(R.id.iv_circleC);
				ivcircle.setColorFilter(Color.rgb( 255 - progress, progress,0));

			}


		});


	}

	private void changeBitmapColor(Bitmap sourceBitmap, ImageView iv, int color) {


		Bitmap workingBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
		Bitmap resultBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
		Paint p = new Paint();
		ColorFilter filter = new LightingColorFilter(color, 1);
		p.setColorFilter(filter);
		iv.setImageBitmap(resultBitmap);

		Canvas canvas = new Canvas(resultBitmap);
		canvas.drawBitmap(resultBitmap, 0, 0, p);
	}


	public void onClick(View v) {
		if (v == btnSend) {
			eventBus.post(new USBDataSendEvent(edtxtHidInput.getText().toString()));
		} else if (v == rbSendDataType) {
			sendToUSBService(Consts.ACTION_USB_DATA_TYPE, rbSendDataType.isChecked());
		} else if (v == btnSelectHIDDevice) {
			eventBus.post(new PrepareDevicesListEvent());
			Log.d(TAG,"Button select HID DEVICE");
		}
	}

	void showListOfDevices(final CharSequence devicesName[]) {
		Log.d(TAG,"showListOfDevices-USBHID");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		if (devicesName.length == 0) {
			builder.setTitle(Consts.MESSAGE_CONNECT_YOUR_USB_HID_DEVICE);
		} else {
			builder.setTitle(Consts.MESSAGE_SELECT_YOUR_USB_HID_DEVICE);
		}

		builder.setItems(devicesName, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				eventBus.post(new SelectDeviceEvent(0));
				Log.d(TAG,"builder.setItems");
				Log.d(TAG, (String) devicesName[0]);
				Log.d(TAG,Integer.toString(which));
			}
		});
		builder.setCancelable(true);
		builder.show();
	}

	public void onEvent(USBDataReceiveEvent event) {

		mLog(event.getData() + " \nReceived " + event.getBytesCount() + " bytes", true);
		Log.d(TAG," void onEvent(USBDataReceiveEvent event");
	}

	public void onEvent(LogMessageEvent event) {
		mLog(event.getData(), true);
		Log.d(TAG, "onEvent(LogMessageEvent event)");
	}

	public void onEvent(ShowDevicesListEvent event) {
		showListOfDevices(event.getCharSequenceArray());
		Log.d(event.getCharSequenceArray().toString(),"ONeVENT");
		Log.d(TAG, "onEvent(ShowDevicesListEvent event)");
	}

	public void onEvent(DeviceAttachedEvent event) {
		btnSend.setEnabled(true);
		Log.d(TAG, "onEvent(DeviceAttachedEvent event)");
	}

	public void onEvent(DeviceDetachedEvent event) {
		btnSend.setEnabled(false);
		Log.d(TAG, "btnSend.setEnabled(false)");
	}

	@Override
	protected void onStart() {
		super.onStart();
		receiveDataFormat = sharedPreferences.getString(Consts.RECEIVE_DATA_FORMAT, Consts.TEXT);
		Log.d(TAG,receiveDataFormat);
		Log.d(TAG, "void onStart()");
		prepareServices();
		setDelimiter();
		eventBus.register(this);
	}

	@Override
	protected void onStop() {
		eventBus.unregister(this);
		super.onStop();
		Log.d(TAG, "void onStop()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu(Menu menu)");
		getMenuInflater().inflate(R.menu.activity_main, menu);
		setSelectedMenuItemsFromSettings(menu);

		return true;
	}

	private void setSelectedMenuItemsFromSettings(Menu menu) {
		Log.d(TAG, "setSelectedMenuItemsFromSettings(Menu menu)");
		receiveDataFormat = sharedPreferences.getString(Consts.RECEIVE_DATA_FORMAT, Consts.TEXT);
		Log.d(TAG,"LINE 374");
		if (receiveDataFormat != null) {
			if (receiveDataFormat.equals(Consts.BINARY)) {
				menu.findItem(R.id.menuSettingsReceiveBinary).setChecked(true);
			} else if (receiveDataFormat.equals(Consts.INTEGER)) {
				menu.findItem(R.id.menuSettingsReceiveInteger).setChecked(true);
			} else if (receiveDataFormat.equals(Consts.HEXADECIMAL)) {
				menu.findItem(R.id.menuSettingsReceiveHexadecimal).setChecked(true);
			} else if (receiveDataFormat.equals(Consts.TEXT)) {
				menu.findItem(R.id.menuSettingsReceiveText).setChecked(true);
			}
		}

		setDelimiter();
		Log.d(TAG, "LINE 388");
		if (settingsDelimiter.equals(Consts.DELIMITER_NONE)) {
			menu.findItem(R.id.menuSettingsDelimiterNone).setChecked(true);
		} else if (settingsDelimiter.equals(Consts.DELIMITER_NEW_LINE)) {
			menu.findItem(R.id.menuSettingsDelimiterNewLine).setChecked(true);
		} else if (settingsDelimiter.equals(Consts.DELIMITER_SPACE)) {
			menu.findItem(R.id.menuSettingsDelimiterSpace).setChecked(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected(MenuItem item)");
		SharedPreferences.Editor editor = sharedPreferences.edit();
		item.setChecked(true);
		switch (item.getItemId()) {
		case R.id.menuSettings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, Consts.RESULT_SETTINGS);
			break;
		case R.id.menuSettingsReceiveBinary:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.BINARY).apply();
			break;
		case R.id.menuSettingsReceiveInteger:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.INTEGER).apply();
			break;
		case R.id.menuSettingsReceiveHexadecimal:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.HEXADECIMAL).apply();
			break;
		case R.id.menuSettingsReceiveText:
			editor.putString(Consts.RECEIVE_DATA_FORMAT, Consts.TEXT).apply();
			break;
		case R.id.menuSettingsDelimiterNone:
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_NONE).apply();
			break;
		case R.id.menuSettingsDelimiterNewLine:
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_NEW_LINE).apply();
			break;
		case R.id.menuSettingsDelimiterSpace:
			editor.putString(Consts.DELIMITER, Consts.DELIMITER_SPACE).apply();
			break;
		}

		receiveDataFormat = sharedPreferences.getString(Consts.RECEIVE_DATA_FORMAT, Consts.TEXT);
		setDelimiter();
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent(Intent intent)");
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		switch (action) {
			case Consts.WEB_SERVER_CLOSE_ACTION:
				stopService(new Intent(this, WebServerService.class));
				break;
			case Consts.USB_HID_TERMINAL_CLOSE_ACTION:
				stopService(new Intent(this, SocketService.class));
				stopService(new Intent(this, WebServerService.class));
				stopService(new Intent(this, USBHIDService.class));
				((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Consts.USB_HID_TERMINAL_NOTIFICATION);
				finish();
				break;
			case Consts.SOCKET_SERVER_CLOSE_ACTION:
				stopService(new Intent(this, SocketService.class));
				sharedPreferences.edit().putBoolean("enable_socket_server", false).apply();
				break;
		}
	}

	private void setDelimiter() {
		Log.d(TAG, "void setDelimiter()");
		settingsDelimiter = sharedPreferences.getString(Consts.DELIMITER, Consts.DELIMITER_NEW_LINE);
		Log.d(TAG,settingsDelimiter);
		if (settingsDelimiter != null) {
			if (settingsDelimiter.equals(Consts.DELIMITER_NONE)) {
				delimiter = "";
			} else if (settingsDelimiter.equals(Consts.DELIMITER_NEW_LINE)) {
				delimiter = Consts.NEW_LINE;
			} else if (settingsDelimiter.equals(Consts.DELIMITER_SPACE)) {
				delimiter = Consts.SPACE;
			}
		}
		usbService.setAction(Consts.RECEIVE_DATA_FORMAT);
		usbService.putExtra(Consts.RECEIVE_DATA_FORMAT, receiveDataFormat);
		usbService.putExtra(Consts.DELIMITER, delimiter);
		startService(usbService);
	}

	void sendToUSBService(String action) {
		Log.d(TAG, "sendToUSBService(String action)");
		Log.d(TAG,action);
		usbService.setAction(action);
		startService(usbService);
	}

	void sendToUSBService(String action, boolean data) {
		Log.d(TAG, "Line 489 - sendToUSBService(String action, boolean data)");
		Log.d(TAG,action);
		Log.d(TAG,Boolean.toString(data));
		usbService.putExtra(action, data);
		sendToUSBService(action);
	}

	void sendToUSBService(String action, int data) {
		Log.d(TAG, "Line 497 - sendToUSBService(String action, int data)");
		Log.d(TAG,action);
		Log.d(TAG,Integer.toString(data));
		usbService.putExtra(action, data);
		sendToUSBService(action);
	}

	private void mLog(String log, boolean newLine) {
		Log.d(TAG, log);
		Log.d(TAG,Boolean.toString(newLine));
		Log.d(TAG,"LINE 507");


	}

	private void webServerServiceIsStart(boolean isStart) {
		Log.d(TAG,"webServerServiceIsStart(boolean isStart)");
		if (isStart) {
			Log.d(TAG,"LINE 515");
			Intent webServerService = new Intent(this, WebServerService.class);
			webServerService.setAction("start");
			webServerService.putExtra("WEB_SERVER_PORT", Integer.parseInt(sharedPreferences.getString("web_server_port", "7799")));
			startService(webServerService);
		} else {
			stopService(new Intent(this, WebServerService.class));
		}
	}

	private void socketServiceIsStart(boolean isStart) {
		Log.d(TAG,"socketServiceIsStart(boolean isStart)");
		if (isStart) {
			Log.d(TAG, "isStart");
			Intent socketServerService = new Intent(this, SocketService.class);
			socketServerService.setAction("start");
			socketServerService.putExtra("SOCKET_PORT", Integer.parseInt(sharedPreferences.getString("socket_server_port", "7899")));
			startService(socketServerService);
		} else {
			Log.d(TAG,"else");
			stopService(new Intent(this, SocketService.class));
		}
	}

	private void setVersionToTitle() {
		Log.d(TAG,"serVersionToTitle");
		try {
			this.setTitle(Consts.SPACE + this.getTitle() + Consts.SPACE + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public  void nmatch_click(View v) {
		totalClicks += 1;
		not_matches += 1;
	}

	public void match_click(View v) {
		totalClicks += 1;
		matches += 1;
	}

} // end brackets