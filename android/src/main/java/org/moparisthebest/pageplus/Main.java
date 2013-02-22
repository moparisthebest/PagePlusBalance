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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class Main extends Activity {

	public static final String PREFS_NAME = "page_plus", USER = "user", PASS = "pass",
			PHONE = "phone", ERROR = "error", DATE = "date", PP_ONLY = "pp_only", EMPTY = "";

	private Button closeButton;
	private EditText username, password, phone;
	private CheckBox pp_only;
	private TextView plan_data;
	private SharedPreferences settings;
	private Context thisContext;

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

		populate_data();

		this.username.setText(this.settings.getString(USER, EMPTY));
		this.password.setText(this.settings.getString(PASS, EMPTY));
		this.phone.setText(this.settings.getString(PHONE, EMPTY));

		this.pp_only.setChecked(this.settings.getBoolean(PP_ONLY, false));

		this.closeButton = (Button) this.findViewById(R.id.save);
		this.closeButton.setOnClickListener(new OnClickListener() {
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
					final ProgressDialog pd = new ProgressDialog(thisContext);
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.setTitle("Grabbing data...");
					pd.setMessage("be patient");
					pd.setIndeterminate(true);
					pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							populate_data();
						}
					});
					pd.show();
					// final Handler pdHandler = new Handler();
					new Thread(new Runnable() {
						public void run() {

							try {
								new AndroidPPInfo(thisContext).grabData();
								editor.putString(DATE, new Date().toLocaleString());
								editor.remove(ERROR);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								editor.putString(ERROR, e.getMessage());
							}
							editor.commit();


							pd.dismiss();
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

	public static void trim_input(EditText et) {
		trim_input(et, false);
	}

	public static void trim_input(EditText et, boolean toLowerCase) {
		et.setText(toLowerCase ? et.getText().toString().trim().toLowerCase() : et.getText().toString().trim());
	}

	private void populate_data() {
		String data = "";
		settings = getSharedPreferences(PREFS_NAME, 0);
		// if there was an error, put that first:
		if (settings.contains(ERROR))
			data += "Error: " + settings.getString(ERROR, EMPTY) + "\nPrevious data:\n";
		for (int x = 0; x < org.moparisthebest.pageplus.plugins.PPInfo.names.length; ++x)
			data += org.moparisthebest.pageplus.plugins.PPInfo.names[x] + ": " + settings.getString(org.moparisthebest.pageplus.plugins.PPInfo.names[x], EMPTY) + "\n";
		data += "Last Updated: " + settings.getString(DATE, EMPTY) + "\n";
		plan_data.setText(data);
	}
}