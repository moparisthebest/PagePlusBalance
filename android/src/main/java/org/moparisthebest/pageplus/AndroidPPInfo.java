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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.moparisthebest.pageplus.plugins.PPInfo;

public class AndroidPPInfo {

	private Context parent;

	public AndroidPPInfo(Context parent) {
		this.parent = parent;
	}

	public void grabData() throws Exception {
		NetworkInfo netInfo = (NetworkInfo) ((ConnectivityManager) parent
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if (netInfo == null || !netInfo.isConnected() || netInfo.isRoaming())
			return;

		SharedPreferences settings = parent.getSharedPreferences(Main.PREFS_NAME, 0);

		PPInfo pp;

		if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE && !settings.getBoolean(Main.PP_ONLY, false))
			pp = (PPInfo) new org.moparisthebest.pageplus.plugins.PPServer();
		else // we are connected with wifi?
			pp = (PPInfo) new org.moparisthebest.pageplus.plugins.PagePlusHTTP();

		String user = settings.getString(Main.USER, Main.EMPTY);
		if (user.equals(Main.EMPTY))
			throw new Exception("Username cannot be empty.");
		String pass = settings.getString(Main.PASS, Main.EMPTY);
		if (pass.equals(Main.EMPTY))
			throw new Exception("Password cannot be empty.");
		String phone = settings.getString(Main.PHONE, Main.EMPTY);
		// phone could be empty, I guess...

		pp.grabData(user, pass, phone);
		SharedPreferences.Editor editor = settings.edit();
		for (int x = 0; x < pp.names.length; ++x)
			editor.putString(pp.names[x], pp.info[x]);
		editor.commit();
	}

}
