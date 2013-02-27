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

package org.moparisthebest.pageplus.plugins;

import org.moparisthebest.pageplus.dto.Balance;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class PPServer extends PPInfo {

	/*
	 * Current tests for Gzip compression are as follows (in bytes, from IPTraf):
	 * without SSL:
	 * no gzip: 406 in, 224 out, 630 total
	 * gzip:    654 in, 452 out, 1106 total
	 * with SSL:
	 * no gzip: 1740 in, 2522 out, 4262 total
	 * gzip:    1926 in, 2688 out, 4614 total
	 * so for data in this small of amounts, it's better to not use any compression
	 * also tested DataOutputStream and DataInputStream and they used more than the 
	 * current BufferedReader and BufferedWriter, so I scrapped them
	 */
	public static final boolean useGzip = false;
	public static final boolean useSSL = false;
	// this is android.moparisthebest.org, it saves a little data not having to do the DNS lookup
	public static final String address = "66.55.93.152";
	//public static final String address = "127.0.0.1"; // for testing
	public static final int port = 1337;
	public static final int sslPort = 1338;

	@Override
	public Balance grabData(String user, String pass, String phone) {
		//System.setProperty("javax.net.ssl.trustStore", "/home/mopar/workspace/PagePlusClient/pageplus");
		//System.setProperty("javax.net.ssl.trustStorePassword", "dvorak");
		//System.setProperty("javax.net.debug", "ssl");

		try {
			Socket s;
			if (useSSL)
				s = SSLSocketFactory.getDefault().createSocket(InetAddress.getByName(address), sslPort);
			else
				s = new Socket(InetAddress.getByName(address), port);

			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			if (useGzip) {
				os = new java.util.zip.GZIPOutputStream(os);
				is = new java.util.zip.GZIPInputStream(is);
			}

/*		DataOutputStream out = new DataOutputStream(os);
		DataInputStream in = new DataInputStream(is);
		out.writeUTF(user);
		out.writeUTF(pass);
		out.writeUTF(phone);
*/
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			out.write(user + "\n");
			out.write(pass + "\n");
			out.write(phone + "\n");

			out.flush();
			if (useGzip)
				((GZIPOutputStream) os).finish();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = is.read(buffer)) != -1)
				baos.write(buffer, 0, length);
			Balance ret = new Balance(new String(baos.toByteArray()));
			// one little trick, the server doesn't send the date because it might not be the same as OUR date, so if
			// error is null, set the date to a current one
			if (ret.error == null)
				ret.successDate = new Date();
			return ret;
		} catch (Throwable e) {
			//e.printStackTrace();
			return new Balance().setError(e.getMessage());
		}
	}

}
