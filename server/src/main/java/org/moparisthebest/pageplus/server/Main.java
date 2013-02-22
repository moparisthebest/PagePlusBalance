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

package org.moparisthebest.pageplus.server;

import org.moparisthebest.pageplus.plugins.PPInfo;

import javax.net.ssl.SSLServerSocket;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

public class Main extends Thread {

	public static void main(String[] args) {
		// System.setProperty("javax.net.ssl.keyStore",
		// "/home/mopar/workspace/PagePlusClient/pageplus");
		// System.setProperty("javax.net.ssl.keyStorePassword", "dvorak");
		// System.setProperty("javax.net.debug", "ssl");

		String address = "69.39.224.53";
		int port = 1337;
		final ServerSocket sSocket;
		final SSLServerSocket sslSocket;
		try {
			sSocket = new ServerSocket(port, 0, InetAddress.getByName(address));
			System.out.println("Listening on " + address + ":" + port);
		} catch (Exception e) {
			System.out.println("Fatal Error:" + e.getMessage());
			return;
		}
		/*
		 * try { sslSocket = (SSLServerSocket)
		 * SSLServerSocketFactory.getDefault().createServerSocket(++port, 0,
		 * InetAddress.getByName(address));
		 * System.out.println("Listening via SSL on " + address + ":" + port); }
		 * catch (Exception e) { System.out.println("Fatal Error:" +
		 * e.getMessage()); return; } // start SSL accepting thread new
		 * Thread(){ public void run() { while (true) try { new
		 * Main(sslSocket.accept()); } catch (IOException e) { //
		 * e.printStackTrace(); } } }.start();
		 */
		// use this thread to accept from regular socket
		while (true)
			try {
				new Main(sSocket.accept());
			} catch (IOException e) {
				// e.printStackTrace();
			}
	}

	private Socket s;

	public Main(Socket s) {
		System.out.println(s.getInetAddress().getHostName() + " connected to server.\n");
		this.s = s;
		this.start();
	}

	public void run() {
		// do stuff with socket
		try {
			OutputStream os = s.getOutputStream();
			InputStream is = s.getInputStream();
			if (org.moparisthebest.pageplus.plugins.PPServer.useGzip) {
				os = new java.util.zip.GZIPOutputStream(os);
				is = new java.util.zip.GZIPInputStream(is);
			}
			// DataOutputStream out = new DataOutputStream(os);
			// DataInputStream in = new DataInputStream(is);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String[] userPassPhone = new String[3];
			for (int x = 0; x < userPassPhone.length; ++x)
				userPassPhone[x] = in.readLine();
			// userPassPhone[x] = in.readUTF();
			// for(String st: userPassPhone) System.out.println("st: "+st);
			PPInfo pp = (PPInfo) new org.moparisthebest.pageplus.plugins.PagePlusHTTP();
			try {
				pp.grabData(userPassPhone);
			} catch (Exception e) {
				// fudge this to send back exception
				pp.info = new String[]{"e", e.getMessage()};
			}
			for (String st : pp.info)
				out.write(st + "\n");
			// out.writeUTF(st);
			out.flush();
			if (org.moparisthebest.pageplus.plugins.PPServer.useGzip)
				((GZIPOutputStream) os).finish();
			s.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
