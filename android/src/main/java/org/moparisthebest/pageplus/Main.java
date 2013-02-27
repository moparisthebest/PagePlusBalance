/*
 * PagePlusBalance retrieves your balance from PagePlusCellular.com, currently for android phones.
 * Copyright (C) 2013 Travis Burtrum (moparisthebest)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.moparisthebest.pageplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import org.moparisthebest.pageplus.dto.Balance;
import org.moparisthebest.pageplus.plugins.PPInfo;

public class Main extends Activity {

	public static final String PREFS_NAME = "page_plus", USER = "user", PASS = "pass",
			PHONE = "phone", PP_ONLY = "pp_only", EMPTY = "";

	public static final String PP_BAL_RECEIVED_ACTION = "PP_BAL_RECEIVED_ACTION";
	public static final String BALANCE = "BALANCE_COMPACT";

	private EditText username, password, phone;
	private CheckBox pp_only;
	private TextView plan_data;
	private SharedPreferences settings;
	private Context thisContext;

	private ProgressDialog pd;

	private IntentFilter intentFilter;
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			populateData(intent.getExtras().getString(BALANCE));
			if (pd != null && pd.isShowing())
				pd.dismiss();
		}
	};


	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.thisContext = this;

		this.plan_data = (TextView) this.findViewById(R.id.plan_data);

		this.username = (EditText) this.findViewById(R.id.username);
		this.password = (EditText) this.findViewById(R.id.password);
		this.phone = (EditText) this.findViewById(R.id.phone);

		this.pp_only = (CheckBox) this.findViewById(R.id.pp_only);

		this.settings = getSharedPreferences(PREFS_NAME, 0);

		populateData();

		this.username.setText(this.settings.getString(USER, EMPTY));
		this.password.setText(this.settings.getString(PASS, EMPTY));
		this.phone.setText(this.settings.getString(PHONE, EMPTY));

		this.pp_only.setChecked(this.settings.getBoolean(PP_ONLY, false));

		this.pd = new ProgressDialog(thisContext);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setIndeterminate(true);
					/*
					pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							populateData();
						}
					});*/

		intentFilter = new IntentFilter();
		intentFilter.addAction(PP_BAL_RECEIVED_ACTION);

		Button saveButton = (Button) this.findViewById(R.id.save);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final SharedPreferences.Editor editor = settings.edit();
				trim_input(username);
				trim_input(password);
				trim_input(phone, true);
				editor.putString(USER, username.getText().toString());
				editor.putString(PASS, password.getText().toString());
				editor.putString(PHONE, phone.getText().toString());
				editor.putBoolean(PP_ONLY, pp_only.isChecked());
				// Commit the edits!
				editor.commit();

				try {
					pd.setTitle("Grabbing data...");
					pd.setMessage("be patient");
					pd.show();
					// final Handler pdHandler = new Handler();
					new Thread(new Runnable() {
						public void run() {
							Balance balance = getBalance();
							Intent broadcastIntent = new Intent();
							broadcastIntent.setAction(Main.PP_BAL_RECEIVED_ACTION);
							broadcastIntent.putExtra(Main.BALANCE, balance == null ? null : balance.compactFormat());
							thisContext.sendBroadcast(broadcastIntent);
						}
					}).start();
				} catch (Exception e) {
					// grabbing data failed
					// currently we just ignore this and go with the last data
					//e.printStackTrace();
					Log.i("PagePlus", Log.getStackTraceString(e));
				}

			}
		});

		Button smsButton = (Button) this.findViewById(R.id.sms);
		smsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					pd.setTitle("Sending SMS...");
					pd.setMessage("and waiting for reply, be patient");
					pd.show();
					// final Handler pdHandler = new Handler();
					new Thread(new Runnable() {
						public void run() {
							SmsManager sms = SmsManager.getDefault();
							sms.sendTextMessage("7243", null, "BAL", null, null);
						}
					}).start();
				} catch (Exception e) {
					// grabbing data failed
					// currently we just ignore this and go with the last data
					//e.printStackTrace();
					Log.i("PagePlus", Log.getStackTraceString(e));
				}

			}
		});

	}

	@Override
	protected void onResume() {
		registerReceiver(intentReceiver, intentFilter);
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(intentReceiver);
		super.onPause();
	}

	private Balance getBalance() {
		NetworkInfo netInfo = ((ConnectivityManager) thisContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if (netInfo == null || !netInfo.isConnected() || netInfo.isRoaming())
			return null;

		SharedPreferences settings = thisContext.getSharedPreferences(Main.PREFS_NAME, 0);

		String user = settings.getString(Main.USER, Main.EMPTY);
		if (user.equals(Main.EMPTY))
			return new Balance().setError("Username cannot be empty.");
		String pass = settings.getString(Main.PASS, Main.EMPTY);
		if (pass.equals(Main.EMPTY))
			return new Balance().setError("Password cannot be empty.");
		String phone = settings.getString(Main.PHONE, Main.EMPTY);
		// phone could be empty, I guess...

		PPInfo pp;

		if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE && !settings.getBoolean(Main.PP_ONLY, false))
			pp = new org.moparisthebest.pageplus.plugins.PPServer();
		else // we are connected with wifi
			pp = new org.moparisthebest.pageplus.plugins.PagePlusHTTP();

		return pp.grabData(user, pass, phone);
	}


	public static void trim_input(EditText et) {
		trim_input(et, false);
	}

	public static void trim_input(EditText et, boolean toLowerCase) {
		et.setText(toLowerCase ? et.getText().toString().trim().toLowerCase() : et.getText().toString().trim());
	}

	private synchronized void populateData(String balanceCompact) {
		SharedPreferences.Editor editor = settings.edit();
		Balance balance = new Balance(balanceCompact);
		if (balance.error != null) {
			// then there was an error and we want to save some info from the last successful attempt
			balanceCompact = settings.getString(BALANCE, null);
			if (balanceCompact != null)
				balanceCompact = balance.copyFrom(new Balance(balanceCompact)).compactFormat();
		}
		editor.putString(BALANCE, balanceCompact);
		editor.commit();
		populateData(balance);
	}

	private synchronized void populateData(Balance balance) {
		plan_data.setText(balance.toString());
	}

	private synchronized void populateData() {
		populateData(new Balance(settings.getString(BALANCE, EMPTY)));
	}
}